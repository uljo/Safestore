package se.cenote.safestore.ui.view;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import org.controlsfx.glyphfont.FontAwesome;

import se.cenote.safestore.ui.entry.EntryView;
import se.cenote.safestore.ui.settings.SettingView;


public class ViewManager extends BorderPane{
	
	private View currView;
	private Map<String, View> viewsByName;
	
	private Button settingsBtn;
	private Node backIcon;
	private Node gearIcon;
	
	public ViewManager(){
		viewsByName = new HashMap<String, View>();
		
		//GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
		backIcon = FontAwesome.Glyph.BACKWARD.create();
		gearIcon = FontAwesome.Glyph.GEAR.create();
		
		settingsBtn = new Button("Inställningar", gearIcon);
		settingsBtn.setOnAction(e -> flip());
		
		Label lbl = new Label("SafeStore");
		lbl.setFont(Font.font(18));
		
		FlowPane labelPane = new FlowPane();
		labelPane.getChildren().add(lbl);
		labelPane.setAlignment(Pos.CENTER);
		//labelPane.setStyle("-fx-background-color: red");
		
		ImageView imgView = new ImageView();
        Image vaultImg = new Image(EntryView.class.getResourceAsStream("vault-1.png"), 100, 100, true, true);
        imgView.setImage(vaultImg);
		
		
		BorderPane p = new BorderPane();
		p.setPadding(new Insets(5));
		p.setLeft(imgView);
		p.setCenter(labelPane);
		p.setRight(settingsBtn);
		p.setStyle("-fx-background-color: slateblue; -fx-text-fill: white;");
		setTop(p);
	}
	
	public void flip(){
		
		if(currView != null && currView.getName() == SettingView.class.getName()){
			show(EntryView.class.getName());
			settingsBtn.setText("Inställningar");
			settingsBtn.setGraphic(gearIcon);
		}
		else{
			show(SettingView.class.getName());
			settingsBtn.setText("Tillbaka");
			settingsBtn.setGraphic(backIcon);
		}
	}
	
	public void add(View view){
		viewsByName.put(view.getName(), view);
		
		//Parent parent = view.getView();
		//parent.setStyle("-fx-background-color: yellow;");
		setCenter(view.getView());
	}
	
	public void show(String name) {
		
		if(currView != null)
			currView.onHide();
		
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
