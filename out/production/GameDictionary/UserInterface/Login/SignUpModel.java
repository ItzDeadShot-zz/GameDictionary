package UserInterface.Login;

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import org.mindrot.jbcrypt.BCrypt;
import utili.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class SignUpModel {
    Connection connection;

    //Constructor
    public SignUpModel() {
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
    public int SignUpStatus(String user, String pass, String fname, String lname) throws Exception {
        PreparedStatement ps = null;

        String insertQuery = "INSERT INTO users (USERNAME, PASSWORD, FIRST_NAME, LAST_NAME) VALUES(?,?,?,?)";

        try {
            ps = this.connection.prepareStatement(insertQuery);
            ps.setString(1, user.toLowerCase());
            ps.setString(2, BCrypt.hashpw(pass, BCrypt.gensalt()));
            ps.setString(3, fname);
            ps.setString(4,lname);

            ps.execute();
            connection.close();
            return 0;
        }

        catch(SQLIntegrityConstraintViolationException ex){
            return -1;
        }

        catch (MysqlDataTruncation ex) {
            return -2;
        }

        catch(SQLException ex){
            ex.printStackTrace();
            return -3;
        }
    }
}
