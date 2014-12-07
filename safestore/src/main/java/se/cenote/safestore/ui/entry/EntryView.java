package se.cenote.safestore.ui.entry;

import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import se.cenote.safestore.AppContext;
import se.cenote.safestore.domain.Entry;
import se.cenote.safestore.ui.entry.EntryPanel.EntryListener;
import se.cenote.safestore.ui.view.BaseView;
import se.cenote.safestore.ui.view.ViewManager;

public class EntryView extends BaseView{

	private ListView<String> nameList;
	private ListListener lst;
	
	private EntryPanel entryPanel;
	
	public EntryView(ViewManager viewMgr){
		super(viewMgr);
		initComponents();
		layoutComponents();
	}
	
	public void update(List<String> names){
		nameList.getItems().clear();
		nameList.getItems().addAll(names);
		
		if(!names.isEmpty()){
			nameList.getSelectionModel().selectFirst();
		}
	}
	
	@Override
	public void onShow() {
		List<String> names = AppContext.getInstance().getApp().getNames();
		update(names);
		System.out.println("[onShow] names=" + names);
	}

	private void selectEntry(String name){
		if(entryPanel.isViewMode()){
			Entry entry = AppContext.getInstance().getApp().getEntry(name);
			entryPanel.update(entry);
		}
	}

	private void initComponents() {
		nameList = new ListView<String>();
		nameList.setPadding(new Insets(5));

		nameList.getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {selectEntry(n);});
		nameList.setPrefSize(160, 200);
		
		lst = new ListListener();
		
		entryPanel = new EntryPanel(new EntryListener() {
			@Override
			public void onSave(Entry entry) {
				List<String> list = AppContext.getInstance().getApp().getNames();
				update(list);
			}

			@Override
			public void onEdit() {
				nameList.setFocusTraversable(false);
				nameList.getSelectionModel().selectedIndexProperty().addListener(lst);
			}

			@Override
			public void onView() {
				nameList.setFocusTraversable(true);
				nameList.getSelectionModel().selectedIndexProperty().removeListener(lst);
			}
			
		});
	}
	
	class ListListener implements ChangeListener<Number>{
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			Platform.runLater(new Runnable() {
                public void run() {
                	nameList.getSelectionModel().select(-1);

                }
            });
		}	
	}

	private void layoutComponents() {
		
		setPadding(new Insets(10));
		
		//nameList.setPadding(new Insets(5));
		setLeft(nameList);
		
		entryPanel.setPadding(new Insets(10));
		setCenter(entryPanel);
	}
}
