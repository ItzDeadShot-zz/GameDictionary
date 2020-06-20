package UserInterface.AdminInterface;

import Model.GameModel;
import animatefx.animation.SlideInLeft;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import utili.DBConnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DeleteGameController extends CommonMethods implements Initializable {

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXButton searchBT;

    @FXML
    private JFXListView<String> gameListView;

    @FXML
    private HBox hbox;

    @FXML
    private AnchorPane deleteGamePane;

    private ObservableList<GameModel> gameModels = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadGamesInList();
        startSearch();
        setSearchBTActions();
    }

    @FXML
    void goBackToManageGames(MouseEvent event) {
        deleteGamePane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/ManageGamesPage.fxml"));
        try {
            deleteGamePane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new SlideInLeft(deleteGamePane).play();
    }

    private void deleteGameFromDatabase(String game) {

        try {
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement("DELETE FROM games WHERE Game_Name = '"+ game + "' ");
            prs.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Can not Delete!");
            alert.setHeaderText("Can not delete game since it is in favorite for some users!");
            alert.setContentText("Careful with the next step!");
            alert.showAndWait();
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGamesInList() {

        try{
            Connection conn = DBConnection.connectToDB();
            String selectGameNameQuery = "Select Game_Name, Game_Description, Game_Rating, Game_Genre, Game_Image, Game_Developer, DATE_FORMAT(Game_Release_Date,'%d %M, %Y.'), Game_Purchase_Link " +
                    "FROM games";

            ResultSet rs = conn.createStatement().executeQuery(selectGameNameQuery);
            while(rs.next()) {
                InputStream is = rs.getBinaryStream("Game_Image");
                Image tempImage = new Image(is);
                gameModels.add(new GameModel(rs.getString(1),rs.getString(2),
                        rs.getString(3),rs.getString(4),tempImage,
                        rs.getString(6), rs.getString(7), rs.getString(8)));
            }

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
                        if (onDeleteConfirmed(event)) {
                            deleteGameFromDatabase(this.gameListView.getSelectionModel().getSelectedItem());
                            goBackToManageGames(event);
                        }
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

}