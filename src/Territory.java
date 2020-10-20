import java.util.*;

/** the class Territory represents a territory in the risk game identifiable by a String ID and Name that can be read.
 *  The Territory can have armies represented by integers be removed,added or read. Lastly it has a set containing all
 *  the ids of adjacent territories.
 *
 * @author Jacob Schmidt
 */
public class Territory {

    private Player owner; // owner of the country
    private int numArmies = 0; //amount of armies contained in the country
    private final String name; //name to identify country by
    private Set<String> setOfAdjacents; //set of ids that belong to other countries adjacent to this one

    /**
     * constructor for territory.
     * @param name longer string used to identify territories
     * @param setOfAdjacents a set containing the ids of adjacent territories
     */
    public Territory(String name, Set<String> setOfAdjacents) {
        this.name = name;
        this.setOfAdjacents = setOfAdjacents;
    }

    /**
     * function that increments the amount of armies in the territory by numAdd as long as the number does
     * not surpass the itneger canAdd
     * @param numAdd integer to add to numArmies
     * @param canAdd integer that limits how much can be added to numArmies
     * @return a boolean representing whether the method was succesfull
     */
    public boolean addArmy(int numAdd, int canAdd ){
        if (numAdd > canAdd){
            return false;
        }
        numArmies += numAdd;
        return true;
    }

    /**
     * function that decrements numArmies in the territory by numRemove as long as the resulting int is
     * larger than 0
     * @param numRemove integer that represent the amount to decrement numArmies by
     * @return a boolean representing whether the method was succesfull
     */
    public boolean removeArmy(int numRemove){
        if (numArmies - numRemove <= 0){
            return false;
        }
        numArmies -= numRemove;
        return true;
    }

    /**
     * a method to read the integer numArmies
     * @return an integer representing numArmies
     */
    public int getNumArmies() {
        return numArmies;
    }

    /**
     * a method used to set owner to an existing player
     * @param owner the player that will be set to owner
     */
    public void setPlayer (Player owner){
        this.owner = owner;
    }

    /**
     * a method to read who the owner is
     * @return a Player, owner
     */
    public Player getOwner() {
        return owner;
    }
    /**
     * a method to read the name of the territory
     * @return a string representation of the name
     */
    public String getName() {
        return name;
    }

    /**
     * a method that checks to see if a String toCheck is contained within the
     * setOFAdjacents set.
     * @param toCheck String that setOfAdjacents is checked for to find a match
     * @return a boolean representing wether a match was found or not.
     */
    public boolean isAdjacentTo(String toCheck){
        if (setOfAdjacents.isEmpty()){
            return false;
        }
        for (String item : setOfAdjacents){
            if (item.equals(toCheck)){
                return true;
            }
        }
        return false;
    }



}

