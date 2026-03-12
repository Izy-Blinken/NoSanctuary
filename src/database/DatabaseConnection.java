package database;

import game.panel;
import java.sql.*;

public class DatabaseConn {

    panel gp;

    public DatabaseConn(panel gp) {
        this.gp = gp;
    }

    public int insert(String username, long totalDuration) {

        int playerID = -1;

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/NoSancDb", "root", "root");

            Statement st = conn.createStatement();
            try {
                st.executeUpdate("CREATE TABLE UserProgress(id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, player_name VARCHAR(50),completion_time FLOAT)");
            } catch (SQLException e) {
            }
            String sql = "INSERT INTO UserProgress(player_name, completion_time) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, username);
            ps.setLong(2, totalDuration);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                playerID = rs.getInt(1);
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerID;
    }

    public void update(int id, float dayC) {
        try{
            Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/NoSancDb", "root", "root");
            
            String sql = "UPDATE UserProgress SET completion_time = ? WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setFloat(1, dayC);
            pst.setInt(2, id);
            pst.executeUpdate();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public String showLeaderboard() {

        StringBuilder sb = new StringBuilder();
        int ranking = 1;

        try {
            Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/NoSancDb", "root", "root");

            String sql = "SELECT * FROM UserProgress ORDER BY completion_time ASC";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rst = ps.executeQuery();

            while (rst.next()) {

                String username = rst.getString("player_name");
                float totalDuration = rst.getFloat("completion_time");

                sb.append(ranking++).append(". ").append(username).append("                   ").append(totalDuration).append(" seconds\n");
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}

