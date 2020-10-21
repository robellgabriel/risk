/**
 * This enum represents all valid command words for the game
 * along with a string of that command.
 *
 * @author  Robell Gabriel
 */
public enum CommandWord
{
    // A value for each command word along with its
    // corresponding user interface string.
    PLAY("play"), ATTACK("attack"), MOVE("move"),
    DONE("done"), CANCEL("cancel"), HELP("help"),
    MAP("map"), UNKNOWN("?"), QUIT("quit");

    // The command string.
    private String commandString;

    /**
     * Initialise with the corresponding command string.
     * @param commandString The command string.
     */
    CommandWord(String commandString)
    {
        this.commandString = commandString;
    }

    /**
     * @return The command word as a string.
     */
    public String toString()
    {
        return commandString;
    }
}
