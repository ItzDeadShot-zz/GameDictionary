package UserInterface.AdminInterface;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NormalUserMainInterfaceController extends CommonMethods implements Initializable {

    @FXML
    private Button homeBT, addGameBT, viewGameBT, bookmarkBT, optionBT, logoutBT, closeBT, minimizeBT;

    @FXML
    AnchorPane holderPane, mainPane;

    @FXML
    AnchorPane homePage, addGamePage, viewGamePage, bookmarkPage, optionsPage;

    @FXML
    private Label usernameLabel;

    @FXML
    private JFXToggleButton toggleBT;

    private double xOffset = 0;
    private double yOffset = 0;
    private String username;

    public NormalUserMainInterfaceController(String username) {
        this.username = username;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            homePage=FXMLLoader.load(getClass().getResource("../../resources/fxml/HomePage.fxml"));
            //addGamePage=FXMLLoader.load(getClass().getResource("../../resources/fxml/AddGamePage.fxml"));
            //viewGamePage=FXMLLoader.load(getClass().getResource("../../resources/fxml/ViewGamePage.fxml"));
            //bookmarkPage=FXMLLoader.load(getClass().getResource("../../resources/fxml/FavoriteGamePage.fxml"));
            //optionsPage=FXMLLoader.load(getClass().getResource("../../resources/fxml/OptionsPage.fxml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        usernameLabel.setText(username);
        setNode(homePage);
        mainPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();

            }
        });
        mainPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mainPane.getScene().getWindow().setX(event.getScreenX() - xOffset);
                mainPane.getScene().getWindow().setY(event.getScreenY() - yOffset);
            }
        });

    }

    private void setNode(Node node) {
        holderPane.getChildren().clear();
        holderPane.getChildren().add((Node) node);
        new FadeIn(holderPane).play();
        }

    void loadWindow(String loc, String title) {
        try {
            URL url = new File(loc).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();

                }
            });
            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
            stage.setTitle(title);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void closeStage(Button button) {
        Stage stage = ((Stage) button.getScene().getWindow());
        stage.close();
    }

    @FXML
    void goToManageGames(ActionEvent event) {
        holderPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/ManageGamesPage.fxml"));
        try {
            holderPane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FadeIn(holderPane).play();
    }

    @FXML
    void goToFavorite(ActionEvent event) {
        holderPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/FavoriteGamePage.fxml"));
        try {
            loader.setController(new FavoriteController(username));
            holderPane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FadeIn(holderPane).play();
    }

    @FXML
    void goToHome(ActionEvent event) { setNode(homePage);}

    @FXML
    void goToOption(ActionEvent event) {
        holderPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/OptionsPage.fxml"));
        try {
            loader.setController(new UserInterface.AdminInterface.OptionController(username));
            holderPane.getChildren().add((Node) loader.load());
            OptionController optionController = loader.getController();
            optionController.clearSignUpForm();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FadeIn(holderPane).play();
    }

    @FXML
    void goToViewGame(ActionEvent event) {
        holderPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/ViewGamePage.fxml"));
        try {
            loader.setController(new ViewGameController(username));
            holderPane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FadeIn(holderPane).play();
    }

}
