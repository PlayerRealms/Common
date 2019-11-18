package com.playerrealms.common.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FileTransferClient {

	private final String host;
	private final int port;
	
	public FileTransferClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void requestDownloadServer(String serverName, String ip, int forwardPort) throws IOException{
		Objects.requireNonNull(serverName);
		Objects.requireNonNull(ip);
		if(port < 0){
			throw new IllegalArgumentException("port cannot be less than 0");
		}
		
		try(Socket client = new Socket(host, port)){
			
			OutputStream os = client.getOutputStream();
			
			os.write(FileTransferProtocol.FORWARD_REQUEST);
			
			writeString(os, serverName);
			writeString(os, ip);
			
			os.write(ByteBuffer.allocate(2).putChar((char) forwardPort).array());
			
			os.flush();
			
			int response = client.getInputStream().read();
			
			if(response != FileTransferProtocol.FORWARD_SUCCESS){
				throw new IOException("Error Code: "+response);
			}
			
		}
		
	}
	
	public void downloadCommons(File dataOutputFile) throws IOException {
		Objects.requireNonNull(dataOutputFile);
		if(!dataOutputFile.exists()){
			dataOutputFile.createNewFile();
		}
		
		try(Socket client = new Socket(host, port)){
			client.getOutputStream().write(FileTransferProtocol.COMMON_FILES_REQUEST);
			
			client.getOutputStream().flush();
			
			int response = client.getInputStream().read();
			
			if(response == FileTransferProtocol.DOWNLOAD_RESPONSE){
				
				InputStream is = client.getInputStream();
				
				readFile(is, dataOutputFile);
				
			}else{
				throw new IOException("Error code "+response);
			}
			
		}
	}
	
	public void downloadServer(String name, File dataOutputFile, Map<String, String> metadata) throws IOException{
		
		Objects.requireNonNull(name);
		Objects.requireNonNull(dataOutputFile);
		Objects.requireNonNull(metadata);
		
		if(!dataOutputFile.exists()){
			dataOutputFile.createNewFile();
		}
		
		try(Socket client = new Socket(host, port)){
			client.getOutputStream().write(FileTransferProtocol.DOWNLOAD_REQUEST);
			
			writeString(client.getOutputStream(), name);
			
			client.getOutputStream().flush();
			
			int response = client.getInputStream().read();
			
			if(response == FileTransferProtocol.SERVER_RUNNING){
				throw new IOException("Couldn't download server, it is running");
			}else if(response == FileTransferProtocol.UNKNOWN_ERROR){
				throw new IOException("Couldn't download server, unknown error");
			}else if(response == FileTransferProtocol.UNKNOWN_SERVER){
				throw new IOException("Couldn't download server, it is unknown");
			}else if(response == FileTransferProtocol.DOWNLOAD_RESPONSE){
				
				InputStream is = client.getInputStream();
				
				int metadataLength = is.read();
				
				for(int i = 0; i < metadataLength;i++){
					String key = readString(is);
					String value = readString(is);
					
					metadata.put(key, value);
				}
				
				readFile(is, dataOutputFile);
				
			}
			
		}
		
	}
	
	private long readFile(InputStream is, File output) throws IOException{
		long fileSize = readLong(is);
		try(FileOutputStream fos = new FileOutputStream(output)){
			
			byte[] buffer = new byte[FileTransferProtocol.TRANSFER_BUFFER_SIZE];
			
			int read;
			
			while((read = is.read(buffer)) != -1){
				fos.write(buffer, 0, read);
			}
			
			fos.flush();
		}
		
		return fileSize;
	}
	
	private long readLong(InputStream is) throws IOException{
		byte[] buf = new byte[8];
		
		is.read(buf);
		
		return ByteBuffer.wrap(buf).getLong();
	}
	
	private void writeString(OutputStream os, String value) throws IOException{
		byte[] data = value.getBytes(FileTransferProtocol.CHARSET);
		
		if(data.length > 255){
			throw new IllegalArgumentException("data length is greater than maximum: "+data.length+" > 255");
		}
		
		os.write(data.length);
		os.write(data);
	}
	
	private String readString(InputStream is) throws IOException{
		int length = is.read();
		
		byte[] buf = new byte[length];
		
		is.read(buf);
		
		return new String(buf, FileTransferProtocol.CHARSET);
	}
	
}
