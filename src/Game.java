import java.util.LinkedList;
import java.util.List;

public class Game {
    private final List<Player> activePlayers;
    private final List<Continent> continents;
    private final Parser parser;

    public Game() {
        activePlayers = new LinkedList<>();
        continents = new LinkedList<>();
        parser = new Parser();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }

    public void play() {

    }

    public void placePhase() {

    }

    public void movePhase() {

    }

    public void attack() {

    }

    private void initialize() {

    }

    private void printMap() {

    }

    private void printHelp() {

    }
}
