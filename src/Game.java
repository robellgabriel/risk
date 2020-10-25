import java.util.*;

/**
 * The main game class for the game Risk. This manages all operations including game initialization,
 * player turns, and command handling. The game ends when there is one player left standing.
 *
 * @author Nicolas Tuttle, Phuc La, Robell Gabriel, Jacob Schmidt
 */
public class Game {
    private final List<Player> activePlayers;
    private final Map<String, Continent> continents;
    private final Parser parser;
    public Game() {
        activePlayers = new LinkedList<>();
        continents = new HashMap<>();
        parser = new Parser();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }

    /**
     *This is the main body of the game, where the players will play the game
     *
     * @author Phuc La
     */
    public void play() {
        //Initializer to get number of players, player's name, distribution of territory and armies
        initialize();
        Player currentplayer = activePlayers.get(0);
        //main game loop where the game happens
        System.out.println("Here are the commands for the game: ");
        parser.showCommands();
        while(activePlayers.size()>1){
            boolean finished = false;
            System.out.println("\n"+currentplayer.getName()+"'s turn: ");
            placePhase(currentplayer);
            while(!finished){
                Command command = parser.getCommand();
                finished = processCommand(command, currentplayer);
            }
            currentplayer = activePlayers.get((activePlayers.indexOf(currentplayer) +1 ) % activePlayers.size());
        }
        System.out.println("Congratulations "+currentplayer.getName()+". You are the winner!!!");
    }

    /**
     * This methods is called during the player's turn to process their command after the
     * first phase of the game is done
     * @param command is the command that decide the action of the player in the game after phase 1
     * @param currplayer is the current player that is playing in this turn
     * @return false if the player is not done with their turn, true other wise
     *
     * @author Phuc La
     */
    private boolean processCommand(Command command, Player currplayer){
        if(command.isUnknown()){
            System.out.println("Not a valid command!");
        }
        String commandWord = command.getCommandWord().toString();
        switch (commandWord) {
            case "help":
                System.out.println("Your command words are: ");
                printHelp();
                break;
            case "attack":
                System.out.println("You are in attacking phase: ");
                attack(currplayer);
                break;
            case "move":
                System.out.println("You are in moving phase.");
                if (movePhase(currplayer)) {
                    System.out.println("You finished moving, moving on to next player.");
                    return true;
                }
                break;
            case "done":
                System.out.println("Your turn is now finished. Move on to the next player");
                return true;
            case "map":
                System.out.println("This is the map of the world: ");
                printMap();
                break;
            case "quit":
                System.out.println("Thanks for playing!");
                System.exit(0);
        }
        return false;
    }

    /**
     * Calculates the number of armies the player will be getting then calls a method that allows
     * the player to distribute said number of armies throughout his owned territories
     * @param currPlayer the player who's turn it is
     * @author Jacob Schmidt
     */
    public void placePhase(Player currPlayer) {
        int armiesRemaining = 3 + (currPlayer.getAllLandOwned().size()-9)/3;
        for (Continent continent : continents.values()) {
            Optional<Player> conqueror = continent.getConqueror();
            if (conqueror.isPresent() && conqueror.get().equals(currPlayer)) armiesRemaining += continent.BONUS_ARMIES;
        }

        System.out.println("Place phase initiated for " + currPlayer.getName());
        System.out.println("You have been given " +armiesRemaining+ " armies");
        while(armiesRemaining > 0){
            System.out.println("Please enter the ID of the Territory you wish to place armies into. There are " +armiesRemaining+ " left to place");
            Territory toPlace = promptForOwnedTerritory(currPlayer,1);
            if(toPlace == null) {
                System.out.println("You cant cancel place phase");
                continue;
            }
            System.out.println("Please choose the number of armies you would like to place");
            int i = promptForInt(armiesRemaining);
            if(i < 0) {
                System.out.println("You cant cancel place phase");
                continue;
            }
            toPlace.addArmy(i,armiesRemaining);
            armiesRemaining -= i;
            System.out.println("Congrats you have placed " + i + " armies into " + toPlace.getName());
        }
        System.out.println("Place phase is over.");
        System.out.println("Here are your commands\n");
        printHelp();
        System.out.println("\nIf you change your mind and would like to go back in attack/move. \nUse the cancel command before choosing the territories to attack/move");
        System.out.println("\nIf you would like to end your turn early, use the done command.");
    }

    /**
     * lets the player move armies from one territory to another as long as they are adjacent to each other and
     * the number of armies in territories allow it. also allows the player to call the map function or cancel
     * the move phase
     * @param currPlayer player who's turn it currently is
     * @return a boolean value that is false if movePhase is canceled or true if movePhase is successful
     * @author Jacob Schmidt
     */
    public boolean movePhase(Player currPlayer) {
        System.out.println("Move phase initiated for " + currPlayer.getName());
        System.out.println("Please enter a territory's ID that u wish to move armies from");
        Territory toRemove = promptForOwnedTerritory(currPlayer,2);
        if(toRemove == null){
            System.out.println("Your commands:");
            printHelp();
            return false;
        }
        System.out.println("Here are all adjacent territories to " +toRemove.getName()+ " by ID: "+toRemove.getAdjacentList());
        System.out.println("Please enter a territory's ID that u wish to move armies To");
        Territory toPlace = promptForAdjacentTerritory(toRemove);
        if(toPlace == null){
            System.out.println("Your commands:");
            printHelp();
            return false;
        }
        System.out.println("Please enter the number of armies you wish to move");
        int i = promptForInt(toRemove.getNumArmies()-1);
        if (i < 0) return false;

        toRemove.removeArmy(i);
        toPlace.addArmy(i,i);
        System.out.println("You have moved " + i + " armies from " + toRemove.getName() + " to " + toPlace.getName());
        System.out.println("Move phase is over");
        return true;
    }

    /**
     * Initiates an attack. Contains all the logic to select an attacking territory and handle results of the attack
     * @param activePlayer The current player
     * @author Nicolas Tuttle
     */
    public void attack(Player activePlayer) {
        // Returning null corresponds to player entering "cancel"
        System.out.println("You have chosen to attack! Enter the ID of the territory you wish to attack from:");
        Territory attacking = promptForOwnedTerritory(activePlayer,2);
        if (attacking == null) {
            System.out.println("Your commands:");
            printHelp();
            return;
        }

        System.out.println("Here are all adjacent territories to " +attacking.getName()+ " by ID: "+attacking.getAdjacentList());
        System.out.println("Select the territory you wish to attack:");
        Territory defending = promptForAdjacentTerritory(attacking);
        if (defending == null) {
            System.out.println("Your commands:");
            printHelp();
            return;
        }
        while (defending.getOwner() == attacking.getOwner()) {
            System.out.println("You cannot attack a territory you own! Select a new territory:");
            defending = promptForAdjacentTerritory(attacking);
            if (defending == null) return;
        }

        System.out.println("Battle! Player " + attacking.getOwner().getName() + " is attacking " + defending.getName() + "!");
        LinkedList<Integer> attackRolls = rollDice(promptForDice(attacking, true));
        LinkedList<Integer> defendRolls = rollDice(promptForDice(defending, false));

        // Sort to find highest pairs
        attackRolls.sort(Collections.reverseOrder());
        defendRolls.sort(Collections.reverseOrder());

        System.out.println("Attacker rolled " + attackRolls.size() + " dice: "+ Arrays.toString(attackRolls.toArray()));
        System.out.println("Defender rolled " + defendRolls.size() + " dice: "+ Arrays.toString(defendRolls.toArray()));

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

            System.out.println("The attacking territory lost " + attackLosses + " unit(s)! It has " + attacking.getNumArmies() + " unit(s) left.");
            System.out.println("The defending territory lost " + defendLosses + " unit(s)! It has " + defending.getNumArmies() + " unit(s) left.");

        } else {
            // Territory conquered! Transfer ownership and move one unit over to defend
            System.out.println("The defending territory lost all units and was conquered by " + attacking.getOwner().getName() + "!");

            Player defendingPlayer = defending.getOwner();

            defending.setPlayer(activePlayer);
            activePlayer.addTerritory(defending);
            defendingPlayer.removeTerritory(defending);

            attacking.removeArmy(attackRolls.size());
            defending.setNumArmies(attackRolls.size() - attackLosses);

            if (defendingPlayer.getAllLandOwned().size() == 0) {
                // Defender has no territories left, they are eliminated
                System.out.println(defendingPlayer.getName() + " has lost all their territories! They have been eliminated.");
                activePlayers.remove(defendingPlayer);
            }
        }
        System.out.println("\nAttack phase is over.");
        System.out.println("Here are your commands:");
        printHelp();
    }

    /**
     * Prompts the user for a territory. Checks whether owner owns the territory
     * If the user enters "cancel", the function returns null.
     * @param owner Will assert this player as the owner of the territory
     * @param lim The minimum number of armies to have on a territory
     * @return The selected territory, or null if the player entered "cancel"
     * @author Nicolas Tuttle
     */
    private Territory promptForOwnedTerritory(Player owner, int lim) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String territoryID = sc.next().toUpperCase();
            CommandWord command = checkForCommand(territoryID);
            if (command == CommandWord.CANCEL) return null;
            // Known command resets prompt
            if (command != CommandWord.UNKNOWN) continue;

            if (findTerritory(territoryID).isEmpty()) {
                System.out.println("That is not a valid territory ID. Please enter a new one:");
                continue;
            }

            Territory territory = findTerritory(territoryID).get();
            if (!territory.getOwner().equals(owner)) {
                System.out.println("You do not own this territory. Please enter a new one:");
                continue;
            }

            if (territory.getNumArmies() < lim) {
                System.out.println("This territory does not have enough units. Please enter a new one:");
                continue;
            }

            return territory;
        }
    }

    /**
     * prompts player for an integer, will check if input is a command, an integer or a invalid input
     * will loop until valid input is entered
     * @param limit the entered integer must be smaller that the integer limit
     * @return returns a valid integer input by the player
     * @author Jacob Schmidt
     */
    private int promptForInt(int limit) {
        Scanner sc = new Scanner(System.in);
        int temp;
        while (true) {
            System.out.print("> ");
            String testInt = sc.next().toUpperCase();
            CommandWord command = checkForCommand(testInt);
            if (command == CommandWord.CANCEL) return -1;
            if (command == CommandWord.MAP) continue;
            // Known command resets prompt
            if (command != CommandWord.UNKNOWN) continue;
            try{
                temp = Integer.parseInt(testInt);
            }catch (NumberFormatException e){
                System.out.println("This is not a number or a command.");
                continue;}

            if (temp <= limit && temp > 0) { return temp;}
            else {
                System.out.println("This is not a valid number");
            }


        }
    }

    /**
     * Prompts the user for a territory. Checks whether it is adjacent to adjacent
     * If the user enters "cancel", the function returns null.
     * @param adjacent Will assert the found territory is adjacent to this territory
     * @return The selected territory, or null if the user entered "cancel"
     * @author Nicolas Tuttle
     */
    private Territory promptForAdjacentTerritory(Territory adjacent) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String territoryID = sc.next().toUpperCase();
            CommandWord command = checkForCommand(territoryID);
            if (command == CommandWord.CANCEL) return null;
            // Known command resets prompt
            if (command != CommandWord.UNKNOWN) continue;

            if (findTerritory(territoryID).isEmpty()) {
                System.out.println("That is not a valid territory ID. Please enter a new one:");
                continue;
            }

            if (!adjacent.isAdjacentTo(territoryID)) {
                System.out.println("This territory is not adjacent to the one you selected. Please enter a new one:");
                continue;
            }

            return findTerritory(territoryID).get();
        }
    }

    /**
     * Checks the user input for a command that resets a prompt and prints an output if applicable
     * @param input The user input
     * @return The corresponding CommandWord if it exists, CommandWord.UNKNOWN otherwise
     * @author Nicolas Tuttle
     */
    private CommandWord checkForCommand(String input) {
        if (input.equals(CommandWord.CANCEL.toString().toUpperCase())) {
            return CommandWord.CANCEL;
        } else if (input.equals(CommandWord.MAP.toString().toUpperCase())) {
            System.out.println("This is the map of the world: ");
            printMap();
            return CommandWord.MAP;
        } else if (input.equals(CommandWord.HELP.toString().toUpperCase())) {
            System.out.println("Your command words are: ");
            printHelp();
            return CommandWord.HELP;
        }
        return CommandWord.UNKNOWN;
    }

    /**
     * Prompts the user for a number of dice to roll. Checks whether it is within the allowed bounds.
     * @param territory The territory the dice are rolled for
     * @param isAttacking True if asking for attacker's dice count, false otherwise
     * @return The number of dice the player wishes to roll
     * @author Nicolas Tuttle
     */
    private int promptForDice(Territory territory, boolean isAttacking) {
        System.out.print((isAttacking) ? "Attacker: " : "Defender: ");
        int maxDice = Math.min(territory.getNumArmies() - (isAttacking ? 1 : 0), isAttacking ? 3 : 2);
        while (true) {
            System.out.println("Select the amount of dice you wish to roll:");
            int numDice = promptForInt(maxDice);

            if (numDice < 0){
                System.out.println("You cannot cancel during a dice roll.");
                continue;
            }

            return numDice;
        }
    }

    /**
     * Roll dice the specified number of times and return the list of results
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
     * @param id The ID corresponding to the desired territory
     * @return The Optional object containing the territory specified by the ID if it exists, an empty Optional otherwise
     * @author Nicolas Tuttle
     */
    private Optional<Territory> findTerritory(String id) {
        if (!(id.length() > 2 && continents.containsKey(id.substring(0, 2)) && isNumeric(id.substring(2)))) return Optional.empty();
        Continent continent = continents.get(id.substring(0, 2));
        return (continent == null) ? Optional.empty() : continent.getTerritoryById(Integer.parseInt(id.substring(2))-1);
    }

    /**
     * Checks whether a string can be parsed as an int
     * @param str The string to be checked
     * @return True if the string can be parsed as int, false otherwise
     * @author Nicolas Tuttle
     */
    private boolean isNumeric(String str) {
        return str.matches("^\\d+$");
    }

    /**
     * This method initializes the game, it creates all territories, continents, and player's user inputted.
     * Then evenly adds territories to players at random and randomly adds armies to territories.
     *
     * @author Robell Gabriel
     */
    private void initialize() {

        //scanner to read user's inputs
        Scanner in = new Scanner(System.in);

        int numPlayers; //total number of players
        String playerName; //each player name
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

        //print out opening message of game
        System.out.println("Welcome to the game Risk!");
        System.out.println("Risk is a strategy based game for 2-6 players");
        System.out.println();
        System.out.println("Enter number of players: ");

        //loop for number of players till input is valid
        while (true) {
            try {
                //ask user number of players
                System.out.print("> ");
                numPlayers = in.nextInt();
                //loop if input out of range
                if (numPlayers < 2 || numPlayers > 6) {
                    System.out.println("There can only be 2-6 players!");
                    continue;
                }
                break;
                //check for non-integer input
            } catch (InputMismatchException e) {
                System.out.println("Not a number!");
            }
            in.nextLine();
        }

        //creates list of temporary Ids for each territory which is shuffled to player's get random territory
        for (String id : continents.keySet()) {
            for (int j = 1; j <= continents.get(id).getTerritoriesSize(); j++) {
                territoryID.push("" + id + j);
            }
        }
        Collections.shuffle(territoryID);

        //insert then shuffle list of Territory total for when there are 4 or 5 players
        handleUnevenTerr4 = new ArrayList<>(Arrays.asList(10,10,11,11));
        handleUnevenTerr5 = new ArrayList<>(Arrays.asList(9,9,8,8,8));
        Collections.shuffle(handleUnevenTerr4);
        Collections.shuffle(handleUnevenTerr5);

        //now adds player's name and their territories with 1 army in each
        for (int i = 0; i < numPlayers; i++) {
            System.out.println("Enter Player " + (i + 1) + " name: ");
            System.out.print("> ");
            playerName = in.next();
            activePlayers.add(new Player(playerName));
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
            for (Territory territory : activePlayers.get(i).getAllLandOwned()){
                territory.addArmy(armyList2.pop(), 100);
            }
        }
    }

    /**
     * This method initializes the player randomly adds territories to player as well as a random amount of armies to territories.
     * All of which are evenly distributed between players.
     *
     * @author Robell Gabriel
     *
     * @param numArmies int total number of armies for player
     * @param numOfTerr int total number of territories for player
     * @param i int used to loop between players
     * @param armyList2 Stack of randomly distributed armies
     * @param territoryID Stack of valid territory ids
     */
    public void initializePlayer(int numArmies, int numOfTerr, int i, Stack<String> territoryID, Stack<Integer> armyList2) {
        int [] armyList = new int[numOfTerr];
        numArmies-=numOfTerr;
        for (int z = 0; z < numOfTerr; z++) {
            //add player to random territory
            findTerritory(territoryID.peek()).orElseThrow().setPlayer(activePlayers.get(i));
            //add random territory to player
            activePlayers.get(i).addTerritory(findTerritory(territoryID.pop()).orElseThrow());
            //generates a list of random numbers that all add up to
            //total number of armies player can own and total amount of random
            //numbers are equal to number of territories they can own
            if (z < (numOfTerr-1)) {
                armyList[z] = (int)(Math.random()*numArmies);
            }
        }
        armyList[numOfTerr-1] = numArmies;
        Arrays.sort(armyList);
        for (int z = numOfTerr-1; z > 0; z--){
            armyList[z] -= armyList[z-1];
        }
        for (int z = 0; z < numOfTerr; z++){
            armyList[z]++;
        }
        //add armyList to stack so it can be added to territories
        for (int army : armyList){
            armyList2.push(army);
        }
    }

    /**
     * This method shows a map of the current state of the game as well
     * as the total player count and the amount of territories they own.
     * ALso includes Continents and their territories with
     * players that own them and their army count
     *
     * @author Robell Gabriel
     */
    private void printMap() {
        System.out.println("\nPlayers still in-game:");
        for (Player player : activePlayers){
            if (player.getAllLandOwned().size() == 1){
                System.out.print(player.getName() + " owns "+player.getAllLandOwned().size()+" territory\n");
            }else{
                System.out.print(player.getName() + " owns "+player.getAllLandOwned().size()+" territories\n");
            }
        }
        System.out.println();
        for (String id : continents.keySet()) {
            System.out.println(continents.get(id).toString()+"\n-----------------------------------------------");
        }
    }

    /**
     * This method prints a list of all commands in game and players still in-game
     *
     * @author Robell Gabriel
     */
    private void printHelp() {
        parser.showCommands();
    }
}