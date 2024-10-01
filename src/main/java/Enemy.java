
import java.io.Console;
import java.util.Map;

/**
 * Enemy Class
 *
 * Text Adventure Game SE374 F24 Final Project Caden Finley Albert Tucker
 * Grijesh Shrestha
 */
public class Enemy {
    public final static Console console = System.console();
    public static String command;
    public static String ignore;
    private static final Map<String, Integer> enemyDamageValues = Map.copyOf(Map.ofEntries( //damage values for each enemy
            //enemies
            Map.entry("Goblin", 5),
            Map.entry("Orc", 10),
            Map.entry("Troll", 15),
            Map.entry("Bandit", 3),
            Map.entry("Skeleton", 5),
            Map.entry("Zombie", 7),
            Map.entry("Ghost", 10),
            Map.entry("Demon", 15),
            Map.entry("Vampire", 20),
            Map.entry("Werewolf", 25),
            Map.entry("Witch", 30),
            Map.entry("Giant", 35),
            Map.entry("Slime", 2),
            Map.entry("Mimic", 5),

            //minibosses
            Map.entry("Golem", 20), //dungeon 1
            Map.entry("Elemental", 25), //dungeon 2
            Map.entry("Forest Guardian", 35), //dungeon 3
            Map.entry("Minotaur", 45), //dungeon 4
            Map.entry("Sphinx", 60), //dungeon 5
            Map.entry("Cyclops", 80), //dungeon 6
            Map.entry("Medusa", 100), //dungeon 7
            Map.entry("Leviathan", 150), //dungeon 8

            //bosses
            Map.entry("Forest Giant", 30), //dungeon 1
            Map.entry("Forest Spirit", 45), //dungeon 2
            Map.entry("Wyvern", 50) //dungeon 3
    ));
    public static int spawnEnemy(String type,int quantity) throws InterruptedException { //return the total damage as negative int so that you can change health
        if(quantity > 1) {
            TextEngine.printWithDelays("You fight the " +quantity+" "+ type + "s!", false);
        } else {
            TextEngine.printWithDelays("You fight the " + type+"!", false);
        }
        checkhealth(type,quantity);
        return 0-(enemyDamageValues.get(type)*quantity);
    }
    private static void checkhealth(String type,int quantity) throws InterruptedException { //check the health of the player
        if(Player.getHealth() <= enemyDamageValues.get(type)*quantity) {
            return;
        }
        if(quantity > 1) {
            TextEngine.printWithDelays("You beat the " +quantity+" "+ type + "s!", false);
        } else {
            TextEngine.printWithDelays("You beat the " + type+"!", false);
        }
        Player.changeGold(enemyDamageValues.get(type)*quantity);
    }
}


//