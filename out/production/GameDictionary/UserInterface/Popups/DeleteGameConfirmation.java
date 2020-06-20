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

public class DeleteGameConfirmation extends CommonMethods implements Initializable {

    @FXML
    private JFXButton yesbtn, nobtn, closebtn;
    @FXML
    private AnchorPane logoutpane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        page="Log Out";
        nobtn.setOnMouseEntered(e -> {
            nobtn.setStyle("-fx-background-color: #FF9A00;");
            nobtn.setEffect(new Bloom(0.8));
        });
        nobtn.setOnMouseExited(e -> {
            nobtn.setStyle("-fx-background-color:  #FF5722;");
            nobtn.setEffect(new Bloom(1));
        });

        yesbtn.setOnMouseEntered(e -> {
            yesbtn.setStyle("-fx-background-color: #FF9A00;");
            yesbtn.setEffect(new Bloom(0.8));
        });
        yesbtn.setOnMouseExited(e -> {
            yesbtn.setStyle("-fx-background-color:  #FF5722;");
            yesbtn.setEffect(new Bloom(1));
        });

        disableAllFocus(logoutpane);
        closebtn.setOnMouseEntered(e -> closebtn.setStyle("-fx-background-color:  #F6490D"));
        closebtn.setOnMouseExited(e -> closebtn.setStyle("-fx-background-color: transparent"));
        moveWindow(logoutpane);
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
            if(btn.getId().equals("yesbtn")){
                CommonMethods.deleteConfirmed=true;
                handleClose(event);
            }else if(btn.getId().equals("nobtn")){
                CommonMethods.deleteConfirmed=false;
                handleClose(event);
            }
        });
    }
}
