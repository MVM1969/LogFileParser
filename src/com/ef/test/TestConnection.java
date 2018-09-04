package com.ef.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ef.Database;

class TestConnection {

	Connection connection;

	@Before
	public void before() {
		connection = Database.getConnection();
	}

	@After
	public void after() {
		Database.closeConnection(connection);
	}

	@Test
	void test1() {
		assertNotNull(Database.getConnection());

	}

	@Test
	void test2() {
		Connection con = Database.getConnection();
		assertNull(Database.closeConnection(con));
	}

}
