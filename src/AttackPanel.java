import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

/**
 * A simple JPanel with a list of available territories to attack from, adjacent territories to attack,
 * and a JSlider to specify how many armies to attack with.
 *
 * @author Nicolas Tuttle
 */
public class AttackPanel extends JPanel {
    private final JList<Territory> ownedList, adjacentList;
    private final JSlider armySlider;

    /**
     * Constructor for class AttackPanel. Initializes all JLists with information from the attacking player
     * and wires up all the relevant listeners.
     * @param attacker The attacking player
     * @param game Main Game model information
     */
    public AttackPanel(Player attacker, Game game) {
        DefaultListModel<Territory> ownedTerritories = new DefaultListModel<>();

        // Get all land owned by attacker with more than 1 unit, and an adjacent enemy territory
        ownedTerritories.addAll(
                attacker.getLandWithAdjacentEnemy(game)
                        .stream()
                        .filter(territory -> territory.getNumArmies() > 1)
                        .collect(Collectors.toList())
        );

        ownedList = new JList<>(ownedTerritories);
        ownedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ownedList.setFixedCellWidth(400);

        DefaultListModel<Territory> adjacentTerritories = new DefaultListModel<>();
        adjacentList = new JList<>(adjacentTerritories);
        adjacentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adjacentList.setFixedCellWidth(400);

        armySlider = new JSlider(1, 2);
        armySlider.setEnabled(false);
        armySlider.setMajorTickSpacing(1);
        armySlider.setPaintTicks(true);
        armySlider.setPaintLabels(true);


        ownedList.addListSelectionListener(e -> {
            Territory attackTerr = ownedList.getSelectedValue();
            int maxArmies = Math.min(attackTerr.getNumArmies() - 1, 3);
            armySlider.setEnabled(maxArmies > 0);
            armySlider.setMaximum(armySlider.isEnabled() ? maxArmies : 1);
            // Clear all entries to replace with new territories
            adjacentTerritories.removeAllElements();
            adjacentTerritories.addAll(attackTerr.getAdjacentEnemy(game));
        });

        JScrollPane ownedScrollPane = new JScrollPane(ownedList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane adjacentScrollPane = new JScrollPane(adjacentList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        setLayout(new GridLayout(2, 3));
        JLabel ownedLabel = new JLabel("Owned territories:", JLabel.CENTER);
        ownedLabel.setVerticalAlignment(JLabel.BOTTOM);
        JLabel adjacentLabel = new JLabel("Adjacent territories:", JLabel.CENTER);
        adjacentLabel.setVerticalAlignment(JLabel.BOTTOM);
        JLabel armyLabel = new JLabel("Number of armies:", JLabel.CENTER);
        armyLabel.setVerticalAlignment(JLabel.BOTTOM);

        add(ownedLabel);
        add(adjacentLabel);
        add(armyLabel);
        add(ownedScrollPane);
        add(adjacentScrollPane);
        add(armySlider);
    }

    /**
     * Get the attacking territory that was selected
     * @return The territory the player wishes to attack from
     */
    public Territory getAttackingTerritory() {
        return ownedList.getSelectedValue();
    }

    /**
     * Get the territory the player wishes to attack
     * @return The territory to attack
     */
    public Territory getDefendingTerritory() {
        return adjacentList.getSelectedValue();
    }

    /**
     * Get the number of armies the player will attack with
     * @return The number of armies
     */
    public int getArmyNum() {
        if (!armySlider.isEnabled()){
            return armySlider.getMaximum();
        }else{
            return armySlider.getValue();
        }
    }
}
