package hu.adsd.tmi.tmi_teammaker.java;

import java.sql.*;

public class Database {

    public Connection Con()
    {
        BScanner scanner = new BScanner();
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");

        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Class not found " + e);
        }

        Connection con = null;

        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://fks1.hesselaar.dev:3306/tmi","tmi","Tmi2023!"
            );
        }

        catch (SQLException e)
        {
            System.out.println("SQL exception occured " + e);
        }

        return con;
    }
}
