/**
 * This class is part of the game "Risk"
 *
 * This class holds information about a command that was issued by the user.
 * The way this is used is: Commands are already checked for being valid
 * command words. If the user entered an invalid command (a word that is not
 * known) then the CommandWord is UNKNOWN.
 *
 * @author  Robell Gabriel
 */

public class Command
{
    private CommandWord commandWord;

    /**
     * Create a command object. First and second words must be supplied
     * @param commandWord The CommandWord. UNKNOWN if the command word
     *                  was not recognised.
     */
    public Command(CommandWord commandWord)
    {
        this.commandWord = commandWord;
    }

    /**
     * Return the command word (the first word) of this command.
     * @return The command word.
     */
    public CommandWord getCommandWord()
    {
        return commandWord;
    }

    /**
     * @return true if this command was not understood.
     */
    public boolean isUnknown()
    {
        return (commandWord == CommandWord.UNKNOWN);
    }
}

