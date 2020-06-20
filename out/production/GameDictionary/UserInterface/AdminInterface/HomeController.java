package UserInterface.AdminInterface;

import animatefx.animation.Shake;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Label dateLabel, timeLabel;

    @FXML
    private AnchorPane dateTimePane, shadow;

    private String todaysdate, todaystime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayTime();
        animate();
    }

    private void displayTime(){
        DateTimeFormatter dateformat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeformat = DateTimeFormatter.ofPattern("hh:mm:ss a");

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            todaysdate=dateformat.format(now).toString();
            todaystime=timeformat.format(now).toString();
            dateLabel.setText(todaysdate+"");
            timeLabel.setText(todaystime+"");
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    void dateTimeAnimation(MouseEvent event) {
        new Shake(dateTimePane).play();
    }
    private void popNode(AnchorPane pane, AnchorPane shadow){
        ScaleTransition st = new ScaleTransition(Duration.millis(800), pane);
        st.setFromX(0.5);
        st.setFromY(0.5);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setRate(1.5);
        st.setCycleCount(1);
        st.play();

        pane.setOnMouseEntered(event -> {
            Timeline timeline = new Timeline();
            timeline.setCycleCount(1);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                    new KeyValue(shadow.minWidthProperty(), 161)));
            timeline.play();
            pane.setEffect(new Glow(0.2));
        });

        pane.setOnMouseExited(event -> {
            Timeline timeline = new Timeline();
            timeline.setCycleCount(1);
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                    new KeyValue (shadow.minWidthProperty(), 1)));
            timeline.play();
            pane.setEffect(new Glow(0));
        });
    }

    private void animate(){
        popNode(dateTimePane, shadow);
    }
}
