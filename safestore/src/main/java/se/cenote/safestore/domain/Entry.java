package se.cenote.safestore.domain;

import java.io.Serializable;
import java.time.LocalTime;

public class Entry implements Serializable{
	
	private static final long serialVersionUID = 1L;
		private String name;
		private String username;
		private byte[] pwd;
		
		private String comments;
		
		private LocalTime created;
		private LocalTime edited;
		
		public Entry(String name, String username, byte[] pwd){
			this(name, username, pwd, null);
		}
		
		public Entry(String name, String username, byte[] pwd, String comments) {
			this.name = name;
			this.username = username;
			this.pwd = pwd;
			this.comments = comments;
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

		public String getComments() {
			return comments;
		}

		public void setComments(String comments) {
			this.comments = comments;
		}

		public LocalTime getCreated() {
			return created;
		}

		public void setCreated(LocalTime created) {
			this.created = created;
		}

		public LocalTime getEdited() {
			return edited;
		}

		public void setEdited(LocalTime edited) {
			this.edited = edited;
		}

		
}
