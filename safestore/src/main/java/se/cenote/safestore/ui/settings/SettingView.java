package se.cenote.safestore.ui.settings;

import org.controlsfx.dialog.Dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.Settings;
import se.cenote.safestore.domain.crypto.CryptoManager;
import se.cenote.safestore.ui.view.BaseView;
import se.cenote.safestore.ui.view.ViewManager;

public class SettingView extends BaseView{
	
	
	private TextField fileFld;
	private ComboBox<String> cipherCmb;
	private ComboBox<Integer> keyLengthCmb;
	
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
		
		fileFld.setText(settings.getPath());
		
		cipherCmb.getSelectionModel().select(settings.getSeletedCrypto());
		
		keyLengthCmb.getSelectionModel().select(settings.getKeyLength());
	}
	
	private void updateKeyLengthCmb(){
		String crypto = cipherCmb.getSelectionModel().getSelectedItem();
		if(crypto.endsWith("DES")){
			keyLengthCmb.setDisable(true);
		}
		else{
			keyLengthCmb.setDisable(false);
		}
	}
	
	private void showKeyLengthWarning(){
		
		Integer keyLength = keyLengthCmb.getSelectionModel().getSelectedItem();
		String msg = null;
		if(keyLength <= 128){
			msg = "En nyckellängd på 128 är mindre säker än 196 eller 256 men kan användas utan Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files.";
		}
		else{
			msg = "Nyckellängder över 128 kräver att JRE uppdaterats med Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files.";
		}
		
		Dialogs.create()
        .owner(null)
        .title("Varning")
        .message(msg)
        .showWarning();
	}

	private void initComponents() {
		
		fileFld = new TextField();
		
		
		CryptoManager cryptoMgr = AppContext.getInstance().getApp().getCrypoManager();
		
		cipherCmb = new ComboBox<String>();
		
		ObservableList<String> cipherList = FXCollections.observableArrayList(cryptoMgr.getCryptoNames());
		cipherCmb.setItems(cipherList);
		cipherCmb.getSelectionModel().select(cryptoMgr.getSelectedCryptoName());
		cipherCmb.getSelectionModel().selectedItemProperty().addListener(e -> {updateKeyLengthCmb();});
		
		keyLengthCmb = new ComboBox<Integer>();
		ObservableList<Integer> keyLengthList = FXCollections.observableArrayList(cryptoMgr.getKeyLengths());
		keyLengthCmb.setItems(keyLengthList);
		keyLengthCmb.getSelectionModel().select((Integer)cryptoMgr.getSelectedKeyLength());
		keyLengthCmb.getSelectionModel().selectedItemProperty().addListener(e -> {showKeyLengthWarning();});
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
		
		grid.add(new Label("Nyckellängd:"), 0, 2);
		grid.add(keyLengthCmb, 1, 2);
		
		setCenter(grid);
	}
}
