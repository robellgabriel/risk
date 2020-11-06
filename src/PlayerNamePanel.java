import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlayerNamePanel extends JPanel {

    private final JTextField name;

    public PlayerNamePanel(int i){
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

    public String getPlayerName(){
        return name.getText();
    }

}
