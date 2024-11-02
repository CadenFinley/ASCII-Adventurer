
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

// Written by Caden Finley ACU 2024
// September 30, 2024
public class DungeonGenerator {

    public static boolean testing = false;
    public static int[][] matrix;
    public static int fails = 0;
    public static int runs = 0;
    public static String yellowColor = "\u001B[33m";
    public static String resetColor = "\u001B[0m";
    public static String redColor = "\u001B[31m";
    public static String greenColor = "\u001B[32m";
    public static String pinkColor = "\u001B[35m";
    private static int[] coord8;
    private static int[] coord9;

    public static void wipe() {
        matrix = null;
        coord8 = null;
        coord9 = null;
        if (testing) {
            System.out.println("Wiped");
        }
    }

    public static void start(int pass) {
        try {
            if (testing) {
                System.out.println("-------------------------------");
                System.out.println("Generating Matrix...");
            }
            runs++;
            if (pass < 5) {
                System.out.println("-------------------------------");
                System.out.println("Matrix size too small, retrying...");
                start(5);
                return;
            }
            int size = pass;
            if (size % 2 == 0) {
                size++;
            }
            if (size > 15) {
                size = 15;
            }
            float changeRatio = 1 + (((size * size) / 1) / 12.5f);
            if (3 + size < changeRatio) {
                changeRatio = size;
            }
            matrix = new int[size][size];
            Random rand = new Random();
            // Place 9 at a random position on the bottom row
            int x1 = size - 1;
            int y1 = rand.nextInt(size);
            matrix[x1][y1] = 9;
            // Place 8 at a random position at least size steps away from (x1, y1)
            int x2, y2;
            do {
                x2 = rand.nextInt(size);
                y2 = rand.nextInt(size);
            } while (Math.abs(x1 - x2) + Math.abs(y1 - y2) < size);
            matrix[x2][y2] = 8;
            // Draw path of 1's to connect 9 and 8
            drawPath(matrix, x1, y1, x2, y2, rand);
            // Save coordinates of 8 and 9
            coord9 = new int[]{x1, y1};
            coord8 = new int[]{x2, y2};
            // Remove 8 and 9 temporarily
            matrix[coord9[0]][coord9[1]] = 0;
            matrix[coord8[0]][coord8[1]] = 0;
            //determines how many random rooms are added
            // Randomly add at least size+size/2 more 1's ensuring they are connected to the main path
            addRandom(matrix, rand, size + (int) changeRatio + 2, 1, size);
            float itemRoomRatio = ((2 * size) - 5.5f) - (size / 2);
            if (itemRoomRatio >= size - 5) {
                itemRoomRatio = size / 2;
            }
            if (itemRoomRatio < 2) {
                itemRoomRatio = 2;
            }
            // Randomly add item rooms (2-5) ensuring they are connected to the main path 2-5 are item rooms
            if (size < 7) {
                addRandom(matrix, rand, 2, 2, size);
            } else {
                addRandom(matrix, rand, (int) itemRoomRatio, 2, size);
            }
            // Randomly add 1 rare item (3) ensuring it is connected to the main path
            addRandom(matrix, rand, 1, 3, size);
            // Randomly add 1 shop room (6) ensuring it is connected to the main path
            addRandom(matrix, rand, 1, 6, size);
            // Randomly add mini boss rooms (4) ensuring it is connected to the main path
            addRandom(matrix, rand, 1, 4, size);
            // Randomly add shop rooms (4) ensuring it is connected to the main path
            //addRandom(matrix, rand, 1, 7);
            // Ensure only one 1 value is adjacent to the 8
            ensureSingleAdjacent(matrix, coord8[0], coord8[1]);
            // Place 8 and 9 back in their original positions
            matrix[coord9[0]][coord9[1]] = 9;
            matrix[coord8[0]][coord8[1]] = 8;
            matrix = trimUnreachableParts(matrix, findValue(matrix, 9));
            if (testArrays(matrix)) {
                if (testing) {
                    printMap(matrix);
                    System.out.println("^^^^^^^^^^^^" + size + "^^^^^^^^^^^^");
                    System.out.println("Matrix connected successfully!");
                    System.out.println("Item Rooms: " + numberOfRooms(matrix, 2) + " Total Rooms: " + (numberOfRooms(matrix, 1) + numberOfRooms(matrix, 2) + numberOfRooms(matrix, 3) + numberOfRooms(matrix, 8) + numberOfRooms(matrix, 9) + numberOfRooms(matrix, 4)));
                    System.out.println("-------------------------------");
                }
                return;
            }
            if (testing) {
                printMap(matrix);
                System.out.println("^^^^^^^^^^^^" + size + "^^^^^^^^^^^^");
                System.out.println("Matrix not connected, retrying...");
                System.out.println("-------------------------------");
            }
            fails++;
            wipe();
            start(size);
        } catch (Exception e) {
            fails++;
            System.out.println("Error: " + e);
            wipe();
            start(pass);
        }
    }

    private static void drawPath(int[][] matrix, int x1, int y1, int x2, int y2, Random rand) {
        while (x1 != x2 || y1 != y2) {
            if (rand.nextBoolean()) {
                // Randomly decide whether to move in the x or y direction
                if (x1 != x2 && rand.nextBoolean()) {
                    x1 += (x1 < x2) ? 1 : -1;
                } else if (y1 != y2) {
                    y1 += (y1 < y2) ? 1 : -1;
                }
            } else {
                // Move in the perpendicular direction
                if (y1 != y2 && rand.nextBoolean()) {
                    y1 += (y1 < y2) ? 1 : -1;
                } else if (x1 != x2) {
                    x1 += (x1 < x2) ? 1 : -1;
                }
            }
            if (matrix[x1][y1] == 0) {
                matrix[x1][y1] = 1;
            }
        }
        if (testing) {
            System.out.println("Path drawn between 8 and 9");
            printMap(matrix);
        }
    }

    private static void addRandom(int[][] matrix, Random rand, int minOnes, int num, int matrixSize) {
        try {
            if (hasValue(matrix, num) && num != 1) {
                fails++;
                System.err.println("Error: " + num + " already exists, retrying...");
                start(matrixSize);
            }
            if (testing) {
                System.out.println("Adding " + num);
            }
            int addedOnes = 0;
            int x, y;
            while (addedOnes < minOnes) {
                do {
                    x = rand.nextInt(matrix.length);
                    y = rand.nextInt(matrix.length);
                } while (!isConnected(matrix, x, y) || matrix[x][y] > 1 || (coord8[0] == x && coord8[1] == y) || (coord9[0] == x && coord9[1] == y));
                matrix[x][y] = num;
                addedOnes++;
            }
            if (testing) {
                printMap(matrix);
                System.out.println("Added " + num);
            }
            if (numberOfAllRooms(matrix) < matrixSize + 2) {
                if (testing) {
                    System.out.println(redColor + "Not enough rooms, retrying..." + resetColor);
                }
                wipe();
                start(matrixSize);
            }
        } catch (Exception e) {
            fails++;
            System.out.println("Error: " + e);
            wipe();
            start(matrixSize);
        }
    }

    private static boolean isConnected(int[][] matrix, int x, int y) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < matrix.length && ny >= 0 && ny < matrix.length && matrix[nx][ny] == 1) {
                return true;
            }
        }
        return false;
    }

    private static void ensureSingleAdjacent(int[][] matrix, int x, int y) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        int adjacentNonZeroCount = 0;
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < matrix.length && ny >= 0 && ny < matrix[0].length && matrix[nx][ny] > 0) {
                adjacentNonZeroCount++;
                if (adjacentNonZeroCount > 1) {
                    matrix[nx][ny] = 0;
                }
            }
        }
        if (testing) {
            System.out.println("Ensured single adjacent to 8");
            printMap(matrix);
        }
    }

    public static boolean isPathConnected(int[][] matrix, int x1, int y1, int x2, int y2) {
        int size = matrix.length;
        boolean[][] visited = new boolean[size][size];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{x1, y1});
        visited[x1][y1] = true;
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cx = current[0];
            int cy = current[1];
            if (cx == x2 && cy == y2) {
                return true;
            }
            for (int i = 0; i < 4; i++) {
                int nx = cx + dx[i];
                int ny = cy + dy[i];
                if (nx >= 0 && nx < size && ny >= 0 && ny < size && !visited[nx][ny] && matrix[nx][ny] > 0) {
                    queue.add(new int[]{nx, ny});
                    visited[nx][ny] = true;
                }
            }
        }
        return false;
    }

    public static boolean testArrays(int[][] localMatrix) {
        int[] pos9 = findValue(localMatrix, 9);
        int[] pos8 = findValue(localMatrix, 8);
        int[] pos3 = findValue(localMatrix, 3);
        int[] pos2 = findValue(localMatrix, 2);
        int[] pos4 = findValue(localMatrix, 4);
        int[] pos6 = findValue(localMatrix, 6);

        if (pos9 == null || pos8 == null || pos3 == null || pos2 == null || pos4 == null || pos6 == null) {
            return false;
        }
        return (isPathConnected(localMatrix, pos9[0], pos9[1], pos8[0], pos8[1]) && isPathConnected(localMatrix, pos9[0], pos9[1], pos3[0], pos3[1]) && isPathConnected(localMatrix, pos9[0], pos9[1], pos2[0], pos2[1]) && isPathConnected(localMatrix, pos9[0], pos9[1], pos4[0], pos4[1]) && !isAdjacent(pos8[0], pos8[1], pos6) && !isAdjacent(pos8[0], pos8[1], pos4) && !isAdjacent(pos8[0], pos8[1], pos3) && numberOfRooms(localMatrix, 2) >= 2);
    }

    public static int[] findValue(int[][] matrix, int value) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == value) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private static boolean isAdjacent(int x, int y, int[] playerPosition) {
        int px = playerPosition[0];
        int py = playerPosition[1];
        return (Math.abs(px - x) == 1 && py == y) || (Math.abs(py - y) == 1 && px == x);
    }

    public static int[][] trimUnreachableParts(int[][] matrix, int[] startPosition) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(startPosition);
        visited[startPosition[0]][startPosition[1]] = true;
        // Directions for moving up, down, left, right
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        // Perform BFS to mark all reachable cells
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (nx >= 0 && nx < rows && ny >= 0 && ny < cols && !visited[nx][ny] && matrix[nx][ny] != 0) {
                    queue.add(new int[]{nx, ny});
                    visited[nx][ny] = true;
                }
            }
        }
        if (testing) {
            System.out.println("Found Unreachable Cells");
            printMap(matrix);
        }
        // Set all unreachable cells to 0
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!visited[i][j]) {
                    matrix[i][j] = 0;
                }
            }
        }
        if (testing) {
            System.out.println("Removed Unreachable Cells");
            printMap(matrix);
        }
        return matrix;
    }

    public static int[][] generateAndReturnMatrix(int size) {
        wipe();
        start(size);
        return matrix;
    }

    public static int[] getDirections(int[][] matrix, int x, int y) {
        List<Integer> directions = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) {
            int nx = x + dx[i];
            int ny = y + dy[i];
            if (nx >= 0 && nx < matrix.length && ny >= 0 && ny < matrix[0].length) {
                directions.add(matrix[nx][ny]);
            } else {
                directions.add(0); // Add 0 if out of bounds
            }
        }
        return directions.stream().mapToInt(i -> i).toArray();
    }

    public static int[][] createRoomsBeenTo(int size) {
        int[][] temp = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                temp[i][j] = 0;
            }
        }
        return temp;
    }

    public static int numberOfRooms(int[][] matrix, int find) {
        int count = 0;
        for (int[] ints : matrix) {
            for (int j = 0; j < matrix.length; j++) {
                if (ints[j] == find) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int numberOfAllRooms(int[][] matrix) {
        int count = 0;
        for (int[] ints : matrix) {
            for (int j = 0; j < matrix.length; j++) {
                if (ints[j] != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public static void printMap(int[][] passedMatrix) {
        for (int[] passedMatrix1 : passedMatrix) {
            for (int j = 0; j < passedMatrix.length; j++) {
                if (passedMatrix1[j] != 0) {
                    System.out.print("[" + passedMatrix1[j] + "] ");
                } else {
                    System.out.print("    ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private static boolean hasValue(int[][] matrix, int value) {
        for (int[] ints : matrix) {
            for (int j = 0; j < matrix.length; j++) {
                if (ints[j] == value) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void printAdjacentRoomsAndCurrentRoomAndUnlockedRooms(int[][] passedMatrix, int[][] unlocked, int[] passedPosition, boolean revealed) {
        if (!revealed) {
            for (int i = 0; i < passedMatrix.length; i++) {
                for (int j = 0; j < passedMatrix.length; j++) {
                    if (passedMatrix[i][j] != 0) {
                        if (i == passedPosition[0] && j == passedPosition[1]) {
                            System.out.print(yellowColor + "[P] " + resetColor);
                        } else if (isAdjacent(i, j, passedPosition)) {
                            switch (passedMatrix[i][j]) {
                                case 2, 5, 7 -> {
                                    if (unlocked[i][j] > 0) {
                                        System.out.print("[■] ");
                                    } else {
                                        System.out.print(greenColor + "[?] " + resetColor);
                                    }
                                }
                                case 8 ->
                                    System.out.print(redColor + "[B] " + resetColor);
                                case 9 ->
                                    System.out.print("[S] ");
                                case 6 ->
                                    System.out.print(greenColor + "[$] " + resetColor);
                                case 4 -> {
                                    if (unlocked[i][j] > 0) {
                                        System.out.print("[■] ");
                                    } else {
                                        System.out.print(redColor + "[!] " + resetColor);
                                    }
                                }
                                case 10 -> {
                                    if (unlocked[i][j] > 0) {
                                        System.out.print("[■] ");
                                    } else {
                                        System.out.print(pinkColor + "[F] " + resetColor);
                                    }
                                }
                                default ->
                                    System.out.print("[~] ");
                            }
                        } else if (unlocked[i][j] > 0) {
                            switch (unlocked[i][j]) { //icon for visited rooms
                                case 6 ->
                                    System.out.print(greenColor + "[$] " + resetColor); // Special marker for shop/trap room
                                case 8 ->
                                    System.out.print(redColor + "[B] " + resetColor); // Special marker for boss room
                                case 9 -> {
                                    if (isAdjacent(i, j, passedPosition)) {
                                        System.out.print("[~] ");
                                    } else {
                                        System.out.print("[S] ");
                                    }
                                }
                                default -> {
                                    if (isAdjacent(i, j, passedPosition)) {
                                        System.out.print("[~] ");
                                    } else {
                                        System.out.print("[■] "); // Default case for other values
                                    }
                                }
                            }
                        } else {
                            System.out.print("[ ] "); // Print brackets around non-zero values
                        }
                    } else if (unlocked[i][j] == 1) {
                        System.out.print("[ ] "); // Print brackets around 0 values if unlocked
                    } else {
                        System.out.print("    "); // No brackets around 0 values
                    }
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Map Key: [P] Player\n [?] Item Room\n [B] Boss Room\n [S] Spawn Room\n [$] Shop Room\n [F] Fairy Room");
            System.out.println(" [~] Available Moves\n [ ] Unvisited Room\n [■] Visited Room");
        } else {
            for (int i = 0; i < passedMatrix.length; i++) {
                for (int j = 0; j < passedMatrix.length; j++) {
                    if (passedMatrix[i][j] != 0) {
                        if (i == passedPosition[0] && j == passedPosition[1]) {
                            System.out.print(yellowColor + "[P] " + resetColor);
                        } else {
                            switch (passedMatrix[i][j]) {
                                case 2, 7 -> {
                                    if (unlocked[i][j] == 2 || unlocked[i][j] == 7) {
                                        if (isAdjacent(i, j, passedPosition)) {
                                            System.out.print("[~] ");
                                        } else {
                                            System.out.print("[■] ");
                                        }
                                    } else {
                                        System.out.print(greenColor + "[?] " + resetColor);
                                    }
                                }
                                case 3, 5 -> {
                                    if (unlocked[i][j] == 3 || unlocked[i][j] == 5) {
                                        if (isAdjacent(i, j, passedPosition)) {
                                            System.out.print("[~] ");
                                        } else {
                                            System.out.print("[■] ");
                                        }
                                    } else {
                                        System.out.print(greenColor + "[K] " + resetColor);
                                    }
                                }
                                case 8 ->
                                    System.out.print(redColor + "[B] " + resetColor);
                                case 4 -> {
                                    if (unlocked[i][j] == 4) {
                                        if (isAdjacent(i, j, passedPosition)) {
                                            System.out.print("[~] ");
                                        } else {
                                            System.out.print("[■] ");
                                        }
                                    } else {
                                        System.out.print(redColor + "[!] " + resetColor);
                                    }
                                }
                                case 9 -> {
                                    System.out.print("[S] ");
                                }
                                case 6 -> {
                                    System.out.print(greenColor + "[$] " + resetColor);
                                }
                                case 1 -> {
                                    if (unlocked[i][j] == 1) {
                                        if (isAdjacent(i, j, passedPosition)) {
                                            System.out.print("[~] ");
                                        } else {
                                            System.out.print("[■] ");
                                        }
                                    } else {
                                        System.out.print("[ ] ");
                                    }
                                }
                                case 10 -> {
                                    if (unlocked[i][j] == 10) {
                                        if (isAdjacent(i, j, passedPosition)) {
                                            System.out.print("[~] ");
                                        } else {
                                            System.out.print("[■] ");
                                        }
                                    } else {
                                        System.out.print(pinkColor + "[F] " + resetColor);
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.print("    ");
                    }
                }
                System.out.println();
            }
            System.out.println();
            System.out.println("Map Key: [P] Player\n [?] Item Room\n [B] Boss Room\n [S] Spawn Room\n [$] Shop Room\n [F] Fairy Room\n [K] Key Room");
        }
    }
}
