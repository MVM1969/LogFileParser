package com.ef;

/*
 * @author Mark Marlow
 * @version 1.0
 * @created 9/1/18
 * 
 * This class has methods that parse a log file and remove rows based on user-input filters.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

	static SimpleDateFormat logDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	static SimpleDateFormat argDateFormatter = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");

	private static void parseFile(String fileName, Date startDate, String duration, int threshold) {
		// String fileName = "C:\\Temp\\Java_MySQL_Test\\access.log";
		String line = "";
		List<LogEntry> entries = new ArrayList<LogEntry>();
		LogEntry le = new LogEntry();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			while ((line = br.readLine()) != null) {
				le = new LogEntry();
				String[] data = line.split("\\|");
				Date date = logDateFormatter.parse(data[0]);

				le.setEntryDate(date);
				le.setIpAddress(data[1]);
				le.setRequest(data[2].replaceAll("\"", ""));
				le.setStatus(Integer.parseInt(data[3]));
				le.setUserAgent(data[4].replaceAll("\"", ""));
				entries.add(le);
			}
			// Database.insertLogEntry(entries);
			findIPs(entries, startDate, duration, threshold);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("yes!");
		}
	}

	private static void findIPs(List<LogEntry> entries, Date startDate, String duration, int threshold) {

		Date tempEndDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate); // sets calendar time/date to startDate

		switch (duration) {
		case "daily": {
			cal.add(Calendar.HOUR_OF_DAY, 24); // adds 24 hour
			threshold = 500;
		}
		case "hourly":
		default: {
			cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
			threshold = 200;

		}
			tempEndDate = cal.getTime(); // returns new date object, one hour in the future
		}
		final Date endDate = tempEndDate;

		// Get list of all entries in the given time period.
		List<LogEntry> sorted = new ArrayList<LogEntry>();
		List<LogEntry> sorted2 = new ArrayList<LogEntry>();

		Date start = new Date();
		System.out.println("Sorted start=" + start);
		for (LogEntry le : entries) {
			if ((le.getEntryDate().equals(startDate) || (le.getEntryDate().after(startDate)))
					&& (le.getEntryDate().equals(endDate) || (le.getEntryDate().before(endDate)))) {
				sorted.add(le);
			}
		}
		Date end = new Date();
		long elapsed = end.getTime() - start.getTime();
		System.out.println("Sorted end=" + elapsed);

		start = new Date();
		System.out.println("Sorted2 start=" + start);
		sorted2 = entries.stream() // convert list to stream
				.filter(entry -> (startDate.getTime() <= entry.getEntryDate().getTime())
						&& (endDate.getTime() >= entry.getEntryDate().getTime())) // get everything after the
																					// startDate into sorted.
				.collect(Collectors.toList());
		end = new Date();
		elapsed = end.getTime() - start.getTime();
		System.out.println("Sorted2 end=" + elapsed);

		/*
		 * sorted2 = entries.stream() // convert list to stream .filter(entry ->
		 * endDate.getTime() >= entry.getEntryDate().getTime()) // now get everything
		 * before // endDate. .collect(Collectors.toList());
		 */

		List<BlockedRequest> thresholdIPs = new ArrayList<BlockedRequest>();
		List<String> alreadyCheckedIPs = new ArrayList<String>();
		int count = 0;
		// Go thru sorted, making a List of any IPs that appear Threshold or more times.
		for (LogEntry le : sorted) {
			count = 0;
			if (alreadyCheckedIPs.contains(le.getIpAddress()))
				continue;
			for (LogEntry inner : sorted) {

				if (le.getIpAddress().equals(inner.getIpAddress())) {
					count++;
				}
			}
			if (count >= threshold) {
				BlockedRequest br = new BlockedRequest();
				br.setStartDate(startDate);
				br.setEndDate(endDate);
				br.setIpAddress(le.getIpAddress());
				br.setDuration(duration);
				br.setRequests(count);
				br.setReason("Number of requests exceeded the duration for the " + duration + " limit, which is "
						+ threshold);
				thresholdIPs.add(br);
			}
			alreadyCheckedIPs.add(le.getIpAddress());
		}

		System.out.println("TBC");
		int x = 0;
		for (BlockedRequest br : thresholdIPs) {
			System.out.println(x + " ->" + br.toString() + "   ");
			x++;
		}
		Database.insertBlockedRequest(thresholdIPs);
	}

	public static void main(String args[]) {
		String fileName = args[0];
		String start = args[1];
		String duration = args[2];
		int threshold = Integer.parseInt(args[3]);
		Date startDate = new Date();
		try {
			startDate = argDateFormatter.parse(start);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(startDate + " " + duration + " " + threshold);

		/*
		 * Date start = new Date(); ; try { start =
		 * argDateFormatter.parse("2017-01-01.00:00:00"); } catch (ParseException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		parseFile(fileName, startDate, duration, threshold);
	}
}
