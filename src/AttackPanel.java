import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AttackPanel extends JPanel {
    private final JList<Territory> ownedList, adjacentList;
    private final JSlider armySlider;

    public AttackPanel(Player attacker, Game game) {
        DefaultListModel<Territory> ownedTerritories = new DefaultListModel<>();
        List<Territory> attackerLand = attacker.getAllLandOwned()
                                        .stream()
                                        .filter(p -> p.getNumArmies() > 1)
                                        .sorted(Comparator.comparing(Territory::getId))
                                        .collect(Collectors.toList());
        // Sort territories by ID
        ownedTerritories.addAll(attackerLand);

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
            int maxArmies = Math.min(ownedList.getSelectedValue().getNumArmies() - 1, 3);
            armySlider.setEnabled(maxArmies > 0);
            armySlider.setMaximum(armySlider.isEnabled() ? maxArmies : 1);
            // Clear all entries to replace with new territories
            adjacentTerritories.removeAllElements();
            for (String id : ownedList.getSelectedValue().getAdjacencyList()) {
                // Add territory to list of adjacents if found and is not owned by attacker
                game.findTerritory(id).ifPresent(territory -> {
                    if (!territory.getOwner().equals(attacker)) adjacentTerritories.addElement(territory);
                });
            }
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

    public Territory getAttackingTerritory() {
        return ownedList.getSelectedValue();
    }

    public Territory getDefendingTerritory() {
        return adjacentList.getSelectedValue();
    }

    public int getArmyNum() {
        return armySlider.getValue();
    }
}
