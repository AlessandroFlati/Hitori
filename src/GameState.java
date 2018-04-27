import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameState {
    private Cell[][] grid;
    private final int size;

    private GameState(Cell[][] grid, int size) {
        this.size = size;
        this.grid = new Cell[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.grid[i][j] = new Cell(grid[i][j], this);
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.grid[i][j].setNeighbors();
            }
        }
    }

    GameState(GameState game) {
        this(game.grid, game.size);
    }

    GameState(Integer[][] numbers) {
        this.size = numbers.length;
        this.grid = new Cell[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.grid[i][j] = new Cell(i, j, numbers[i][j], this);
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.grid[i][j].setNeighbors();
            }
        }
    }

    GameState(int size) {
        this.size = size;
        this.grid = new Cell[size][size];

        int i, j;

        // Initialization
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                this.grid[i][j] = new Cell(i, j, -1, true, this);
            }
        }
        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                this.grid[i][j].setNeighbors();
            }
        }

        GameState solution = gridCreation(this);

        if(solution != null){
            this.grid = solution.grid;

            this.getBlackCells().forEach(cell -> cell.setValue(ThreadLocalRandom.current().nextInt(1,size+1)));

            for (i = 0; i < size; i++) {
                for (j = 0; j < size; j++) {
                    this.grid[i][j].revertColor();
                }
            }
        }
    }

    private static GameState gridCreation(GameState state) {

        Stack<GameState> stack = new Stack<>();
        stack.push(state);
        while (stack.size() != 0) {
            GameState element = stack.pop();

//            System.out.println("I'm trying with ");
//            System.out.println(element);

            try {
                element.creationInfer();
            } catch (GameState.ImpossibleStateException e) {
                return null;
            }

            if (element.isSolved()) {
                return element;
            }

            for (GameState g : element.getNextStepCreationOptions()) {
                stack.push(g);
            }
        }

        return null;
    }

    Cell[][] getGrid() {
        return grid;
    }

    int getSize() {
        return size;
    }

    private boolean isLegit() {
        return !connectedBlacksInRows() && !connectedBlacksInColumns() && this.isConnected();
    }

    private boolean connectedBlacksInColumns() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                if (grid[j][i].isBlack() && grid[j + 1][i].isBlack()) return true;
            }
        }
        return false;
    }

    private boolean connectedBlacksInRows() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                if (grid[i][j].isBlack() && grid[i][j + 1].isBlack()) return true;
            }
        }
        return false;
    }

    boolean isConnected() {
        Set<Cell> nonBlackCells = getNonBlackCells();
        Set<Cell> reachableCells = nonBlackCells.iterator().next().getReachableCells();
        resetVisitedCells();
        return nonBlackCells.equals(reachableCells);
    }

    private void resetVisitedCells() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j].setVisited(false);
            }
        }
    }

    private Set<Cell> getNonBlackCells() {
        Set<Cell> nonBlackCells = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!grid[i][j].isBlack()) nonBlackCells.add(grid[i][j]);
            }
        }
        return nonBlackCells;
    }

    private Set<Cell> getBlackCells() {
        Set<Cell> blackCells = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j].isBlack()) blackCells.add(grid[i][j]);
            }
        }
        return blackCells;
    }

    private Set<Cell> getNonColoredCells() {
        Set<Cell> nonColoredCells = new HashSet<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!grid[i][j].isBlack() && !grid[i][j].isWhite()) nonColoredCells.add(grid[i][j]);
            }
        }
        return nonColoredCells;
    }

    private boolean repetitionsInColumns() {
        Set<Integer> numbers;
        for (int i = 0; i < size; i++) {
            numbers = new HashSet<>();
            for (int j = 0; j < size; j++) {
                if (grid[j][i].isBlack()) continue;
                Integer number = grid[j][i].getValue();
                if (numbers.contains(number)) return true;
                else numbers.add(number);
            }
        }
        return false;

    }

    private boolean repetitionsInRows() {
        Set<Integer> numbers;
        for (int i = 0; i < size; i++) {
            numbers = new HashSet<>();
            for (int j = 0; j < size; j++) {
                if (grid[i][j].isBlack()) continue;
                Integer number = grid[i][j].getValue();
                if (numbers.contains(number)) return true;
                else numbers.add(number);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                s.append(String.format("%1$" + (int) (Math.log10(size) + 2) + "s", grid[i][j].toString()));
            }
            for (int j = 0; j < Math.log10(size); j++) {
                s.append("\n");
            }
        }
        return String.valueOf(s);
    }

    Set<GameState> getNextStepOptions() {
        Set<GameState> options = new HashSet<>();
        Set<Cell> cellOptions = getNonColoredCells();
        for (Cell c : cellOptions) {
            try {
                GameState g = c.setBlackAndGetState();
                options.add(g);
            } catch (ImpossibleStateException ignored) {}
        }
        return options;
    }

    private Set<GameState> getNextStepCreationOptions() {
        Set<GameState> options = new HashSet<>();
        List<Cell> cellOptions = new ArrayList<>(getBlackCells());
        Collections.shuffle(cellOptions);
        for (Cell c : cellOptions) {
            Set<GameState> possibleGames = c.setAssignableValuesAndGetStates();
            options.addAll(possibleGames);
        }
        return options;
    }

    boolean isSolved() {
        return this.isLegit() && !this.repetitionsInRows() && !this.repetitionsInColumns();
    }

    void infer() throws ImpossibleStateException {
        GameState original = new GameState(this);
        this.inferSurroundedCell();
        this.inferXYXPattern();
        this.inferXXYZXPattern();
        if (!this.equals(original)) this.infer();
    }

    private void creationInfer() throws ImpossibleStateException {
        GameState original = new GameState(this);
        // TODO
        if (!this.equals(original)) this.infer();
    }

    private void inferSurroundedCell() throws ImpossibleStateException {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                try {
                    Cell c = grid[i][j];
                    Set<Cell> cNeighbors = c.getNeighbors();
                    if (cNeighbors.stream().filter(Cell::isBlack).count() == cNeighbors.size() - 1) {
                        for (Cell n : cNeighbors) {
                            if (!n.isBlack()) n.setWhite();
                        }
                    }
                } catch (Cell.AlreadyColoredException ignored) {
                    throw new ImpossibleStateException();
                }
            }
        }
    }

    private void inferXYXPattern() throws ImpossibleStateException {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 2; j++) {
                try {
                    if (grid[i][j].getValue() == grid[i][j + 2].getValue()) grid[i][j + 1].setWhite();
                    if (grid[j][i].getValue() == grid[j + 2][i].getValue()) grid[j + 1][i].setWhite();
                } catch (Cell.AlreadyColoredException ignored) {
                    throw new ImpossibleStateException();
                }
            }
        }
    }

    private void inferXXYZXPattern() throws ImpossibleStateException {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                try {
                    if (grid[i][j].getValue() == grid[i][j + 1].getValue()) {
                        for (Cell c : grid[i][j].getRow()) {
                            if (!c.equals(grid[i][j]) && !c.equals(grid[i][j + 1]) && c.getValue() == grid[i][j].getValue())
                                c.setBlack();
                        }
                    }
                    if (grid[j][i].getValue() == grid[j + 1][i].getValue()) {
                        for (Cell c : grid[j][i].getColumn()) {
                            if (!c.equals(grid[j][i]) && !c.equals(grid[j + 1][i]) && c.getValue() == grid[j][i].getValue())
                                c.setBlack();
                        }
                    }
                } catch (Cell.AlreadyColoredException ignored) {
                    throw new ImpossibleStateException();
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GameState)) return false;

        GameState g = (GameState) obj;
        if (g.size != this.size) return false;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Cell thisCell = this.grid[i][j];
                Cell gCell = g.grid[i][j];
                if (!thisCell.equals(gCell)) {
                    return false;
                }
            }
        }

        return true;
    }

    static class ImpossibleStateException extends Throwable {
    }
}
