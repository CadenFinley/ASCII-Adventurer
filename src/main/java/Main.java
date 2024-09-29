import java.io.Console;

/**
 * Room Engine /make this game engine and each room or area of rooms in their
 * own individual classes
 *
 * Text Adventure Game SE374 F24 Final Project Caden Finley Albert Tucker
 * Grijesh Shrestha
 */
//need to create enemy damage calcualtor class  Enemy.spawnEnemy(Bandit,3) returns 15 damage for example  or Enemy.spawnEnemy(Bandit,2)+Enemy.spawnEnemy(Dragon,1) equals 20 damage
class Main {

    private final static Console console = System.console();
    private static String command;
    private static String ignore;
    public static boolean playerCreated = false;
    public static String savedPlace = null;
    private static final String OS_NAME = System.getProperty("os.name");
    public static void main(String[] args) throws InterruptedException {
        createGameItems();
        startMenu();
    }
    private static void createGameItems(){
        //the value is equal to the damage,defense, or healing potential the item provides
        //this is only to use when you use the item not when you have it in your inventory or when it is on the map
        InventoryManager.createItem("weapon","Sword", 2);
        InventoryManager.createItem("weapon","Axe", 3);
        InventoryManager.createItem("weapon","Bow", 3);
        InventoryManager.createItem("weapon","Great Sword", 10);
        InventoryManager.createItem("armor","Shield", 1);
        InventoryManager.createItem("armor","Chainmail Set", 2);
        InventoryManager.createItem("armor","Full armor Kit", 3);
        InventoryManager.createItem("armor","Angel Armor", 7);
        InventoryManager.createItem("potion","Health Potion", 10);
        InventoryManager.createItem("potion","Heart Container", 10);
        InventoryManager.createItem("key","Key", 0);
    }
    public static void startMenu() throws InterruptedException {
        TextEngine.clearScreen();
        TextEngine.printNoDelay("Adventure V1, by BarrettHall: Albert Tucker, Caden Finley, and Grijesh Shrestha", false);
        if (hasSave()) {
            TextEngine.printNoDelay("Welcome " + Player.getName() + "!", false);
        } else {
            TextEngine.printNoDelay("Welcome Hero!", false);
        }
        TextEngine.printWithDelays("What is your command: Start, Settings, Exit", true);
        while (true) {
            ignore = console.readLine();
            command = console.readLine();
            switch (command.toLowerCase()) {
                case "start":
                    TextEngine.clearScreen();
                    start();
                case "settings":
                    TextEngine.clearScreen();
                    SettingsMenu.start();
                case "help":
                    TextEngine.printWithDelays("You can type 'start' to start the game\nor 'settings' to change the text speed\nor 'exit' to leave the game.", true);
                    continue;
                case "exit":
                    TextEngine.printWithDelays("See ya next time!", false);
                    TextEngine.printNoDelay("Press Enter to Continue)", false);
                    ignore = console.readLine();
                    TextEngine.clearScreen();
                    System.exit(0);
                case "fast":
                    TextEngine.speedSetting = "Fast";
                    TextEngine.printWithDelays("Text speed set to Fast", true);
                    continue;
                case "slow":
                    TextEngine.speedSetting = "Slow";
                    TextEngine.printWithDelays("Text speed set to Slow", true);
                    continue;
                case "normal":
                    TextEngine.speedSetting = "Normal";
                    TextEngine.printWithDelays("Text speed set to Normal", true);
                    continue;
                case "nodelay":
                    TextEngine.speedSetting = "NoDelay";
                    TextEngine.printWithDelays("Text speed set to NoDelay", true);
                    continue;
                case "debug":
                    TextEngine.speedSetting = "NoDelay";
                    TextEngine.clearScreen();
                    TextEngine.printWithDelays("Using System Property: " + getOS_NAME(), false);
                    TextEngine.printWithDelays("Using Console: " + console, false);
                    TextEngine.printWithDelays("Text Speed: " + TextEngine.speedSetting, false);
                    TextEngine.printNoDelay("(Press enter to continue)", false);
                    ignore = console.readLine();    
                    Player.debugStart();
                default:
                    TextEngine.printWithDelays("I'm sorry, I don't understand that command.", true);
            }
        }
    }

    public static void inGameDefaultTextHandling(String data) throws InterruptedException {
        switch (data) {
            case "help" ->{
                if(getSavedPlace().equals("Dungeon")){
                    TextEngine.printWithDelays("You can type 'restart' to restart the dungeon", false);
                }
                TextEngine.printWithDelays("You can type 'stats' to see your stats\n'inventory' to see your inventory\n'settings' or type 'save' to save \n or 'exit' to return to the main menu.", true); 
            }
            case "inventory" ->
                Player.openInventory();
            case "settings" -> {
                SettingsMenu.start();
                TextEngine.clearScreen();
            }
            case "save" ->
                checkSave(getSavedPlace());
            case "exit" -> {
                TextEngine.printWithDelays("Returning to main menu.", false);
                TextEngine.clearScreen();
                startMenu();
            }
            case "stats" -> {
                Player.printStats();
            }
            default ->
                TextEngine.printWithDelays("I'm sorry, I don't understand that command.", true);
        }
    }

    public static void saveSpace(String place) throws InterruptedException {
        if (savedPlace != null) {
            TextEngine.printWithDelays("Game saved!", false);
        }
        savedPlace = place;
    }

    public static void loadSave() throws InterruptedException {
        if (getSavedPlace() == null) {
            startMenu();
        } else {
            switch (getSavedPlace()) {
                case "SpawnRoom" ->
                    SpawnRoom.startRoom();
                case "OpenWorld" ->
                    OpenWorld.startRoom();
                case "Village" ->
                    Village.startRoom();
                case "Dungeon" ->
                    Dungeon.startRoom();
                default ->
                    startMenu();
            }
        }
    }

    public static void wipeSave() throws InterruptedException {
        savedPlace = null;
        Room.reset("all");
    }

    public static int getRoomId() {
        return Room.getRoom();
    }

    public static String getSavedPlace() {
        return savedPlace;
    }

    public static boolean hasSave() {
        return getSavedPlace() != null;
    }

    public static void checkSave(String place) throws InterruptedException {
        if (!hasSave()) {
            saveSpace(place);
        } else if (!getSavedPlace().equals(place)) {
            saveSpace(place);
        }
    }

    public static void start() throws InterruptedException {
        if (hasSave()) {
            TextEngine.printWithDelays("Would you like to load your saved game? (yes or no) ", true);
            ignore = console.readLine();
            command = console.readLine();
            if (command.toLowerCase().equals("no")) {
                String textState = TextEngine.speedSetting;
                TextEngine.speedSetting = "Slow";
                TextEngine.printWithDelays("All data will be wiped if you proceed. (yes or no) ", false);
                TextEngine.printWithDelays("Are you sure?", true);
                ignore = console.readLine();
                command = console.readLine();
                if (command.toLowerCase().equals("yes")) {
                    TextEngine.clearScreen();
                    TextEngine.printWithDelays("Starting new game...", false);
                    TextEngine.speedSetting = textState;
                    wipeSave();
                    Player.playerStart();
                } else {
                    TextEngine.speedSetting = textState;
                    loadSave();
                }
            } else {
                loadSave();
            }
        } else if (playerCreated) {
            saveSpace("SpawnRoom");
            loadSave();
        } else {
            Player.playerStart();
        }
    }
    public static void printStatus() {
        TextEngine.printNoDelay(Player.getName(), false);
        TextEngine.printNoDelay("Health: " + Player.getHealth(), false);
        TextEngine.printNoDelay(getSavedPlace() + " " + getRoomId() + "\n", false);
        if(getSavedPlace().equals("Dungeon")){
            TextEngine.printNoDelay(Dungeon.getDungeon(), false);
        }
        TextEngine.printNoDelay("\n", false);
    }

    public static void screenRefresh() throws InterruptedException {
        TextEngine.clearScreen();
        printStatus();
    }

    public static String getOS_NAME() {
        return OS_NAME;
    }
}
