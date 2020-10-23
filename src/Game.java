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

    public void placePhase(Player player) {

    }

    public boolean movePhase(Player player) {
        return true;
    }

    /**
     * Initiates an attack. Contains all the logic to select an attacking territory and handle results of the attack
     * @param activePlayer The current player
     */
    public void attack(Player activePlayer) {
        // Returning null corresponds to player entering "cancel"
        System.out.println("You have chosen to attack! Enter the ID of the territory you wish to attack from:");
        Territory attacking = promptForOwnedTerritory(activePlayer);
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
    private Territory promptForOwnedTerritory(Player owner) {
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

            if (territory.getNumArmies() < 2) {
                System.out.println("This territory does not have enough units to attack. Please enter a new one:");
                continue;
            }

            return territory;
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
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Select the amount of dice you wish to roll:");
            String input = sc.next();

            if (!isNumeric(input)) {
                System.out.print("This is not a valid number! ");
                continue;
            }

            int numDice = Integer.parseInt(input);
            if (numDice > maxDice) {
                System.out.print("You selected too many dice! ");
                continue;
            }

            if (numDice == 0) {
                System.out.print("You must roll at least one die. ");
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
