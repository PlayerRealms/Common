package com.playerrealms.common;

public enum WorldGeneratorType {

	NORMAL("n"),
	FLAT("f"),
	VOID("v"),
	UPLOAD("u");
	
	private final String id;
	
	private WorldGeneratorType(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public static WorldGeneratorType getType(String id){
		for(WorldGeneratorType type : values()){
			if(type.id.equals(id)){
				return type;
			}
		}
		return null;
	}
	
}
