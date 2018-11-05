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


        Player player = new Player(10, 10, '\u2620');
        List<Flake> snowFlakes = new ArrayList<>();
        List<Flake> iceCreams = new ArrayList<>();
        final int timeCounterThreshold = 80;
        int timeCounter = 0;

        int counter = 0;

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
                        System.out.println(counter);
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


                    terminal.flush(); // don't forget to flush to see any updates!
                }

            } while (keyStroke == null);
            if (isPlayerDead(player, snowFlakes)){
                break;
            }

            if (isPlayerScoring(player, iceCreams)) {
                counter++;
                System.out.println(counter);
            }
            movePlayer(player, keyStroke);
            printPlayer(terminal, player);
            terminal.flush(); // don't forget to flush to see any updates!

        }
        drawGameOver(terminal);

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
        terminal.setCursorPosition(7, 30);


    }

    private static void printPlayer(Terminal terminal, Player player) throws IOException {
        terminal.setCursorPosition(player.getPreviousX(), player.getPreviousY());
        terminal.putCharacter(' ');

        terminal.setCursorPosition(player.getX(), player.getY());
        terminal.putCharacter(player.getSymbol());

    }

    private static void printSnowFlakes(List<Flake> snowFlakes, Terminal terminal) throws IOException {
//        terminal.clearScreen();
        for (Flake flake : snowFlakes) {
            terminal.setCursorPosition(flake.getX(), flake.getY());
            terminal.putCharacter(flake.getSymbol());
        }

    }


    private static void moveSnowFlakes(List<Flake> snowFlakes) {
        for (Flake flake : snowFlakes) {
            flake.fall();
        }
    }

    private static void addRandomFlakes(List<Flake> snowFlakes, char symbol) {

        double probability = ThreadLocalRandom.current().nextDouble();
        if (probability <= 0.4) {
            snowFlakes.add(new Flake(ThreadLocalRandom.current().nextInt(30), 0, symbol));
        }
    }


    private static void movePlayer(Player player, KeyStroke keyStroke) {
        switch (keyStroke.getKeyType()) {
            case ArrowUp:
                player.moveUp();
                break;
            case ArrowDown:
                player.moveDown();
                break;
            case ArrowLeft:
                player.moveLeft();
                break;
            case ArrowRight:
                player.moveRight();
                break;
        }
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
        for (Flake iceCream : iceCreams) {
            if (player.getX() == iceCream.getX() && player.getY() == iceCream.getY()) {

                return true;
            }
        }
        return false;
    }

    private static void drawGameOver(Terminal terminal) throws IOException {
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
        terminal.flush();

    }



}


