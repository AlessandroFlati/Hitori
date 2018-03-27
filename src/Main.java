import umontreal.iro.lecuyer.util.Chrono;

import java.util.Stack;

class Main {

    public static void main(String[] args) {

        int size = 5;

        Chrono timerToGenerate = new Chrono();
        Chrono timerToSolve = new Chrono();
        GameState solution = null;
        GameState g;
        GameState originalGame = null;
        int counterOfImpossiblePuzzles = -1;
        while (solution == null) {
            g = new GameState(size);

//            Integer[][] grid = {{3,5,3,4,4},{5,2,1,1,4},{1,4,5,2,1},{4,2,2,3,3},{1,3,4,2,5}};
//            g = new GameState(grid);

//            Integer[][] grid = {
//                    {4,2,5,7,2,8,2,6},
//                    {6,5,8,4,7,1,6,4},
//                    {6,7,2,8,3,6,5,2},
//                    {5,8,6,1,2,2,3,7},
//                    {3,6,1,5,2,6,7,5},
//                    {7,4,2,6,8,6,1,5},
//                    {2,6,7,4,6,5,6,3},
//                    {5,2,6,1,5,7,7,1}
//            };
//            g = new GameState(grid);
            timerToSolve = new Chrono();
            originalGame = new GameState(g);
            solution = BST(g);
            counterOfImpossiblePuzzles++;
        }

        System.out.println("I had to generate " + counterOfImpossiblePuzzles + " random impossible puzzles of size " + size + " before the one that follows.\nThis process took " + timerToGenerate.getSeconds() + " seconds.\n");

        System.out.println(originalGame);
        System.out.println("SOLUTION:\n" + solution.toString());

        System.out.println("To solve it, it took " + timerToSolve.getSeconds() + " seconds.\n\n");

    }

    private static GameState BST(GameState state) {

    Stack<GameState> stack = new Stack<>();
    stack.push(state);
    while (stack.size() != 0) {
        GameState element = stack.pop();
        try {
            element.infer();
        } catch (GameState.ImpossibleStateException e) {
            return null;
        }

        if (element.isSolved()) {
            return element;
        }

        for (GameState g : element.getNextStepOptions()) {
            stack.push(g);
        }
    }
    return null;
    }
}
