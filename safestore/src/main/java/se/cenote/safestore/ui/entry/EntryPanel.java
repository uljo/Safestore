package se.cenote.safestore.ui.entry;

import java.time.LocalDateTime;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.CalendarUtil;
import se.cenote.safestore.domain.Entry;
import se.cenote.safestore.ui.widget.DotLabel;

public class EntryPanel extends BorderPane{
	
	private enum Mode {VIEW, NEW, EDIT} 
	
	private Label nameLbl;
	private Label userLbl;
	//private Label pwdLbl;
	private DotLabel pwdLbl;
	
	private TextField nameFld;
	private TextField userFld;
	private TextField pwdFld;
	
	//private Button copyBtn;
	//private Node copyIcon;
	
	private TextArea commentsFld;
	private Label createdLbl;
	private Label editedLbl;
	
	private GridPane grid;
	
	private Mode mode = Mode.VIEW;
	
	private boolean showPwd;
	
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
			pwdLbl.setText(showPwd ? new String(entry.getPwd()) : "******");
			
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
			
			String oldName = this.entry.getName();
			
			this.entry.setName(name);
			this.entry.setUsername(user);
			this.entry.setPwd(pwd);
			this.entry.setComments(comments);
			this.entry.setEdited(LocalDateTime.now());
			
			AppContext.getInstance().getApp().update(oldName, this.entry);
			
			editedLbl.setText(format(this.entry.getEdited()));
			
			if(lst != null){
				lst.onSave(entry);
			}
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
			pwdFld.setText(entry != null ? new String(entry.getPwd()) : "");
			
			commentsFld.setEditable(true);
			
			grid.getChildren().remove(nameLbl);
			grid.add(nameFld, 1, 0);
			
			grid.getChildren().remove(userLbl);
			GridPane.setMargin(userFld, new Insets(20, 0, 0, 0));
			grid.add(userFld, 1, 1);
			
			grid.getChildren().remove(pwdLbl);
			grid.add(pwdFld, 1, 2);
			
			lst.onEdit();
		}
		else{
			nameLbl.setText(nameFld.getText());
			userLbl.setText(userFld.getText());
			pwdLbl.setText(showPwd ? pwdFld.getText() : "******");
			
			commentsFld.setEditable(false);
			
			grid.getChildren().remove(nameFld);
			grid.add(nameLbl, 1, 0);
			
			grid.getChildren().remove(userFld);
			GridPane.setMargin(userLbl, new Insets(20, 0, 0, 0));
			grid.add(userLbl, 1, 1);
			
			grid.getChildren().remove(pwdFld);
			grid.add(pwdLbl, 1, 2);
			
			lst.onView();
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
		userLbl.setOnMouseClicked(e -> copyUsername(e));
		
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
		
		//pwdLbl = new FlashLabel();
		pwdLbl = new DotLabel();
		pwdLbl.setOnMouseClicked(e -> copyPwd(e));
		
		pwdFld = new TextField();
		pwdFld.setPrefWidth(60);
		pwdFld.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent event) {
		        if(event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
		            event.consume();
		            commentsFld.requestFocus();
		        }
		    }
		});
		
		
		//copyIcon = FontAwesome.Glyph.COPY.create();
		//copyBtn = new Button("", copyIcon);
		//copyBtn.setOnAction(e -> copyPwd());
		
		commentsFld = new TextArea();
		commentsFld.setPrefRowCount(4);
		commentsFld.setEditable(false);
		
		createdLbl = new Label(format(LocalDateTime.now()));
		createdLbl.setStyle("-fx-font-size: 10pt;");
		
		editedLbl = new Label("--");
		editedLbl.setStyle("-fx-font-size: 10pt;");
		
		
		newBtn = new Button("Ny");
		newBtn.setOnAction(e -> doCreate());
		
		editBtn = new Button("Editera");
		editBtn.setOnAction(e -> doEdit());
		
		saveBtn = new Button("Spara");
		saveBtn.setOnAction(e -> doSave());
		
		cancelBtn = new Button("Avbryt");
		cancelBtn.setOnAction(e -> doCancel());
		
	}

	private void copyUsername(MouseEvent e) {
		if(e.getButton() == MouseButton.SECONDARY){
			
			String text = entry.getUsername();
			Label lbl = userLbl;
			
			
			final Animation animation = new Transition() {
		        {
		            setCycleDuration(Duration.millis(500));
		        }
		    
		        protected void interpolate(double frac) {
		            final int length = text.length();
		            final int n = Math.round(length * (float) frac);
		            lbl.setText(text.substring(0, n));
		        }
		    };
		    animation.play();
		    
			copyText(text);
		}
	}
	
	private void copyPwd(MouseEvent e) {
		if(e.getButton() == MouseButton.SECONDARY){
			
			pwdLbl.play();
			
			String text = entry != null ? new String(entry.getPwd()) : "";
			copyText(text);
		}
	}
	

	/**
	 * Copy specified text to clipboard.
	 * 
	 * @param text specified text tpo be copied.
	 */
	private void copyText(String text) {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		
		content.putString(text);
		clipboard.setContent(content);
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
		//grid.add(copyBtn, 2, 2);
		
		
		Label notePrompt = new Label("Notering:");
		GridPane.setMargin(notePrompt, new Insets(20, 0, 0, 0));
		
		grid.add(notePrompt, 0, 3);
		grid.add(commentsFld, 0, 4, 2, 1);
		GridPane.setHgrow(commentsFld, Priority.ALWAYS);
		
		
		Label createdPrompt = new Label("Skapad:");
		createdPrompt.setStyle("-fx-font-size: 10pt;");
		//createdPrompt.setFont(Font.font(10));
		GridPane.setMargin(createdPrompt, new Insets(10, 0, 0, 0));
		GridPane.setMargin(createdLbl, new Insets(10, 0, 0, 0));
		grid.add(createdPrompt, 0, 5);
		grid.add(createdLbl, 1, 5);
		
		Label editedPrompt = new Label("Ändrad:");
		editedPrompt.setStyle("-fx-font-size: 10pt;");
		//editedPrompt.setFont(Font.font(10));
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
		public void onEdit();
		public void onView();
	}


}
