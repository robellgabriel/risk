import javax.swing.*;
import java.awt.*;

/**
 * This class creates a JPanel welcoming user to the game and asks
 * total number of players
 *
 * @author Robell Gabriel
 */
public class WelcomePanel extends JPanel {

    private final JSlider playerCount;

    /**
     * Constructor for WelcomePanel which asks user number of players
     */
    public WelcomePanel(){
        playerCount = new JSlider(2,6);
        playerCount.setPaintTicks(true);
        playerCount.setPaintLabels(true);
        playerCount.setMajorTickSpacing(1);

        JLabel labelTitle = new JLabel("Welcome to the game Risk!",JLabel.CENTER);
        JLabel askNumPlayers = new JLabel("Choose number of players:");
        askNumPlayers.setVerticalAlignment(JLabel.BOTTOM);

        setLayout(new GridLayout(3,1));
        add(labelTitle);
        add(askNumPlayers);
        add(playerCount);
    }

    /**
     * getting total number of players
     * @return int numbers of players
     */
    public int getPlayerCount(){
        return playerCount.getValue();
    }
}
