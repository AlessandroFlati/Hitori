import umontreal.iro.lecuyer.util.Chrono;

import java.util.Stack;

class Main {

    public static void main(String[] args) {

        int size = 9;

        Chrono timerToGenerate = new Chrono();
        Chrono timerToSolve = new Chrono();
        GameState solution = null;
        GameState g;
        GameState originalGame = null;

        while (solution == null) {
            g = new GameState(size);
            timerToSolve = new Chrono();
            originalGame = new GameState(g);
            solution = BST(g);
        }

//        System.out.println("I had to generate " + counterOfImpossiblePuzzles + " semi-random impossible puzzles of size " + size + " before the one that follows.\nThis process took " + (int)(timerToGenerate.getHours()) + " hours, " + (int)(timerToGenerate.getMinutes()) + " minutes and " + (int)(timerToGenerate.getSeconds()) + " seconds.\n");

        System.out.println(originalGame);
        System.out.println("SOLUTION:\n" + solution.toString());

        System.out.println("To create it, it took " + ((int) (timerToGenerate.getSeconds()*1000) ) + " milliseconds.");
        System.out.println("To solve it, it took " + ((int) (timerToSolve.getSeconds()*1000) ) + " milliseconds.\n\n");

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
