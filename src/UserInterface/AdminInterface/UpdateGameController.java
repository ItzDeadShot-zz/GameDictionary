package UserInterface.AdminInterface;

import animatefx.animation.Shake;
import animatefx.animation.SlideInLeft;
import com.jfoenix.controls.*;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import utili.DBConnection;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateGameController extends CommonMethods implements Initializable {
    @FXML
    private JFXTextField nameField, linkField, ratingField, developerField, genreField, searchField;

    @FXML
    private JFXDatePicker dateField;

    @FXML
    private JFXTextArea descriptionField;

    @FXML
    private ImageView imageView;

    @FXML
    private Label fileName, errorLabel;

    @FXML
    private JFXButton browseBT, submitBT, searchBT;

    @FXML
    private AnchorPane updateGamePane;

    @FXML
    private JFXComboBox<String> updateComboBox, gameComboBox;

    private String selectColoumNamesQuery = "SHOW COLUMNS FROM games;";
    private ObservableList<String> comboOptions = FXCollections.observableArrayList();
    private ObservableList<String> gameNames = FXCollections.observableArrayList();
    private String imageFilePath;
    private String choice;
    private final static long MAX_SIZE = (2048L * 2048L);           //Maximum length of file should be 2MB

    //AddGameController(){}

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setGameComboBox();
        setUpdateComboBox();
        startSearch();
        validateFields();
        setSearchBTActions();
    }

    @FXML
    void showEditable(ActionEvent event) {
        choice = "";
        hideAll();
        String selection = updateComboBox.getValue().toString();
        if (selection.equals("Game_Name")){
            nameField.setVisible(true);
            choice = "Game_Name";
        }
        else if (selection.equals("Game_Description")){
            descriptionField.setVisible(true);
            choice = "Game_Description";
        }
        else if (selection.equals("Game_Rating")){
            ratingField.setVisible(true);
            choice = "Game_Rating";
        }
        else if (selection.equals("Game_Genre")){
            genreField.setVisible(true);
            choice = "Game_Genre";
        }
        else if (selection.equals("Game_Image")){
            imageView.setVisible(true);
            browseBT.setVisible(true);
            choice = "Game_Image";
        }
        else if (selection.equals("Game_Developer")){
            developerField.setVisible(true);
            choice = "Game_Developer";
        }
        else if (selection.equals("Game_Release_Date")){
            dateField.setVisible(true);
            choice = "Game_Release_Date";
        }
        else if (selection.equals("Game_Purchase_Link")){
            linkField.setVisible(true);
            choice = "Game_Purchase_Link";
        }
    }

    private void hideAll(){
        nameField.setVisible(false);
        descriptionField.setVisible(false);
        ratingField.setVisible(false);
        genreField.setVisible(false);
        imageView.setVisible(false);
        browseBT.setVisible(false);
        developerField.setVisible(false);
        dateField.setVisible(false);
        linkField.setVisible(false);
    }

    @FXML
    public void updateGameInDatabase(ActionEvent event) {
        boolean check = false;
        String updateQuery = "UPDATE games SET " +choice+ " = ? where Game_Name = '" +this.gameComboBox.getValue()+ "' ";
        try{
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement(updateQuery);

            if (choice == null) {
                errorLabel.setText("Make sure that you have selected a game!");
                new Shake(errorLabel).play();
            }

            switch (choice) {
                case "Game_Name":
                    if (this.nameField.getText().equals("")) {
                        errorLabel.setText("Make sure the required field is filled");
                        new Shake(errorLabel).play();
                    }
                    else {
                        prs.setString(1, this.nameField.getText());
                        prs.executeUpdate();
                        check = true;
                    }
                    break;
                case "Game_Description":
                    if (this.descriptionField.getText().equals("")) {
                        errorLabel.setText("Make sure the required field is filled");
                        new Shake(errorLabel).play();
                    }
                    else {
                        prs.setString(1, this.descriptionField.getText());
                        prs.executeUpdate();
                        check = true;
                    }

                    break;
                case "Game_Rating":
                    if (this.ratingField.getText().equals("")) {
                        errorLabel.setText("Make sure the required field is filled");
                        new Shake(errorLabel).play();

                    } else if (!isNumeric(this.ratingField.getText())) {
                        errorLabel.setText("Rating should be a number between 0.0 to 10.0");
                        new Shake(errorLabel).play();
                    }
                    else {
                        prs.setString(1, this.ratingField.getText());
                        prs.executeUpdate();
                        check = true;
                    }
                    break;
                case "Game_Genre":
                    if (this.genreField.getText().equals("")) {
                        errorLabel.setText("Make sure the required field is filled");
                        new Shake(errorLabel).play();
                    }
                    else {
                        prs.setString(1, this.genreField.getText());
                        prs.executeUpdate();
                        check = true;
                    }

                    break;
                case "Game_Image":
                    if (isImageEmpty(this.imageView)) {
                        errorLabel.setText("Make Sure Poster/Picture is chosen from Computer!!");
                        new Shake(errorLabel).play();
                    }
                    else {
                        File file = new File(imageFilePath);
                        InputStream is = new FileInputStream(file);
                        prs.setBlob(1, is);
                        prs.executeUpdate();
                        check = true;
                    }
                    break;
                case "Game_Developer":
                    if (this.developerField.getText().equals("")) {
                        errorLabel.setText("Make the required field is filled");
                        new Shake(errorLabel).play();
                    }
                    else {
                        prs.setString(1, this.developerField.getText());
                        prs.executeUpdate();
                        check = true;
                    }
                    break;
                case "Game_Release_Date":
                    if (this.dateField.getValue() == null) {
                        errorLabel.setText("Make the required field is filled");
                        new Shake(errorLabel).play();
                    }
                    else {
                        prs.setDate(1, Date.valueOf(this.dateField.getValue()));
                        prs.executeUpdate();
                        check = true;
                    }
                    break;
                case "Game_Purchase_Link":
                    if (this.linkField.getText().equals("")) {
                        errorLabel.setText("Make the required fields is filled");
                        new Shake(errorLabel).play();
                    }
                    else {
                        prs.setString(1, this.linkField.getText());
                        prs.executeUpdate();
                        check = true;
                    }
                    break;
            }
            if(check) {
                clearGameForm();
                System.out.println(" It worked!!!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Congratulation");
                alert.setHeaderText("Game information updated Successfully!!");
                alert.setContentText("You can check the game information in 'View Game' Tab");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                goBackToManageGames(event);
            }

        } catch(SQLException | FileNotFoundException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void chooseFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Image File", "*.jpg", "*.gif", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        imageFilePath = selectedFile.getAbsolutePath();
        fileName.setText(selectedFile.getName());
        if(checkFile(selectedFile)){
            System.out.println(imageFilePath);
            Image image = new Image("file:" + imageFilePath);
            imageView.setImage(image);
            imageView.setFitWidth(325.0);
            imageView.setFitHeight(350.0);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
        }
        else {
            System.out.println("No Data");
        }
    }

    @FXML
    void goBackToManageGames(ActionEvent event) {
        updateGamePane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/ManageGamesPage.fxml"));
        try {
            updateGamePane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new SlideInLeft(updateGamePane).play();
    }

    @FXML
    void allowImageDrag(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    void dragImage(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        try {
            Image img = new Image(new FileInputStream(files.get(0)));
            imageFilePath = files.get(0).getAbsolutePath();
            imageView.setImage(img);
            imageView.setFitWidth(325.0);
            imageView.setFitHeight(350.0);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            fileName.setText(files.get(0).getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void setUpdateComboBox() {
        try {
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement(selectColoumNamesQuery);
            ResultSet rs = prs.executeQuery();

            while (rs.next()) {
                comboOptions.add(rs.getString(1));
            }
            comboOptions.remove(0);
            comboOptions.remove(0);
            this.updateComboBox.getItems().addAll(comboOptions);
            prs.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setGameComboBox(){
        try {
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement("select Game_Name From games");
            ResultSet rs = prs.executeQuery();

            while (rs.next()) {
                gameNames.add(rs.getString(1));
            }
            this.gameComboBox.getItems().addAll(gameNames);
            prs.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startSearch() {

        ArrayList<String> tempGameArr = new ArrayList<>();
        for (int i = 0; i < gameNames.size(); i++)
            tempGameArr.add(gameNames.get(i));

        AutoCompletionBinding bind= TextFields.bindAutoCompletion(searchField,tempGameArr);
        bind.setOnAutoCompleted(event -> {
            this.gameComboBox.getItems().clear();
            this.gameComboBox.getItems().add(searchField.getText());
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
        for (int i=0; i<gameNames.size(); i++){
            if(gameNames.get(i).equalsIgnoreCase(game)){
                this.gameComboBox.getItems().clear();
                this.gameComboBox.getItems().add(searchField.getText());
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


    public boolean checkFile(File file) {
        try {
            return file != null &&
                    file.isFile() &&
                    file.canRead() &&
                    file.length() <= MAX_SIZE;
        }
        catch (SecurityException exc) {
            System.err.println(exc.getMessage());
            return false;
        }
    }

    public void clearGameForm() {

        nameField.setText("");
        descriptionField.setText("");
        ratingField.setText("");
        linkField.setText("");
        imageView.setImage(null);
        fileName.setText("");
        developerField.setText("");
        dateField.setValue(null);
        genreField.setText("");
        errorLabel.setText("");

        nameField.resetValidation();
        descriptionField.resetValidation();
        linkField.resetValidation();
        developerField.resetValidation();
        ratingField.resetValidation();
        genreField.resetValidation();

    }

    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
            if (d < 0.0 || d > 10.0) {
                return false;
            }
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public void validateFields() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        NumberValidator numberValidator = new NumberValidator();
        validator.setMessage("*Required Field");
        numberValidator.setMessage("*Must be a number from 0 - 10");

        nameField.getValidators().add(validator);
        nameField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    nameField.validate();
                }
            }
        });

        descriptionField.getValidators().add(validator);
        descriptionField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    descriptionField.validate();
                }
            }
        });

        linkField.getValidators().add(validator);
        linkField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    linkField.validate();
                }
            }
        });

        developerField.getValidators().add(validator);
        developerField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    developerField.validate();
                }
            }
        });

        ratingField.getValidators().addAll(numberValidator, validator);
        ratingField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    ratingField.validate();
                }
            }
        });

        genreField.getValidators().add(validator);
        genreField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    genreField.validate();
                }
            }
        });
    }

    public static boolean isImageEmpty(ImageView imageView) {
        Image image = imageView.getImage();
        return image == null || image.isError();
    }

}