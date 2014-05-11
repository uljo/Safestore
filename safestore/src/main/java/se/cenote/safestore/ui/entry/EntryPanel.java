package se.cenote.safestore.ui.entry;

import java.util.Collection;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.TextFields;

import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.Entry;

public class EntryPanel extends BorderPane{
	
	private enum EditMode {VIEW, NEW, EDIT} 
	
	private TextField nameFld;
	private TextField userFld;
	private TextField pwdFld;
	
	private EditMode editMode;
	
	private Button newBtn;
	private Button editBtn;
	private Button saveBtn;
	private Button cancelBtn;
	
	private EntryListener lst;
	private Entry entry;
	
	public EntryPanel(EntryListener lst){
		this.lst = lst;
		
		initComponents();
		layoutComponents();
	}
	
	public void update(Entry entry){
		
		this.entry = entry;
		
		nameFld.setText("");
		userFld.setText("");
		pwdFld.setText("");
		
		if(entry != null){
			nameFld.setText(entry.getName());
			userFld.setText(entry.getUsername());
			pwdFld.setText(new String(entry.getPwd()));
		}
	}
	
	private void doCreate(){
		update(null);
		
		toggleMode(true);
	}
	
	private void doEdit(){
		toggleMode(true);
	}
	
	private void doSave(){
		
		String name = nameFld.getText();
		String user = userFld.getText();
		byte[] pwd = pwdFld.getText().getBytes();
		
		if(entry == null){
			entry = AppContext.getInstance().getApp().add(name, user, pwd);
			if(lst != null){
				lst.onSave(entry);
			}
		}
		else{
			this.entry.setName(name);
			this.entry.setUsername(user);
			this.entry.setPwd(pwd);
		}
		
		toggleMode(false);
	}
	
	
	private void doCancel(){
		update(entry);
		toggleMode(false);
	}
	
	private void toggleMode(boolean enable){
		
		nameFld.setEditable(enable);
		nameFld.setFocusTraversable(enable);
		
		userFld.setEditable(enable);
		userFld.setFocusTraversable(enable);
		
		pwdFld.setEditable(enable);
		pwdFld.setFocusTraversable(enable);
		
		newBtn.setDisable(enable);
		editBtn.setDisable(enable);
		saveBtn.setDisable(!enable);
		cancelBtn.setDisable(!enable);

		if(enable){
			nameFld.requestFocus();
		}
	}

	private void initComponents() {
		nameFld = new TextField();
		
		userFld = new TextField();
		
		TextFields.bindAutoCompletion(userFld, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>>() {
			@Override
			public Collection<String> call(ISuggestionRequest param) {
				if(!param.isCancelled())
					return AppContext.getInstance().getApp().getSuggestions(param.getUserText());
				else
					return null;
			}
		});
		
		
		pwdFld = new TextField();
		
		
		newBtn = new Button("Ny");
		newBtn.setOnAction(e -> doCreate());
		
		editBtn = new Button("Editera");
		editBtn.setOnAction(e -> doEdit());
		
		saveBtn = new Button("Spara");
		saveBtn.setOnAction(e -> doSave());
		
		cancelBtn = new Button("Avbryt");
		cancelBtn.setOnAction(e -> doCancel());
		
		toggleMode(false);
	}

	private void layoutComponents() {
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(4);
		grid.setVgap(4);
		grid.add(new Label("Namn:"), 0, 0);
		grid.add(nameFld, 1, 0);
		
		grid.add(new Label("Användare:"), 0, 1);
		grid.add(userFld, 1, 1);
		
		grid.add(new Label("Lösenord:"), 0, 2);
		grid.add(pwdFld, 1, 2);
		
		FlowPane btnPanel = new FlowPane();
		btnPanel.setPadding(new Insets(5));
		btnPanel.setHgap(10);
		//btnPanel.setAlignment(Pos.CENTER);
		btnPanel.getChildren().addAll(newBtn, editBtn, saveBtn, cancelBtn);
		
		setCenter(grid);
		setBottom(btnPanel);
	}
	
	public interface EntryListener{
		public void onSave(Entry entry);
	}

}
