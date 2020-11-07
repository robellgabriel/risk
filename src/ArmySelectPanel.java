import javax.swing.*;

/**
 * A simple JPanel with a JSlider to select a number of armies
 *
 * @author Nicolas Tuttle
 */
public class ArmySelectPanel extends JPanel {
    private final JSlider armySlider;

    /**
     * Constructor for ArmySelectPanel, takes a min and max amount of armies
     * @param minArmies The minimum amount allowed to be selected
     * @param maxArmies The maximum amount allowed to be selected
     */
    public ArmySelectPanel(int minArmies, int maxArmies) {
        armySlider = new JSlider(minArmies, maxArmies);
        armySlider.setMajorTickSpacing(1);
        armySlider.setPaintTicks(true);
        armySlider.setPaintLabels(true);
        add(armySlider);
    }

    /**
     * Get the number of armies that were specified by the user
     * @return The number of armies
     */
    public int getArmyNum() {
        return armySlider.getValue();
    }
}
