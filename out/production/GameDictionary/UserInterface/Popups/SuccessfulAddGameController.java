package UserInterface.Popups;

import UserInterface.AdminInterface.CommonMethods;
import com.jfoenix.controls.JFXButton;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SuccessfulAddGameController extends CommonMethods implements Initializable {

    @FXML
    private JFXButton goToLoginBT, closebtn;
    @FXML
    private AnchorPane succSignUpPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        page="Log Out";

        goToLoginBT.setOnMouseEntered(e -> {
            goToLoginBT.setStyle("-fx-background-color: #FF9A00;");
            goToLoginBT.setEffect(new Bloom(0.8));
        });
        goToLoginBT.setOnMouseExited(e -> {
            goToLoginBT.setStyle("-fx-background-color:  #FF5722;");
            goToLoginBT.setEffect(new Bloom(1));
        });

        disableAllFocus(succSignUpPane);
        closebtn.setOnMouseEntered(e -> closebtn.setStyle("-fx-background-color:  #F6490D"));
        closebtn.setOnMouseExited(e -> closebtn.setStyle("-fx-background-color: transparent"));
        moveWindow(succSignUpPane);

    }

    public void handleConfirmation(ActionEvent event){
        JFXButton btn=(JFXButton)event.getSource();
        TranslateTransition t1=new TranslateTransition(Duration.millis(270),btn);
        t1.setToY(-17d);
        PauseTransition p1=new PauseTransition(Duration.millis(40));
        TranslateTransition t2=new TranslateTransition(Duration.millis(270),btn);
        t2.setToY(0d);

        SequentialTransition d=new SequentialTransition(btn, t1,p1,t2);
        d.play();
        d.setOnFinished(event1 -> {
            if(btn.getId().equals("goToLoginBT")){
                CommonMethods.succConfirmed=true;
                handleClose(event);
            }
        });
    }

}
