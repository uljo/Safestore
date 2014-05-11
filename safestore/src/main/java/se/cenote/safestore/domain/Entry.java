package se.cenote.safestore.domain;

import java.io.Serializable;

public class Entry implements Serializable{
	
	private static final long serialVersionUID = 1L;
		private String name;
		private String username;
		private byte[] pwd;
		
		public Entry(String name, String username, byte[] pwd) {
			this.name = name;
			this.username = username;
			this.pwd = pwd;
		}
		
		public String getName() {
			return name;
		}
		
		public String getUsername() {
			return username;
		}
		
		public byte[] getPwd() {
			return pwd;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public void setPwd(byte[] pwd) {
			this.pwd = pwd;
		}

		
}
