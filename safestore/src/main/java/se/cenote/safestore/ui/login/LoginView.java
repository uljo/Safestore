package se.cenote.safestore.ui.login;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

import org.controlsfx.dialog.Dialogs;

import se.cenote.safestore.AppContext;
import se.cenote.safestore.ui.view.BaseView;
import se.cenote.safestore.ui.view.ViewManager;

public class LoginView extends BaseView{

	private PasswordField passwordFld;
	private Button loginBtn;
	
	private Label firstTimeLbl;
	
	private static final String FIRST_TIME_TEXT = "Välkommen!\nFörsta gången måste du ange\nett huvud-lösenord som du använder\nvarje gång du loggar in i applikationen.";
	
	public LoginView(ViewManager viewMgr) {
		super(viewMgr);
		
		initComponents();
		layoutComponents();
	}
	
	
	
	@Override
	public void onShow() {
		passwordFld.requestFocus();
	}



	private void login(){
		try{
			AppContext.getInstance().getApp().login(passwordFld.getText().toCharArray());
			
			passwordFld.clear();
			
			getViewManger().showEntryView();
		}
		catch(Exception e){
			
			//passwordFld.setTextFill(Color.rgb(210, 39, 30));
			
			Dialogs.create()
	        .owner(getScene().getWindow())
	        .title("Error Dialog")
	        .message("Inloggning godkändes ej!")
	        .showError();
		}
	}

	private void initComponents() {
		
		firstTimeLbl = new Label();
		firstTimeLbl.setWrapText(true);
		
		passwordFld = new PasswordField();
		passwordFld.setOnAction(e -> login());
		
		loginBtn = new Button("Logga in");
		loginBtn.setOnAction(e -> login());
	}

	private void layoutComponents() {
		
		FlowPane textPane = new FlowPane();
		textPane.setPadding(new Insets(20));
		textPane.setAlignment(Pos.CENTER);
		textPane.getChildren().add(firstTimeLbl);
		
		if(AppContext.getInstance().getApp().isFirstTime()){
			firstTimeLbl.setText(FIRST_TIME_TEXT);
		}
		else{
			firstTimeLbl.setText("Välkommen tillbaka!");
		}
		
		setTop(textPane);
		
		
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(20));
		grid.setHgap(5);
		grid.setVgap(20);
		
		grid.setAlignment(Pos.CENTER);
		
		grid.add(new Label("Lösenord:"), 0, 0);
		grid.add(passwordFld, 1, 0);
		
		grid.add(loginBtn, 0, 1, 2, 1);
		
		setCenter(grid);
	}

}
