import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;


/**
 * The main game class for the game Risk. This manages all operations including game initialization,
 * player turns, and command handling. The game ends when there is one player left standing.
 *
 * @author Nicolas Tuttle, Phuc La, Robell Gabriel, Jacob Schmidt
 */
public class Game{
    private final List<Player> activePlayers;
    private final Map<String, Continent> continents;
    private Player currentPlayer;
    private final String[] options = {"OK"};


    private ArrayList<GameView> gv ;

    /**
     * Constructor for the Game class. Initializes the model and player info
     * @author Phuc La and Robell Gabriel
     */
    public Game() {
        activePlayers = new LinkedList<>();
        continents = new HashMap<>();
        gv = new ArrayList<>();
    }





    /**
     * places a certain amount of armies into the designated territory.
     * Updates the action log in the view with what happened during place phase
     *
     * @param mt is a hashmap of type territory for key and integer for value, places the value into
     *           the corresponding key
     * @author Robell Gabriel and Phuc La
     */
    public void placePhase( HashMap<Territory,Integer> mt) {
        for (Territory terr : mt.keySet()){
            terr.addArmy(mt.get(terr));
            printLine(terr.getOwner().getName() + " has placed " + mt.get(terr) + " armies into " + terr.getName() +
                    " which now has " + terr.getNumArmies() + " armies");
        }
        updateView("Place");
    }


    /**
     * completes logic for moving armies from one territory to another and updates view accordingly
     *
     * @param i is an integer that represents how many armies to move from toRemove to toPlace
     * @param toRemove is the territory that has armies removed from it
     * @param toPlace is the territory that has armies placed into it
     *
     * @author Jacob Schmidt
     */
    public void movePhase(int i, Territory toRemove, Territory toPlace) {
        toRemove.removeArmy(i);
        toPlace.addArmy(i);
        currentPlayer = activePlayers.get((activePlayers.indexOf(currentPlayer) + 1) % activePlayers.size());
        updateView("Move");
        printLine("You have moved " + i + " armies from " + toRemove.getName() + " to " + toPlace.getName());
        printLine("Move phase is over");

    }

    /**
     * completes logic for one territory attacking another and updates the view accordingly
     *
     * @param armyNum and int that represents the amout of armies to attack with
     * @param attacking a territory that is attacking the territory deffending
     * @param defending a territory that is defending the territory attacking
     * @param defendArmy an int that represents the amount of armies to defend with
     * @return a boolean representing wether the attacker takes over the territory
     * @author Nicolas Tuttle
     */
    public boolean attack(int armyNum, Territory attacking, Territory defending, int defendArmy) {
        LinkedList<Integer> attackRolls = rollDice(armyNum);
        LinkedList<Integer> defendRolls = rollDice(defendArmy);

        // Sort to find highest pairs
        attackRolls.sort(Collections.reverseOrder());
        defendRolls.sort(Collections.reverseOrder());

        printLine(attacking.getOwner().getName() + " is attacking " + defending.getName() + " with " + attacking.getName() + "!");
        printLine("Attacker rolled " + attackRolls.size() + " dice: " + Arrays.toString(attackRolls.toArray()));
        printLine("Defender rolled " + defendRolls.size() + " dice: " + Arrays.toString(defendRolls.toArray()) + "\n");

        int attackLosses = 0;
        int defendLosses = 0;
        while (!attackRolls.isEmpty() && !defendRolls.isEmpty()) {
            int attack = attackRolls.removeFirst();
            int defend = defendRolls.removeFirst();

            // Compare pairs with ties going to defender
            if ((attack > defend)) {
                defendLosses++;
            } else {
                attackLosses++;
            }
        }

        if (defending.removeArmy(defendLosses)) {
            // Defending still has units left
            attacking.removeArmy(attackLosses);

            printLine("The attacking territory lost " + attackLosses + " unit(s)! It has " + attacking.getNumArmies() + " unit(s) left.");
            printLine("The defending territory lost " + defendLosses + " unit(s)! It has " + defending.getNumArmies() + " unit(s) left.\n");
            updateView("Attack");
            return false;

        } else {

            return true;
        }

    }

    /**
     * If an attack conquers a territory this method completes the logic to move armies into territory
     * @param attacking the terriotry whos owner conquers the territory edfending
     * @param defending the territory that is conquered by the owner of attacking
     * @param armyNum the number of armies to move from atacking to defending
     * @author Nicolas Tuttle
     */
    public void attackWon( Territory attacking, Territory defending, int armyNum) {
        Player defendingPlayer = defending.getOwner();

        defending.setPlayer(currentPlayer);
        currentPlayer.addTerritory(defending);
        defendingPlayer.removeTerritory(defending);
        attacking.removeArmy(armyNum);
        defending.setNumArmies(armyNum);
        printLine(attacking.getOwner().getName() + " has taken over " + defending.getName());
        //CHEAT CODE -> winner tester
            /*List<Territory> lst = new ArrayList<Territory>(defendingPlayer.getAllLandOwned());
            for(Territory ter : lst){
                defendingPlayer.removeTerritory(ter);
            }*/

        if (defendingPlayer.getAllLandOwned().size() == 0) {
            // Defender has no territories left, they are eliminated
            printLine(defendingPlayer.getName() + " has lost all their territories! They have been eliminated.\n");
            activePlayers.remove(defendingPlayer);
        }
        updateView("Attack");
    }

    /**
     * moves the turn over to the next player
     */
    public void done(){
        currentPlayer = activePlayers.get((activePlayers.indexOf(currentPlayer) + 1) % activePlayers.size());
        updateView("Done");
    }

    /**
     * Roll dice the specified number of times and return the list of results
     *
     * @param numDice The number of times to roll the dice
     * @return The result of the rolls
     * @author Nicolas Tuttle
     */
    private LinkedList<Integer> rollDice(int numDice) {
        LinkedList<Integer> rolls = new LinkedList<>();
        Random random = new Random();

        for (int i = 0; i < numDice; i++) {
            rolls.add(random.nextInt(6) + 1);
        }

        return rolls;
    }

    /**
     * Gets a territory by its full ID (continent + ID)
     *
     * @param id The ID corresponding to the desired territory
     * @return The Optional object containing the territory specified by the ID if it exists, an empty Optional otherwise
     * @author Nicolas Tuttle
     */
    public Optional<Territory> findTerritory(String id) {
        // Return empty if the ID is not 2 letters followed by digits (invalid ID)
        if (!(id.length() > 2 && continents.containsKey(id.substring(0, 2)) && id.substring(2).matches("^\\d+$")))
            return Optional.empty();

        Continent continent = continents.get(id.substring(0, 2));
        return (continent == null) ? Optional.empty() : continent.getTerritoryById(Integer.parseInt(id.substring(2)) - 1);
    }

    /**
     * Initializes the game
     * Calls upon user with Welcome and PlayerName panel for number of players and their names.
     * Creates all territories, continents then evenly adds territories to
     * players at random and randomly adds armies to territories.
     *
     * @author Robell Gabriel
     */
    public void initialize(int numPlayers, List<String> playerName) {

        ArrayList<Integer> handleUnevenTerr4; //list of Territory total for when numPLayer=4
        ArrayList<Integer> handleUnevenTerr5; //list of Territory total for when numPLayer=5
        Stack<String> territoryID = new Stack<>(); //stack of temporary territory IDs
        Stack<Integer> armyList2 = new Stack<>(); //stack of temporary armyList

        //Creating all territory lists
        ArrayList<Territory> NA = new ArrayList<>();
        ArrayList<Territory> EU = new ArrayList<>();
        ArrayList<Territory> AS = new ArrayList<>();
        ArrayList<Territory> SA = new ArrayList<>();
        ArrayList<Territory> AF = new ArrayList<>();
        ArrayList<Territory> AU = new ArrayList<>();

        //inputting all territories + adjacent territories into corresponding continent
        NA.add(new Territory("Alaska", "NA1", Arrays.asList("NA2", "NA6", "AS6")));
        NA.add(new Territory("Alberta (Western Canada)", "NA2", Arrays.asList("NA1", "NA6", "NA7", "NA9")));
        NA.add(new Territory("Central America", "NA3", Arrays.asList("NA4", "NA9", "SA4")));
        NA.add(new Territory("Eastern United States", "NA4", Arrays.asList("NA3", "NA7", "NA8", "NA9")));
        NA.add(new Territory("Greenland", "NA5", Arrays.asList("NA6", "NA7", "NA8", "EU2")));
        NA.add(new Territory("Northwest Territory", "NA6", Arrays.asList("NA1", "NA2", "NA5", "NA7")));
        NA.add(new Territory("Ontario (Central Canada)", "NA7", Arrays.asList("NA2", "NA4", "NA5", "NA6", "NA8", "NA9")));
        NA.add(new Territory("Quebec (Eastern Canada)", "NA8", Arrays.asList("NA4", "NA5", "NA7")));
        NA.add(new Territory("Western United States", "NA9", Arrays.asList("NA2", "NA3", "NA4", "NA7")));

        EU.add(new Territory("Great Britain (Great Britain & Ireland)", "EU1", Arrays.asList("EU2", "EU3", "EU4", "EU7")));
        EU.add(new Territory("Iceland", "EU2", Arrays.asList("EU1", "EU4", "NA5")));
        EU.add(new Territory("Northern Europe", "EU3", Arrays.asList("EU1", "EU4", "EU5", "EU6", "EU7")));
        EU.add(new Territory("Scandinavia", "EU4", Arrays.asList("EU1", "EU2", "EU3", "EU6")));
        EU.add(new Territory("Southern Europe", "EU5", Arrays.asList("EU3", "EU6", "EU7", "AF3", "AF5", "AS7")));
        EU.add(new Territory("Ukraine (Eastern Europe, Russia)", "EU6", Arrays.asList("EU3", "EU4", "EU5", "AS1", "AS7", "AS11")));
        EU.add(new Territory("Western Europe", "EU7", Arrays.asList("EU1", "EU3", "EU5", "AF5")));

        AS.add(new Territory("Afghanistan", "AS1", Arrays.asList("AS2", "AS3", "AS7", "AS11", "EU6")));
        AS.add(new Territory("China", "AS2", Arrays.asList("AS1", "AS3", "AS8", "AS9", "AS10", "AS11")));
        AS.add(new Territory("India (Hindustan)", "AS3", Arrays.asList("AS1", "AS2", "AS7", "AS9")));
        AS.add(new Territory("Irkutsk", "AS4", Arrays.asList("AS6", "AS8", "AS10", "AS12")));
        AS.add(new Territory("Japan", "AS5", Arrays.asList("AS6", "AS8")));
        AS.add(new Territory("Kamchatka", "AS6", Arrays.asList("AS4", "AS5", "AS8", "AS12", "NA1")));
        AS.add(new Territory("Middle East", "AS7", Arrays.asList("AS1", "AS3", "EU5", "EU6", "AF2", "AF3")));
        AS.add(new Territory("Mongolia", "AS8", Arrays.asList("AS2", "AS4", "AS5", "AS6", "AS10")));
        AS.add(new Territory("Siam (Southeast Asia)", "AS9", Arrays.asList("AS2", "AS3", "AU2")));
        AS.add(new Territory("Siberia", "AS10", Arrays.asList("AS2", "AS4", "AS8", "AS11", "AS12")));
        AS.add(new Territory("Ural", "AS11", Arrays.asList("AS1", "AS2", "AS10", "EU6")));
        AS.add(new Territory("Yakutsk", "AS12", Arrays.asList("AS4", "AS6", "AS10")));

        SA.add(new Territory("Argentina", "SA1", Arrays.asList("SA2", "SA3")));
        SA.add(new Territory("Brazil", "SA2", Arrays.asList("SA1", "SA3", "SA4", "AF5")));
        SA.add(new Territory("Peru", "SA3", Arrays.asList("SA1", "SA2", "SA4")));
        SA.add(new Territory("Venezuela", "SA4", Arrays.asList("SA2", "SA3", "NA3")));

        AF.add(new Territory("Congo (Central Africa)", "AF1", Arrays.asList("AF2", "AF5", "AF6")));
        AF.add(new Territory("East Africa", "AF2", Arrays.asList("AF1", "AF3", "AF4", "AF5", "AF6", "AS7")));
        AF.add(new Territory("Egypt", "AF3", Arrays.asList("AF2", "AF5", "EU5", "AS7")));
        AF.add(new Territory("Madagascar", "AF4", Arrays.asList("AF2", "AF6")));
        AF.add(new Territory("North Africa", "AF5", Arrays.asList("AF1", "AF2", "AF3", "EU5", "EU7", "SA2")));
        AF.add(new Territory("South Africa", "AF6", Arrays.asList("AF1", "AF2", "AF4")));

        AU.add(new Territory("Eastern Australia", "AU1", Arrays.asList("AU3", "AU4")));
        AU.add(new Territory("Indonesia", "AU2", Arrays.asList("AU3", "AU4", "AS9")));
        AU.add(new Territory("New Guinea", "AU3", Arrays.asList("AU1", "AU2", "AU4")));
        AU.add(new Territory("Western Australia", "AU4", Arrays.asList("AU1", "AU2", "AU3")));

        //creating continents and adding corresponding territory lists
        continents.put("NA", new Continent("North America", NA, 5));
        continents.put("EU", new Continent("Europe", EU, 5));
        continents.put("AS", new Continent("Asia", AS, 7));
        continents.put("SA", new Continent("South America", SA, 2));
        continents.put("AF", new Continent("Africa", AF, 3));
        continents.put("AU", new Continent("Australia", AU, 2));

        //creates list of temporary Ids for each territory which is shuffled to player's get random territory
        for (String id : continents.keySet()) {
            for (int j = 1; j <= continents.get(id).getTerritoriesSize(); j++) {
                territoryID.push("" + id + j);
            }
        }
        Collections.shuffle(territoryID);

        //insert then shuffle list of Territory total for when there are 4 or 5 players
        handleUnevenTerr4 = new ArrayList<>(Arrays.asList(10, 10, 11, 11));
        handleUnevenTerr5 = new ArrayList<>(Arrays.asList(9, 9, 8, 8, 8));
        Collections.shuffle(handleUnevenTerr4);
        Collections.shuffle(handleUnevenTerr5);

        //Player name panel asks names then adds player's name and their territories with random amount of armies each
        for (int i = 0; i < numPlayers; i++) {
            activePlayers.add(new Player(playerName.get(i)));
            switch (numPlayers) {
                case 2:
                    initializePlayer(50, 21, i, territoryID, armyList2);
                    break;
                case 3:
                    initializePlayer(35, 14, i, territoryID, armyList2);
                    break;
                case 4:
                    //randomly distribute uneven amount of territories
                    initializePlayer(30, handleUnevenTerr4.get(i), i, territoryID, armyList2);
                    break;
                case 5:
                    //randomly distribute uneven amount of territories
                    initializePlayer(25, handleUnevenTerr5.get(i), i, territoryID, armyList2);
                    break;
                default:
                    initializePlayer(20, 7, i, territoryID, armyList2);
                    break;
            }
            //add random amount of armies to each territory
            for (Territory territory : activePlayers.get(i).getAllLandOwned()) {
                territory.addArmy(armyList2.pop());
            }
        }
        currentPlayer = activePlayers.get(0);
    }

    /**
     * Initializes the player
     * Randomly adds territories to player as well as a random amount of armies to territories.
     * All of which are evenly distributed between players.
     *
     * @param numArmies   int total number of armies for player
     * @param numOfTerr   int total number of territories for player
     * @param i           int used to loop between players
     * @param armyList2   Stack of randomly distributed armies
     * @param territoryID Stack of valid territory ids
     * @author Robell Gabriel
     */
    private void initializePlayer(int numArmies, int numOfTerr, int i, Stack<String> territoryID, Stack<Integer> armyList2) {
        int[] armyList = new int[numOfTerr];
        numArmies -= numOfTerr;
        for (int z = 0; z < numOfTerr; z++) {
            //add player to random territory
            findTerritory(territoryID.peek()).orElseThrow().setPlayer(activePlayers.get(i));
            //add random territory to player
            activePlayers.get(i).addTerritory(findTerritory(territoryID.pop()).orElseThrow());
            //generates a list of random numbers that all add up to
            //total number of armies player can own and total amount of random
            //numbers are equal to number of territories they can own
            if (z < (numOfTerr - 1)) {
                armyList[z] = (int) (Math.random() * numArmies);
            }
        }
        armyList[numOfTerr - 1] = numArmies;
        Arrays.sort(armyList);
        for (int z = numOfTerr - 1; z > 0; z--) {
            armyList[z] -= armyList[z - 1];
        }
        for (int z = 0; z < numOfTerr; z++) {
            armyList[z]++;
        }
        //add armyList to stack so it can be added to territories
        for (int army : armyList) {
            armyList2.push(army);
        }
    }
    private void updateView(String code){
        for (GameView v : gv){
            v.updateView(code,continents,currentPlayer,activePlayers);
        }
    }
    private void printLine(String message) {
        for (GameView v : gv){
            v.printLine(message);
        }
    }

    public  Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Map<String, Continent> getContinents() {
        return continents;
    }
    public List<Player> getActivePlayers() {
        return activePlayers;
    }
    public void addGameView(GameView view){
        gv.add(view);
    }
}