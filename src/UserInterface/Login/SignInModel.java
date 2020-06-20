package UserInterface.Login;

import org.mindrot.jbcrypt.BCrypt;
import utili.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInModel {

    Connection connection;

    private String userType;

    public String getUserType() {
        return userType;
    }

    //Constructor
    public SignInModel() {
        try {
            this.connection = DBConnection.connectToDB();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        if(this.connection == null) {
            System.exit(1);
        }
    }

    //return connection status
    public boolean isConnected(){
        return this.connection != null;
    }

    //UserInterface.Login Module for user authentication
    public boolean isSignIn(String user, String pass) throws Exception {
        PreparedStatement pr = null;
        ResultSet rs = null;
        String selectQuery = "SELECT password, Status FROM USERS WHERE username = ?";

        try {
            pr = this.connection.prepareStatement(selectQuery);
            pr.setString(1, user);

            rs = pr.executeQuery();

            //check if user information is in database
            while(rs.next()) {
                String s = rs.getString("password");
                if (BCrypt.checkpw(pass, s)) {
                    this.userType = rs.getString("Status");
                    return true;
                }
            }
            return false;
        }

        catch(SQLException ex){
            System.out.println("Alert!!!!!!!!!!");
            ex.printStackTrace();
            return false;
        }

        finally {
           pr.close();
           rs.close();
        }

    }

}
