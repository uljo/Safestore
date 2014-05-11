package se.cenote.safestore.ui;

import se.cenote.safestore.AppContext;
import se.cenote.safestore.ui.entry.EntryView;
import se.cenote.safestore.ui.settings.SettingView;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SafeStoreGui extends Application{
	
	private static final String TITLE = "SafeStore";
	private double width = 400;
	private double height = 400;

	public void start(Stage stage) throws Exception {
		
		EntryView entryView = new EntryView();
		
		SettingView settingView = new SettingView();
		
		Group group = new Group();
		group.getChildren().add(entryView);
		//group.getChildren().add(settingView);
		
		Scene scene = new Scene(group, width, height);
		
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
