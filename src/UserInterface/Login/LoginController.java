package UserInterface.Login;

import UserInterface.AdminInterface.AdminMainInterfaceController;
import UserInterface.AdminInterface.CommonMethods;
import animatefx.animation.Hinge;
import animatefx.animation.Shake;
import animatefx.animation.SlideInLeft;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends CommonMethods implements Initializable {

    @FXML
    private JFXButton signInBT, closeBT, signUpBT, minimizeBT;

    @FXML
    private AnchorPane signUpAnchor, signInAnchor, mainPane;

    @FXML
    private JFXProgressBar progressBar;

    @FXML
    private Hyperlink alreadyHaveAnAccount, forgotPass;

    /*
    SignIn Anchor Components
     */
    @FXML
    private JFXTextField inUsernameField;

    @FXML
    private JFXPasswordField inPasswordField;

    @FXML
    private Label inLabel;

    @FXML
    private JFXButton signIpSuccBT;

    /*
    SignUp Anchor Components
     */
    @FXML
    private JFXTextField upFnameField, upUsernameField, upLnameField;

    @FXML
    private JFXPasswordField upConfirmPasswordField, upPasswordField;

    @FXML
    private JFXButton signUpSuccBT;

    @FXML
    private Label upLabel;

    @FXML
    private JFXButton dontClickMeBT;

    /*
    Global Variable declarations
     */
    private SignInModel signIn = new SignInModel();
    private SignUpModel signUp = new SignUpModel();
    private String userType;
    private boolean loginsuccess, vanished;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (signIn.isConnected() && signUp.isConnected()) {
            System.out.println("DataBase Connected");
        } else {
            System.out.println("DataBase Not Connected");
            System.exit(1);
        }
        moveWindow(mainPane);
        inPasswordField.setOnAction(this::signInActionHandler);
        upConfirmPasswordField.setOnAction(this::signUpActionHandler);
        validateFields();
    }//End of initialize

    @FXML
    private void selectLoginOption(ActionEvent event) {
        if (event.getSource() == signIpSuccBT) {
            signInAnchor.toFront();
            clearSignInForm();
            new SlideInLeft(signInAnchor).play();
        } else if (event.getSource() == signUpSuccBT) {
            signUpAnchor.toFront();
            clearSignUpForm();
            new SlideInLeft(signUpAnchor).play();
        }
    }//End of select UserInterface.Login Option

    @FXML
    private void signInActionHandler(ActionEvent event) {

        String username = inUsernameField.getText();
        String pass = inPasswordField.getText();

        try {

            if (username.equals("") || pass.equals("")) {
                inLabel.setText("Make sure to fill all the fields below!");
                new Shake(inLabel).play();                      //animation
            }
            else if (signIn.isSignIn(username, pass)) {
                Task task = animateLogin(7);
                progressBar.setVisible(true);
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(task.progressProperty());
                task.setOnSucceeded(e ->{
                    Stage stage = (Stage) signInAnchor.getScene().getWindow();
                    if (signIn.getUserType().equals("Admin"))
                        goToMainInterface(username);
                    else
                        goToNormalUserMainInterface(username);
                });
                Thread thread = new Thread(task);
                thread.start();
            }
            else {
                inLabel.setText("Username or Password is incorrect!");
                new Shake(inLabel).play();                              //animation
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void signUpActionHandler(ActionEvent event) {

        //statusCode represents is produced by different SQL exceptions

        String username = upUsernameField.getText();
        String pass = upPasswordField.getText();
        String fname = upFnameField.getText();
        String lname = upLnameField.getText();
        String confirmPass = upConfirmPasswordField.getText();
        try {

            //to ensure that all the fields are filled
            if (username.equals("") || pass.equals("") || fname.equals("") || lname.equals("") || confirmPass.equals("")) {
                upLabel.setText("Make sure to fill all the fields below!");
                new Shake(upLabel).play();                      //animation
            }

            //to make password is more than 8 characters and less than 25 characters
            else if (pass.length() < 8 || pass.length() > 25) {
                upLabel.setText("Password should be between 8-25 characters long!");
                new Shake(upLabel).play();                      //animation
            }

            //to make sure user has entered same password
            else if (!pass.equals(confirmPass)) {
                upLabel.setText("Passwords are not same!");
                new Shake(upLabel).play();                      //animation
            }

            //if everything is correct then start saving data in database
            else {
                int statusCode = signUp.SignUpStatus(username, pass, fname, lname);
                if (statusCode == 0) {
                    loadSuccessfulPopup(event);
                    clearSignUpForm();
                    System.out.println("Hurrah!!!!");
                }
                else if (statusCode == -1) {
                    upLabel.setText("Username already taken!");
                    new Shake(upLabel).play();                      //animation
                }
                else if (statusCode == -2) {
                    upLabel.setText("Make Sure Length of Username, First Name and Last Name \ndoes not exceed 30 characters!");
                    new Shake(upLabel).play();                      //animation
                }
                else {
                    upLabel.setText("Something went wrong!");
                    new Shake(upLabel).play();                      //animation
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void dontClickMe(ActionEvent event) {
        new Hinge(dontClickMeBT).play();
    }

    @FXML
    void alreadyHaveAcc(ActionEvent event) {
        signInAnchor.toFront();
    }

    @FXML
    void forgotPassPopUp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Pasword");
        alert.setHeaderText("This function is still under development");
        alert.setContentText("If you still want to retrieve the password then contact the admin at " +
                "awesomeiqrar@gmail.com and send your USERNAME to retrieve your password!");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
        forgotPass.setVisited(false);
    }


    /*
    to clear sign In form
     */
    public void clearSignInForm() {

        inUsernameField.setText("");
        inPasswordField.setText("");
        inLabel.setText("");

        inUsernameField.resetValidation();
        inPasswordField.resetValidation();
        forgotPass.setVisited(false);
    }

    /*
    to clear signup form
     */
    public void clearSignUpForm() {

        upUsernameField.setText("");
        upPasswordField.setText("");
        upFnameField.setText("");
        upLnameField.setText("");
        upConfirmPasswordField.setText("");
        upLabel.setText("");

        upUsernameField.resetValidation();
        upPasswordField.resetValidation();
        upFnameField.resetValidation();
        upLnameField.resetValidation();
        upConfirmPasswordField.resetValidation();
        alreadyHaveAnAccount.setVisited(false);

    }

    public void goToMainInterface(String username) {
        try {
            URL url = new File("src/resources/fxml/MainInterface.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(url);
            loader.setController(new AdminMainInterfaceController(username));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setTitle("Game Dictionary");
            stage.setScene(scene);
            stage.show();


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Stage stage = (Stage) signInBT.getScene().getWindow();
        stage.close();
    }

    public void goToNormalUserMainInterface(String username) {
        try {
            URL url = new File("src/resources/fxml/NormalUserMainInterface.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(url);
            loader.setController(new AdminMainInterfaceController(username));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setTitle("Game Dictionary");
            stage.setScene(scene);
            stage.show();


        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Stage stage = (Stage) signInBT.getScene().getWindow();
        stage.close();
    }

    public void validateFields() {
        RequiredFieldValidator validator = new RequiredFieldValidator();
        NumberValidator numberValidator = new NumberValidator();
        validator.setMessage("*Required Field");
        numberValidator.setMessage("*Must be a number from 0 - 10");

        inUsernameField.getValidators().add(validator);
        inUsernameField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    inUsernameField.validate();
                }
            }
        });

        inPasswordField.getValidators().add(validator);
        inPasswordField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue){
                    inPasswordField.validate();
                }
            }
        });

        upUsernameField.getValidators().add(validator);
        upUsernameField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    upUsernameField.validate();
                }
            }
        });

        upFnameField.getValidators().add(validator);
        upFnameField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    upFnameField.validate();
                }
            }
        });

        upLnameField.getValidators().add(validator);
        upLnameField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    upLnameField.validate();
                }
            }
        });

        upPasswordField.getValidators().add(validator);
        upPasswordField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    upPasswordField.validate();
                }
            }
        });

        upConfirmPasswordField.getValidators().add(validator);
        upConfirmPasswordField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    upConfirmPasswordField.validate();
                }
            }
        });

    }

    private Task animateLogin(int second) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i=0; i<second; i++) {
                    updateProgress(i+1, second);
                    Thread.sleep(100);
                }
                return null;
            }
        };
    }


}
