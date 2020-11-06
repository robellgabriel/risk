import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {

    private final JSlider playerCount;

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

    public int getPlayerCount(){
        return playerCount.getValue();
    }
}
