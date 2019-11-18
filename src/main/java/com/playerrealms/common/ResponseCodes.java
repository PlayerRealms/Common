package com.playerrealms.common;

public enum ResponseCodes {

	/**
	 * Server unknown
	 */
	UNKNOWN_SERVER(0),
	/**
	 * Operation cannot be completed because the server is offline
	 */
	SERVER_NOT_RUNNING(1),
	/**
	 * Server stopped successfully
	 */
	SERVER_STOPPED(2),
	/**
	 * Server force stopped successfully
	 */
	SERVER_FORCE_STOPPED(3),
	/**
	 * Server name contains illegal characters.
	 * Only numbers, lettings and '-' are allowed
	 */
	SERVER_NAME_INVALID(4),
	/**
	 * Length exceeds 20 characters or is less than 3
	 */
	SERVER_NAME_LENGTH_INVALID(5),
	/**
	 * Name is taken
	 */
	SERVER_NAME_TAKEN(6),
	/**
	 * Server created successfully
	 */
	SERVER_CREATED(7),
	/**
	 * Server starting, {@link ResponseCodes#SERVER_STARTED} will be sent when the server finishes turning on
	 */
	SERVER_STARTING(8),
	/**
	 * Command executed on server successfully
	 */
	SERVER_COMMAND_EXECUTED(9),
	/**
	 * Server deleted successfully
	 */
	SERVER_REMOVED(10),
	/**
	 * Server already running, cannot be started
	 */
	SERVER_ALREADY_RUNNING(11), 
	/**
	 * An unknown error has occured
	 */
	UNKNOWN_ERROR(12),
	/**
	 * When a console read contract is created
	 */
	CONTRACT_CREATED(13),
	/**
	 * When metadata is successfully saved
	 */
	METADATA_SAVED(14),
	/**
	 * When metadata is successfully set
	 */
	METADATA_SET(15),
	
	/**
	 * If the server type is unknown
	 */
	UNKNOWN_SERVER_TYPE(16),
	
	/**
	 * The server is being restarted
	 */
	SERVER_RESTARTING(17),
	
	/**
	 * The server has been renamed
	 */
	SERVER_RENAMED(18), 
	
	/**
	 * Hardware memory limit reached, could not start another server
	 */
	MEMORY_LIMIT_REACHED(19),
	
	;
	
	private final int code;
	
	private ResponseCodes(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}

	public static ResponseCodes getById(int responseType) {
		for(ResponseCodes code : values()){
			if(responseType == code.getCode()){
				return code;
			}
		}
		return null;
	}
	
	
	
}
