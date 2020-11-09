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
    private final Map< Territory, Integer> toAdd;

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
        toAdd = new HashMap<>();
        DefaultListModel<Territory> mapList = new DefaultListModel<>();

        //creates a deep copy of current map to update mainmenu map but not placepanel map
        List<Territory> menuMapList = new ArrayList<>();
        Iterator<Territory> iterator = currPlayer.getAllLandOwned().iterator();
        while (iterator.hasNext()){
            Territory territory = iterator.next();
            Territory temp = new Territory(territory.getName(), territory.getId(), territory.getAdjacentList());
            temp.setPlayer(territory.getOwner());
            temp.setNumArmies(territory.getNumArmies());
            menuMapList.add(temp);
        }

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
                    Territory menuTer = null;
                    ter.addArmy(1);

                    for (Territory territory : menuMapList){
                        if (territory.getName().equals(ter.getName())){
                            menuTer = territory;
                        }
                    }

                    if (toAdd.containsKey(menuTer)) {
                        toAdd.replace(menuTer,toAdd.get(menuTer)+1);
                    }
                    else{
                        toAdd.put(menuTer,1);
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
    public HashMap<Territory, Integer> territoriesArmyIncreased(){
        return (HashMap)toAdd;
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