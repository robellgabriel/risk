import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * The main game class for the game Risk. This manages all operations including game initialization,
 * player turns, and command handling. The game ends when there is one player left standing.
 *
 * @author Nicolas Tuttle, Phuc La, Robell Gabriel, Jacob Schmidt
 */
public class Game extends JFrame {
    private final List<Player> activePlayers;
    private final Map<String, Continent> continents;
    private final Parser parser;
    private static Player currentPlayer;
    private final String[] options = {"OK"};

    public Game() {
        super("RISK");
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
     * @author Phuc La and Robell Gabriel
     */
    public void play() {
        //Initializer to get number of players, player's name, distribution of territory and armies
        initialize();

        currentPlayer = activePlayers.get(0);

        //JList of each continent's Territories
        DefaultMutableTreeNode mapList = new DefaultMutableTreeNode();
        for (String id : continents.keySet()) {
            DefaultMutableTreeNode contList = new DefaultMutableTreeNode(continents.get(id).getName());
            for (Territory territory : continents.get(id).getTerritoryList()){
                DefaultMutableTreeNode terr = new DefaultMutableTreeNode(territory.toString());
                contList.add(terr);
            }
            mapList.add(contList);
        }
        JTree map = new JTree(mapList);
        map.setRootVisible(false);
        for (int i = 0; i < map.getRowCount(); i++){
            map.expandRow(i);
        }
        JScrollPane mapScrollPane = new JScrollPane(map);
        JLabel mapLabel = new JLabel("Map");

        //JList of player leaderboard
        DefaultListModel<String> leaderBoardList = new DefaultListModel<>();
        for (Player player : activePlayers){
            leaderBoardList.addElement(player.getName() + " owns "+player.getAllLandOwned().size()+" territories");
        }
        JList <String> leaderBoard = new JList<>(leaderBoardList);
        JLabel leaderBoardLabel = new JLabel("Leaderboard");
        leaderBoardLabel.setVerticalAlignment(JLabel.BOTTOM);

        //Jlabel for player turn and armies remaining to place
        JLabel playerTurn = new JLabel("It is " +currentPlayer.getName()+ "'s turn");

        //JButtons for attack, move and done
        JButton attack = new JButton ("Attack");
        JButton move = new JButton ("Move");
        JButton done = new JButton ("Done");
        JButton place = new JButton("Place");
        attack.addActionListener(e -> {
            int result;
            attack(currentPlayer);
            resetMap(mapList, map);
            leaderBoardList.removeAllElements();
            for (Player player : activePlayers){
                leaderBoardList.addElement(player.getName() + " owns "+player.getAllLandOwned().size()+" territories");
            }
            if (activePlayers.size()==1) {
                do {
                    result = JOptionPane.showOptionDialog(this, "Congratulations " + currentPlayer.getName() + ". You are the winner!!!","Winner",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);
                }
                while (result == JOptionPane.CLOSED_OPTION);
                this.dispose();
            }
        });
        move.addActionListener(e -> {
            if(movePhase(currentPlayer)){
                resetMap(mapList, map);
                currentPlayer = activePlayers.get((activePlayers.indexOf(currentPlayer) +1 ) % activePlayers.size());
                playerTurn.setText("It is " +currentPlayer.getName()+ "'s turn: ");
                place.setEnabled(true);
                attack.setEnabled(false);
                move.setEnabled(false);
                done.setEnabled(false);
            }
        });
        done.addActionListener(e -> {
            currentPlayer = activePlayers.get((activePlayers.indexOf(currentPlayer) +1 ) % activePlayers.size());
            playerTurn.setText("It is " +currentPlayer.getName()+ "'s turn: ");
            place.setEnabled(true);
            attack.setEnabled(false);
            move.setEnabled(false);
            done.setEnabled(false);

        });
        place.addActionListener(e -> {
            placePhase(currentPlayer);
            resetMap(mapList, map);
            place.setEnabled(false);
            attack.setEnabled(true);
            move.setEnabled(true);
            done.setEnabled(true);
        });

        //TextArea to show the log
        JTextArea actionlog = new JTextArea();
        JLabel actionlogLabel = new JLabel("Action log");
        actionlogLabel.setSize(10,10);
        //disable all buttons until place phase is done
        attack.setEnabled(false);
        move.setEnabled(false);
        done.setEnabled(false);
        //panels for main menu
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1,4));
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());

        //adding features to main menu
        setLayout(new BorderLayout());
        leftPanel.add(mapLabel);
        leftPanel.add(mapScrollPane);
        add(leftPanel,BorderLayout.WEST);
        p1.add(leaderBoard,BorderLayout.CENTER);
        rightPanel.add(leaderBoardLabel);
        rightPanel.add(p1);
        rightPanel.add(actionlogLabel);
        rightPanel.add(actionlog);
        rightPanel.add(playerTurn);
        add(rightPanel,BorderLayout.CENTER);
        bottomPanel.add(attack);
        bottomPanel.add(move);
        bottomPanel.add(done);
        bottomPanel.add(place);
        add(bottomPanel,BorderLayout.SOUTH);
        this.setSize(1200,600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    /**
     * Updates the map as the game progresses (IE: attack/move territories losing/gaining armies)
     *
     * @param mapList TreeNode containing all territories categorized by corresponding continent
     * @param map JTree contain mapList of territory view for mainscreen
     *
     * @author Robell Gabriel and Phuc La
     */
    public void resetMap(DefaultMutableTreeNode mapList, JTree map){
        mapList.removeAllChildren();
        for (String id : continents.keySet()) {
            DefaultMutableTreeNode contList = new DefaultMutableTreeNode(continents.get(id).getName());
            for (Territory territory : continents.get(id).getTerritoryList()){
                DefaultMutableTreeNode terr = new DefaultMutableTreeNode(territory.toString());
                contList.add(terr);
            }
            mapList.add(contList);

        }
        SwingUtilities.updateComponentTreeUI(map);
        for (int i = 0; i < map.getRowCount(); i++) {
            map.expandRow(i);
        }
    }

    /**
     * Place phase for RISK game
     * @param currPlayer is the player that is playing at the moment
     *
     * @author Robell Gabriel and Phuc La
     */
    public void placePhase(Player currPlayer){
        int result;
       PlacePanel plp = new PlacePanel(currPlayer,continents);
        do {
            result = JOptionPane.showOptionDialog(this, plp,"Place", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        }
        while (result == JOptionPane.CLOSED_OPTION || plp.getArmiesRemaining()>0);
        List<Territory> playerLand = currPlayer.getAllLandOwned();
        for (int i = 0; i < playerLand.size(); i++) {
            for (Territory terr : plp.territoriesArmyIncreased()) {
                if (playerLand.get(i).getName().equals(terr.getName())){
                    playerLand.set(i, terr);
                }
            }
        }
    }


    /**
     * lets the player move armies from one territory to another as long as they are adjacent to each other and
     * the number of armies in territories allow it. also allows the player to call the map function or cancel
     * the move phase
     * @param currPlayer player who's turn it currently is
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


            //cheat code -> winner tester
            /*List<Territory> lst = new ArrayList<Territory>(defendingPlayer.getAllLandOwned());
            for(Territory ter : lst){
                defendingPlayer.removeTerritory(ter);
            }*/

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
        int result;
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

        //welcome panel of risk game
        WelcomePanel wp = new WelcomePanel();
        do {
            result = JOptionPane.showOptionDialog(this, wp,"Welcome",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
        }
        while (result == JOptionPane.CLOSED_OPTION);
        int numPlayers=wp.getPlayerCount(); //total number of players

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

        //Player name panel asks names then adds player's name and their territories with random amount of armies each
        for (int i = 0; i < numPlayers; i++) {
            PlayerNamePanel pmp = new PlayerNamePanel(i);
            do {
                result = JOptionPane.showOptionDialog(
                        this, pmp, "Keep Name Short",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
            }while (result == JOptionPane.CLOSED_OPTION || pmp.getPlayerName().isBlank()
                     || pmp.getPlayerName().length()>15 || pmp.getPlayerName().equals("Name here"));
            playerName=pmp.getPlayerName();
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