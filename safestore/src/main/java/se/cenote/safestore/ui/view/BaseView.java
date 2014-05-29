package se.cenote.safestore.ui.view;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public abstract class BaseView extends BorderPane implements View{

	private ViewManager viewMgr;
	
	public BaseView(ViewManager viewMgr){
		this.viewMgr = viewMgr;
	}
	
	public String getName(){
		return getClass().getName();
	}
	
	public void onShow(){
		
	}
	
	public void onHide(){
		
	}
	
	public Parent getView(){
		return this;
	}
	
	public ViewManager getViewManger(){
		return viewMgr;
	}
}
