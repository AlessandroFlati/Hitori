import java.util.HashSet;
import java.util.Set;

public class Cell {
    private final int value;
    private final int x;
    private final int y;
    private final GameState gameState;
    private final Set<Cell> neighbors = new HashSet<>();
    private boolean black = false;
    private boolean white = false;
    private boolean visited = false;

    Cell(Cell cell, GameState gameState) {
        this.value = cell.value;
        this.x = cell.x;
        this.y = cell.y;
        this.black = cell.black;
        this.white = cell.white;
        this.gameState = gameState;
    }

    Cell(int i, int j, int value, GameState gameState) {
        this.x = i;
        this.y = j;
        this.value = value;
        this.gameState = gameState;
    }

    int getValue() {
        return value;
    }

    boolean isBlack() {
        return black;
    }

    void setBlack() throws AlreadyColoredException {
        if (this.black) return;
        if (this.white) throw new AlreadyColoredException();
        else {
            this.black = true;
            for (Cell c : neighbors) {
                c.setWhite();
            }
        }
    }

    boolean isWhite() {
        return white;
    }

    private boolean hasBeenVisited() {
        return visited;
    }

    void setVisited(boolean visited) {
        this.visited = visited;
    }

    void setWhite() throws AlreadyColoredException {
        if (this.white) return;
        if (this.black) throw new AlreadyColoredException();
        else {
            this.white = true;
            for (Cell c : this.getRow()) {
                if (c.getValue() == this.value && !c.equals(this)) c.setBlack();
            }
            for (Cell c : this.getColumn()) {
                if (c.getValue() == this.value && !c.equals(this)) c.setBlack();
            }
        }
    }

    Cell[] getRow() {
        return this.gameState.getGrid()[this.x];
    }

    Cell[] getColumn() {
        Cell[] column = new Cell[gameState.getSize()];
        for (int i = 0; i < gameState.getSize(); i++) {
            column[i] = gameState.getGrid()[i][this.y];
        }
        return column;
    }

    void setNeighbors() {
        if (neighbors.isEmpty()) {
            if (x != 0) neighbors.add(gameState.getGrid()[x - 1][y]);
            if (x != gameState.getSize() - 1) neighbors.add(gameState.getGrid()[x + 1][y]);
            if (y != 0) neighbors.add(gameState.getGrid()[x][y - 1]);
            if (y != gameState.getSize() - 1) neighbors.add(gameState.getGrid()[x][y + 1]);
        }
    }

    Set<Cell> getNeighbors() {
        return neighbors;
    }

    Set<Cell> getReachableCells() {
        Set<Cell> reachables = new HashSet<>();
        this.setVisited(true);

        if (black) return reachables;

        reachables.add(this);

        for (Cell c : neighbors) {
            if (c.hasBeenVisited()) continue;
            if (!c.isBlack()) {
                reachables.addAll(c.getReachableCells());
            }
        }

        return reachables;
    }

    GameState setBlackAndGetState() throws GameState.ImpossibleStateException {
        if (neighbors.stream().anyMatch(Cell::isBlack)) throw new GameState.ImpossibleStateException();

        try {
            GameState g = new GameState(this.gameState);
            g.getGrid()[x][y].setBlack();

            if (!g.isConnected()) throw new GameState.ImpossibleStateException();

            return g;
        } catch (AlreadyColoredException e) {
            throw new GameState.ImpossibleStateException();
        }
    }

    @Override
    public String toString() {
        return black ? "\u25A0" : String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) return false;
        Cell c2 = (Cell) obj;
        return x == c2.x && y == c2.y && value == c2.value && white == c2.white && black == c2.black;
    }

    class AlreadyColoredException extends Throwable {
    }
}