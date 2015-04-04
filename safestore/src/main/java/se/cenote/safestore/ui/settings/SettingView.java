package se.cenote.safestore.ui.settings;

import java.io.File;
import java.util.Optional;

import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.Settings;
import se.cenote.safestore.domain.crypto.CryptoManager;
import se.cenote.safestore.ui.view.BaseView;
import se.cenote.safestore.ui.view.ViewManager;

public class SettingView extends BaseView{
	
	private static Logger logger = LoggerFactory.getLogger(SettingView.class);
	
	private TextField fileFld;
	private Button changeFileBtn;
	private ComboBox<String> cipherCmb;
	private ComboBox<Integer> keyLengthCmb;
	
	private Button okBtn;
	private Button cancelBtn;
	
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
		
		String path = "";
		File file = settings.getStorageFile();
		if(file != null)
			path = file.getAbsolutePath();
		fileFld.setText(path);
		
		cipherCmb.getSelectionModel().select(settings.getSeletedCrypto());
		
		keyLengthCmb.getSelectionModel().select((Integer)settings.getKeyLength());
		
		setDirty(false);
		disableBtns(true);
	}
	
	private void disableBtns(boolean value){
		okBtn.setDisable(value);
		cancelBtn.setDisable(value);
		
		logger.debug("[disableBtns] value=" + value + ", okBtn=" + okBtn.isDisabled() + ", cancelBtn: " + cancelBtn.isDisabled());
	}
	
	private void doChange(){
		File file = new File(fileFld.getText());
		String crypto = cipherCmb.getSelectionModel().getSelectedItem();
		int keyLength = keyLengthCmb.getSelectionModel().getSelectedItem();
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Bekräfta ändring");
		alert.setHeaderText("Verifiera ändringsbegäran");
		alert.setContentText("Vill du verkligen ändra?");

		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK){
			AppContext.getInstance().getApp().changeStorage(file, crypto, keyLength);
		} 
		else {
		    logger.debug("[doChange] User abort action.");
		}
		
		disableBtns(true);
	}
	
	private void doCancel(){
		
		onShow();
		
		disableBtns(true);
		
		logger.debug("[doCancel] entered...");
	}
	
	private void changeFile(){
		String path = fileFld.getText();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName(path);
		File file = fileChooser.showOpenDialog(getScene().getWindow());
		if(file != null && file.exists()){
			//String crypto = cipherCmb.getSelectionModel().getSelectedItem();
			//int keyLength = keyLengthCmb.getSelectionModel().getSelectedItem();
			//AppContext.getInstance().getApp().changeStorage(file, crypto, keyLength);
			fileFld.setText(file.getAbsolutePath());
			
			disableBtns(false);
			setDirty(true);
		}
	}
	
	private void changeCrypto(){

		updateKeyLengthCmb();
		disableBtns(false);
		setDirty(true);
	}
	
	private void changeKeyLength(){

		showKeyLengthWarning();
		disableBtns(false);
		setDirty(true);
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
		
		Settings settings = AppContext.getInstance().getApp().getSettings();
		String selectedCrypto = settings.getSeletedCrypto();
		if(selectedCrypto == null)
			selectedCrypto = CryptoManager.getDefaultCrypto();
		
		int selectedKeyLength = settings.getKeyLength();
		if(selectedKeyLength == 0)
			selectedKeyLength = CryptoManager.getDefaultKeyLength();
		
		fileFld = new TextField();
		fileFld.setEditable(false);
		
		changeFileBtn = new Button("...");
		changeFileBtn.setOnAction(e -> changeFile());
		
		cipherCmb = new ComboBox<String>();
		
		ObservableList<String> cipherList = FXCollections.observableArrayList(CryptoManager.getCryptoNames());
		cipherCmb.setItems(cipherList);
		if(settings.getSeletedCrypto() != null)
		cipherCmb.getSelectionModel().select(selectedCrypto);
		cipherCmb.getSelectionModel().selectedItemProperty().addListener(e -> changeCrypto());
		
		keyLengthCmb = new ComboBox<Integer>();
		ObservableList<Integer> keyLengthList = FXCollections.observableArrayList(CryptoManager.getKeyLengths());
		keyLengthCmb.setItems(keyLengthList);
		keyLengthCmb.getSelectionModel().select(selectedKeyLength);
		keyLengthCmb.getSelectionModel().selectedItemProperty().addListener(e -> changeKeyLength());
		
		okBtn = new Button("OK");
		okBtn.setDisable(true);
		okBtn.setOnAction(e -> doChange());
		
		cancelBtn = new Button("Avbryt");
		cancelBtn.setDisable(true);
		cancelBtn.setOnAction(e -> doCancel());
	}

	private void layoutComponents() {
		
		setPadding(new Insets(10));
		
		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(15);
		grid.add(new Label("Fil:"), 0, 0);
		grid.add(fileFld, 1, 0);
		grid.add(changeFileBtn, 2, 0);
		
		grid.add(new Label("Krypto:"), 0, 1);
		grid.add(cipherCmb, 1, 1);
		
		grid.add(new Label("Nyckellängd:"), 0, 2);
		grid.add(keyLengthCmb, 1, 2);
		
		setCenter(grid);
		
		setBottom(buildBtnPanel());
		
	}
	
	private FlowPane buildBtnPanel(){
		
		FlowPane btnPanel = new FlowPane();
		btnPanel.setHgap(10);
		btnPanel.setAlignment(Pos.CENTER);
		btnPanel.getChildren().addAll(okBtn, cancelBtn);
		
		return btnPanel;
	}
}
