package se.cenote.safestore.domain;

import java.util.List;

public class Settings {
	
	private String path = "c:/home";
	
	private List<String> chiphers;
	
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
