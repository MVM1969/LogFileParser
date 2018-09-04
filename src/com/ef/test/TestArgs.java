package com.ef.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ef.Error;
import com.ef.Parser;

class TestArgs {

	@Test
	/*
	 * Test for invalid args
	 */
	void test1() {
		String args[] = { "C:\\Temp\\Java_MySQL_Test\\access.log", "2017-01-01.15:00:00", "hourly" };
		assertEquals(Error.ARGS.getDescription(), Parser.parseAndSave(args).getDescription());

	}

	@Test
	/*
	 * Test for invalid startdate
	 */
	void test2() {
		String args[] = { "C:\\Temp\\Java_MySQL_Test\\access.log", "2017-01-01.25:00", "hourly", "200" };
		assertEquals(Error.INVALID_STARTDATE.getDescription(), Parser.parseAndSave(args).getDescription());

	}

	@Test
	/*
	 * Test for invalid duration
	 */
	void test3() {
		String args[] = { "C:\\Temp\\Java_MySQL_Test\\access.log", "2017-01-01.15:00:00", "yearly", "200" };
		assertEquals(Error.INVALID_DURATION.getDescription(), Parser.parseAndSave(args).getDescription());

	}

	@Test
	/*
	 * Test for invalid threshold
	 */
	void test4() {
		String args[] = { "C:\\Temp\\Java_MySQL_Test\\access.log", "2017-01-01.15:00:00", "hourly", "200x" };
		assertEquals(Error.INVALID_THRESHOLD.getDescription(), Parser.parseAndSave(args).getDescription());

	}

	@Test
	/*
	 * Test for invalid logfile
	 */
	void test5() {
		String args[] = { "C:\\Temp\\Java_MySQL_Test\\access11.log", "2017-01-01.15:00:00", "hourly", "200" };
		assertEquals(Error.INVALID_ACCESS_LOG.getDescription(), Parser.parseAndSave(args).getDescription());

	}

}
