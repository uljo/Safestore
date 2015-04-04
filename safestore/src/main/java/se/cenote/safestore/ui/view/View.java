package se.cenote.safestore.ui.view;

import javafx.scene.Parent;


public interface View{
	public String getName();
	public Parent getView();
	public void onShow();
	public void onHide();
	public boolean isDirty();
}
