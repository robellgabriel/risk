import java.util.*;
public class Player {
    /**
     * This class is part of the game "RISK"
     *
     * This class hold the command to create an instance of player as well as
     * return the player name and the list of territories that the player owned
     *
     * @author Phuc La
     */
    private String name;
    private List<Territory> ownedlands;

    /**
     * Constructor for the player that will play the game
     * @param name is the name that the player will use in the game
     */
    public Player(String name) {
        this.name = name;
        ownedlands = new LinkedList<>();
    }

    /**
     * Get the player name
     * @return a string that is the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Add a new territory that the player conquered into the list of lands that the
     * player owned
     * @param ter is the new territory that will be added to the list
     */
    public void addTerritory(Territory ter){
        ownedlands.add(ter);
    }

    /**
     * Remove a territory from the player's owned lands
     * @param ter The territory to remove
     * @return True if successful, false otherwise
     */
    public boolean removeTerritory(Territory ter) {
        return ownedlands.remove(ter);
    }

    /**
     * Return the list of lands owned by the player so far in the game
     * @return the list that contained all the land that the player owned at the moment
     */
    public List<Territory> getAllLandOwned(){
        return ownedlands;
    }
}
