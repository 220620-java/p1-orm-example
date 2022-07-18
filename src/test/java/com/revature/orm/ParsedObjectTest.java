package com.revature.orm;

import org.junit.jupiter.api.Test;

import com.revature.orm.annotations.PrimaryKey;
import com.revature.orm.annotations.Relationship;
import com.revature.orm.annotations.Table;
import com.revature.orm.enums.RelationshipType;

public class ParsedObjectTest {
	@Test
	public void createParsedObject() {
		ParsedObject obj = new ParsedObject(TestClass.class);
		
		System.out.println(obj.getPrimaryKeyColumn());
		System.out.println(obj.getTableName());
		System.out.println(obj.getColumns());
		System.out.println(obj.getRelationships());
		
		for (ParsedObject obj1 : obj.getRelationships().keySet()) {
			System.out.println(obj1.getPrimaryKeyColumn());
			System.out.println(obj1.getTableName());
			System.out.println(obj1.getColumns());
			System.out.println(obj1.getRelationships());
		}
	}
	
}

class TestClass {
	@PrimaryKey
	public String hello;
	public String helloHello;
	public String helloHelloHello;
	@Relationship(type=RelationshipType.MANY_TO_ONE, ownerJoinColumn="hello4_id")
	public HelloHello helloHelloHelloHello;
	private String helloHelloHelloHelloHelloHelloHelloHelloHello = "D";
	
	public String getHelloHelloHelloHelloHelloHelloHelloHelloHello() {
		return this.helloHelloHelloHelloHelloHelloHelloHelloHello;
	}
}

@Table(name = "hello_")
class HelloHello {
	@PrimaryKey
	public int id;
	public String name;
}