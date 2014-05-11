package se.cenote.safestore.ui.settings;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.BeanPropertyUtils;

import se.cenote.safestore.domain.Settings;

public class SettingView extends BorderPane{
	
	private PropertySheet propertySheet;
	private Settings settings;
	
	public SettingView(){
		initComponents();
		layoutComponents();
	}
	
	public void update(Settings settings){
		this.settings = settings;
		if(settings != null){
			ObservableList<Item> items = BeanPropertyUtils.getProperties(settings);
			propertySheet.getItems().addAll(items);
		}
	}

	private void initComponents() {
		propertySheet = new PropertySheet();
		
		settings = new Settings();
		
		ObservableList<Item> items = BeanPropertyUtils.getProperties(settings);
		propertySheet.getItems().addAll(items);
		
		
		
		
	}

	private void layoutComponents() {
		
		setPadding(new Insets(10));
		
		setCenter(propertySheet);
	}
}
