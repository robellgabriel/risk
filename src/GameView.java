import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The GameView for the GUI component of the game Risk, handles the graphical representation of the GameModel.
 * This class displays the main menu for the Game
 *
 * @author Nicolas Tuttle, Phuc La, Robell Gabriel, Jacob Schmidt
 */
public class GameView extends JFrame {
    private final String[] options = {"OK"};
    private final JTextArea actionLog;
    private final DefaultMutableTreeNode mapList;
    private final JTree map;
    private final JLabel playerTurn;
    private final DefaultListModel<String> leaderBoardList;
    private final JButton attack,move,done,place;

    /**
     * constructor of the GameView that initializes the view of the GameModel
     *
     * @author Robell Gabriel and Phuc La
     */
    public GameView() {
        int result;
        int numPlayers;
        List<String> playerName = new ArrayList<>();

        //welcome panel of risk game
        WelcomePanel wp = new WelcomePanel();
        do {
            result = JOptionPane.showOptionDialog(this, wp, "Welcome",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
        }
        while (result == JOptionPane.CLOSED_OPTION );
        numPlayers = wp.getPlayerCount();

        //PlayerName panel of risk game
        for (int i = 0; i < numPlayers; i++) {
            PlayerNamePanel pmp = new PlayerNamePanel(i);
            do {
                result = JOptionPane.showOptionDialog(
                        this, pmp, "Keep Name Short",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);
            } while (result == JOptionPane.CLOSED_OPTION || pmp.getPlayerName().isBlank()
                    || pmp.getPlayerName().length() > 15 || pmp.getPlayerName().equals("Name here"));
            playerName.add(pmp.getPlayerName());
        }

        Game game  = new Game();
        game.addGameView(this);

        //Initializer to get number of players, player's name, distribution of territory and armies
        game.initialize(numPlayers,playerName);




        mapList = new DefaultMutableTreeNode();
        Map<String, Continent> continents = game.getContinents();
        List<Player> activePlayers = game.getActivePlayers();
        Player currentPlayer = game.getCurrentPlayer();
        GameController gc = new GameController(game,this);
        for (String id : continents.keySet()) {
            DefaultMutableTreeNode contList = new DefaultMutableTreeNode(continents.get(id).getName());
            for (Territory territory : continents.get(id).getTerritoryList()) {
                DefaultMutableTreeNode terr = new DefaultMutableTreeNode(territory.toString());
                contList.add(terr);
            }
            mapList.add(contList);
        }
        map = new JTree(mapList);

        map.setRootVisible(false);
        for (int i = 0; i < map.getRowCount(); i++) {
            map.expandRow(i);
        }
        JScrollPane mapScrollPane = new JScrollPane(map);
        JLabel mapLabel = new JLabel("Map");

        //JList of player leaderboard
        leaderBoardList = new DefaultListModel<>();
        for (Player player : activePlayers) {
            leaderBoardList.addElement(player.getName() + " owns " + player.getAllLandOwned().size() + " territories");
        }
        JList<String> leaderBoard = new JList<>(leaderBoardList);
        JLabel leaderBoardLabel = new JLabel("Leaderboard");
        leaderBoardLabel.setVerticalAlignment(JLabel.BOTTOM);

        //JLabel for player turn and armies remaining to place
        playerTurn = new JLabel("It is " + currentPlayer.getName() + "'s turn");

        //JButtons for attack, move and done
        attack = new JButton("Attack");
        move = new JButton("Move");
        done = new JButton("Done");
        place = new JButton("Place");
        attack.addActionListener(gc);
        move.addActionListener(gc);
        done.addActionListener(gc);
        place.addActionListener(gc);

        //TextArea to show the log
        actionLog = new JTextArea();
        actionLog.setEditable(false);
        actionLog.setRows(20);
        JScrollPane actionLogScroll = new JScrollPane(actionLog);
        actionLogScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        JLabel actionLogLabel = new JLabel("Action log");
        actionLogLabel.setSize(10, 10);
        //disable all buttons until place phase is done
        attack.setEnabled(false);
        move.setEnabled(false);
        done.setEnabled(false);
        //panels for main menu
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 4));
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());

        //adding features to main menu
        setLayout(new BorderLayout());
        leftPanel.add(mapLabel);
        leftPanel.add(mapScrollPane);
        add(leftPanel, BorderLayout.WEST);
        p1.add(leaderBoard, BorderLayout.CENTER);
        rightPanel.add(leaderBoardLabel);
        rightPanel.add(p1);
        rightPanel.add(actionLogLabel);
        rightPanel.add(actionLogScroll);
        rightPanel.add(playerTurn);
        add(rightPanel, BorderLayout.CENTER);
        bottomPanel.add(attack);
        bottomPanel.add(move);
        bottomPanel.add(done);
        bottomPanel.add(place);
        add(bottomPanel, BorderLayout.SOUTH);
        this.setSize(1200, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Updates the view based on which method from the game model has called the update
     *
     * @param calledBy is a string that contains information on which method updated the view
     * @param continents is a list that contains all the continents
     * @param currentPlayer a Player representing who's turn it currently is
     * @param activePlayers a list of Players that are active in the game
     *
     * @author Phuc La and Robell Gabriel
     */
    public void updateView(String calledBy,Map<String, Continent> continents,Player currentPlayer,List<Player> activePlayers ) {
        switch (calledBy) {
            case "Place":
                resetMap(mapList, map, continents);
                place.setEnabled(false);
                attack.setEnabled(true);
                move.setEnabled(true);
                done.setEnabled(true);
                break;

            case "Move":
                resetMap(mapList, map, continents);
                playerTurn.setText("It is " + currentPlayer.getName() + "'s turn: ");
                place.setEnabled(true);
                attack.setEnabled(false);
                move.setEnabled(false);
                done.setEnabled(false);
                break;

            case "Attack":
                int result;
                resetMap(mapList, map, continents);
                leaderBoardList.removeAllElements();
                for (Player player : activePlayers) {
                    leaderBoardList.addElement(player.getName() + " owns " + player.getAllLandOwned().size() + " territories");
                }
                if (activePlayers.size() == 1) {
                    do {
                        result = JOptionPane.showOptionDialog(this, "Congratulations " + currentPlayer.getName() + ". You are the winner!!!", "Winner",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                                null, options, options[0]);
                    }
                    while (result == JOptionPane.CLOSED_OPTION);
                    this.dispose();
                }
                break;

            case "Done":
                playerTurn.setText("It is " + currentPlayer.getName() + "'s turn: ");
                place.setEnabled(true);
                attack.setEnabled(false);
                move.setEnabled(false);
                done.setEnabled(false);

        }
    }

    public static void main(String[] args) {
        new GameView();
    }

    /**
     * Updates the map as the game progresses (IE: attack/move territories losing/gaining armies)
     *
     * @param mapList TreeNode containing all territories categorized by corresponding continent
     * @param map     JTree contain mapList of territory view for main menu
     * @param continents a list of continents on the map
     *
     * @author Robell Gabriel and Phuc La
     */
    public void resetMap(DefaultMutableTreeNode mapList, JTree map,Map<String, Continent> continents ) {
        mapList.removeAllChildren();
        for (String id : continents.keySet()) {
            DefaultMutableTreeNode contList = new DefaultMutableTreeNode(continents.get(id).getName());
            for (Territory territory : continents.get(id).getTerritoryList()) {
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
     * Prints a line to the action log
     *
     * @param str The line to be printed to the action log
     *
     * @author Nicolas Tuttle
     */
    public void printLine(String str) {
        actionLog.append(str + "\n");
        actionLog.setCaretPosition(actionLog.getDocument().getLength());
    }

}
