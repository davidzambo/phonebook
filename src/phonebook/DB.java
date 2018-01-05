
package phonebook;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class DB {
    final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    final String URL = "jdbc:derby:sampleDB;create=true";
    final String USERNAME = "";
    final String PASSWORD = "";
    // Létrehozzuk a kapcsolatot (hidat)
    Connection conn = null;
    // Ha életre kelt, csinálunk egy megpakolható teherautót
    Statement createStatement = null;
    // Megnézzük, hogy üres-e az adatbázis
    DatabaseMetaData dbmd = null;
    
    public DB(){
        
        // Megpróbáljuk életre kelteni
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("A híd létrejött.");
        } catch (SQLException ex) {
            System.out.println("Valami baj van a connection ágnál.");
            System.out.println(""+ex);
            //Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (conn != null){
            
            try {
                createStatement = conn.createStatement();
                System.out.println("A statement létrejött.");
            } catch (SQLException ex) {
                System.out.println("Valami baj van a statement ágnál!");
                System.out.println(""+ex);
            }
        }
        
        try {
            dbmd = conn.getMetaData();
            System.out.println("A DatabaseMetaData megérkezett.");
        } catch (SQLException ex) {
            System.out.println("Valami baj van a DatabaseMetaData ágnál!");
            System.out.println(""+ex);
        }
        
        
        try {
            ResultSet rs = dbmd.getTables(null, "APP", "CONTACTS", null);
            if (!rs.next()){
                createStatement.execute("create table contacts(id INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), lastname varchar(20), firstname varchar(20), email varchar(30))");
                System.out.println("A contacts táblát létrehoztam");
            }
            System.out.println("Az adatbázis létezik.");
        } catch (SQLException ex) {
            System.out.println("Valami baj van a resultset ágnál!");
            System.out.println(""+ex);
        }
    }
    
    public ArrayList<Person> getAllContacts(){
        String sql = "select * from contacts";
        ResultSet rs;
        ArrayList<Person> users = null;
        try {
            rs = createStatement.executeQuery(sql);
            users = new ArrayList<>();
            
            while (rs.next()){
                Person actualPerson = new Person(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname"), rs.getString("email"));
//                System.out.println(name + "\n" + age);
                users.add(actualPerson);
            }
        } catch (SQLException ex) {
            System.out.println("Valami baj van a lekérdezéssel!");
            System.out.println(""+ex);
        }
        return users;
    }
    
    public void addContact(Person person) {
        try {
            String sql = "insert into contacts (lastname, firstname, email) values (?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.execute();
            System.out.println("Successfully executed: " + sql);
        } catch (SQLException ex) {
            System.out.println("Error on statement: " + person.getLastName() + " " + person.getFirstName() + " " + person.getEmail() + " " + ex);
        }

    }
    
        public void updateContact(Person person) {
        try {
            String sql = "update contacts set lastname = ?, firstname = ?, email = ? where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.setInt(4, Integer.parseInt(person.getId()));
            preparedStatement.execute();
            System.out.println("Successfully executed: " + sql);
        } catch (SQLException ex) {
            System.out.println("Error on statement: " + person.getLastName() + " " + person.getFirstName() + " " + person.getEmail() + " " + ex);
        }

    }
}
