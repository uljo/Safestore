package se.cenote.safestore.ui.widget;

import java.util.Random;

import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class DotLabel extends HBox{
	
	private String text;
	
	private Circle[] dots;
	
	public DotLabel(){
		
		setAlignment(Pos.CENTER_LEFT);
		setSpacing(2);
		
		int count = 6;
		dots = new Circle[count];
		for(int i = 0; i < count; i++){
			Circle dot = new Circle(4);
			dot.setFill(Color.BLACK);
			
			dots[i] = dot;
			getChildren().add(dot);
		}
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public void play(){
		DotTransition t = new DotTransition(Duration.millis(500), dots);
		t.play();
	}

	
	private class DotTransition extends Transition{
		
		private Random rand = new Random();
		
		private Circle[] dots;
		  
		public DotTransition(Duration duration, Circle[] dots){
			  
			setCycleDuration(duration);
			
			setAutoReverse(true);
			
			this.dots = dots;
			  
			setOnFinished(e -> reset());
		}  

		@Override
		protected void interpolate(double frac) {
			//drop(frac);
			shake(frac);
			//fade(frac);
		}
		
		private void shake(double frac){
			
			for(int i = 0; i < dots.length; i++){
				
				Circle dot = dots[i];
				
				double y = dot.getTranslateY();
				y += rand.nextDouble() * (rand.nextBoolean() ? 1 : -1);
				dot.setTranslateY(y);
			}
		}
		
		private void drop(double frac){
			
			int i = (int)(frac*dots.length); // i = [1..10]
			
			if(i >= 0 && i < dots.length){
				Circle dot = dots[i];
				
				double y = dot.getTranslateY();
				y += 4;
				dot.setTranslateY(y);
			}
		}
		
		private void fade(double frac){
			for(int i = 0; i < dots.length; i++){
				
				Circle dot = dots[i];
				
				double op = dot.getOpacity();
				op = frac;
				dot.setOpacity(op);
			}
		}
		
		public void reset(){
			for(Circle dot : dots){
				dot.setTranslateY(0);
			}
			//System.out.println("[reset] Stopped!");
		}
	} 

}
