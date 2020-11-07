import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class GameController implements ActionListener {
    private Game game;
    private GameView gv;
    private final String[] options = {"OK"};

    /**
     * constructor for gameCOntroller class
     * @param game the game that is controlled by controller
     * @param gv the view that represents the model that is controlled by this
     */
    public GameController(Game game,GameView gv) {
        this.game = game;
        this.gv = gv;
    }

    /**
     * updates the model based on which button was pressed
     * @param e the button that was pressed
     */
    @Override
    public void actionPerformed(ActionEvent e){
        Object o = e.getSource();
        JButton button = (JButton) o;
        Player player = game.getCurrentPlayer();

        //if place button is pressed pull up a PlacePanel to get input from user and update model accordingly
        if (button.getText().equals("Place")) {
            int result;
            PlacePanel plp = new PlacePanel(player, game.getContinents());
            do {
                result = JOptionPane.showOptionDialog(gv, plp, "Place", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            }
            while (result == JOptionPane.CLOSED_OPTION || plp.getArmiesRemaining() > 0);

                game.placePhase(plp.territoriesArmyIncreased());


        }
        //if move button is pressed pull up a movePanel to get input from user and update model accordingly
        else if(button.getText().equals("Move")){
                MovePanel test = new MovePanel(player,game);
                int result = JOptionPane.CLOSED_OPTION;

                while (
                    // If user has not selected valid territories or closed the window, reopen to prompt again
                        result == JOptionPane.CLOSED_OPTION
                                || (
                                result == JOptionPane.OK_OPTION
                                        && (test.getMoveTo() == null
                                        || test.getMoveFrom() == null)
                        )
                ) {
                    result = JOptionPane.showConfirmDialog(
                            gv,
                            test,
                            "Select a territory to move from!",
                            JOptionPane.OK_CANCEL_OPTION
                    );
                }
                if (result != JOptionPane.CANCEL_OPTION) {
                    game.movePhase(test.getArmiesToMove(), test.getMoveFrom(), test.getMoveTo());
                }
        }
        //if attack button is pressed pull up a AttackPanel to get input from user and update model accordingly
        else if(button.getText().equals("Attack")){
            AttackPanel test = new AttackPanel(player,game);
            int result = JOptionPane.CLOSED_OPTION;

            while (
                // If user has not selected valid territories or closed the window, reopen to prompt again
                    result == JOptionPane.CLOSED_OPTION
                            || (
                            result == JOptionPane.OK_OPTION
                                    && (test.getAttackingTerritory() == null
                                    || test.getDefendingTerritory() == null)
                    )
            ) {
                result = JOptionPane.showConfirmDialog(
                        gv,
                        test,
                        "Select a territory to attack from!",
                        JOptionPane.OK_CANCEL_OPTION
                );
            }
            if (result != JOptionPane.CANCEL_OPTION) {
                Territory defending = test.getDefendingTerritory();
                Territory attacking = test.getAttackingTerritory();
                result = JOptionPane.CLOSED_OPTION;
                ArmySelectPanel dp = new ArmySelectPanel(1,defending.getNumArmies() > 1? 2 : 1);

                while (result == JOptionPane.CLOSED_OPTION) {
                    result = JOptionPane.showOptionDialog(
                            gv,
                            dp,
                            "Select a number of armies!",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );
                }
                boolean won = game.attack(test.getArmyNum(), test.getAttackingTerritory(), test.getDefendingTerritory(), dp.getArmyNum());
                if (won) {
                    result = JOptionPane.CLOSED_OPTION;
                    ArmySelectPanel transfer = new ArmySelectPanel(test.getArmyNum(), attacking.getNumArmies() - 1);

                    while (result == JOptionPane.CLOSED_OPTION) {
                        result = JOptionPane.showOptionDialog(
                                gv,
                                transfer,
                                "Select a number of armies to transfer!",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]
                        );
                    }
                    game.attackWon( attacking, defending, transfer.getArmyNum());
                }
            }
        }
        else if (button.getText().equals("Done")){
            game.done();
        }

    }

}
