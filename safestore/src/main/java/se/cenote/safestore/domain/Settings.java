package se.cenote.safestore.domain;

import java.util.List;

public class Settings {
	
	private String path = "c:/home";
	
	private List<String> cryptos;
	private String selectedCrypto;
	
	private int keyLength;
	
	public Settings(List<String> cryptos, int keyLength){
		path = System.getProperty("user.home");
		
		this.cryptos = cryptos;
		
		this.keyLength = keyLength;
	}
	
	public String getPath(){
		return path;
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public int getKeyLength(){
		return keyLength;
	}
	
	public void setKeyLength(int keyLength){
		this.keyLength = keyLength;
	}

	public List<String> getCryptos() {
		return cryptos;
	}
	
	public String getSeletedCrypto(){
		return selectedCrypto;
	}

	public void setSeletedCrypto(String crypto) {
		this.selectedCrypto = crypto;
	}

}
