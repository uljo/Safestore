package se.cenote.safestore.ui.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.Settings;
import se.cenote.safestore.ui.view.BaseView;
import se.cenote.safestore.ui.view.ViewManager;

public class SettingView extends BaseView{
	
	private Settings settings;
	
	private TextField fileFld;
	private ComboBox<String> cipherCmb;
	
	public SettingView(ViewManager viewMgr){
		super(viewMgr);
		initComponents();
		layoutComponents();
	}
	
	@Override
	public void onShow(){
		Settings settings = AppContext.getInstance().getApp().getSettings();
		update(settings);
	}
	
	public void update(Settings settings){
		this.settings = settings;
		
		fileFld.setText(settings.getPath());
		
		cipherCmb.getItems().clear();
		for(String crypto : settings.getCryptos()){
			cipherCmb.getItems().add(crypto);
		}
		cipherCmb.getSelectionModel().select(settings.getSeletedCrypto());
	}

	private void initComponents() {
		
		fileFld = new TextField();
		
		cipherCmb = new ComboBox<String>();
		ObservableList<String> observableList = FXCollections.observableArrayList();
		cipherCmb.setItems(observableList);
	}

	private void layoutComponents() {
		
		setPadding(new Insets(10));
		
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(15);
		grid.add(new Label("Fil:"), 0, 0);
		grid.add(fileFld, 1, 0);
		
		grid.add(new Label("Krypto:"), 0, 1);
		grid.add(cipherCmb, 1, 1);
		
		setCenter(grid);
	}
}
