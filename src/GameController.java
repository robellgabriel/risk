import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 * The GameController for the GUI component of the game Risk, handles the user inputs which updates the GameModel and GameView.
 * This class prompts panels for when command buttons are clicked
 *
 * @author Nicolas Tuttle, Phuc La, Robell Gabriel, Jacob Schmidt
 */

public class GameController implements ActionListener {
    private final Game game;
    private final GameView gameView;
    private final String[] options = {"OK"};

    /**
     * constructor for gameController class
     * @param game the game that is controlled by controller
     * @param gameView the view that represents the model that is controlled by this
     */
    public GameController(Game game,GameView gameView) {
        this.game = game;
        this.gameView = gameView;
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
        switch (button.getText()) {
            case "Place": {
                int result;
                PlacePanel plp = new PlacePanel(player, game.getContinents());
                do {
                    result = JOptionPane.showOptionDialog(gameView,
                            plp,
                            "Place",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);
                }
                while (result == JOptionPane.CLOSED_OPTION || plp.getArmiesRemaining() > 0);

                game.placePhase(plp.territoriesArmyIncreased());
                break;
            }
            //if move button is pressed pull up a movePanel to get input from user and update model accordingly
            case "Move": {
                MovePanel test = new MovePanel(player, game);
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
                            gameView,
                            test,
                            "Select a territory to move from!",
                            JOptionPane.OK_CANCEL_OPTION
                    );
                }
                if (result != JOptionPane.CANCEL_OPTION) {
                    game.movePhase(test.getArmiesToMove(), test.getMoveFrom(), test.getMoveTo());
                }
                break;
            }
            //if attack button is pressed pull up a AttackPanel to get input from user and update model accordingly
            case "Attack": {
                AttackPanel test = new AttackPanel(player, game);
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
                            gameView,
                            test,
                            "Select a territory to attack from!",
                            JOptionPane.OK_CANCEL_OPTION
                    );
                }
                if (result != JOptionPane.CANCEL_OPTION) {
                    Territory defending = test.getDefendingTerritory();
                    Territory attacking = test.getAttackingTerritory();
                    result = JOptionPane.CLOSED_OPTION;
                    ArmySelectPanel dp = new ArmySelectPanel(1, defending.getNumArmies() > 1 ? 2 : 1);

                    while (result == JOptionPane.CLOSED_OPTION) {
                        result = JOptionPane.showOptionDialog(
                                gameView,
                                dp,
                                "Select a number of armies!",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]
                        );
                    }
                    boolean won = game.attack(test.getArmyNum(), attacking, defending, dp.getArmyNum());
                    if (won) {
                        result = JOptionPane.CLOSED_OPTION;
                        ArmySelectPanel transfer = new ArmySelectPanel(test.getArmyNum(), attacking.getNumArmies() - 1);

                        while (result == JOptionPane.CLOSED_OPTION) {
                            result = JOptionPane.showOptionDialog(
                                    gameView,
                                    transfer,
                                    "Select a number of armies to transfer!",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null,
                                    options,
                                    options[0]
                            );
                        }
                        game.attackWon(attacking, defending, transfer.getArmyNum());
                    }
                }
                break;
            }
            case "Done":
                game.done();
                break;
        }
    }
}
