package UserInterface.AdminInterface;

import Model.GameModel;
import animatefx.animation.SlideInLeft;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import utili.DBConnection;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class GameDetailController1 implements Initializable {

    @FXML
    private JFXButton goBackBT, buyBT;

    @FXML
    private AnchorPane gameDetailPane;

    @FXML
    private ImageView gamePoster, favoriteImageView;

    @FXML
    private Label gameGenre, gameReleaseDate, gameDeveloper, gameName, gameRating;

    @FXML
    private JFXTextArea gameDescription;

    private ObservableList<GameModel> gameModels;
    private ObservableList<String> favoriteGames = FXCollections.observableArrayList();
    private String game;
    private String username;
    private boolean bookmarked = false;

    GameDetailController1(String game, String username, ObservableList<GameModel> gameModel) {
        this.gameModels = FXCollections.observableArrayList(gameModel);
        this.game = game;
        this.username = username;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadContentInPage();
        loadFavoriteGames();
        for (int i=0; i<favoriteGames.size(); i++)
        {
            if (favoriteGames.get(i).equals(game)) {
                favoriteImageView.setImage(new Image("resources/images/filledHeart.png"));
                bookmarked = true;
                break;
            }
        }
    }

    public void loadContentInPage(){

        final int j;
        gameName.setText(game);
        for (int i = 0; i < gameModels.size(); i++) {
            if (gameModels.get(i).getName().equals(game)){
                gameDeveloper.setText(gameModels.get(i).getDeveloper());
                gameReleaseDate.setText(gameModels.get(i).getReleaseDate());
                gameGenre.setText(gameModels.get(i).getGenre());
                gameDescription.setText(gameModels.get(i).getDescription());
                gameRating.setText(gameModels.get(i).getRating());
                gamePoster.setImage(gameModels.get(i).getImage());
                gamePoster.setFitWidth(325.0);
                gamePoster.setFitHeight(425.0);
                gamePoster.setPreserveRatio(true);
                gamePoster.setSmooth(true);
                j=i;
                buyBT.setOnAction(event -> {
                    Desktop browser = Desktop.getDesktop();
                    try {
                        browser.browse(new URI(gameModels.get(j).getPurchaseLink()));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }

                });
                break;
            }
        }
    }

    @FXML
    void goBackToViewGamePage(ActionEvent event) {
        gameDetailPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/FavoriteGamePage.fxml"));
        try {
            loader.setController(new FavoriteController(username));
            gameDetailPane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new SlideInLeft(gameDetailPane).play();
    }

    @FXML
    void favoriteGame(ActionEvent event) {
        if (bookmarked){
            deleteFavoriteGame();
            favoriteImageView.setImage(new Image("resources/images/heart.png"));
            bookmarked = false;
        }
        else{
            addFavoriteGame();
            favoriteImageView.setImage(new Image("resources/images/filledHeart.png"));
            bookmarked = true;
        }

    }

    public void addFavoriteGame(){
        try {
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement("INSERT INTO favorite(Favorite_Game, USERNAME) VALUES(?,?)");

            prs.setString(1,game);
            prs.setString(2,username.toLowerCase());
            prs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFavoriteGame(){
        try {
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement("DELETE FROM favorite WHERE Favorite_Game = '"+game+"' " +
                                                                "AND USERNAME = '"+username.toLowerCase()+"' ");
            prs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadFavoriteGames() {

        try {
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement("SELECT Favorite_Game FROM favorite WHERE USERNAME = '"+username.toLowerCase()+"' ");
            ResultSet rs = prs.executeQuery();

            while(rs.next()) {
                favoriteGames.add(rs.getString("Favorite_Game"));
            }
            prs.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
