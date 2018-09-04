package com.ef;

/*
 * @author Mark Marlow
 * @version 1.0
 * @created 9/1/18
 * 
 * This class contains methods for Database CRUD.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Database {

	private static final String INSERT_LOG_ENTRY_SQL = "insert into LOG_ENTRY (LOG_DATE, IP_ADDRESS, REQUEST, STATUS, USER_AGENT) values (?,?,?,?,?)";
	private static final String INSERT_BLOCKED_REQUEST_SQL = "insert into BLOCKED_REQUEST (START_DATE,END_DATE,IP_ADDRESS,DURATION,REQUESTS,REASON)"
			+ " values (?,?,?,?,?,?)";
	private static final int BATCH_SIZE = 50;

	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/wallet_hub?autoReconnect=true&useSSL=false&user=root&password=mysql123&&rewriteBatchedStatements=true");

		} catch (Exception e) {
			System.out.println(e);
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		}
		return conn;

	}

	/*
	 * This method iterates thru a List of LogEntry objects and INSERTs rows into
	 * the LOG_ENTRY table with the object data.
	 * 
	 * @param logEntries List of LogEntry objects parsed from an access.log file.
	 * 
	 */
	public static void insertLogEntry(List<LogEntry> logEntries) {
		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(INSERT_LOG_ENTRY_SQL)) {
			int batch = 0;
			int count = 0;
			for (LogEntry log : logEntries) {
				java.sql.Timestamp time = new java.sql.Timestamp(log.getEntryDate().getTime());
				ps.setTimestamp(1, time);
				ps.setString(2, log.getIpAddress());
				ps.setString(3, log.getRequest());
				ps.setInt(4, log.getStatus());
				ps.setString(5, log.getUserAgent());
				ps.addBatch();
				batch++;
				count++;
				if (batch > BATCH_SIZE) {
					ps.executeBatch();
					batch = 0;

				}
			}
			int[] inserted = ps.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();

		}
		return;
	}

	/*
	 * This method iterates thru a List of BlockedRequest objects and INSERTs rows
	 * into the BLOCKED_REQUEST table with the object data.
	 * 
	 * @param blockedRequests List of BlockedRequest objects parsed from an
	 * access.log file.
	 * 
	 */
	public static void insertBlockedRequest(List<BlockedRequest> blockedRequests) {
		try (Connection con = getConnection();
				PreparedStatement ps = con.prepareStatement(INSERT_BLOCKED_REQUEST_SQL)) {
			int batch = 0;
			int count = 0;
			for (BlockedRequest request : blockedRequests) {
				java.sql.Timestamp time = new java.sql.Timestamp(request.getStartDate().getTime());
				ps.setTimestamp(1, time);
				time = new java.sql.Timestamp(request.getEndDate().getTime());
				ps.setTimestamp(2, time);
				ps.setString(3, request.getIpAddress());
				ps.setString(4, request.getDuration());
				ps.setInt(5, request.getRequests());
				ps.setString(6, request.getReason());
				ps.addBatch();
				batch++;
				count++;
				if (batch > BATCH_SIZE) {
					ps.executeBatch();
					batch = 0;
				}
			}
			int[] inserted = ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return;
	}

	public static Connection closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn = null;
		}
		return conn;
	}

	public static void main(String args[]) {
		Connection conn = getConnection();
		closeConnection(conn);
	}

}
