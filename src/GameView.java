/**
 * interface for GameView includes two methods one to update the buttons and maps
 * and one to print text
 *
 * @author Jacob Schmidt
 */
public interface GameView {
    /**
     * updates the map and buttons to match the state of the game
     * @param game an event of the model class that contains all necessary information to
     *                  update view
     */
    void updateView(Game game);

    /**
     * adds text to a text area
     * @param str the string to add too a text area
     */
    void printLine (String str);
}


