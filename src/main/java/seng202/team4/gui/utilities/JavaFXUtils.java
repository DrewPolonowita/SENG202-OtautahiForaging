package seng202.team4.gui.utilities;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Methods for helping with JavaFXX functionality
 * @author Team 4
 */
public final class JavaFXUtils {
    private JavaFXUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    /**
     * Animates the width on two different javafx elements
     */
    public static void animateWidth(Region element1, Region element2, double startWidth, double endWidth, int time, Runnable onFinished) {
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.ZERO,
                new KeyValue(element1.minWidthProperty(), startWidth),
                new KeyValue(element1.prefWidthProperty(), startWidth),
                new KeyValue(element1.maxWidthProperty(), startWidth),
                new KeyValue(element2.minWidthProperty(), startWidth),
                new KeyValue(element2.prefWidthProperty(), startWidth),
                new KeyValue(element2.maxWidthProperty(), startWidth)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(time),
                new KeyValue(element1.minWidthProperty(), endWidth, Interpolator.EASE_BOTH),
                new KeyValue(element1.prefWidthProperty(), endWidth, Interpolator.EASE_BOTH),
                new KeyValue(element1.maxWidthProperty(), endWidth, Interpolator.EASE_BOTH),
                new KeyValue(element2.minWidthProperty(), endWidth, Interpolator.EASE_BOTH),
                new KeyValue(element2.prefWidthProperty(), endWidth, Interpolator.EASE_BOTH),
                new KeyValue(element2.maxWidthProperty(), endWidth, Interpolator.EASE_BOTH)));
        timeline.setOnFinished(event -> onFinished.run());
        timeline.play();
    }
}
