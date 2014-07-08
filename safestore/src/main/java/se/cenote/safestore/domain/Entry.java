package se.cenote.safestore.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Entry implements Serializable{
	
	private static final long serialVersionUID = 1L;
		private String name;
		private String username;
		private byte[] pwd;
		
		private String comments;
		
		private LocalDateTime created;
		private LocalDateTime edited;
		
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

		public LocalDateTime getCreated() {
			return created;
		}

		public void setCreated(LocalDateTime created) {
			this.created = created;
		}

		public LocalDateTime getEdited() {
			return edited;
		}

		public void setEdited(LocalDateTime edited) {
			this.edited = edited;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < pwd.length; i++){
				builder.append("*");
			}
			String pass = builder.toString();
			return "Entry(name=" + name + ", username=" + username + ", pwd="
					+ pass/*Arrays.toString(pwd)*/ + ", comments=" + comments
					+ ", created=" + CalendarUtil.formatDateTime(created) + ", edited=" + CalendarUtil.formatDateTime(edited) + ")";
		}

		
}
