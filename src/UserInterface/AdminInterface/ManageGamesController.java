package UserInterface.AdminInterface;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManageGamesController implements Initializable {

    @FXML
    private JFXButton submitBT, submitBT1, submitBT2;

    @FXML
    private AnchorPane managePane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void goToAddGame(ActionEvent event) {
        managePane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/AddGamePage.fxml"));
        try {
            managePane.getChildren().add((Node) loader.load());
            AddGameController addGameController = loader.getController();
            addGameController.clearGameForm();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FadeIn(managePane).play();
    }

    @FXML
    void goToDeleteGame(ActionEvent event) {
        managePane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/DeleteGamePage.fxml"));
        try {
            managePane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FadeIn(managePane).play();
    }

    @FXML
    void goToUpdateGame(ActionEvent event) {
        managePane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/UpdateGamePage.fxml"));
        try {
            managePane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FadeIn(managePane).play();
    }


}
