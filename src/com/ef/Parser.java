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

public class Parser {

	static final SimpleDateFormat logDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	static final SimpleDateFormat argDateFormatter = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
	static final int DAILY = 500;
	static final int HOURLY = 200;

	/*
	 * This method parses an access log file and builds a List of LogEntry objects
	 * from it using the parameters below
	 * 
	 * @fileName - the full path and filename of the access log file
	 * 
	 * @startDate -
	 */

	private static List<LogEntry> parseFile(String fileName) {
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		return entries;
	}

	private static List<BlockedRequest> findIPs(List<LogEntry> entries, Date startDate, String duration,
			int threshold) {

		Date tempEndDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);

		switch (duration.toUpperCase()) {
		case "DAILY": {
			cal.add(Calendar.HOUR_OF_DAY, 24);
			threshold = DAILY;
		}
		case "HOURLY":
		default: {
			cal.add(Calendar.HOUR_OF_DAY, 1);
			threshold = HOURLY;
		}
			tempEndDate = cal.getTime();
		}
		final Date endDate = tempEndDate;

		// Get list of all entries in the given time period.
		List<LogEntry> sorted = new ArrayList<LogEntry>();
		List<LogEntry> sorted2 = new ArrayList<LogEntry>();

		/*
		 * Repeated time tests showed that of the 2 sorting methods below, simple
		 * iteration was over 500% faster than using Java 8 Streams and Lambda
		 */

		// Date start = new Date();
		// System.out.println("Sorted start=" + start);
		for (LogEntry le : entries) {
			if ((le.getEntryDate().equals(startDate) || (le.getEntryDate().after(startDate)))
					&& (le.getEntryDate().equals(endDate) || (le.getEntryDate().before(endDate)))) {
				sorted.add(le);
			}
			// Once we get an entry that's past the end-date, we
			// can stop.
			if (le.getEntryDate().getTime() > endDate.getTime())
				break;
		}
		// Date end = new Date();
		// long elapsed = end.getTime() - start.getTime();
		// System.out.println("Sorted end=" + elapsed);

		// start = new Date();
		// System.out.println("Sorted2 start=" + start);
		/*
		 * sorted2 = entries.stream() // convert list to stream .filter(entry ->
		 * (startDate.getTime() <= entry.getEntryDate().getTime()) && (endDate.getTime()
		 * >= entry.getEntryDate().getTime())) .collect(Collectors.toList());
		 */
		// end = new Date();
		// elapsed = end.getTime() - start.getTime();
		// System.out.println("Sorted2 end=" + elapsed);

		List<BlockedRequest> thresholdIPs = new ArrayList<BlockedRequest>(); // ArrayList of requests that exceed the
																				// threshold.
		List<String> alreadyCheckedIPs = new ArrayList<String>(); // ArrayList of already-checked IP Addresses.
		int count = 0;
		// Go thru sorted, making a List of any IPs that appear Threshold# or more
		// times.
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

		int x = 0;
		System.out.println(
				"The following IP Addresses were blocked for exceeding their " + duration + " limit of " + threshold);
		for (BlockedRequest br : thresholdIPs) {
			System.out.println(br.getIpAddress());

		}
		return thresholdIPs;

	}

	/*
	 * arg[0] - path to Access Log file arg[1] - parsing start date arg[2] -
	 * duration - either hourly or daily arg[3] - threshold - needs to be an int.
	 */
	public static void main(String args[]) {
		String fileName = args[0];
		String start = args[1];
		String duration = args[2];
		if (duration.equalsIgnoreCase("HOURLY") || (duration.equalsIgnoreCase("DAILY"))) {
			try {
				int threshold = Integer.parseInt(args[3]);
				Date startDate = new Date();

				startDate = argDateFormatter.parse(start);
				List<LogEntry> entries = parseFile(fileName);
				if (entries.size() == 0) {
					System.out.println("There were no rows of data in the log file");

				} else {
					Database.insertLogEntry(entries);
					List<BlockedRequest> thresholdIPs = findIPs(entries, startDate, duration, threshold);
					if (thresholdIPs.size() == 0) {
						System.out.println("There were no blocked requests for the given parameters");
					} else {
						Database.insertBlockedRequest(thresholdIPs);
						System.out.println("Parsing Complete.");
					}
				}
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				System.out.println("Error. Duration not a number.");
			} catch (ParseException pe) {
				pe.printStackTrace();
				System.out.println("Error. Date not in yyyy-MM-dd.HH:mm:ss format");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error.  Incomplete.");
			}
		} else
			System.out.println("Invalid Duration. Can only be DAILY or HOURLY");

	}
}
