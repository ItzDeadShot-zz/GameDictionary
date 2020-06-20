package UserInterface.AdminInterface;

import animatefx.animation.Shake;
import animatefx.animation.SlideInLeft;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import utili.DBConnection;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class AddGameController extends CommonMethods implements Initializable {
    @FXML
    private JFXTextField nameField, linkField, ratingField, developerField, genreField;

    @FXML
    private JFXDatePicker dateField;

    @FXML
    private JFXTextArea descriptionField;

    @FXML
    private ImageView imageView;

    @FXML
    private Label fileName, errorLabel;

    @FXML
    private JFXButton browseBT, submitBT;

    @FXML
    private AnchorPane addGamePane;

    private String insertQuery = "INSERT INTO games(Game_Name, Game_Description, Game_Rating, Game_Genre, Game_Image, Game_Developer, Game_Release_Date, Game_Purchase_Link) VALUES (?,?,?,?,?,?,?,?)";
    private String imageFilePath;
    private final static long MAX_SIZE = (2048L * 2048L);           //Maximum length of file should be 2MB

    //AddGameController(){}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateFields();
    }

    @FXML
    public void addGameToDatabase(ActionEvent event) {
        try{
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement(insertQuery);

            //Checking if any field is empty and user is still clicking
            if ((this.nameField.getText().equals("")) || (this.descriptionField.getText().equals("")) ||
                    (this.ratingField.getText().equals("")) || (this.linkField.getText().equals("")) ||
                        (this.developerField.getText().equals("")) || isImageEmpty(this.imageView )) {

                errorLabel.setText("Make sure all the required fields are filled and Poster/Picture is chosen from Computer!!");
                new Shake(errorLabel).play();
            }

            else if (!isNumeric(this.ratingField.getText())){
                errorLabel.setText("Rating should be a number between 0.0 to 10.0");
                new Shake(errorLabel).play();
            }

            else if (!isValid(this.linkField.getText())){
                errorLabel.setText("Invalid URL");
                new Shake(errorLabel).play();
            }

            else {
                    prs.setString(1, this.nameField.getText());
                    prs.setString(2, this.descriptionField.getText());
                    prs.setString(3, this.ratingField.getText());
                    prs.setString(4,this.genreField.getText());

                    if (imageFilePath == null){
                        prs.setBlob(5, (Blob) null);
                    } else {
                        File file = new File(imageFilePath);
                        InputStream is = new FileInputStream(file);
                        prs.setBlob(5, is);
                    }//end of Blob if

                    prs.setString(6, this.developerField.getText());

                    if (dateField.getValue() == null) {
                        prs.setDate(7, (Date) null);
                    }
                    else{
                        prs.setDate(7, Date.valueOf(this.dateField.getValue()));
                    }//end of Date if

                prs.setString(8, this.linkField.getText());
                prs.executeUpdate();
                clearGameForm();
                System.out.println("It worked!!!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Congratulation");
                alert.setHeaderText("Game added Successfully!!");
                alert.setContentText("You can check the game information in View Game Tab");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                goBackToManageGames(event);
            }

        }catch (SQLIntegrityConstraintViolationException ex) {
            errorLabel.setText("Game Already Exists!");
            new Shake(errorLabel).play();
        }
        catch(MysqlDataTruncation ex) {
            errorLabel.setText("Data Entered is too large, make sure description is 2038 and others are 203 characters long");
            new Shake(errorLabel).play();
        }

        catch(SQLException | FileNotFoundException ex){
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
        addGamePane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../resources/fxml/ManageGamesPage.fxml"));
        try {
            addGamePane.getChildren().add((Node) loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new SlideInLeft(addGamePane).play();
    }

    @FXML
    void allowImageDrag(DragEvent event) {
        final Dragboard db = event.getDragboard();

        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png")
                || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpeg")
                || db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");
        if (event.getDragboard().hasFiles() && isAccepted) {
            event.acceptTransferModes(TransferMode.ANY);
        } else
        {
            event.consume();
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

    public static boolean isValid(String url) {

        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

}