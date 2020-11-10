import javax.swing.*;
import java.awt.event.*;

/**
 * This class create a JPanel asking the players for their names
 *
 * @author Robell Gabriel uthor Robell Gabriel
 */
public class PlayerNamePanel extends JPanel {
    private final JTextField name;

    /**
     * Constructor for PlayerNamePanel asking player's name
     * @param i int looping for each player
     */
    public PlayerNamePanel(int i) {
        name = new JTextField("Name here",10);
        name.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                name.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        JLabel askName = new JLabel("Enter Player " + (i + 1) + " name: ");
        askName.setVerticalAlignment(JLabel.BOTTOM);

        add(askName);
        add(name);
    }

    /**
     * getting each player's name
     * @return String player's name
     */
    public String getPlayerName() {
        return name.getText();
    }
}
