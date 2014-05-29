package se.cenote.safestore.ui.entry;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.Entry;
import se.cenote.safestore.ui.entry.EntryPanel.EntryListener;
import se.cenote.safestore.ui.view.BaseView;
import se.cenote.safestore.ui.view.ViewManager;

public class EntryView extends BaseView{

	private ListView<String> nameList;
	private EntryPanel entryPanel;
	
	
	
	public EntryView(ViewManager viewMgr){
		super(viewMgr);
		initComponents();
		layoutComponents();
	}
	
	public void update(List<String> names){
		nameList.getItems().clear();
		nameList.getItems().addAll(names);
	}
	
	private void selectEntry(String name){
		Entry entry = AppContext.getInstance().getApp().getEntry(name);
		entryPanel.update(entry);
	}

	private void initComponents() {
		nameList = new ListView<String>();
		nameList.setPadding(new Insets(5));
		
		List<String> names = AppContext.getInstance().getApp().getNames();
		ObservableList<String> items = FXCollections.observableArrayList(names);
		nameList.setItems(items);
		
		nameList.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {selectEntry(n);});
		nameList.setPrefSize(100, 200);
		
		entryPanel = new EntryPanel(new EntryListener() {
			@Override
			public void onSave(Entry entry) {
				List<String> list = AppContext.getInstance().getApp().getNames();
				update(list);
			}
		});
		
	}

	private void layoutComponents() {
		
		setPadding(new Insets(10));
		
		
		
		//nameList.setPadding(new Insets(5));
		setLeft(nameList);
		
		entryPanel.setPadding(new Insets(10));
		setRight(entryPanel);
	}
}
