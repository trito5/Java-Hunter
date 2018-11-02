import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        try {
            startSimulation();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
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

        while (true) {
            KeyStroke keyStroke;
            do {
                // everything inside this loop will be called approximately every ~5 millisec.
                Thread.sleep(5);
                keyStroke = terminal.pollInput();

                timeCounter++;
                if (timeCounter >= timeCounterThreshold) {
                    timeCounter = 0;

                    addRandomIce(iceCreams, 'I');
                    moveSnowFlakes(iceCreams);
                    removeDeadFlakes(iceCreams);
                    printSnowFlakes(iceCreams, terminal);

//                    addRandomFlakes(snowFlakes, 'O');
//                    moveSnowFlakes(snowFlakes);
//                    removeDeadFlakes(snowFlakes);
//                    printSnowFlakes(snowFlakes, terminal);

                    printPlayer(terminal, player);
                    isPlayerDead(player, snowFlakes);

                    terminal.flush(); // don't forget to flush to see any updates!
                }


            } while (keyStroke == null);

            movePlayer(player, keyStroke);
            printPlayer(terminal, player);
            terminal.flush(); // don't forget to flush to see any updates!
        }
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

    private static void printPlayer(Terminal terminal, Player player) throws IOException {
        terminal.setCursorPosition(player.getPreviousX(), player.getPreviousY());
        terminal.putCharacter(' ');

        terminal.setCursorPosition(player.getX(), player.getY());
        terminal.putCharacter(player.getSymbol());

    }

    private static void printSnowFlakes(List<Flake> snowFlakes, Terminal terminal) throws IOException {
        terminal.clearScreen();
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
        if (probability <= 0.5) {
            snowFlakes.add(new Flake(ThreadLocalRandom.current().nextInt(30), 0, symbol));
        }
    }

    private static void addRandomIce(List<Flake> snowFlakes, char symbol) {

        Random random = new Random();
        int probability = random.nextInt(10) + 1;
        if (probability > 1)
            snowFlakes.add(new Flake(ThreadLocalRandom.current().nextInt(30), 0, symbol));
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

    private static boolean isPlayerDead(Player player, List<Flake> flakes) {
        for (Flake flake : flakes) {
            if (player.getX() == flake.getX() && player.getY() == flake.getY()) {
                System.out.println("DÖD");
                return true;
            }
        }
        return false;
    }

}


