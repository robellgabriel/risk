/**
 * This class is part of the game "Risk"
 *
 * This class holds information about a command that was issued by the user.
 * A command currently consists of two parts: a CommandWord and a string
 * (for example, if the command was "attack brazil", then the two parts
 * are ATTACK and "brazil").
 *
 * The way this is used is: Commands are already checked for being valid
 * command words. If the user entered an invalid command (a word that is not
 * known) then the CommandWord is UNKNOWN.
 *
 * If the command had only one word, then the second word is <null>.
 *
 * @author  Robell Gabriel
 */

public class Command
{
    private CommandWord commandWord;
    private String secondWord;

    /**
     * Create a command object. First and second words must be supplied, but
     * the second may be null.
     * @param commandWord The CommandWord. UNKNOWN if the command word
     *                  was not recognised.
     * @param secondWord The second word of the command. May be null.
     */
    public Command(CommandWord commandWord, String secondWord)
    {
        this.commandWord = commandWord;
        this.secondWord = secondWord;
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
     * @return The second word of this command. Returns null if there was no
     * second word.
     */
    public String getSecondWord()
    {
        return secondWord;
    }

    /**
     * @return true if this command was not understood.
     */
    public boolean isUnknown()
    {
        return (commandWord == CommandWord.UNKNOWN);
    }

    /**
     * @return true if the command has a second word.
     */
    public boolean hasSecondWord()
    {
        return (secondWord != null);
    }
}

