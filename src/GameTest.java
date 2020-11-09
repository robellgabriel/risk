import org.junit.*;
import java.util.*;

import static org.junit.Assert.*;


public class GameTest {
    Game game;

    /**
     * This method test place phase when player add all bonus armies into a single territory
     */
    @org.junit.Test
    public void testPlacePhaseSignleTer(){
        game = new Game();
        //Testing place phase for 1 specific territory
        Territory testTer = new Territory("Alaska", "NA1", Arrays.asList("NA2", "NA6", "AS6"));
        Player testPlayer = new Player("a");
        testPlayer.addTerritory(testTer);
        testTer.setPlayer(testPlayer);
        HashMap<Territory,Integer> mt = new HashMap<>();
        mt.put(testTer,3);
        game.placePhase(mt);
        assertEquals(3,testTer.getNumArmies());
    }

    /**
     * This method test place phase when player distribute multiple armies to multiple territories
     */
    @org.junit.Test
    public void testPlacePhaseMultipleTer(){
        game = new Game();
        //Testing place phase for multiple territories involved
        Territory testTer1,testTer2,testTer3;
        //Creating multiple territory
        testTer1 = new Territory("Alaska", "NA1", Arrays.asList("NA2", "NA6", "AS6"));
        testTer2 = new Territory("Great Britain (Great Britain & Ireland)", "EU1", Arrays.asList("EU2", "EU3", "EU4", "EU7"));
        testTer3 = new Territory("Afghanistan", "AS1", Arrays.asList("AS2", "AS3", "AS7", "AS11", "EU6"));
        //Create player who will own the territories
        Player testPlayer = new Player("a");
        //Add the territories to the player own list
        testPlayer.addTerritory(testTer1);
        testPlayer.addTerritory(testTer2);
        testPlayer.addTerritory(testTer3);
        //Set the owner of the territory to the player
        testTer1.setPlayer(testPlayer);
        testTer2.setPlayer(testPlayer);
        testTer3.setPlayer(testPlayer);
        //Making hashmap to fulfil the parameter of place phase in game model
        HashMap<Territory,Integer> mt = new HashMap<>();
        //Setting up for testing
        mt.put(testTer1,3);
        mt.put(testTer2,2);
        mt.put(testTer3,1);
        game.placePhase(mt);
        assertEquals(3,testTer1.getNumArmies());
        assertEquals(2,testTer2.getNumArmies());
        assertEquals(1,testTer3.getNumArmies());
    }

}
