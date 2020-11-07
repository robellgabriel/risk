import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class create a JPanel where the player will perform the place phase in their turn
 *
 * @author Robell Gabriel and Phuc La
 */
public class PlacePanel extends JPanel {
    private int armiesRemaining;
    private final List<Territory> owned = new ArrayList<>();

    /**
     * Constructor for PlacePanel asking player where they want to place
     * given armies at start of turn
     *
     * @param currPlayer PLayer object of current player
     * @param continents Hashmap of continents
     */
    public PlacePanel(Player currPlayer, Map<String, Continent> continents){
        armiesRemaining = 3 + (currPlayer.getAllLandOwned().size()-9)/3;
        for (Continent continent : continents.values()) {
            Optional<Player> conqueror = continent.getConqueror();
            if (conqueror.isPresent() && conqueror.get().equals(currPlayer)) armiesRemaining += continent.BONUS_ARMIES;
        }
        //GUI for the place phase
        DefaultListModel<Territory> mapList = new DefaultListModel<>();
        mapList.addAll(currPlayer.getAllLandOwned());
        JList <Territory> map = new JList<>(mapList);
        map.setFixedCellWidth(700);
        JScrollPane mapScrollPane = new JScrollPane(map);
        JLabel playerter = new JLabel("Owned territories");
        JButton place = new JButton("Place");
        JLabel numarmies = new JLabel("You have "+armiesRemaining+" armies left");


        map.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        place.addActionListener(e->{
            if(map.getSelectedValue()==null){
                JOptionPane.showMessageDialog(this,"You have not specify which territory you want to add armies to.");
            }else{
                if(armiesRemaining>0) {
                    Territory ter = map.getSelectedValue();
                    ter.addArmy(1, armiesRemaining);
                    owned.add(ter);
                    List<Territory> playerLand = owned;
                    for (int i = 0; i < playerLand.size(); i++) {
                        if (playerLand.get(i).getName().equals(ter.getName())){
                            playerLand.set(i, ter);
                        }
                    }
                    armiesRemaining--;
                    numarmies.setText("You have " + armiesRemaining + " armies left");
                    SwingUtilities.updateComponentTreeUI(map);
                }
            }

        });

        setLayout(new BorderLayout());
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1,BoxLayout.Y_AXIS));
        p1.add(playerter);
        p1.add(mapScrollPane);
        add(p1, BorderLayout.CENTER);
        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2,BoxLayout.Y_AXIS));
        p2.add(place);
        p2.add(numarmies);
        add(p2, BorderLayout.EAST);



    }

    /**
     * This method find all the territories that the player owned so that they can choose to place bonus armies in
     * during the place phase
     * @return the list of territories that the player owned;
     */
    public List<Territory> territoriesArmyIncreased(){
        return owned;
    }

    /**
     * This method find the number of armies remained to be place during the place phase
     * until the number of armies reach 0
     * @return the integer that is the number of armies the player still have during the place phase.
     */
    public int getArmiesRemaining(){
        return armiesRemaining;
    }

}
