package se.cenote.safestore.domain;

import java.util.ArrayList;
import java.util.List;

public class Settings {
	
	private String path = "c:/home";
	
	private List<String> chiphers;
	
	public Settings(){
		path = System.getProperty("user.home");
		
		chiphers = new ArrayList<String>();
		chiphers.add("PBKDF2WithHmacSHA1");
		chiphers.add("3DES");
	}
	
	public String getPath(){
		return path;
	}
	
	public void setPath(String path){
		this.path = path;
	}

	public List<String> getChiphers() {
		return chiphers;
	}

	public void setChiphers(List<String> chiphers) {
		this.chiphers = chiphers;
	}

}
