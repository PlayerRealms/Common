package com.playerrealms.common.file;

import java.nio.charset.Charset;

public class FileTransferProtocol {

	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static final int DOWNLOAD_REQUEST = 0;
	public static final int COMMON_FILES_REQUEST = 1;
	public static final int FORWARD_REQUEST = 2;
	
	public static final int UNKNOWN_REQUEST = 0;
	public static final int UNKNOWN_SERVER = 1;
	public static final int SERVER_RUNNING = 2;
	public static final int UNKNOWN_ERROR = 3;
	public static final int DOWNLOAD_RESPONSE = 4;
	public static final int FORWARD_SUCCESS = 5;
	
	public static final int TRANSFER_BUFFER_SIZE = 2048;

	public static final int DEFAULT_PORT = 8485;
	
	
}
