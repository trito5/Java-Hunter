public class Flake {
    //    final static int COUNTER_LIMIT = 500;
    private int x;
    private int y;
    private char symbol;

    public Flake(int x, int y, char symbol) {
        this.x = x;
        this.y = y;
        this.symbol = symbol;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getSymbol() {
        return symbol;
    }

    public void fall(){
        y++;
    }


}
