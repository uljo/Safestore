package se.cenote.safestore.ui.view;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import org.controlsfx.glyphfont.FontAwesome;

import se.cenote.safestore.AppContext;
import se.cenote.safestore.ui.entry.EntryView;
import se.cenote.safestore.ui.login.LoginView;
import se.cenote.safestore.ui.settings.SettingView;


public class ViewManager extends BorderPane{
	
	private View currView;
	private Map<String, View> viewsByName;
	
	private Button settingsBtn;
	private Node backIcon;
	private Node gearIcon;
	
	private static final String GRADIENT_1 = "-fx-background-color: radial-gradient(center 50% 50% , radius 120px , #ffebcd, #008080);"; 
	private static final String GRADIENT_2 = "-fx-background-color: radial-gradient(center 20% 20% , radius 50% , #f5f5dc, #8b4513);"; 
	private static final String GRADIENT_3 = "-fx-background-color: linear-gradient(#69B4E4 0%, #0070B9 100%);";
	
	public ViewManager(){
		viewsByName = new HashMap<String, View>();
		
		//GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
		backIcon = FontAwesome.Glyph.BACKWARD.create();
		gearIcon = FontAwesome.Glyph.GEAR.create();
		
		Label lbl = new Label("SafeStore");
		lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24pt;");
		
		DropShadow effect = new DropShadow();
		effect.setOffsetY(5.0);
        effect.setOffsetX(5.0);
        effect.setColor(Color.GRAY);
        Reflection reflection = new Reflection(8, 20, 70, 0);
        //effect.setInput(reflection);    
		lbl.setEffect(effect);
		
		FlowPane labelPane = new FlowPane();
		labelPane.getChildren().add(lbl);
		labelPane.setAlignment(Pos.CENTER);
		//labelPane.setStyle("-fx-background-color: red");
		
		ImageView imgView = new ImageView();
		InputStream in = EntryView.class.getResourceAsStream("/se/cenote/safestore/ui/vault-1.png");
		if(in != null){
			Image vaultImg = new Image(in, 100, 100, true, true);
			imgView.setImage(vaultImg);
		}
        imgView.setOnMouseClicked(e -> logOut());
		
        BorderPane topPane = new BorderPane();
        topPane.setPadding(new Insets(5));
        topPane.setLeft(imgView);
        topPane.setCenter(labelPane);
		//topPane.setRight(settingsBtn);
        //topPane.setStyle("-fx-background-color: slateblue; -fx-text-fill: white;");
        topPane.setStyle(GRADIENT_3);
        
        settingsBtn = new Button("", gearIcon);
		settingsBtn.setOnAction(e -> flip());
		settingsBtn.setVisible(false);
        
        FlowPane menuPane = new FlowPane();
        menuPane.setPadding(new Insets(4));
        menuPane.setAlignment(Pos.CENTER_RIGHT);
        menuPane.getChildren().add(settingsBtn);
        
        VBox vBox = new VBox();
        vBox.getChildren().addAll(topPane, menuPane);
		setTop(vBox);
	}
	
	public void logOut(){
		if(currView != null && currView.getName() != LoginView.class.getName()){
			AppContext.getInstance().getApp().logout();
			showLoginView();
		}
	}
	
	public void flip(){
		
		if(currView != null && currView.getName() == SettingView.class.getName()){
			show(EntryView.class.getName());
			//settingsBtn.setText("Inställningar");
			settingsBtn.setGraphic(gearIcon);
		}
		else{
			show(SettingView.class.getName());
			//settingsBtn.setText("Tillbaka");
			settingsBtn.setGraphic(backIcon);
		}
	}
	
	public void add(View view){
		viewsByName.put(view.getName(), view);
		
		//Parent parent = view.getView();
		//parent.setStyle("-fx-background-color: yellow;");
		setCenter(view.getView());
	}
	
	public void showLoginView(){
		show(LoginView.class.getName());
		if(settingsBtn.isVisible()){
			settingsBtn.setVisible(false);
		}
	}
	
	public void showEntryView(){
		show(EntryView.class.getName());
		
		if(!settingsBtn.isVisible()){
			settingsBtn.setGraphic(gearIcon);
			settingsBtn.setVisible(true);
			System.out.println("Enable settings btn: " + settingsBtn.isVisible());
		}
	}
	
	public void showSettingView(){
		show(SettingView.class.getName());
	}
	
	public void show(String name) {
		
		if(currView != null){
			currView.onHide();
		}
		
		View prevView = currView;
		
		currView = viewsByName.get(name);
		if(currView != null){
			Node viewNode = currView.getView();
			if(viewNode != null){
				
				if(prevView != null){
					SlideTransition slideTran = new SlideTransition(prevView.getView(), currView.getView(), 600);
					slideTran.play();
				}
				else{
					setCenter(viewNode);
				}
				currView.onShow();
			}
		}
		else{
			System.err.println("[show] Cant find view: " + name + ". Availables: " + viewsByName.keySet());
		}
	}
	
	class SlideTransition{
		
		private Node from;
		private Node to;
		
		private TranslateTransition slideOut;
		private TranslateTransition slideIn;
		
		
		public SlideTransition(Node from, Node to, double duration){
			this.from = from;
			this.to = to;
			
			//int location = 600;
			double location = getLocation(from);
			
			to.setTranslateY(location * -1);
			
			slideOut = getTransition(duration, location, true);
			slideIn = getTransition(duration, location, false);
		}
		
		private double getLocation(Node node){
			double sceneHeight = getScene().getHeight();
			double nodeHeight = from.getLayoutBounds().getHeight();
			double location = nodeHeight + ((sceneHeight - nodeHeight)/2);
			System.out.println("[init] location=" + location);
			return location;
		}
		
		public void play(){
			slideOut.play();
			System.out.println("[event] Starting trans out...");
		}
		
		private TranslateTransition getTransition(double duration, double location, boolean slideOut){
			
			double fromX = slideOut ? 0 : location * -1;
			double toX = slideOut ? location : 0;
			Node node = slideOut ? from : to;
			
			TranslateTransition t = new TranslateTransition(Duration.millis(duration), node);
			t.setFromY(fromX);
			t.setToY(toX);
			t.setCycleCount(1);
			t.setAutoReverse(true);
			if(slideOut){
				t.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						setCenter(to);
						slideIn.play();
					}
			    });
			}
			return t;
		}
	}

}
