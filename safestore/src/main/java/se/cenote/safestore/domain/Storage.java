package se.cenote.safestore.domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.cenote.safestore.domain.crypto.CryptoManager;

public class Storage {
	
	private File getFile(){
		File file = new File(System.getProperty("user.home"), ".safeStore");
		return file;
	}
	
	public void store(List<Entry> entries){
		
		File file = new File(System.getProperty("user.home"), ".safeStore");
		
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(file));
			for(Entry entry : entries){
				writer.write(entry.getName() + ", ");
				writer.write(entry.getUsername() + ", ");
				
				//String base64 = asBase64(entry.getPwd());
				String base64 = CryptoManager.encrypt(entry.getPwd());
				
				writer.write(base64 + "\n");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				writer.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<Entry> load(){
		List<Entry> list = new ArrayList<Entry>();
		
		File file = getFile();
		if(file.exists()){
			
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new FileReader(file));
				String row = null;
				while((row = reader.readLine()) != null){
					String[] parts = row.split(",");
					String name = parts[0].trim();
					String user = parts[1].trim();
					String data = parts[2].trim();
					
					//byte[] pwd = asBytes(data);
					byte[] pwd = CryptoManager.decrypt(data);
					
					Entry entry = new Entry(name, user, pwd);
					list.add(entry);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				try {
					if(reader != null)
						reader.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}

}
