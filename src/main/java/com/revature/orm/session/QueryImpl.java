package com.revature.orm.session;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.revature.orm.ORMQuery;
import com.revature.orm.ParsedObject;
import com.revature.orm.connection.ConnectionManager;
import com.revature.orm.connection.RevConnectionManager;
import com.revature.orm.connection.StatementWriter;
import com.revature.orm.exceptions.UnsupportedModelException;

public class QueryImpl<T> implements ORMQuery<T> {
	private final Connection conn;
	private final StatementWriter<T> writer;
	private final ParsedObject parsedObj;
	
	QueryImpl(Connection conn, StatementWriter<T> writer, ParsedObject parsedObj) {
		this.conn = conn;
		this.writer = writer;
		this.parsedObj = parsedObj;
	}

	@Override
	public T findById(Object id) throws SQLException {
		T obj = null;
		
		try (PreparedStatement stmt = writer.findById(id, conn)) {
			stmt.setObject(1, id);
			ResultSet resultSet = stmt.executeQuery();
			
			if (resultSet.next()) {
				try {
					obj = (T) parsedObj.getOriginalType().newInstance();
					
					try {
						for (String fieldName : parsedObj.getColumns().keySet()) {
							String columnName = parsedObj.getColumns().get(fieldName);
							Object columnValue = resultSet.getObject(columnName);
							
							Field field = obj.getClass().getField(fieldName);
							if (field.isAccessible()) {
								field.set(obj, columnValue);
							} else {
								String methodName = "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
								obj.getClass().getMethod(methodName, columnValue.getClass()).invoke(obj, columnValue);
							}
						}
					} catch (Exception e) {
						throw new UnsupportedModelException("Your model has inaccessible fields (private and missing setter method).");
					}
				} catch (Exception e) {
					throw new UnsupportedModelException("Your model is missing a no-arguments constructor.");
				}
			}
		} catch (SQLException e) {
			throw e;
		}
		
		closeConn();
		return obj;
	}

	@Override
	public T findOneBy(String field, Object value) throws SQLException {
		T obj = null;
		
		try (PreparedStatement stmt = writer.findBy(field, value, conn)) {
			stmt.setObject(1, value);
			ResultSet resultSet = stmt.executeQuery();
			
			if (resultSet.next()) {
				try {
					obj = (T) parsedObj.getOriginalType().newInstance();
					
					try {
						for (String fieldName : parsedObj.getColumns().keySet()) {
							String columnName = parsedObj.getColumns().get(fieldName);
							Object columnValue = resultSet.getObject(columnName);
							
							Field currentField = obj.getClass().getField(fieldName);
							if (currentField.isAccessible()) {
								currentField.set(obj, columnValue);
							} else {
								String methodName = "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
								obj.getClass().getMethod(methodName, columnValue.getClass()).invoke(obj, columnValue);
							}
						}
					} catch (Exception e) {
						throw new UnsupportedModelException("Your model has inaccessible fields (private and missing setter method).");
					}
				} catch (Exception e) {
					throw new UnsupportedModelException("Your model is missing a no-arguments constructor.");
				}
			}
		} catch (SQLException e) {
			throw e;
		}
		
		closeConn();
		return obj;
	}

	@Override
	public List<T> findAllBy(String field, Object value) throws SQLException {
		List<T> list = new ArrayList<>();
		
		try (PreparedStatement stmt = writer.findBy(field, value, conn)) {
			stmt.setObject(1, value);
			ResultSet resultSet = stmt.executeQuery();
			
			while (resultSet.next()) {
				try {
					T obj = (T) parsedObj.getOriginalType().newInstance();
					
					try {
						for (String fieldName : parsedObj.getColumns().keySet()) {
							String columnName = parsedObj.getColumns().get(fieldName);
							Object columnValue = resultSet.getObject(columnName);
							
							Field currentField = obj.getClass().getField(fieldName);
							if (currentField.isAccessible()) {
								currentField.set(obj, columnValue);
							} else {
								String methodName = "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
								obj.getClass().getMethod(methodName, columnValue.getClass()).invoke(obj, columnValue);
							}
							list.add(obj);
						}
					} catch (Exception e) {
						throw new UnsupportedModelException("Your model has inaccessible fields (private and missing setter method).");
					}
				} catch (Exception e) {
					throw new UnsupportedModelException("Your model is missing a no-arguments constructor.");
				}
			}
		} catch (SQLException e) {
			throw e;
		}
		
		closeConn();
		return list;
	}

	@Override
	public List<T> findAll() throws SQLException {
		List<T> list = new ArrayList<>();
		
		try (Statement stmt = conn.createStatement()) {
			ResultSet resultSet = stmt.executeQuery(writer.findAll());
			
			while (resultSet.next()) {
				try {
					T obj = (T) parsedObj.getOriginalType().newInstance();
					
					try {
						for (String fieldName : parsedObj.getColumns().keySet()) {
							String columnName = parsedObj.getColumns().get(fieldName);
							Object columnValue = resultSet.getObject(columnName);
							
							Field currentField = obj.getClass().getField(fieldName);
							if (currentField.isAccessible()) {
								currentField.set(obj, columnValue);
							} else {
								String methodName = "set" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
								obj.getClass().getMethod(methodName, columnValue.getClass()).invoke(obj, columnValue);
							}
							list.add(obj);
						}
					} catch (Exception e) {
						throw new UnsupportedModelException("Your model has inaccessible fields (private and missing setter method).");
					}
				} catch (Exception e) {
					throw new UnsupportedModelException("Your model is missing a no-arguments constructor.");
				}
			}
		} catch (SQLException e) {
			throw e;
		}
		
		closeConn();
		return list;
	}
	
	private void closeConn() {
		ConnectionManager mgr = RevConnectionManager.getConnectionManager();
		mgr.releaseConnection(conn);
	}

}
