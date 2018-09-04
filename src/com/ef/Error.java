package com.ef;

public enum Error {
	SUCCESS(0, "Processing successfully completed."), ARGS(1, "There is a problem with the command line arguments."),
	NO_LOG_ROWS(2, "There were no rows of data in the log file"),
	NO_BLOCKED_REQUESTS(3, "There were no blocked requests for the given parameters"),
	INVALID_DURATION(4, "Invalid Duration."), INVALID_STARTDATE(5, "Invalid Start Date"),
	INVALID_THRESHOLD(6, "Invalid Threshold."), INVALID_ACCESS_LOG(7, "Invalid Access File"),
	SYSTEM_ERROR(8, "Error - See logs for more details.");

	private final int code;
	private final String description;

	private Error(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code + ": " + description;
	}
}