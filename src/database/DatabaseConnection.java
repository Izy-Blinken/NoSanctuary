/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

/**
 *
 * @author ADMIN
 */
import java.sql.*;
import java.util.*;

public class DatabaseConnection {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int score = 0;
        int ranking = 1;
        String sql;
        try {
            //Database connection
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/NoSancDB");

            Statement st = con.createStatement();

            //paggawa lang naman ng table reh, pag nakagawa kana pwede munang tanggalin, hindi kasi supported ng apache derby ung may "IF NOT EXISTS" na keyword
            //irerestart mo pa ung netbeans para lang malagay na sa table folder ung table na gawa mo
            
            /*st.executeUpdate("CREATE TABLE UserProgress(id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,\n"
                    + "    player_name VARCHAR(50),\n"
                    + "    final_score INT, \n"
                    + "    completion_time BIGINT)");*/
            
            //PWEDE MONG I-UNCOMMENT UNG executeUpdate kung ayaw mong mano-mano ung paggawa ng table, pero ung sa part ng database, dapat ata mano-mano mo syang gawin
            //di kasi nagana sakin ung katulad nung ginawa ko sa executeUpdate
            
            
            //Testing lang ito para sa paggawa ng leaderboard sa game
            String[] questions = {
                "1.What is HTML?",
                "2.What is CSS?",
                "3.Who created c?",
                "4.Who modified c?",
                "5.Who created java?"
            };
            String[][] choices = {
                {"A. Hypertext Markup Language", "B. How to make lumpia", "C. Wally b.", "D."},
                {"A. Cascading Style Sheets", "B. Computer Style Syntax", "C. Alvin Aragon", "D."},
                {"A. Dennis Ritchie", "B. James Gosling", "C. Norman Mangusin", "D."},
                {"A. Bjarne Stroustrup", "B. Dennis Ritchie", "C. Enchong Dee", "D."},
                {"A. James Gosling", "B. Dennis Ritchie", "C. Vic sotto", "D."}
            };
            String[] answers = {
                "A", "A", "A", "A", "A"
            };

            System.out.println("Simple quiz by Mary Sharmain Salamat");
            System.out.print("Username: ");
            String username = input.nextLine();

            System.out.println("Select haha");
            System.out.println("1. PLAY");
            System.out.println("2. View leaderboard");
            int choice = input.nextInt();
            input.nextLine();
            switch (choice) {
                case 1:
                    long startTime = System.currentTimeMillis();


                    for (int i = 0; i < questions.length; i++) {

                        System.out.println(questions[i]);

                        for (int j = 0; j < choices[i].length; j++) {
                            System.out.println(choices[i][j]);
                        }

                        System.out.print("Enter answer: ");
                        String answer = input.nextLine();

                        if (answer.equalsIgnoreCase(answers[i])) {
                            score++;
                        }
                    }
                    System.out.println("Your score: " + score);
                    
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime; 
                    
                    sql = ("INSERT INTO UserProgress(player_name,final_score, completion_time) VALUES ('" + username + "', " + score + ", "+ duration +")");
                    st.executeUpdate(sql);
                    System.out.println("Progress is saved!");
                    break;
                case 2:
                    sql = "SELECT * FROM UserProgress ORDER BY final_score DESC, completion_time ASC FETCH FIRST 10 ROWS ONLY";
                    ResultSet rs = st.executeQuery(sql);
                    System.out.println("Leaderboard");
                    while (rs.next()) {
                        String name = rs.getString("player_name");
                        score = rs.getInt("final_score");
                        long time = rs.getLong("completion_time");
                        double finalTime = time/1000.0;

                        System.out.println(ranking + " | " + "Username: " + name + " | Score: " + score + " | Completion time: " + finalTime);
                        ranking++;
                    }

                    rs.close();
                    break;
            }
            con.close();
        } catch (SQLException E) {
            E.printStackTrace();
        }
    }

}
