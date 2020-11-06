import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class PlacePanel extends JPanel {
    private int armiesRemaining;
    private List<Territory> owned = new ArrayList<Territory>();
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
    public List<Territory> territoriesArmyIncreased(){
        return owned;
    }
    public int getArmiesRemaining(){
        return armiesRemaining;
    }

}
