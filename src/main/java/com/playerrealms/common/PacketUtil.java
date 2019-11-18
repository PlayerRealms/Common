package com.playerrealms.common;

import io.netty.buffer.ByteBuf;

public class PacketUtil {

	public static final byte[] PASSWORD = new byte[] {
			(byte)213,(byte)97,(byte)132,(byte)208,(byte)11,(byte)112,(byte)144,(byte)208,(byte)246,(byte)93,(byte)99,(byte)250,(byte)161,(byte)2,(byte)43,(byte)171,
			(byte)51,(byte)180,(byte)169,(byte)36,(byte)204,(byte)58,(byte)237,(byte)175,(byte)216,(byte)176,(byte)131,(byte)126,(byte)108,(byte)164,(byte)27,(byte)140,
			(byte)15,(byte)68,(byte)109,(byte)236,(byte)170,(byte)84,(byte)251,(byte)229,(byte)25,(byte)8,(byte)49,(byte)138,(byte)81,(byte)228,(byte)243,(byte)234,
			(byte)47,(byte)52,(byte)252,(byte)235,(byte)239,(byte)213,(byte)52,(byte)130,(byte)166,(byte)186,(byte)199,(byte)254,(byte)168,(byte)106,(byte)245,(byte)176,
			(byte)12,(byte)210,(byte)134,(byte)199,(byte)63,(byte)173,(byte)134,(byte)37,(byte)229,(byte)196,(byte)48,(byte)2,(byte)219,(byte)111,(byte)54,(byte)70,
			(byte)40,(byte)68,(byte)244,(byte)150,(byte)118,(byte)253,(byte)215,(byte)177,(byte)66,(byte)74,(byte)39,(byte)78,(byte)68,(byte)63,(byte)239,(byte)188,
			(byte)89,(byte)224,(byte)230,(byte)16
	};
	
	private PacketUtil(){}
	
	public static int calculateStringLength(String str) {
		byte[] data = str.getBytes();
		
		return data.length + 4;
	}
	
	public static void writeString(String str, ByteBuf buf){
		byte[] data = str.getBytes();
		
		if(data.length > 1024){
			throw new IllegalArgumentException("String length exceeds 1024! "+str);
		}
		
		buf.writeInt(data.length);
		buf.writeBytes(data);
	}
	
	public static String readString(ByteBuf buf){
		int length = buf.readInt();
		
		if(length > 1024){
			throw new IllegalArgumentException("Length cannot be greater than 1024! Length: "+length);
		}
		
		byte[] data = new byte[length];
		
		buf.readBytes(data);
		
		return new String(data);
	}
	
}
