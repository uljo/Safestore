package se.cenote.safestore.ui.widget;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.util.Duration;

class FlashLabel extends Label{
	
	private FadeTransition animation;
	
	public FlashLabel(){
		animation = new FadeTransition(Duration.millis(1000), this);
        animation.setFromValue(1.0);
        animation.setToValue(0);
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.setAutoReverse(true);
        animation.play();

        visibleProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> source, Boolean oldValue, Boolean newValue){
                if (newValue){
                    animation.playFromStart();
                }
                else{
                    animation.stop();
                }
            }
        });
	}
}