package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.security.KeyStore;
import java.sql.*;
import java.util.Properties;

public class DataBaseConfig {

	//private String password = "rootroot";
	

	
	
    private static final Logger logger = LogManager.getLogger("DataBaseConfig");

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");
     //   return DriverManager.getConnection("jdbc:mysql://localhost:3306/prod","root","rootroot");
        Properties prop = new Properties();
        try (InputStream input = DataBaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {



            // load a properties file
            prop.load(input);

            // get the property value and print it out
         /*   System.out.println(prop.getProperty("db.url"));
            System.out.println(prop.getProperty("db.user"));
            System.out.println(prop.getProperty("db.password")); */

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return DriverManager.getConnection(prop.getProperty("db.url"),prop.getProperty("db.user"),prop.getProperty("db.password"));
    }

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
