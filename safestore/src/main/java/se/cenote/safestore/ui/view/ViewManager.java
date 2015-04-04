package se.cenote.safestore.ui.view;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.cenote.safestore.AppContext;
import se.cenote.safestore.ui.SafeStoreGui;
import se.cenote.safestore.ui.entry.EntryView;
import se.cenote.safestore.ui.login.LoginView;
import se.cenote.safestore.ui.settings.SettingView;


public class ViewManager extends BorderPane{
	
	private static final String TITLE_STYLE = "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 24pt;";
	
	private static final String ICON_VAULT = "/se/cenote/safestore/ui/vault-1.png";

	private static Logger logger = LoggerFactory.getLogger(ViewManager.class);
	
	private View currView;
	private Map<String, View> viewsByName;
	
	private Button settingsBtn;
	private Node backIcon;
	private Node gearIcon;
	
	private static final String GRADIENT_1 = "-fx-background-color: radial-gradient(center 50% 50% , radius 120px , #ffebcd, #008080);"; 
	private static final String GRADIENT_2 = "-fx-background-color: radial-gradient(center 20% 20% , radius 50% , #f5f5dc, #8b4513);"; 
	private static final String GRADIENT_3 = "-fx-background-color: linear-gradient(#69B4E4 0%, #0070B9 100%);";
	
	public ViewManager(){
		
		initComponents();
		layoutComponents();
	}
	
	private void initComponents(){
		
		viewsByName = new HashMap<String, View>();
		
		backIcon = FontAwesome.Glyph.BACKWARD.create();
		gearIcon = FontAwesome.Glyph.GEAR.create();
		
        settingsBtn = new Button("", gearIcon);
		settingsBtn.setOnAction(e -> flip());
		settingsBtn.setVisible(false);
	}
	
	private void layoutComponents(){
		
		Label titleLbl = buildTitle(SafeStoreGui.TITLE);
		
		ImageView imgView = buildIcon(ICON_VAULT);
        imgView.setOnMouseClicked(e -> logOut());
		

		FlowPane titlePane = new FlowPane();
		titlePane.getChildren().add(titleLbl);
		titlePane.setAlignment(Pos.CENTER);
		//labelPane.setStyle("-fx-background-color: red");
		
		
        BorderPane topPane = new BorderPane();
        topPane.setPadding(new Insets(5));
        topPane.setLeft(imgView);
        topPane.setCenter(titlePane);
		//topPane.setRight(settingsBtn);
        //topPane.setStyle("-fx-background-color: slateblue; -fx-text-fill: white;");
        topPane.setStyle(GRADIENT_3);
        
        
        FlowPane menuPane = new FlowPane();
        menuPane.setPadding(new Insets(4));
        menuPane.setAlignment(Pos.CENTER_RIGHT);
        menuPane.getChildren().add(settingsBtn);
        
        VBox vBox = new VBox();
        vBox.getChildren().addAll(topPane, menuPane);
		setTop(vBox);
	}
	
	private Label buildTitle(String text){
		Label lbl = new Label(text);
		lbl.setStyle(TITLE_STYLE);
		
		DropShadow effect = new DropShadow();
		effect.setOffsetY(5.0);
        effect.setOffsetX(5.0);
        effect.setColor(Color.GRAY);
        Reflection reflection = new Reflection(8, 20, 70, 0);
        //effect.setInput(reflection);    
		lbl.setEffect(effect);
		return lbl;
	}
	
	private ImageView buildIcon(String iconPath){
		ImageView imgView = new ImageView();
		InputStream in = EntryView.class.getResourceAsStream(iconPath);
		if(in != null){
			Image vaultImg = new Image(in, 100, 100, true, true);
			imgView.setImage(vaultImg);
		}
		return imgView;
	}
	
	public void logOut(){
		if(currView != null && currView.getName() != LoginView.class.getName()){
			
			if(currView.isDirty()){
				showDirtyWarning();
				return;
			}
			
			AppContext.getInstance().getApp().logout();
			showLoginView();
		}
	}
	
	public void flip(){
		
		if(currView != null && currView.getName() == SettingView.class.getName()){
			
			if(currView.isDirty()){
				showDirtyWarning();
				return;
			}
			
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
	
	private void showDirtyWarning(){
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Varning");
		alert.setHeaderText("Du har osparade ändringar!");
		alert.setContentText("Du måste spara eller avbryta ändringar innan du kan lämna vyn.");

		alert.showAndWait();
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
			logger.debug("Enable settings btn: " + settingsBtn.isVisible());
		}
	}
	
	public void showSettingView(){
		show(SettingView.class.getName());
	}
	
	public void show(String name) {
		
		if(currView != null){
			
			if(currView.isDirty()){
				showDirtyWarning();
				return;
			}
			
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
			logger.error("[show] Cant find view: " + name + ". Availables: " + viewsByName.keySet());
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
			
			double location = getLocation(from);
			
			to.setTranslateY(location * -1);
			
			slideOut = getTransition(duration, location, true);
			slideIn = getTransition(duration, location, false);
		}
		
		private double getLocation(Node node){
			double sceneHeight = getScene().getHeight();
			double nodeHeight = from.getLayoutBounds().getHeight();
			double location = nodeHeight + ((sceneHeight - nodeHeight)/2);
			//logger.debug("[init] location=" + location);
			return location;
		}
		
		public void play(){
			slideOut.play();
			//logger.debug("[event] Starting trans out...");
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
