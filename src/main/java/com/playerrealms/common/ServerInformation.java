package com.playerrealms.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ServerInformation implements Comparable<ServerInformation> {

	public static final int DEFAULT_MAX_PLAYERS = 15;

	public static final int ULTRA_MAX_PLAYERS = 50;
	
	public static final int PREMIUM_MAX_PLAYERS = 25;
	
	private String ip;
	private int port;
	private String name;
	private int playersOnline;
	private Map<String, String> metadata;
	private ServerStatus status;
	
	public ServerInformation(String ip, int port, String name, int playersOnline, ServerStatus status, Map<String, String> metadata) {
		this.ip = ip;
		this.port = port;
		this.name = name;
		this.playersOnline = playersOnline;
		this.status = status;
		this.metadata = metadata;
	}
	
	public boolean isWhitelistEnabled(){
		return Boolean.parseBoolean(metadata.getOrDefault("whitelist", "false"));
	}
	
	public long getStartTime(){
		if(!metadata.containsKey("st")){
			return 0;
		}
		return Long.parseLong(metadata.get("st"));
	}
	
	public boolean isOfficial(){
		return metadata.getOrDefault("official", "false").equals("true");
	}
	
	public boolean hasServerIcon(){
		return metadata.containsKey("icon");
	}
	
	public String getServerIcon(){
		if(hasServerIcon()){
			return metadata.get("icon");
		}
		
		return null;
	}
	
	public double getScore() {
		
		double score = 0D;
		
		if(hasMotd()) {
			score += 5;
		}
		
		if(System.currentTimeMillis() - getStartTime() < TimeUnit.MINUTES.toMillis(15)) {
			score += 1.5D;
		}
		
		score += getPlayersOnline();
	
		//Votes are worth more if you have more players
		double votes = getVotes();
		
		votes /= 10D;
		
		score += votes;
		
		if(isWhitelistEnabled()) {
			score = 0D;
		}
		
		
		return score;
		
	}
	
	public boolean isUltraPremium(){
		if(!isPremium()){
			return false;
		}
		if(metadata.containsKey("ultra_time")) {
			
			long time = Long.parseLong(metadata.get("ultra_time"));
			
			if(time - System.currentTimeMillis() > 0) {
				return true;
			}
			
		}
		return metadata.getOrDefault("ultra", "n").equals("y");
	}

	public boolean isBeta(){
		return Boolean.parseBoolean(metadata.getOrDefault("beta", "false"));
	}

	public boolean isMundo() { return Boolean.parseBoolean(metadata.getOrDefault("mundo", "false")); }

	public boolean isBan() { return Boolean.parseBoolean(metadata.getOrDefault("ban","false")); }

	public String getBanReason() {
		if(!isBan()){
			return "";
		}
		return metadata.getOrDefault("ban_reason", "No Reason");
	}

	public boolean isThirdParty(){
		return metadata.getOrDefault("thirdparty", "n").equals("y");
	}
	
	public String getThirdPartyCode(){
		if(!isThirdParty()){
			return "";
		}
		return metadata.get("code");
	}
	
	public long getThirdPartyTimeLeft(){
		if(!metadata.containsKey("tptime")){
			return 0;
		}
		
		long time = Long.parseLong(metadata.get("tptime"));
		
		return Math.max(time - System.currentTimeMillis(), 0);
	}
	
	public boolean isClosedForDevelopment(){
		return Boolean.parseBoolean(metadata.getOrDefault("indev", "false"));
	}
	
	public String getLanguage(){
		return metadata.getOrDefault("lang", "en_us");
	}
	
	public int getEarnings(){
		if(!metadata.containsKey("earnings")){
			return 0;
		}
		
		return Integer.parseInt(metadata.get("earnings"));
	}
	
	public double getCoinMultiplier(){
		if(!metadata.containsKey("multi")){
			return 0D;//No multiplier
		}
		
		if(getCoinMultiplierTimeLeft() == 0L){
			return 0D;
		}
		
		double d = Double.parseDouble(metadata.get("multi"));
		
		return d;
	}
	
	public long getCoinMultiplierTimeLeft() {
		if(!metadata.containsKey("multitime")){
			return 0;
		}
		
		long time = Long.parseLong(metadata.get("multitime"));
		
		return Math.max(time - System.currentTimeMillis(), 0);
	}
	
	public boolean isPremium(){
		if(isThirdParty()){
			return false;
		}
		if(!metadata.containsKey("premiumtime")){
			return false;
		}
		
		long time = Long.parseLong(metadata.get("premiumtime"));
		
		return time - System.currentTimeMillis() > 0;
	}
	
	public long getPremiumLeft() {
		if(!isPremium()){
			return 0;
		}
		long time = Long.parseLong(metadata.get("premiumtime"));
		
		time = time - System.currentTimeMillis();
		
		if(time < 0){
			return 0;
		}
		
		return time;
	}
	
	public ServerStatus getStatus() {
		if(isThirdParty()){
			if(metadata.getOrDefault("tponline", "n").equals("y")){
				return ServerStatus.ONLINE;
			}else{
				return ServerStatus.OFFLINE;
			}
		}
		return status;
	}
	
	public int getVotes(){
		if(!metadata.containsKey("votes")){
			return 0;
		}
		return Integer.parseInt(metadata.get("votes"));
	}
	
	@Override
	public String toString() {
		if(status == ServerStatus.ONLINE){
			return name+">>> "+ip+":"+port+" >> online players "+playersOnline;
		}else if(status == ServerStatus.STARTING){
			return name+">>> "+ip+":"+port+" >> STARTING";
		}else{
			return name+">>> "+ip+":"+port+" >> OFFLINE";
		}

	}
	
	public boolean hasOwner(){
		return getOwner() != null;
	}
	
	public UUID getOwner(){
		if(metadata.containsKey("owner")){
			return UUID.fromString(metadata.get("owner"));
		}
		return null;
	}

	public boolean hasMotd(){
		return getMotd() != null;
	}
	
	public int getMaxPlayers(){
		if(metadata.containsKey("maxplayers")){
			return Integer.parseInt(metadata.get("maxplayers"));
		}
		
		if(isUltraPremium()) {
			return ULTRA_MAX_PLAYERS;
		}
		
		if(isPremium()){
			return PREMIUM_MAX_PLAYERS;
		}
		
		return DEFAULT_MAX_PLAYERS;
	}
	
	public String getMotd(){
		return metadata.get("motd");
	}
	
	public ServerType getServerType(){
		String type = metadata.getOrDefault("type", "unknown");
		
		if(type.contains("hub")){
			return ServerType.HUB;
		}else if(type.equalsIgnoreCase("player")){
			return ServerType.PLAYER;
		}else if(type.contains("game")){
			return ServerType.OFFICIAL_GAME;
		}
		
		return ServerType.UNKNOWN;
	}
	
	public String getIp() {
		if(isThirdParty()){
			return metadata.getOrDefault("tpip", ip);
		}
		return ip;
	}

	public int getPort() {
		if(isThirdParty()){
			return Integer.parseInt(metadata.getOrDefault("tpport", String.valueOf(port)));
		}
		return port;
	}

	public String getName() {
		return name;
	}
	
	public int getTaxedMoney(){
		if(!metadata.containsKey("taxed")){
			return 0;
		}
		return Integer.valueOf(metadata.get("taxed"));
	}

	public int getPlayersOnline() {
		return playersOnline;
	}

	public ByteBuf encode(){
		
		ByteBuf buf = Unpooled.buffer();
		PacketUtil.writeString(ip, buf);
		buf.writeChar(port);
		PacketUtil.writeString(name, buf);
		buf.writeInt(playersOnline);
		buf.writeByte(status.ordinal());
		
		buf.writeByte(metadata.size());
		for(String key : metadata.keySet()){
			String value = metadata.get(key);
			
			PacketUtil.writeString(key, buf);
			PacketUtil.writeString(value, buf);
		}
		
		return buf.capacity(buf.writerIndex());
	}
	
	public static ServerInformation decode(ByteBuf buf){
		
		String ip = PacketUtil.readString(buf);
		int port = buf.readUnsignedShort();
		String name = PacketUtil.readString(buf);
		int onlinePlayers = buf.readInt();
		byte status = buf.readByte();
		
		byte dataAmount = buf.readByte();
		
		Map<String, String> meta = new HashMap<String, String>();
		
		for(int i = 0; i < dataAmount;i++){
			String key = PacketUtil.readString(buf);
			String value = PacketUtil.readString(buf);
			
			meta.put(key, value);
		}
		
		return new ServerInformation(ip, port, name, onlinePlayers, ServerStatus.values()[status], meta);
		
	}

	public int compareTo(ServerInformation o) {
		if(o.playersOnline > playersOnline){
			return 1;
		}else if(o.playersOnline < playersOnline){
			return -1;
		}
		return 0;
	}

	public int getTPS() {
		if(metadata.containsKey("tps")){
			return Integer.parseInt(metadata.get("tps"));
		}
		return 0;
	}
	
	public UUID getUUID() {
		return UUID.fromString(metadata.get("uuid"));
	}

	public Map<String, String> getMetadata() {
		return new HashMap<String, String>(metadata);
	}

	public boolean areCommandBlocksEnabled() {
		return metadata.getOrDefault("cb", "on").equals("on");
	}
	
	public boolean areFlightEnabled() {
		return metadata.getOrDefault("af", "on").equals("on");
	}

	public String getResourcePack() {
		if(metadata.containsKey("rp")) {
			return metadata.get("rp");
		}
		return null;
	}
	
	public static boolean validateName(String name) {
		if(name.length() < 3){
			return false;
		}
		if(name.length() > 20){
			return false;
		}
		for(char c : name.toCharArray()){
			if(!Character.isLetter(c) && !Character.isDigit(c) && c != '-'){
				return false;
			}
		}
		
		return true;
	}


}
