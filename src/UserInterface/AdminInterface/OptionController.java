package UserInterface.AdminInterface;

import animatefx.animation.Shake;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import org.mindrot.jbcrypt.BCrypt;
import utili.DBConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class OptionController implements Initializable {

    private IntegerProperty brightness = new SimpleIntegerProperty();

    @FXML
    private JFXPasswordField oldPasswordField, confirmPasswordField, newPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    AnchorPane pane;

    @FXML
    JFXButton themeBT;

    private String username;

    OptionController(String username) {
        this.username = username;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        validateFields();
        confirmPasswordField.setOnAction(this::changePassword);
    }

    @FXML
    void changePassword(ActionEvent event) {

        //check if all field are filled or not
        if (oldPasswordField.getText().equals("") || newPasswordField.getText().equals("")
                || confirmPasswordField.getText().equals("")) {
            errorLabel.setText("Make sure all fields are filed");
            new Shake(errorLabel).play();
        }

         else if (!checkPassword()){
            errorLabel.setText("Incorrect Password!");
            new Shake(errorLabel).play();
        }
         else if (!newPasswordField.getText().equals(confirmPasswordField.getText()))  {
            errorLabel.setText("Both password does not match!");
            new Shake(errorLabel).play();
        }
         else{
            updatePassword();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Forgot Pasword");
            alert.setHeaderText("This function is still under development");
            alert.setContentText("If you still want to retrieve the password then contact the admin at " +
                    "awesomeiqrar@gmail.com and send your USERNAME to retrieve your password!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }

        clearSignUpForm();
    }


    public boolean checkPassword() {

        try {
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement("SELECT password FROM USERS WHERE username = '" + username.toLowerCase() + "' ");
            ResultSet rs = prs.executeQuery();

            while (rs.next()) {
                String s = rs.getString("password");
                if (BCrypt.checkpw(this.oldPasswordField.getText(), s)) {
                    return true;
                }
            }

            prs.close();
            rs.close();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updatePassword() {

        try {
            Connection conn = DBConnection.connectToDB();
            PreparedStatement prs = conn.prepareStatement("UPDATE users SET PASSWORD = ? where USERNAME = '" + username.toLowerCase() + "' ");
            prs.setString(1,BCrypt.hashpw(this.newPasswordField.getText(), BCrypt.gensalt()));

            prs.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearSignUpForm() {
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");

        oldPasswordField.resetValidation();
        newPasswordField.resetValidation();
        confirmPasswordField.resetValidation();
    }

    public void validateFields() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        NumberValidator numberValidator = new NumberValidator();
        validator.setMessage("*Required Field");
        numberValidator.setMessage("*Must be a number from 0 - 10");

        oldPasswordField.getValidators().add(validator);
        oldPasswordField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    oldPasswordField.validate();
                }
            }
        });

        newPasswordField.getValidators().add(validator);
        newPasswordField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    newPasswordField.validate();
                }
            }
        });

        confirmPasswordField.getValidators().add(validator);
        confirmPasswordField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    confirmPasswordField.validate();
                }
            }
        });
    }
}
