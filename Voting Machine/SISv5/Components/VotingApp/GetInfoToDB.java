/**
 * Created by Xiaokai Wang.
 * Queries are provided.
 */


//STEP 1. Import required packages
import java.sql.*;
import java.util.Random;

import com.mysql.jdbc.Driver;
public class GetInfoToDB {
    // JDBC driver name and database URL

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://sql5.freesqldatabase.com/sql5114097";

    //  Database credentials
    static final String USER = "sql5114097";
    static final String PASS = "dVgZpI9vLn";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Inserting...");
            stmt = conn.createStatement();
            // get user email
            //String email = getEmail();
            //String pid = getPid();

            String sql1;
            String sql3;

            String[] emails = {"www@pitt.edu", "222@pitt.edu", "333@gmail.com", "444@gmail.com",
                    "dsaa@gmail.com", "joedoe@hotmail.com", "binary@pitt.edu", "yourname@cmu.edu"};
            for (int o = 0; o < 8; o++){
                int id1 = o+1;
                String email = emails[o];
                sql1 = "INSERT INTO Vote VALUES('"+email+"', '"+id1+"')";
                stmt.executeUpdate(sql1);
            }
            String[] allEmails = {"dsaa12@gmail.com", "joedoe3@hotmail.com", "binary22@pitt.edu", "yourname5@cmu.edu"};
            for (int m = 0; m < 4; m++){
                int id2 = m+1;
                String email = allEmails[m];
                sql3 = "INSERT INTO Vote VALUES('"+email+"', '"+id2+"')";
                stmt.executeUpdate(sql3);
            }
            String sql;
            sql = "INSERT INTO Vote VALUES('duplicate@hotmail.com','2')";
            stmt.executeUpdate(sql);

            String sql0;
            String e = "duplicate@hotmail.com";
            sql0 = "SELECT email FROM Vote WHERE email = '"+e+"'";
            ResultSet rs0 = stmt.executeQuery(sql0);
            rs0.next();
            String em = rs0.getString("email");



            if (em == null || em.isEmpty()) {

                String sql99;
                int id = 3;

                sql99 = "INSERT INTO Vote VALUES('" + e + "', '" + id + "')";
                stmt.executeUpdate(sql99);
            }else{
                //Do nothing.
                System.out.println(em + " already voted, fail.");
            }

            //sql1 = "INSERT INTO Vote VALUES('"+email+"', '"+pid+"')";
            //stmt.executeUpdate(sql1);

            String sql2;
            sql2 = "SELECT Vote.id as new, Posters.content as con, COUNT(Vote.id) AS c " +
                    "FROM Vote LEFT JOIN Posters ON Vote.id = Posters.id "+
                    "GROUP BY new "+
                    "ORDER BY c DESC ";
            ResultSet rs = stmt.executeQuery(sql2);

            //STEP 5: Extract data from result set
            System.out.println("Now the results are: " );
            System.out.println("---------------------------------------------------");
            System.out.println("Poster ID " +" | "+" Total  " + " | " + " Content ");
            while(rs.next()){
                //Retrieve by column name
                String id  = rs.getString("new");
                int total = rs.getInt("c");
                String con = rs.getString("con");
                // Print Data.
                System.out.println("  "+ id + "        |   " + total + "      |  " + con + " " );

            }
            System.out.println("----------------------------------------------------");



            //STEP 6: Clean-up environment
            rs0.close();
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }//end main

    // pass one email, with its poster id voted.
    public void store(String email, int id){
        Connection cnc = null;
        Statement stmnt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            cnc = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Selecting...");
            stmnt = cnc.createStatement();
            String sql0;
            sql0 = "SELECT email FROM Vote WHERE email = '"+email+"'";
            ResultSet rs = stmnt.executeQuery(sql0);
            String em = "";
            while(rs.next()){
                em = rs.getString("email");
            }
            // if the email is not in the DB, we insert it to table Vote
            if (em == null || em.isEmpty()) {

                String sql99;

                sql99 = "INSERT INTO Vote VALUES('" + email + "', '" + id + "')";
                stmnt.executeUpdate(sql99);
            }else{
                //Do nothing.
                System.out.println(em + " already voted, fail.");
            }
            //STEP 6: Clean-up environment
            rs.close();
            stmnt.close();
            cnc.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmnt!=null)
                    stmnt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(cnc!=null)
                    cnc.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }

    // get trend
    public void getTrend(){
        Connection cnc = null;
        Statement stmnt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            cnc = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Selecting...");
            stmnt = cnc.createStatement();

            String sql2;
            sql2 = "SELECT Vote.id as new, Posters.content as con, COUNT(Vote.id) AS c " +
                    "FROM Vote LEFT JOIN Posters ON Vote.id = Posters.id "+
                    "GROUP BY new "+
                    "ORDER BY c DESC "
            ;
            ResultSet rs = stmnt.executeQuery(sql2);

            //STEP 5: Extract data from result set
            System.out.println("Now the results are: " );
            System.out.println("----------------------------------------------------");
            System.out.println("Poster ID " +" | "+" Total  " + " | " + " Content ");
            while(rs.next()){
                //Retrieve by column name
                String id  = rs.getString("new");
                int total = rs.getInt("c");
                String con = rs.getString("con");
                // Print Data.
                System.out.println("  "+ id + "        |   " + total + "      |  " + con + " " );

            }
            System.out.println("--------------------------------------------------------");

            //STEP 6: Clean-up environment
            rs.close();
            stmnt.close();
            cnc.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmnt!=null)
                    stmnt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(cnc!=null)
                    cnc.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }

}//end
