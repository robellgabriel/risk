import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * this program creates a jpanel to model how a movephase in risk would look like
 * @author Jacob Schmidt
 */
public class MovePanel extends JPanel {
    //create global objects that will be shown on the jframe
    private final JList<Territory> ownedTerritories, ownedAdjacentTerritories;
    private final JSlider movePossibility;

    //create global variables that other classes will be able to access through get method
    private Territory moveFrom, moveTo;
    private int armiesToMove;

    public MovePanel(Player player, Game game) {
        //initialize all JPanels
        JPanel movePanel = new JPanel();
        JPanel labelPanel = new JPanel(new GridLayout(1,3));
        JPanel buttonPanel = new JPanel(new GridLayout(1,3));
        JPanel centerPanel = new JPanel(new GridLayout(1,3));
        //initialize all JComponents that are not containers

        //initialize global JComponents
        DefaultListModel<Territory> ownedList = new DefaultListModel<>();
        ownedList.addAll(generateValidList(game, player));
        ownedTerritories = new JList<>(ownedList);
        ownedAdjacentTerritories = new JList<>();
        movePossibility = new JSlider();
        movePossibility.setSnapToTicks(true);
        movePossibility.setMajorTickSpacing(1);
        movePossibility.setEnabled(false);


            /*
        create action listener for the JList ownedTerritories such that when a territory is selected
        a second JList is created from the selected territories adjacent list, the movePossibility slider
        is enables with possible value ranging from the amounts of armies that can be moved out
         */
        ownedTerritories.addListSelectionListener( e -> {
            moveFrom = ownedTerritories.getSelectedValue();
            ownedAdjacentTerritories.setListData(generateValidArray(game, moveFrom.getAdjacentList(),player));
            movePossibility.setEnabled(true);
            movePossibility.setPaintLabels(true);
            movePossibility.setMinimum(1);
            movePossibility.setMaximum(moveFrom.getNumArmies()-1);
        });

        //moves units between territories with all inputs at random, for AI
        if (player.isAI()){
            Random rnd = new Random();
            int rng;

            List<Territory> playerTerrs = new ArrayList<>(generateValidList(game, player));
            rng = rnd.nextInt(playerTerrs.size());
            moveFrom = playerTerrs.get(rng);

            Territory[] adjacentTerrs= generateValidArray(game, moveFrom.getAdjacentList(),player);
            rng = rnd.nextInt(adjacentTerrs.length);
            moveTo = adjacentTerrs[rng];

            int min = 1;
            int max = moveFrom.getNumArmies()-1;
            rng = rnd.nextInt(max + 1 - min) + min;
            armiesToMove = rng;
        }



        //set widths of  Jlists so that the important values can be read
        ownedTerritories.setFixedCellWidth(300);
        ownedAdjacentTerritories.setFixedCellWidth(300);
        //create scroll pane for the JLists so that they dont have to take up to much vertical space
        JScrollPane ownedTerritoriesScrl = new JScrollPane(ownedTerritories, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane ownedAdjacentTerritoriesScrl = new JScrollPane(ownedAdjacentTerritories, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //adding all nececarry labels and componenets to their respective panels and then adding all panels into
        //final movePanel
        movePanel.setLayout(new BorderLayout());
        labelPanel.add(new JLabel("Owned territories"));
        labelPanel.add(new JLabel("Chose territory to move to"));
        labelPanel.add(new JLabel("Chose number of armies"));
        centerPanel.add(ownedTerritoriesScrl);
        centerPanel.add(ownedAdjacentTerritoriesScrl);
        centerPanel.add(movePossibility);
        movePanel.add(buttonPanel,BorderLayout.SOUTH);
        movePanel.add(centerPanel,BorderLayout.CENTER);
        movePanel.add(labelPanel,BorderLayout.NORTH);
        //adding final movepanel to frame and setting the frame to true,
        this.add(movePanel);
    }

    /**
     * Helper method to create a list with all valid territories
     * @param game the game that is used to search for territories by id string from listTerritories
     * @param player player used to check ownership and make sure the array given only contains territories owned
     *               by player
     * @return List containing all valid territores
     */
    private List<Territory> generateValidList(Game game, Player player) {
        ArrayList<Territory> temp = new ArrayList<>();
        for (Territory terr : player.getAllLandOwned()){
            for (String id : terr.getAdjacentList()){
                Territory tempTerritory = game.findTerritory(id).get();
                if (tempTerritory.getOwner().equals(player) && terr.getNumArmies() > 1){
                    temp.add(terr);
                    break;
                }
            }
        }
        temp.sort(Comparator.comparing(Territory::getId));
        return temp;
    }

    /**
     * helper method that creates an array of valid territories
     * @param game the game that is used to search for territories by id string from listTerritories
     * @param listTerritories list of ids used to search for the territory
     * @param player player used to check ownership and make sure the array given only contains territories owned
     *               by player
     * @return an array containing only valid territories
     */
    private Territory[] generateValidArray(Game game, List<String> listTerritories, Player player) {
        ArrayList<Territory> temp = new ArrayList<>();
        Territory tempTerritory;
        for (String toCheck : listTerritories){
            tempTerritory = game.findTerritory(toCheck).get();
            if (tempTerritory.getOwner().equals(player)){
                temp.add(tempTerritory);
            }
        }
        temp.sort(Comparator.comparing(Territory::getId));
        return temp.toArray(new Territory[0]);
    }

    /**
     * getter method to return moveFrom
     * @return Territory moveFrom
     */
    public Territory getMoveFrom() {
        return moveFrom;
    }

    /**
     * getter method to return moveTo
     * @return Territory moveTo
     */
    public Territory getMoveTo() {
        if (ownedAdjacentTerritories.isSelectionEmpty()){
            return moveTo;
        }else{
            return ownedAdjacentTerritories.getSelectedValue();
        }
    }

    /**
     * getter method to return armiesToMove
     * @return int armiesToMove
     */
    public int getArmiesToMove() {
        if (!movePossibility.isEnabled()){
            return armiesToMove;
        }else{
            return movePossibility.getValue();
        }
    }
}