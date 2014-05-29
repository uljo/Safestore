package se.cenote.safestore.ui.entry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.Entry;

public class EntryPanel extends BorderPane{
	
	private enum EditMode {VIEW, NEW, EDIT} 
	
	private Label nameLbl;
	private Label userLbl;
	private Label pwdLbl;
	
	private TextField nameFld;
	private TextField userFld;
	private TextField pwdFld;
	
	private TextArea commentsFld;
	private Label createdLbl;
	private Label editedLbl;
	
	private GridPane grid;
	
	private EditMode editMode;
	
	private Button newBtn;
	private Button editBtn;
	private Button saveBtn;
	private Button cancelBtn;
	
	private EntryListener lst;
	private Entry entry;
	
	private Button settingsBtn;
	
	public EntryPanel(EntryListener lst){
		this.lst = lst;
		
		initComponents();
		layoutComponents();
	}
	
	public void update(Entry entry){
		
		this.entry = entry;
		
		nameLbl.setText("");
		userLbl.setText("");
		pwdLbl.setText("");
		
		if(entry != null){
			nameLbl.setText(entry.getName());
			userLbl.setText(entry.getUsername());
			pwdLbl.setText(new String(entry.getPwd()));
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
		
		if(enable){
			nameFld.setText(nameLbl.getText());
			userFld.setText(userLbl.getText());
			pwdFld.setText(pwdLbl.getText());
			
			grid.getChildren().remove(nameLbl);
			grid.add(nameFld, 1, 0);
			grid.getChildren().remove(userLbl);
			grid.add(userFld, 1, 1);
			grid.getChildren().remove(pwdLbl);
			grid.add(pwdFld, 1, 2);
		}
		else{
			nameLbl.setText(nameFld.getText());
			userLbl.setText(userFld.getText());
			pwdLbl.setText(pwdFld.getText());
			
			grid.getChildren().remove(nameFld);
			grid.add(nameLbl, 1, 0);
			grid.getChildren().remove(userFld);
			grid.add(userLbl, 1, 1);
			grid.getChildren().remove(pwdFld);
			grid.add(pwdLbl, 1, 2);
		}
		
		newBtn.setDisable(enable);
		editBtn.setDisable(enable);
		saveBtn.setDisable(!enable);
		cancelBtn.setDisable(!enable);

		if(enable){
			nameFld.requestFocus();
		}
	}

	private void initComponents() {
		nameLbl = new Label();
		nameFld = new TextField();
		nameFld.setPrefWidth(60);
		
		userLbl = new Label();
		userFld = new TextField();
		userFld.setPrefWidth(60);
		
		/*
		TextFields.bindAutoCompletion(userFld, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>>() {
			@Override
			public Collection<String> call(ISuggestionRequest param) {
				if(!param.isCancelled())
					return AppContext.getInstance().getApp().getSuggestions(param.getUserText());
				else
					return null;
			}
		});
		*/
		
		pwdLbl = new Label();
		pwdFld = new TextField();
		pwdFld.setPrefWidth(60);
		
		commentsFld = new TextArea();
		commentsFld.setMaxWidth(200);
		
		createdLbl = new Label(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
		editedLbl = new Label("--");
		
		newBtn = new Button("Ny");
		newBtn.setOnAction(e -> doCreate());
		
		editBtn = new Button("Editera");
		editBtn.setOnAction(e -> doEdit());
		
		saveBtn = new Button("Spara");
		saveBtn.setOnAction(e -> doSave());
		
		cancelBtn = new Button("Avbryt");
		cancelBtn.setOnAction(e -> doCancel());
		
	}

	private void layoutComponents() {
		
		setPrefWidth(260);
		
		grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setHgap(4);
		grid.setVgap(4);
		grid.add(new Label("Namn:"), 0, 0);
		grid.add(nameLbl, 1, 0);
		
		grid.add(new Label("Användare:"), 0, 1);
		grid.add(userLbl, 1, 1);
		
		grid.add(new Label("Lösenord:"), 0, 2);
		grid.add(pwdLbl, 1, 2);
		
		grid.add(new Label("Notering:"), 0, 3);
		grid.add(commentsFld, 0, 4, 2, 1);
		
		
		FlowPane flowPane = new FlowPane();
		flowPane.setHgap(5);
		flowPane.getChildren().addAll(new Label("Skapad:"), createdLbl, new Label("Ändrad:"), editedLbl);
		
		grid.add(flowPane, 0, 5, 2, 1);
		
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
