import java.util.*;

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
     * Author @Phuc La
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
            System.out.println(currentplayer.getName()+"'s turn: ");
            placePhase(currentplayer);
            while(!finished){
                Command command = parser.getCommand();
                finished = processCommand(command, currentplayer);
            }
            currentplayer = activePlayers.get((activePlayers.indexOf(currentplayer) +1 ) % activePlayers.size());
        }
        System.out.println("Congratulations "+currentplayer+". You are the winner!!!");
    }

    /**
     * This methods is called during the player's turn to process their command after the
     * first phase of the game is done
     * @param command is the command that decide the action of the player in the game after phase 1
     * @param currplayer is the current player that is playing in this turn
     * @return false if the player is not done with their turn, true other wise
     *
     * Author @Phuc La
     */
    private boolean processCommand(Command command, Player currplayer){
        if(command.isUnknown()){
            System.out.println("Not valid command!");
        }
        String commandWord = command.getCommandWord().toString();
        if(commandWord.equals("help")){
            System.out.println("Your command words are: ");
            printHelp();
        }else if (commandWord.equals("attack")){
            System.out.println("You are in attacking phase: ");
            attack(currplayer);
        }else if(commandWord.equals("move")){
            System.out.println("You are in moving phase.");
            if(movePhase(currplayer)){
                System.out.println("You finished moving, moving on to next player.");
                return true;
            }
        }else if(commandWord.equals("done")){
            System.out.println("Your turn is now finished. Move on to the next player");
            return true;
        }else if(commandWord.equals("map")){
            System.out.println("This is the map of the world: ");
            printMap();
        }
        return false;
    }

    /**
     * Calculates the number of armies the player will be getting then calls a method that allows
     * the player to distribute said number of armies throughout his owned territories
     * @param currPlayer the player whos turn it is
     */
    public void placePhase(Player currPlayer) {
        int armiesRemaining = 3 + (currPlayer.getAllLandOwned().size()-9)/3;
        for (Continent continent : continents.values()) {
            Optional<Player> conqueror = continent.getConqueror();
            if (conqueror.isPresent() && conqueror.get().equals(currPlayer)) armiesRemaining += continent.BONUS_ARMIES;
        }

        System.out.println("Place phase initiated for " + currPlayer.getName());
        while(armiesRemaining > 0){
            System.out.println("Please chose a Territory to place armies into");
            Territory toPlace = promptForOwnedTerritory(currPlayer,1);
            if(toPlace == null) {
                System.out.println("You cant cancel place phase");
                continue;
            }
            System.out.println("Please chose a The number of armies you would like to place");
            int i = promptForInt(armiesRemaining);
            if(i < 0) {
                System.out.println("You cant cancel place phase");
                continue;
            }
            toPlace.addArmy(i,armiesRemaining);
            armiesRemaining -= i;
            System.out.println("Congrats you have placed " + i + "armies into " + toPlace.getName());

        }
        System.out.println("Place phase is over.");
    }

    /**
     * lets the player move armies from one territory to another as long as they are adjacent to eachother and
     * the number of armies in territoires allow it. also allows the player to call the map function or cancel
     * the move phase
     * @param currPlayer player whos turn it currently is
     * @return a boolean value that is false if movePhase is canceled or true if movePhase is successful
     */
    public boolean movePhase(Player currPlayer) {
        System.out.println("Move phase initiated for " + currPlayer.getName());
        System.out.println("Please enter a territory's ID that u wish to move armies from");
        Territory toRemove = promptForOwnedTerritory(currPlayer,2);
        if(toRemove == null) return false;
        System.out.println("Please enter a territory's ID that u wish to move armies To");
        Territory toPlace = promptForAdjacentTerritory(toRemove);
        if(toRemove == null) return false;
        System.out.println("Please enter the number of armies you wish to move");
        int i = promptForInt(toRemove.getNumArmies()-1);
        if (i < 0) return false;

        toRemove.removeArmy(i);
        toPlace.addArmy(i,i);
        System.out.println("You have moved " + i + " armies from" + toRemove.getName() + " to " + toPlace.getName());
        System.out.println("Move phase is over");
        return true;
    }

    /**
     * Initiates an attack. Contains all the logic to select an attacking territory and handle results of the attack
     * @param activePlayer The current player
     */
    public void attack(Player activePlayer) {
        // Returning null corresponds to player entering "cancel"
        System.out.println("You have chosen to attack! Enter the ID of the territory you wish to attack from:");
        Territory attacking = promptForOwnedTerritory(activePlayer,2);
        if (attacking == null) return;

        System.out.println("Select the territory you wish to attack:");
        Territory defending = promptForAdjacentTerritory(attacking);
        if (defending == null) return;
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
            activePlayer.addTerritories(defending);
            defendingPlayer.removeTerritory(defending);

            attacking.removeArmy(attackRolls.size());
            defending.setNumArmies(attackRolls.size() - attackLosses);

            if (defendingPlayer.getAllLandOwned().size() == 0) {
                // Defender has no territories left, they are eliminated
                System.out.println(defendingPlayer.getName() + " has lost all their territories! They have been eliminated.");
                activePlayers.remove(defendingPlayer);
            }
        }
    }

    /**
     * Prompts the user for a territory. Checks whether owner owns the territory
     * If the user enters "cancel", the function returns null.
     * @param owner Will assert this player as the owner of the territory
     * @return The selected territory, or null if the player entered "cancel"
     */
    private Territory promptForOwnedTerritory(Player owner, int lim) {
        Scanner sc = new Scanner(System.in);
        while (true) {
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
     * promts player for an integer, will check if input is a command, an integer or a invalid input
     * will loop until valid input is entered
     * @param limit the entered integer must be smaller that the integer limit
     * @return returns a valid integer input by the player
     */
    private int promptForInt(int limit) {
        Scanner sc = new Scanner(System.in);
        int temp = 0;
        while (true) {
            String testInt = sc.next();
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

            if (temp < limit && temp > 0) { return temp;}
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
     */
    private Territory promptForAdjacentTerritory(Territory adjacent) {
        Scanner sc = new Scanner(System.in);
        while (true) {
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
            if (!adjacent.isAdjacentTo(territory)) {
                System.out.println("This territory is not adjacent to the one you selected. Please enter a new one:");
                continue;
            }

            return territory;
        }
    }

    /**
     * Checks the user input for a command that resets a prompt and prints an output if applicable
     * @param input The user input
     * @return The corresponding CommandWord if it exists, CommandWord.UNKNOWN otherwise
     */
    private CommandWord checkForCommand(String input) {
        if (input.equals(CommandWord.CANCEL.toString().toUpperCase())) {
            return CommandWord.CANCEL;
        } else if (input.equals(CommandWord.MAP.toString().toUpperCase())) {
            printMap();
            return CommandWord.MAP;
        } else if (input.equals(CommandWord.HELP.toString().toUpperCase())) {
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
     */
    private LinkedList<Integer> rollDice(int numDice) {
        LinkedList<Integer> rolls = new LinkedList<>();
        Random random = new Random();

        for (int i = 0; i < numDice; i++) {
            rolls.add(random.nextInt(6));
        }

        return rolls;
    }

    /**
     * Gets a territory by its full ID (continent + ID)
     * @param id The ID corresponding to the desired territory
     * @return The Optional object containing the territory specified by the ID if it exists, an empty Optional otherwise
     */
    private Optional<Territory> findTerritory(String id) {
        if (id.length() > 2 && continents.containsKey(id.substring(0, 1)) && isNumeric(id.substring(2))) return Optional.empty();
        Continent continent = continents.get(id.substring(0, 1));
        return (continent == null) ? Optional.empty() : continent.getTerritoryById(id.substring(2));
    }

    /**
     * Checks whether a string can be parsed as an int
     * @param str The string to be checked
     * @return True if the string can be parsed as int, false otherwise
     */
    private boolean isNumeric(String str) {
        return str.matches("^\\d+$");
    }

    private void initialize() {

    }

    private void printMap() {

    }

    private void printHelp() {

    }
}
