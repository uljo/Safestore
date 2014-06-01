package se.cenote.safestore.ui.login;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

import org.controlsfx.dialog.Dialogs;

import se.cenote.safestore.AppContext;
import se.cenote.safestore.ui.entry.EntryView;
import se.cenote.safestore.ui.view.BaseView;
import se.cenote.safestore.ui.view.ViewManager;

public class LoginView extends BaseView{

	private PasswordField passwordFld;
	private Button loginBtn;
	
	public LoginView(ViewManager viewMgr) {
		super(viewMgr);
		initComponents();
		layoutComponents();
	}
	
	private void login(){
		try{
			AppContext.getInstance().getApp().login(passwordFld.getText().toCharArray());
			
			getViewManger().show(EntryView.class.getName());
		}
		catch(Exception e){
			Dialogs.create()
	        .owner(null)
	        .title("Error Dialog")
	        .message("Inloggning godkändes ej!")
	        .showError();
		}
	}

	private void initComponents() {
		passwordFld = new PasswordField();
		loginBtn = new Button("Logga in");
		loginBtn.setOnAction(e -> login());
	}

	private void layoutComponents() {
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
