package UserInterface.AdminInterface;

import Model.GameModel;
import animatefx.animation.SlideInUp;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import utili.DBConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ViewGameController extends CommonMethods implements Initializable {

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXButton searchBT;

    @FXML
    private JFXListView<String> gameListView;

    @FXML
    private AnchorPane viewGamePane;

    @FXML
    private HBox hbox;

    private JFXButton allGamesBT, byGenreBT, byRatingBT;


    private boolean rotatedpane = false;
    private String username;
    private ObservableList<GameModel> gameModels = FXCollections.observableArrayList();
    String selectAllGamesQuery = "Select Game_Name, Game_Description, Game_Rating, Game_Genre," +
            "Game_Image, Game_Developer, DATE_FORMAT(Game_Release_Date,'%d %M, %Y.'), Game_Purchase_Link FROM games ORDER BY Game_Name ASC;";
    String selectGamesByRatingQuery = "Select Game_Name, Game_Description, Game_Rating, Game_Genre," +
            "Game_Image, Game_Developer, DATE_FORMAT(Game_Release_Date,'%d %M, %Y.'), Game_Purchase_Link FROM games ORDER BY Game_Rating DESC;";
    String selectGamesByGenreQuery = "Select Game_Name, Game_Description, Game_Rating, Game_Genre," +
            "Game_Image, Game_Developer, DATE_FORMAT(Game_Release_Date,'%d %M, %Y.'), Game_Purchase_Link FROM games ORDER BY Game_Genre ASC;";

    ViewGameController(String username) {
        this.username = username;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadGamesInList(selectAllGamesQuery);
        startSearch();
        setSearchBTActions();
    }

    @FXML
    void goToGameDetails(String game) {
        viewGamePane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/GameDetailPage.fxml"));
        try {
            loader.setController(new GameDetailController(game, username, gameModels));
            viewGamePane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new SlideInUp(viewGamePane).play();
    }

    private void loadGamesInList(String query) {
        gameModels.clear();
        try{
            Connection conn = DBConnection.connectToDB();
            //PreparedStatement prs = conn.prepareStatement(selectGameNameQuery);
            ResultSet rs = conn.createStatement().executeQuery(query);
            while(rs.next()) {
                InputStream is = rs.getBinaryStream("Game_Image");
                Image tempImage = new Image(is);
                gameModels.add(new GameModel(rs.getString(1),rs.getString(2),
                        rs.getString(3),rs.getString(4),tempImage,
                        rs.getString(6), rs.getString(7), rs.getString(8)));
            }

            gameListView.getItems().clear();
            for (int i =0; i < gameModels.size(); i++)
                gameListView.getItems().add(gameModels.get(i).getName());

            gameListView.setCellFactory(lv -> {
                ListCell<String> cell= new ListCell<String>() {
                    private ImageView imageView = new ImageView();
                    @Override
                    public void updateItem(String name, boolean empty) {
                        super.updateItem(name, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            for (int i = 0; i < gameModels.size(); i++) {
                                if (name.equals(gameModels.get(i).getName())){
                                    imageView.setImage(gameModels.get(i).getImage());
                                    imageView.setFitWidth(50.0);
                                    imageView.setFitHeight(75.0);
                                    imageView.setPreserveRatio(true);
                                    imageView.setSmooth(true);
                                    break;
                                }
                            }
                            setText(name);
                            setGraphic(imageView);
                        }
                    }};

                    cell.setOnMouseClicked(event -> {
                        if (!cell.isEmpty()) {
                            goToGameDetails(gameListView.getSelectionModel().getSelectedItem());
                            event.consume();
                        }
                    });
                return cell;
            });
            rs.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void startSearch() {

        ArrayList<String> tempGameArr = new ArrayList<>();
        for (int i = 0; i < gameModels.size(); i++)
            tempGameArr.add(gameModels.get(i).getName());

        AutoCompletionBinding bind= TextFields.bindAutoCompletion(searchField,tempGameArr);
        bind.setOnAutoCompleted(event -> {
            gameListView.getItems().clear();
            gameListView.getItems().add(searchField.getText());
        });
        searchField.setOnKeyReleased(event -> {
            if(event.getCode()== KeyCode.ENTER ){
                searchGame(searchField.getText());
            }else if(event.getCode()==KeyCode.DOWN && searchField.getText().isEmpty()){
                searchField.setText(" ");
            }
        });
    }

    private void searchGame(String game){
        boolean exists=false;
        for (int i=0; i<gameModels.size(); i++){
            if(gameModels.get(i).getName().equalsIgnoreCase(game)){
                gameListView.getItems().clear();
                gameListView.getItems().add(searchField.getText());
                exists=true;
                break;
            }
        }
        if(exists==false){// #181818 def
            searchBT.setStyle("-fx-background-color:#fc1919 ");
            rotateButton(searchBT);
            searchField.setOnKeyPressed(event -> {
            });
        }
    }

    public void setSearchBTActions() {
        searchBT.setOnMouseReleased(event -> searchGame(searchField.getText()));
        searchBT.setOnMouseEntered(event -> {
            searchBT.setStyle("-fx-background-color: #524A7B");
            searchBT.setEffect(new Bloom(0.75));
        });
        searchBT.setOnMouseExited(event -> {
            searchBT.setStyle("-fx-background-color:  #131022");
            searchBT.setEffect(new Bloom(1));
        });
    }

    @FXML
    void showAllGames(ActionEvent event) {
        loadGamesInList(selectAllGamesQuery);
    }

    @FXML
    void showGamesByGenre(ActionEvent event) {
        loadGamesInList(selectGamesByGenreQuery);
    }

    @FXML
    void showGamesByRating(ActionEvent event) {
        loadGamesInList(selectGamesByRatingQuery);
    }

}
