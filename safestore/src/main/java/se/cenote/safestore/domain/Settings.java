package se.cenote.safestore.domain;

import java.util.List;

public class Settings {
	
	private String path = "c:/home";
	
	private List<String> cryptos;
	private String selectedCrypto;
	
	public Settings(List<String> cryptos){
		path = System.getProperty("user.home");
		
		this.cryptos = cryptos;
	}
	
	public String getPath(){
		return path;
	}
	
	public void setPath(String path){
		this.path = path;
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
