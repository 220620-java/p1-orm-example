package com.revature.orm.connection.postgres;
//package com.revature.orm.connection.postgres;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.lang.reflect.Modifier;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.LinkedList;
//import java.util.List;
//
//import com.revature.orm.ParsedObject;
//import com.revature.orm.annotations.PrimaryKey;
//import com.revature.orm.annotations.Relationship;
//import com.revature.orm.annotations.Table;
//import com.revature.orm.annotations.Transient;
//import com.revature.orm.connection.StatementWriter;
//import com.revature.orm.enums.RelationshipType;
//
//public class PostgresWriter<T> implements StatementWriter<T> {
//	public PostgresWriter(ParsedObject obj) {
//		
//	}
//
//	@Override
//	public T insert(T obj, Connection conn) throws SQLException {
//
//	}
//
//	@Override
//	public T findById(int id, Connection conn) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public T findFirstBy(String field, Object value, Connection conn) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<T> findAllBy(String field, Object value, Connection conn) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<T> findAll(Connection conn) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public T update(T obj, Connection conn) throws SQLException {
//		
//	}
//
//	@Override
//	public T delete(T obj, Connection conn) throws SQLException {
//
//	}
//
//	PreparedStatement setValues(T obj, PreparedStatement stmt) {
//		for (String fieldName : camelCaseFieldStrings) {
//			if (!"this$0".equals(fieldName)) {
//				try {
//					Field field = obj.getClass().getDeclaredField(fieldName);
//					Object value = null;
//
//					// if the field is not a simple type and has the @Table annotation
//					if (!isSimpleType(field) && field.getType().isAnnotationPresent(Table.class)) {
//						// and if the field is not a collection (many-to-many/one-to-many)
//						if (!Collection.class.isAssignableFrom(field.getType())) {
//							for (Field subField : field.get(obj).getClass().getDeclaredFields()) {
//								if (subField.getAnnotation(PrimaryKey.class) != null) {
//									value = subField.get(field.get(obj));
//								}
//							}
//						}
//					} else {
//						try {
//							value = field.get(obj);
//						} catch (IllegalAccessException e) {
//							value = null;
//						}
//						if (!field.isAccessible() && value == null) {
//							try {
//								String getterName = "get" + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
//								Method getter = obj.getClass().getDeclaredMethod(getterName);
//								value = getter.invoke(obj);
//							} catch (NoSuchMethodException e) {
//								value = null;
//							}
//						}
//					}
//
//					System.out.println(value);
//					if (value != null) {
//						// stmt.setObject(camelCaseFieldStrings.indexOf(fieldName)+1, value);
//					} else {
//						throw new RuntimeException(
//								"The field " + fieldName + " is private and missing a getter method.");
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return stmt;
//	}
//
//	private boolean isSimpleType(Field field) {
//		Class type = field.getType();
//		return (type.isPrimitive() || type == Integer.class || type == String.class || type == Boolean.class
//				|| type == Double.class || type == Long.class || type == Character.class || type == Short.class
//				|| type == Float.class || type == Byte.class);
//	}
//
//}
