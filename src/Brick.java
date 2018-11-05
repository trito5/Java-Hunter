public class Brick {

    private int y;
    private int x;
    private char symbol;

    public Brick(int x, int y) {
        this.y = y;
        this.x = x;
        this.symbol = '\u8965';
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }
}
