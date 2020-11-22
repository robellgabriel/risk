import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * This class creates a JPanel where the player will perform the place phase in their turn
 *
 * @author Robell Gabriel and Phuc La
 */
public class PlacePanel extends JPanel {
    private int armiesRemaining;
    private final Map<String, Integer> toAdd;

    /**
     * Constructor for PlacePanel asking player where they want to place
     * given armies at start of turn
     *
     * @param currPlayer PLayer object of current player
     * @param continents Hashmap of continents
     */
    public PlacePanel(Player currPlayer, Map<String, Continent> continents) {
        armiesRemaining = 3 + (currPlayer.getAllLandOwnedSize() - 9) / 3;
        for (Continent continent : continents.values()) {
            Optional<Player> conqueror = continent.getConqueror();
            if (conqueror.isPresent() && conqueror.get().equals(currPlayer)) armiesRemaining += continent.BONUS_ARMIES;
        }

        //GUI for the place phase
        toAdd = new HashMap<>();
        DefaultListModel<Territory> mapList = new DefaultListModel<>();

        //creates a deep copy of current map to update place panel map but not main menu map (leave that to model calls)
        ArrayList<Territory> copy = new ArrayList<>();
        for (Territory territory : currPlayer.getAllLandOwned()) {
            Territory temp = new Territory(territory.getName(), territory.getId(), territory.getAdjacentList());
            temp.setNumArmies(territory.getNumArmies());
            temp.setPlayer(territory.getOwner());
            copy.add(temp);
        }
        copy.sort(Comparator.comparing(Territory::getId));
        mapList.addAll(copy);

        JList <Territory> map = new JList<>(mapList);
        map.setFixedCellWidth(700);
        JScrollPane mapScrollPane = new JScrollPane(map);
        JLabel territoriesLabel = new JLabel("Owned territories");
        JButton placeButton = new JButton("Place");
        JLabel numArmiesLabel = new JLabel("You have " + armiesRemaining + " armies left");

        map.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        placeButton.addActionListener(e -> {
            if (map.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(this,"You have not specified which territory you want to add armies to.");
            } else if (armiesRemaining > 0) {
                Territory ter = map.getSelectedValue();
                ter.addArmy(1);

                if (toAdd.containsKey(ter.getId())) {
                    toAdd.replace(ter.getId(), toAdd.get(ter.getId()) + 1);
                } else {
                    toAdd.put(ter.getId(), 1);
                }

                armiesRemaining--;
                numArmiesLabel.setText("You have " + armiesRemaining + " armies left");
                SwingUtilities.updateComponentTreeUI(map);
            }
        });

        setLayout(new BorderLayout());
        JPanel territoryListPanel = new JPanel();
        territoryListPanel.setLayout(new BoxLayout(territoryListPanel,BoxLayout.Y_AXIS));
        territoryListPanel.add(territoriesLabel);
        territoryListPanel.add(mapScrollPane);
        add(territoryListPanel, BorderLayout.CENTER);
        JPanel placeButtonPanel = new JPanel();
        placeButtonPanel.setLayout(new BoxLayout(placeButtonPanel,BoxLayout.Y_AXIS));
        placeButtonPanel.add(placeButton);
        placeButtonPanel.add(numArmiesLabel);
        add(placeButtonPanel, BorderLayout.EAST);
    }

    /**
     * This method find all the territories that the player owned so that they can choose to place bonus armies in
     * during the place phase
     * @return the list of territories that the player owned;
     */
    public Map<String, Integer> territoriesArmyIncreased() {
        return toAdd;
    }

    /**
     * This method find the number of armies remained to be place during the place phase
     * until the number of armies reach 0
     * @return the integer that is the number of armies the player still have during the place phase.
     */
    public int getArmiesRemaining() {
        return armiesRemaining;
    }
}