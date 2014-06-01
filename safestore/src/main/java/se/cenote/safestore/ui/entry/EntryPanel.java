package se.cenote.safestore.ui.entry;

import java.time.LocalDateTime;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.CalendarUtil;
import se.cenote.safestore.domain.Entry;

public class EntryPanel extends BorderPane{
	
	private enum Mode {VIEW, NEW, EDIT} 
	
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
	
	private Mode mode = Mode.VIEW;
	
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
	
	public boolean isViewMode() {
		return Mode.VIEW.equals(mode);
	}
	
	public void update(Entry entry){
		
		this.entry = entry;
		
		nameLbl.setText("");
		userLbl.setText("");
		pwdLbl.setText("");
		
		commentsFld.setText("");
		createdLbl.setText("");
		editedLbl.setText("");
		
		if(entry != null){
			nameLbl.setText(entry.getName());
			userLbl.setText(entry.getUsername());
			pwdLbl.setText(new String(entry.getPwd()));
			
			commentsFld.setText(entry.getComments());
			createdLbl.setText(format(entry.getCreated()));
			editedLbl.setText(format(entry.getEdited()));
		}
		
		toggleBtns(false);
		mode = Mode.VIEW;
		
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
		String comments = commentsFld.getText();
		
		if(entry == null){
			entry = AppContext.getInstance().getApp().add(name, user, pwd, comments);
			createdLbl.setText(format(this.entry.getCreated()));
			if(lst != null){
				lst.onSave(entry);
			}
		}
		else{
			this.entry.setName(name);
			this.entry.setUsername(user);
			this.entry.setPwd(pwd);
			this.entry.setComments(comments);
			this.entry.setEdited(LocalDateTime.now());
			
			editedLbl.setText(format(this.entry.getEdited()));
		}
		
		toggleMode(false);
	}
	
	
	private void doCancel(){
		update(entry);
		toggleMode(false);
	}
	
	private void toggleMode(boolean enable){
		
		mode = enable ? Mode.EDIT : Mode.VIEW;
		
		if(enable){
			nameFld.setText(nameLbl.getText());
			userFld.setText(userLbl.getText());
			pwdFld.setText(pwdLbl.getText());
			
			commentsFld.setEditable(true);
			
			grid.getChildren().remove(nameLbl);
			grid.add(nameFld, 1, 0);
			
			grid.getChildren().remove(userLbl);
			GridPane.setMargin(userFld, new Insets(20, 0, 0, 0));
			grid.add(userFld, 1, 1);
			
			grid.getChildren().remove(pwdLbl);
			grid.add(pwdFld, 1, 2);
		}
		else{
			nameLbl.setText(nameFld.getText());
			userLbl.setText(userFld.getText());
			pwdLbl.setText(pwdFld.getText());
			
			commentsFld.setEditable(false);
			
			grid.getChildren().remove(nameFld);
			grid.add(nameLbl, 1, 0);
			
			grid.getChildren().remove(userFld);
			GridPane.setMargin(userLbl, new Insets(20, 0, 0, 0));
			grid.add(userLbl, 1, 1);
			
			grid.getChildren().remove(pwdFld);
			grid.add(pwdLbl, 1, 2);
		}
		
		toggleBtns(enable);

		if(enable){
			nameFld.requestFocus();
		}
	}
	
	private void toggleBtns(boolean enable){
		newBtn.setDisable(enable);
		editBtn.setDisable(enable);
		saveBtn.setDisable(!enable);
		cancelBtn.setDisable(!enable);
	}
	
	private static String format(LocalDateTime dateTime){
		return CalendarUtil.formatDateTime(dateTime);
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
		commentsFld.setPrefRowCount(4);
		commentsFld.setEditable(false);
		
		createdLbl = new Label(format(LocalDateTime.now()));
		createdLbl.setFont(Font.font(9));
		editedLbl = new Label("--");
		editedLbl.setFont(Font.font(9));
		
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
		
		
		Label namePrompt = new Label("Namn:");
		grid.add(namePrompt, 0, 0);
		grid.add(nameLbl, 1, 0);
		
		Label userPrompt = new Label("Användare:");
		GridPane.setMargin(userPrompt, new Insets(20, 0, 0, 0));
		GridPane.setMargin(userLbl, new Insets(20, 0, 0, 0));
		grid.add(userPrompt, 0, 1);
		grid.add(userLbl, 1, 1);
		
		grid.add(new Label("Lösenord:"), 0, 2);
		grid.add(pwdLbl, 1, 2);
		
		
		Label notePrompt = new Label("Notering:");
		GridPane.setMargin(notePrompt, new Insets(20, 0, 0, 0));
		
		grid.add(notePrompt, 0, 3);
		grid.add(commentsFld, 0, 4, 2, 1);
		GridPane.setHgrow(commentsFld, Priority.ALWAYS);
		
		
		Label createdPrompt = new Label("Skapad:");
		createdPrompt.setFont(Font.font(10));
		GridPane.setMargin(createdPrompt, new Insets(10, 0, 0, 0));
		GridPane.setMargin(createdLbl, new Insets(10, 0, 0, 0));
		grid.add(createdPrompt, 0, 5);
		grid.add(createdLbl, 1, 5);
		
		Label editedPrompt = new Label("Ändrad:");
		editedPrompt.setFont(Font.font(10));
		grid.add(editedPrompt, 0, 6);
		grid.add(editedLbl, 1, 6);
		
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
