import java.util.Scanner;

/**
 * This class is part of the game "Risk"
 *
 * This parser reads user input and tries to interpret it as an "Attack"
 * command. Every time it is called it reads a line from the terminal and
 * tries to interpret the line as a command. It returns the command
 * as an object of class Command.
 *
 * The parser has a set of known command words. It checks user input against
 * the known commands, and if the input is not one of the known commands, it
 * returns a command object that is marked as an unknown command.
 *
 * @author  Robell Gabriel
 */
public class Parser
{
    private CommandWords commands;  // holds all valid command words
    private Scanner reader;         // source of command input

    /**
     * Create a parser to read from the terminal window.
     */
    public Parser()
    {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }

    /**
     * @return The next command from the user.
     */
    public Command getCommand()
    {
        Scanner in = new Scanner(System.in);

        System.out.print("> ");     // print prompt

        String word1 = word1 = in.nextLine();

        return new Command(commands.getCommandWord(word1));
    }

    /**
     * Print out a list of valid command words.
     */
    public void showCommands()
    {
        commands.showAll();
    }
}
