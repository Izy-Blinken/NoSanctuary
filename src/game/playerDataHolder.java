package game;

//FUNCTION HELPER LANG 'TO PARA MAIPASA YUNG CURRENT USERNAME AND ID SA PANEL

public class playerDataHolder {

    private String username;
    private int playerID = -1;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
}
