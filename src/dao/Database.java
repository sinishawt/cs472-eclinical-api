package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Database {
	private Connection connection;
	private PreparedStatement statement = null;
	private static Database database;
	// JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/eclinical";
    //  Database credentials
    static final String USER = "root";
    static final String PASS = "";
    
	private Database() {}
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		if(connection == null) {
			//STEP 2: Register JDBC driver
		    Class.forName("com.mysql.jdbc.Driver");
		    //STEP 3: Open a connection
		    connection = (Connection) DriverManager.getConnection(DB_URL,USER,PASS);
		}
		return connection;
	}
	public static Database getInstance() {
		if(database == null)
			database = new Database();
		return database;
	}
	public boolean executeStatement(String sql, List<Object> objects) {
		boolean isSuccess = false;
		try {
			statement = getConnection().prepareStatement(sql);
			if(objects != null) {
				for(int i=0; i<objects.size(); i++)
					statement.setObject(i + 1, objects.get(i));
			}
			isSuccess = statement.execute();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return isSuccess;
	}
	public ResultSet getResult(String sql, List<Object> objects) {
		try {
			statement = getConnection().prepareStatement(sql);
			if(objects != null) {
				for(int i=0; i<objects.size(); i++)
					statement.setObject(i + 1, objects.get(i));
			}
			return statement.executeQuery();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
