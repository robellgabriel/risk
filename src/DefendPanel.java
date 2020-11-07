import javax.swing.*;

public class DefendPanel extends JPanel {
    private final JSlider armySlider;

    public DefendPanel(Territory defendingTerritory) {
        armySlider = new JSlider(1, Math.min(defendingTerritory.getNumArmies(), 2));
        armySlider.setMajorTickSpacing(1);
        armySlider.setPaintTicks(true);
        armySlider.setPaintLabels(true);
        add(armySlider);
    }

    public int getArmyNum() {
        return armySlider.getValue();
    }
}
