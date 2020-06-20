package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GameMain extends Application {

    public static void main(String[] args) { launch(args);}

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = (Parent) FXMLLoader.load(getClass().getResource("../resources/fxml/Login.fxml"));
        stage.initStyle(StageStyle.TRANSPARENT);

        stage.getIcons().add(new Image(this.getClass().getResource("../resources/images/gameslogo.png").toString()));
        stage.setTitle("Game Dictionary Login");
        Scene loginScene = new Scene(root);
        loginScene.setFill(Color.TRANSPARENT);
        stage.setScene(loginScene);
        stage.show();
    }
}

