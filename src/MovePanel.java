import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * This program creates a JPanel to model how a move phase in risk would look like
 * @author Jacob Schmidt
 */
public class MovePanel extends JPanel {
    // will be shown on the JFrame
    private final JList<Territory> ownedTerritories, ownedAdjacentTerritories;
    private final JSlider movePossibility;

    public MovePanel(Player player, Game game) {
        //initialize all JPanels
        JPanel labelPanel = new JPanel(new GridLayout(1,3));
        JPanel buttonPanel = new JPanel(new GridLayout(1,3));
        JPanel centerPanel = new JPanel(new GridLayout(1,3));

        //initialize global JComponents
        DefaultListModel<Territory> ownedList = new DefaultListModel<>();
        ownedList.addAll(
                player.getLandWithAdjacentAlly(game)
                        .stream()
                        .filter(territory -> territory.getNumArmies() > 1)
                        .collect(Collectors.toList())
        );
        ownedTerritories = new JList<>(ownedList);
        DefaultListModel<Territory> adjacentList = new DefaultListModel<>();
        ownedAdjacentTerritories = new JList<>(adjacentList);
        movePossibility = new JSlider();
        movePossibility.setSnapToTicks(true);
        movePossibility.setMajorTickSpacing(1);
        movePossibility.setEnabled(false);

        /*
        create action listener for the JList ownedTerritories such that when a territory is selected
        it updates a second JList with the selected territories adjacent list, the movePossibility slider
        is enabled with possible values ranging from the amounts of armies that can be moved out
         */
        ownedTerritories.addListSelectionListener(e -> {
            Territory moveFrom = ownedTerritories.getSelectedValue();
            adjacentList.clear();
            adjacentList.addAll(moveFrom.getAdjacentFriendly(game));
            movePossibility.setEnabled(adjacentList.size() > 0);
            movePossibility.setPaintLabels(true);
            movePossibility.setMinimum(1);
            movePossibility.setMaximum(moveFrom.getNumArmies() - 1);
        });

        //set widths of JLists so that the important values can be read
        ownedTerritories.setFixedCellWidth(300);
        ownedAdjacentTerritories.setFixedCellWidth(300);
        //create scroll pane for the JLists so that they don't have to take up too much vertical space
        JScrollPane ownedTerritoriesScroll = new JScrollPane(
                ownedTerritories,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        JScrollPane ownedAdjacentTerritoriesScroll = new JScrollPane(
                ownedAdjacentTerritories,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        //adding all necessary labels and components to their respective panels
        labelPanel.add(new JLabel("Owned territories"));
        labelPanel.add(new JLabel("Chose territory to move to"));
        labelPanel.add(new JLabel("Chose number of armies"));
        centerPanel.add(ownedTerritoriesScroll);
        centerPanel.add(ownedAdjacentTerritoriesScroll);
        centerPanel.add(movePossibility);
        setLayout(new BorderLayout());
        add(buttonPanel,BorderLayout.SOUTH);
        add(centerPanel,BorderLayout.CENTER);
        add(labelPanel,BorderLayout.NORTH);
    }

    /**
     * getter method to return moveFrom
     * @return Territory moveFrom
     */
    public Territory getMoveFrom() {
        return ownedTerritories.getSelectedValue();
    }

    /**
     * getter method to return moveTo
     * @return Territory moveTo
     */
    public Territory getMoveTo() {
        return ownedAdjacentTerritories.getSelectedValue();
    }

    /**
     * getter method to return armiesToMove
     * @return int armiesToMove
     */
    public int getArmiesToMove() {
        if (!movePossibility.isEnabled()) {
            return movePossibility.getMaximum();
        } else {
            return movePossibility.getValue();
        }
    }
}