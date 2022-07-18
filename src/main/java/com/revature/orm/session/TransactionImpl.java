package com.revature.orm.session;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.LinkedList;
import java.util.List;

import com.revature.orm.ORMTransaction;
import com.revature.orm.ParsedObject;
import com.revature.orm.connection.ConnectionManager;
import com.revature.orm.connection.RevConnectionManager;
import com.revature.orm.connection.StatementWriter;
import com.revature.orm.exceptions.InvalidKeywordException;
import com.revature.orm.exceptions.UnsupportedModelException;

public class TransactionImpl<T> implements ORMTransaction<T> {
	private Connection conn;
	private final StatementWriter<T> writer;
	// objects will be savepoint names (strings) and preparedstatements
	private List<Object> stmts;
	private List<Savepoint> savepoints = new LinkedList<>();
	private List<Object> generatedKeys = new LinkedList<>();

	TransactionImpl(Connection conn, StatementWriter<T> writer, List<Object> stmts) {
		this.conn = conn;
		this.writer = writer;
		this.stmts = stmts;
	}

	@Override
	public ORMTransaction<T> addStatement(String keyword, Object obj) throws SQLException {
		keyword = keyword.toUpperCase();
		PreparedStatement stmt = null;
		switch (keyword) {
		case "INSERT":
			stmt = writer.insert((T) obj, conn);
			stmt = setInsertValues(obj);
			break;
		case "UPDATE":
			stmt = writer.update((T) obj, conn);
			Object primaryKeyValue1 = getPkValue(obj);
			stmt.setObject(1, primaryKeyValue1);
			break;
		case "DELETE":
			stmt = writer.delete((T) obj, conn);
			Object primaryKeyValue2 = getPkValue(obj);
			stmt.setObject(1, primaryKeyValue2);
			break;
		default:
			throw new InvalidKeywordException();
		}
		if (stmt != null) {
			stmts.add(stmt);
		}
		
		return new TransactionImpl<T>(conn, writer, stmts);
	}

	private PreparedStatement setInsertValues(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object getPkValue(Object obj) {
		Object primaryKeyValue = null;
		
		try {
			ParsedObject parsedObj = new ParsedObject(obj.getClass());
			String pkFieldName = parsedObj.getPrimaryKeyField();
			Field pkField = obj.getClass().getField(pkFieldName);
			if (pkField.isAccessible()) {
				primaryKeyValue = pkField.get(obj);
			} else {
				String methodName = "get" + pkFieldName.toUpperCase().charAt(0) + pkFieldName.substring(1);
				primaryKeyValue = obj.getClass().getMethod(methodName).invoke(obj);
			}
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			throw new UnsupportedModelException("Your model has inaccessible fields (private with no getter).");
		}
		
		return primaryKeyValue;
	}

	@Override
	public int execute() throws SQLException {
		conn.setAutoCommit(false);

		int rowsUpdated = 0;
		for (Object stmtOrSvpt : stmts) {
			if (stmtOrSvpt instanceof PreparedStatement) {
				PreparedStatement stmt = (PreparedStatement) stmtOrSvpt;
				rowsUpdated += stmt.executeUpdate();
				ResultSet resultSet = stmt.getGeneratedKeys();
				if (resultSet.next()) {
					generatedKeys.add(resultSet.getObject(1));
				}
			} else if (stmtOrSvpt instanceof String) {
				Savepoint svpt = conn.setSavepoint(stmtOrSvpt.toString());
				savepoints.add(svpt);
			}
		}

		return rowsUpdated;
	}

	@Override
	public List<Object> getGeneratedKeys() {
		return generatedKeys;
	}

	@Override
	public void commit() throws SQLException {
		conn.commit();
		closeConn();
	}

	@Override
	public void rollback() throws SQLException {
		conn.rollback();
		stmts = new LinkedList<>();
	}
	
	@Override
	public void rollbackToSavepoint(String name) throws SQLException {
		int index = stmts.indexOf(name);
		for (int i=index;i<stmts.size();i++) {
			stmts.remove(i);
		}
		
		for (Savepoint svpt : savepoints) {
			if (svpt.getSavepointName().equals(name)) {
				conn.rollback(svpt);
				return;
			}
		}
		throw new SQLException("No savepoint found with that name.");
	}

	@Override
	public ORMTransaction<T> addSavepoint(String name) {
		stmts.add(name);
		return new TransactionImpl<T>(conn, writer, stmts);
	}

	private void closeConn() {
		ConnectionManager mgr = RevConnectionManager.getConnectionManager();
		mgr.releaseConnection(conn);
		conn=null;
	}

	@Override
	public void close() throws SQLException {
		closeConn();
	}

}
