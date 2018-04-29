import java.util.*;

public class Cell {
    private int value;
    private final int x;
    private final int y;
    private final GameState gameState;
    private final Set<Cell> neighbors = new HashSet<>();
    private boolean black = false;
    private boolean white = false;
    private boolean visited = false;
    private List<Integer> possibleValues = new ArrayList<>();

    Cell(Cell cell, GameState gameState, boolean preserveCellPossibilities) {
        this.value = cell.value;
        this.x = cell.x;
        this.y = cell.y;
        this.black = cell.black;
        this.white = cell.white;
        this.gameState = gameState;
        if(!preserveCellPossibilities){
            for (int k = 0; k < gameState.getSize(); k++) {
                this.possibleValues.add(k+1);
            }
        } else {
            this.possibleValues = new ArrayList<>(cell.possibleValues);
        }
    }

    Cell(Cell cell, GameState gameState){
        this(cell, gameState, false);
    }

    Cell(int i, int j, int value, GameState gameState) {
        this.x = i;
        this.y = j;
        this.value = value;
        this.gameState = gameState;
        for (int k = 0; k < gameState.getSize(); k++) {
            possibleValues.add(k+1);
        }
    }

    Cell(int i, int j, int value, boolean black, GameState gameState) {
        this(i,j,value,gameState);
        this.black = black;
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

    void setCreationBlack() throws AlreadyColoredException, GameState.ImpossibleStateException {
        if (this.black) return;
        if (this.white) throw new AlreadyColoredException();
        else {
            this.black = true;
            if(!gameState.isConnected()) throw new GameState.ImpossibleStateException();
            for (Cell c : neighbors) {
                c.setCreationWhite();
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


    void setCreationWhite() throws AlreadyColoredException, GameState.ImpossibleStateException {
        if (this.white) return;
        if (this.black) throw new AlreadyColoredException();
        else {
            this.white = true;

            if(this.value > 0){ // I have already created black pattern
                for (Cell c : this.getRow()) {
                    if (c.getValue() == this.value && !c.equals(this)) c.setBlack();

                    c.possibleValues.remove(new Integer(this.value));
                }
                for (Cell c : this.getColumn()) {
                    if (c.getValue() == this.value && !c.equals(this)) c.setBlack();

                    c.possibleValues.remove(new Integer(this.value));
                }
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
        return x == c2.x && y == c2.y && (black == c2.black || (value == c2.value && white == c2.white));
    }

    void revertColor() {
        this.black = false;
        this.white = false;
    }

    void setValue(int value) {
        this.value = value;
    }

    void setValueAndWhiteIt(Integer n) throws AlreadyColoredException, GameState.ImpossibleStateException {
        this.setValue(n);
        this.setCreationWhite();
    }

    List<GameState> setAssignableValuesAndGetStates() {
        List<GameState> games = new ArrayList<>();
        for (Integer k : possibleValues){
            GameState g = new GameState(this.gameState, true);
            Cell c = g.getGrid()[x][y];
            try {
                c.setValueAndWhiteIt(k);
            } catch (AlreadyColoredException | GameState.ImpossibleStateException e) {
                continue;
            }
            games.add(g);
        }
        Collections.shuffle(games);
        return games;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Integer> getPossibleValues() {
        return possibleValues;
    }

    class AlreadyColoredException extends Throwable {
    }
}