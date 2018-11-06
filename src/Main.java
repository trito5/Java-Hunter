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


        Player player = new Player(39, 13, '\u2620');
        List<Flake> snowFlakes = new ArrayList<>();
        List<Flake> iceCreams = new ArrayList<>();
        List<Brick> bricks = generateWalls();
        int timeCounterThreshold = 80;
        int timeCounter = 0;
        int counter = 0;
        int speedChange = 15;
        int level = 1;


        while (true) {
            KeyStroke keyStroke;
            do {
                // everything inside this loop will be called approximately every ~5 millisec.
                Thread.sleep(5);
                keyStroke = terminal.pollInput();
                if (isPlayerDead(player, snowFlakes)) {
                    break;
                }

                timeCounter++;
                if (timeCounter >= timeCounterThreshold) {

                    timeCounter = 0;

                    if (isPlayerScoring(player, iceCreams)) {
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
                    addRandomFlakes(iceCreams, 'I');
                    moveSnowFlakes(iceCreams);
                    removeDeadFlakes(iceCreams);


                    printSnowFlakes(iceCreams, terminal);


                    addRandomFlakes(snowFlakes, 'O');
                    moveSnowFlakes(snowFlakes);
                    removeDeadFlakes(snowFlakes);
                    printSnowFlakes(snowFlakes, terminal);

                    printScore(terminal, counter);
                    printPlayer(terminal, player);
                    printWalls(bricks, terminal, level);
                    printSpeed(terminal, level);

                    terminal.flush(); // don't forget to flush to see any updates!
                }

            } while (keyStroke == null);
            if (isPlayerDead(player, snowFlakes)) {
                break;
            }

            if (isPlayerScoring(player, iceCreams)) {
                counter++;

            }
            movePlayer(player, keyStroke, bricks);
            printPlayer(terminal, player);
            terminal.flush(); // don't forget to flush to see any updates!

        }
        drawGameOver(terminal, counter, level);

    }


    private static void removeDeadFlakes(List<Flake> snowFlakes) {
        List<Flake> flakesToRemove = new ArrayList<>();
        for (Flake flake : snowFlakes) {
            if (flake.getY() >= 20) {
                flakesToRemove.add(flake);
            }
        }
        snowFlakes.removeAll(flakesToRemove);
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

    private static void printSnowFlakes(List<Flake> snowFlakes, Terminal terminal) throws IOException {
        for (Flake flake : snowFlakes) {
            terminal.setCursorPosition(flake.getX(), flake.getY());
            terminal.putCharacter(flake.getSymbol());
        }

    }

    private static void printWalls(List<Brick> bricks, Terminal terminal, int level) throws IOException {
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


        for (Brick brick : bricks) {
            terminal.setCursorPosition(brick.getX(), brick.getY());
            terminal.putCharacter(brick.getSymbol());
        }
    }

    private static List<Brick> generateWalls() {
        List<Brick> tempList = new ArrayList<>();

        for (int i = 10; i <= 68; i++) {
            tempList.add(new Brick(i, 0));
        }

        for (int i = 10; i <= 68; i++) {
            tempList.add(new Brick(i, 20));
        }

        for (int i = 1; i < 20; i++) {
            tempList.add(new Brick(10, i));
        }

        for (int i = 1; i < 20; i++) {
            tempList.add(new Brick(68, i));
        }
        return tempList;
    }


    private static void moveSnowFlakes(List<Flake> snowFlakes) {
        for (Flake flake : snowFlakes) {
            flake.fall();
        }
    }

    private static void addRandomFlakes(List<Flake> snowFlakes, char symbol) {

        double probability = ThreadLocalRandom.current().nextDouble();
        if (probability <= 0.4) {
            snowFlakes.add(new Flake(ThreadLocalRandom.current().nextInt(57) + 11, 0, symbol));
        }
    }


    private static void movePlayer(Player player, KeyStroke keyStroke, List<Brick> bricks) {
        switch (keyStroke.getKeyType()) {
            case ArrowUp:
                if (!isPlayerHittingWall(player.getX(), player.getY() - 1, bricks)) {
                    player.moveUp();
                }
                break;
            case ArrowDown:
                if (!isPlayerHittingWall(player.getX(), player.getY() + 1, bricks)) {
                    player.moveDown();
                }
                break;
            case ArrowLeft:
                if (!isPlayerHittingWall(player.getX() - 1, player.getY(), bricks)) {
                    player.moveLeft();
                }
                break;
            case ArrowRight:
                if (!isPlayerHittingWall(player.getX() + 1, player.getY(), bricks)) {
                    player.moveRight();
                }
                break;
        }
    }

    private static boolean isPlayerHittingWall(int x, int y, List<Brick> bricks) {
        for (Brick brick : bricks) {
            if (x == brick.getX() && y == brick.getY()) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPlayerDead(Player player, List<Flake> flakes) throws IOException {
        for (Flake flake : flakes) {
            if (player.getX() == flake.getX() && player.getY() == flake.getY()) {


                return true;
            }
        }
        return false;
    }

    private static boolean isPlayerScoring(Player player, List<Flake> iceCreams) throws IOException {
        for (int i = 0; i < iceCreams.size(); i++) {
            if (player.getX() == iceCreams.get(i).getX() && player.getY() == iceCreams.get(i).getY()) {
                iceCreams.remove(i);
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


