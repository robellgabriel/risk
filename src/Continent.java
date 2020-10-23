import java.util.*;

/**
 * This class represents a continent in the game of Risk. It gives operations to find the conqueror of a
 * continent, add new territories, and get a String representation of the object.
 *
 * @author Nicolas Tuttle
 */
public class Continent {
    private final String name, id;
    private final List<Territory> territories;

    public final int BONUS_ARMIES;

    /**
     * Constructor for class Continent. Initializes the object with a name and empty territory list.
     * @param name The continent's name
     * @param id The continent's ID
     */
    public Continent(String name, String id, int bonusArmies) {
        this.name = name;
        this.id = id;
        this.territories = new ArrayList<>();
        this.BONUS_ARMIES = bonusArmies;
    }

    /**
     * Constructor for class Continent. Initializes the object with a name and the specified territory collection
     * @param name The continent's name
     * @param id The continent's ID
     * @param territories The collection of continents to be used to initialize the object
     */
    public Continent(String name, String id, Collection<Territory> territories, int bonusArmies) {
        this.name = name;
        this.id = id;
        this.territories = new ArrayList<>(territories);
        this.BONUS_ARMIES = bonusArmies;
    }

    /**
     * Adds a territory to the continent.
     * @param newTerritory The new territory to be added to the continent
     */
    public void addTerritory(Territory newTerritory) {
        territories.add(newTerritory);
    }

    /**
     * Adds a collection of territories to the continent.
     * @param territories The collection of territories to be added to the continent.
     */
    public void addTerritory(Collection<Territory> territories) {
        this.territories.addAll(territories);
    }

    /**
     * Get an Optional object containing the player that has conquered all territories on a continent
     * @return An Optional object containing the player object corresponding to the conqueror, or an empty Optional if
     * the continent is not conquered
     */
    public Optional<Player> getConqueror() {
        HashSet<Player> conquerors = new HashSet<>();
        for (Territory territory: territories) {
            conquerors.add(territory.getOwner());
        }

        // Return the conqueror if all territories have the same owner, or an empty optional object otherwise
        return (conquerors.size() == 1) ? Optional.of(conquerors.iterator().next()) : Optional.empty();
    }

    /**
     * Find the territory associated with a given ID
     * @param id The requested territory's ID
     * @return The territory associated with the ID
     */
    public Optional<Territory> getTerritoryById(int id) {
        try {
            return Optional.of(territories.get(id));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }
    /**
     * Get the amount of territories in continent
     * @return an int that is the size of territories list
     */
    public int getTerritoriesSize(){
        return territories.size();
    }

    /**
     * Get the short form ID for this continent
     * @return The continent's ID
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns a String representation of this continent. The String representation consists of the
     * continent's name followed by each territory contained within it and its respective owner.
     * Different territories are separated by a new line ("\n")
     * @return A string representation of the continent
     */
    @Override
    public String toString() {
        ArrayList<String> output = new ArrayList<>(Collections.singletonList(name + ":"));
        for (Territory territory: territories) {
            output.add(territory.toString());
        }
        return String.join("\n", output);
    }
}
