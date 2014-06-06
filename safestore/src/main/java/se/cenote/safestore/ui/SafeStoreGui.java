package se.cenote.safestore.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se.cenote.safestore.AppContext;
import se.cenote.safestore.ui.entry.EntryView;
import se.cenote.safestore.ui.login.LoginView;
import se.cenote.safestore.ui.settings.SettingView;
import se.cenote.safestore.ui.view.ViewManager;

public class SafeStoreGui extends Application{
	
	private static final String TITLE = "SafeStore";
	private double width = 380;
	private double height = 500;
	
	private ViewManager viewMgr;
	
	private LoginView loginView;
	private EntryView entryView;
	private SettingView settingView;
	
	public SafeStoreGui(){
		viewMgr = new ViewManager();
		
		settingView = new SettingView(viewMgr);
		viewMgr.add(settingView);
		
		entryView = new EntryView(viewMgr);
		viewMgr.add(entryView);
		
		loginView = new LoginView(viewMgr);
		viewMgr.add(loginView);
		
		viewMgr.show(LoginView.class.getName());
	}

	public void start(Stage stage) throws Exception {
		
		//Group group = new Group();
		//group.getChildren().add(viewMgr);
		
		Scene scene = new Scene(viewMgr, width, height);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		stage.setScene(scene);
		stage.setTitle(TITLE);
		stage.show();
	}
	
	@Override
	public void stop() throws Exception {
		AppContext.getInstance().getApp().close();
	}

	public static void show() {
        launch((String[])null);
    }
}
