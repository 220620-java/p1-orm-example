package com.revature.orm.connection.postgres;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import com.revature.orm.ParsedObject;
import com.revature.orm.ParsedObject.RelationshipInfo;
import com.revature.orm.connection.StatementWriter;

public class PostgresWriter<T> implements StatementWriter<T> {
	private final ParsedObject parsedObj;

	public PostgresWriter(ParsedObject parsedObj) {
		this.parsedObj = parsedObj;
	}

	@Override
	public PreparedStatement insert(T obj, Connection conn) throws SQLException {
		StringBuilder sql = new StringBuilder("insert into " + parsedObj.getTableName() + " (");
		sql.append(this.insertColumnsList(parsedObj.getColumns().values()));
		sql.append(") values ");
		for (String column : parsedObj.getColumns().keySet()) {
			if (column.equals(parsedObj.getPrimaryKeyField())) {
				sql.append("default,");
			} else {
				sql.append("?,");
			}
		}
		sql.deleteCharAt(sql.length() - 1); // delete that extra comma
		sql.append(")");

		PreparedStatement stmt = conn.prepareStatement(sql.toString());

		return stmt;
	}

	@Override
	public PreparedStatement findById(Object id, Connection conn) throws SQLException {
		StringBuilder sql = new StringBuilder("select ");
		sql.append(this.selectColumnsList());
		sql.append(" from " + parsedObj.getTableName());
		// adding any necessary joins
		sql.append(this.selectJoinsList(parsedObj));

		sql.append(" where " + parsedObj.getPrimaryKeyColumn() + "=?");

		PreparedStatement stmt = conn.prepareStatement(sql.toString());

		return stmt;
	}

	@Override
	public PreparedStatement findBy(String field, Object value, Connection conn) throws SQLException {
		StringBuilder sql = new StringBuilder("select ");
		sql.append(this.selectColumnsList());
		sql.append(" from " + parsedObj.getTableName());
		// adding any necessary joins
		sql.append(this.selectJoinsList(parsedObj));

		if (field.matches("(\\w+[.]\\w+[.]?)+")) {
			String[] fields = field.split(".");
			try {
				Field tableField = parsedObj.getClass().getField(fields[fields.length-2]);
				ParsedObject fieldObj = new ParsedObject(tableField.getClass());
				String subField = fieldObj.getColumns().get(fields[fields.length-1]);
				sql.append(" where " + fieldObj.getTableName() + "." + subField + "=?");
			} catch (NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
		} else {
			sql.append(" where " + field + "=?");
		}

		PreparedStatement stmt = conn.prepareStatement(sql.toString());

		return stmt;
	}

	@Override
	public String findAll() {
		StringBuilder sql = new StringBuilder("select ");
		sql.append(this.selectColumnsList());
		sql.append(" from " + parsedObj.getTableName());
		// adding any necessary joins
		sql.append(this.selectJoinsList(parsedObj));

		return sql.toString();
	}

	@Override
	public PreparedStatement update(T obj, Connection conn) throws SQLException {
		StringBuilder sql = new StringBuilder("update " + parsedObj.getTableName() + " set ");
		sql.append(this.updateColumnsList(parsedObj.getColumns().values()));
		sql.append(" where " + parsedObj.getPrimaryKeyColumn() + "=?");

		PreparedStatement stmt = conn.prepareStatement(sql.toString());

		return stmt;
	}

	@Override
	public PreparedStatement delete(T obj, Connection conn) throws SQLException {
		StringBuilder sql = new StringBuilder(
				"delete from " + parsedObj.getTableName() + " where " + parsedObj.getPrimaryKeyColumn() + "=?");

		PreparedStatement stmt = conn.prepareStatement(sql.toString());

		return stmt;
	}

	private String insertColumnsList(Collection<String> values) {
		StringBuilder columnsList = new StringBuilder("");

		for (String column : values) {

			columnsList.append(column + ",");
		}

		columnsList.deleteCharAt(values.size() - 1);
		return columnsList.toString();
	}

	private Object selectColumnsList() {
		StringBuilder sql = new StringBuilder("");
		for (String column : parsedObj.getColumns().values()) {
			sql.append(parsedObj.getTableName() + "." + column + ",");
		}

		for (ParsedObject innerObj : parsedObj.getRelationships().keySet()) {
			for (String column : innerObj.getColumns().values()) {
				sql.append(innerObj.getTableName() + "." + column + ",");
			}
			// if the inner object also has relationships
			if (!innerObj.getRelationships().isEmpty()) {
				sql.append(selectJoinsList(innerObj));
			}
		}
		return sql.toString();
	}

	private String selectJoinsList(ParsedObject parsedObj) {
		StringBuilder sql = new StringBuilder("");

		for (ParsedObject innerObj : parsedObj.getRelationships().keySet()) {
			RelationshipInfo info = parsedObj.getRelationships().get(innerObj);
			switch (info.getType()) {
			case ONE_TO_ONE:
			case MANY_TO_ONE:
				sql.append(" left join " + innerObj.getTableName() + " on " + parsedObj.getTableName() + "."
						+ info.getOwnerJoinColumn() + "=" + innerObj.getTableName() + "."
						+ innerObj.getPrimaryKeyColumn());
				break;
			case ONE_TO_MANY:
				sql.append(" left join " + innerObj.getTableName() + " on " + parsedObj.getTableName() + "."
						+ parsedObj.getPrimaryKeyColumn() + "=" + innerObj.getTableName() + "."
						+ info.getOwnedJoinColumn());
				break;
			case MANY_TO_MANY:
				sql.append(" left join " + info.getJoinTable() + " on " + parsedObj.getTableName() + "."
						+ parsedObj.getPrimaryKeyColumn() + "=" + info.getJoinTable() + "." + info.getOwnerJoinColumn()
						+ " join " + innerObj.getTableName() + " on " + innerObj.getTableName() + "."
						+ innerObj.getPrimaryKeyColumn() + "=" + info.getJoinTable() + "." + info.getOwnedJoinColumn());
				break;
			}
			// if the inner object also has relationships
			if (!innerObj.getRelationships().isEmpty()) {
				sql.append(selectJoinsList(innerObj));
			}
		}
		return sql.toString();
	}

	private String updateColumnsList(Collection<String> values) {
		StringBuilder columnsList = new StringBuilder("");

		for (String column : values) {

			columnsList.append(column + "=?,");
		}

		columnsList.deleteCharAt(values.size() - 1);
		return columnsList.toString();
	}
}
