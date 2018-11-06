import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        try {
            startSimulation();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);

        } catch (NullPointerException e) {
            System.out.println("Fel: " + e);
        } finally {
            System.out.println("Simulation over!");
        }

    }

    private static void startSimulation() throws IOException, InterruptedException {
        Terminal terminal = createTerminal();

        simulationLoop(terminal);
    }

    private static Terminal createTerminal() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setCursorVisible(false);
        return terminal;
    }

    private static void simulationLoop(Terminal terminal) throws InterruptedException, IOException {

        Player player = new Player(39, 13, '\u263B');
        List<FallingObject> sharpList = new ArrayList<>();
        List<FallingObject> javaList = new ArrayList<>();
        List<Brick> brickList = generateWalls();
        int timeCounterThreshold = 80;
        int timeCounter = 0;
        int counter = 0;
        int speedChange = 15;
        int level = 1;

        while (true) {
            KeyStroke keyStroke;
            do {
                // Looping every 5 milliseconds
                Thread.sleep(5);
                keyStroke = terminal.pollInput();
                if (isPlayerDead(player, sharpList)) {
                    break;
                }

                timeCounter++;
                if (timeCounter >= timeCounterThreshold) {
                    timeCounter = 0;

                    //If player scores check to see if level should increase
                    if (isPlayerScoring(player, javaList)) {
                        counter++;
                        if (counter > 10 && counter % 5 == 0) {
                            timeCounterThreshold -= speedChange;
                            speedChange--;
                            level++;
                        } else if (counter <= 10 && counter % 3 == 0) {
                            timeCounterThreshold -= speedChange;
                            speedChange--;
                            level++;
                        }
                    }

                    terminal.clearScreen();

                    //Creating falling Java cups
                    addRandomFallingObjects(javaList, '\u2615');
                    moveFallingObjects(javaList);
                    removeDeadObjects(javaList);
                    terminal.setForegroundColor(TextColor.ANSI.GREEN);
                    printFallingObjects(javaList, terminal);

                    //Creating falling Sharp symbols
                    addRandomFallingObjects(sharpList, '\u266F');
                    moveFallingObjects(sharpList);
                    removeDeadObjects(sharpList);
                    terminal.setForegroundColor(TextColor.ANSI.RED);
                    printFallingObjects(sharpList, terminal);

                    //Printouts
                    terminal.setForegroundColor(TextColor.ANSI.WHITE);
                    printScore(terminal, counter);
                    printSpeed(terminal, level);
                    terminal.setForegroundColor(TextColor.ANSI.CYAN);
                    printGameName(terminal);

                    //Printing player and walls
                    terminal.setForegroundColor(TextColor.ANSI.WHITE);
                    printPlayer(terminal, player);
                    printWalls(brickList, terminal, level);

                    //Flushing to see any updates
                    terminal.flush();
                }

            } while (keyStroke == null);
            if (isPlayerDead(player, sharpList)) {
                break;
            }

            //If player scores check to see if level should increase
            if (isPlayerScoring(player, javaList)) {
                counter++;
                if (counter > 10 && counter % 5 == 0) {
                    timeCounterThreshold -= speedChange;
                    speedChange--;
                    level++;
                } else if (counter <= 10 && counter % 3 == 0) {
                    timeCounterThreshold -= speedChange;
                    speedChange--;
                    level++;
                }
            }

            //Sets players position
            movePlayer(player, keyStroke, brickList);
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            printPlayer(terminal, player);

            //Flushing to see any updates
            terminal.flush();

        }

        terminal.setForegroundColor(TextColor.ANSI.RED);
        drawGameOver(terminal, counter, level);

    }

    private static void removeDeadObjects(List<FallingObject> fallingObjects) {
        List<FallingObject> objectsToRemove = new ArrayList<>();
        for (FallingObject fallingObject : fallingObjects) {
            if (fallingObject.getY() >= 20) {
                objectsToRemove.add(fallingObject);
            }
        }
        fallingObjects.removeAll(objectsToRemove);
    }

    private static void printGameName(Terminal terminal) throws IOException {
        terminal.setCursorPosition(35, 23);
        terminal.putCharacter('J');
        terminal.setCursorPosition(36, 23);
        terminal.putCharacter('A');
        terminal.setCursorPosition(37, 23);
        terminal.putCharacter('V');
        terminal.setCursorPosition(38, 23);
        terminal.putCharacter('A');
        terminal.setCursorPosition(40, 23);
        terminal.putCharacter('H');
        terminal.setCursorPosition(41, 23);
        terminal.putCharacter('U');
        terminal.setCursorPosition(42, 23);
        terminal.putCharacter('N');
        terminal.setCursorPosition(43, 23);
        terminal.putCharacter('T');
        terminal.setCursorPosition(44, 23);
        terminal.putCharacter('E');
        terminal.setCursorPosition(45, 23);
        terminal.putCharacter('R');
    }

    private static void printScore(Terminal terminal, int counter) throws IOException {
        terminal.setCursorPosition(0, 30);
        terminal.putCharacter('S');
        terminal.setCursorPosition(1, 30);
        terminal.putCharacter('C');
        terminal.setCursorPosition(2, 30);
        terminal.putCharacter('O');
        terminal.setCursorPosition(3, 30);
        terminal.putCharacter('R');
        terminal.setCursorPosition(4, 30);
        terminal.putCharacter('E');
        terminal.setCursorPosition(5, 30);
        terminal.putCharacter(':');
        terminal.setCursorPosition(6, 30);
        terminal.putCharacter(' ');
        String score = Integer.toString(counter);

        for (int i = 0; i < score.length(); i++) {
            terminal.setCursorPosition(i + 7, 30);
            terminal.putCharacter(score.charAt(i));
        }
    }

    private static void printSpeed(Terminal terminal, int level) throws IOException {
        terminal.setCursorPosition(70, 23);
        terminal.putCharacter('S');
        terminal.setCursorPosition(71, 23);
        terminal.putCharacter('P');
        terminal.setCursorPosition(72, 23);
        terminal.putCharacter('E');
        terminal.setCursorPosition(73, 23);
        terminal.putCharacter('E');
        terminal.setCursorPosition(74, 23);
        terminal.putCharacter('D');
        terminal.setCursorPosition(75, 23);
        terminal.putCharacter(':');
        terminal.setCursorPosition(76, 23);
        terminal.putCharacter(' ');
        String levels = Integer.toString(level);
        terminal.setCursorPosition(77, 23);
        terminal.putCharacter(levels.charAt(0));
    }

    private static void printPlayer(Terminal terminal, Player player) throws IOException {

        terminal.setCursorPosition(player.getPreviousX(), player.getPreviousY());
        terminal.putCharacter(' ');

        terminal.setCursorPosition(player.getX(), player.getY());
        terminal.putCharacter(player.getSymbol());
    }

    private static void printFallingObjects(List<FallingObject> fallingObjectList, Terminal terminal) throws IOException {

        for (FallingObject fallingObject : fallingObjectList) {
            terminal.setCursorPosition(fallingObject.getX(), fallingObject.getY());
            terminal.putCharacter(fallingObject.getSymbol());
        }
    }

    private static void printWalls(List<Brick> brickList, Terminal terminal, int level) throws IOException {
        switch (level) {
            case 1:
                terminal.setForegroundColor(TextColor.ANSI.WHITE);
                break;
            case 2:
                terminal.setForegroundColor(TextColor.ANSI.YELLOW);
                break;
            case 3:
                terminal.setForegroundColor(TextColor.ANSI.GREEN);
                break;
            case 4:
                terminal.setForegroundColor(TextColor.ANSI.CYAN);
                break;
            case 5:
                terminal.setForegroundColor(TextColor.ANSI.BLUE);
                break;
            case 6:
                terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                break;
            case 7:
                terminal.setForegroundColor(TextColor.ANSI.RED);
                break;
            default:
                terminal.setForegroundColor(TextColor.ANSI.WHITE);
                break;
        }

        for (Brick brick : brickList) {
            terminal.setCursorPosition(brick.getX(), brick.getY());
            terminal.putCharacter(brick.getSymbol());
        }
    }

    private static List<Brick> generateWalls() {
        List<Brick> temporaryList = new ArrayList<>();

        for (int i = 10; i <= 68; i++) {
            temporaryList.add(new Brick(i, 0));
        }

        for (int i = 10; i <= 68; i++) {
            temporaryList.add(new Brick(i, 20));
        }

        for (int i = 1; i < 20; i++) {
            temporaryList.add(new Brick(10, i));
        }

        for (int i = 1; i < 20; i++) {
            temporaryList.add(new Brick(68, i));
        }
        return temporaryList;
    }

    private static void moveFallingObjects(List<FallingObject> fallingObjectList) {
        for (FallingObject fallingObject : fallingObjectList) {
            fallingObject.fall();
        }
    }

    private static void addRandomFallingObjects(List<FallingObject> fallingObjectList, char symbol) {

        double probability = ThreadLocalRandom.current().nextDouble();
        if (probability <= 0.4) {
            fallingObjectList.add(new FallingObject(ThreadLocalRandom.current().nextInt(57) + 11, 0, symbol));
        }
    }

    private static void movePlayer(Player player, KeyStroke keyStroke, List<Brick> brickList) {
        switch (keyStroke.getKeyType()) {
            case ArrowUp:
                if (!isPlayerHittingWall(player.getX(), player.getY() - 1, brickList)) {
                    player.moveUp();
                }
                break;
            case ArrowDown:
                if (!isPlayerHittingWall(player.getX(), player.getY() + 1, brickList)) {
                    player.moveDown();
                }
                break;
            case ArrowLeft:
                if (!isPlayerHittingWall(player.getX() - 1, player.getY(), brickList)) {
                    player.moveLeft();
                }
                break;
            case ArrowRight:
                if (!isPlayerHittingWall(player.getX() + 1, player.getY(), brickList)) {
                    player.moveRight();
                }
                break;
        }
    }

    private static boolean isPlayerHittingWall(int x, int y, List<Brick> brickList) {
        for (Brick brick : brickList) {
            if (x == brick.getX() && y == brick.getY()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPlayerDead(Player player, List<FallingObject> fallingObjectList) throws IOException {
        for (FallingObject fallingObject : fallingObjectList) {
            if (player.getX() == fallingObject.getX() && player.getY() == fallingObject.getY()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPlayerScoring(Player player, List<FallingObject> fallingObjectList) throws IOException {
        for (int i = 0; i < fallingObjectList.size(); i++) {
            if (player.getX() == fallingObjectList.get(i).getX() && player.getY() == fallingObjectList.get(i).getY()) {
                fallingObjectList.remove(i);
                return true;
            }
        }
        return false;
    }

    private static void drawGameOver(Terminal terminal, int counter, int level) throws IOException {
        terminal.clearScreen();
        terminal.setCursorPosition(33, 10);
        terminal.putCharacter('G');
        terminal.setCursorPosition(34, 10);
        terminal.putCharacter('A');
        terminal.setCursorPosition(35, 10);
        terminal.putCharacter('M');
        terminal.setCursorPosition(36, 10);
        terminal.putCharacter('E');
        terminal.setCursorPosition(38, 10);
        terminal.putCharacter('O');
        terminal.setCursorPosition(39, 10);
        terminal.putCharacter('V');
        terminal.setCursorPosition(40, 10);
        terminal.putCharacter('E');
        terminal.setCursorPosition(41, 10);
        terminal.putCharacter('R');
        printScore(terminal, counter);
        printSpeed(terminal, level);
        terminal.flush();
    }
}
