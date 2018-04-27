import umontreal.iro.lecuyer.util.Chrono;

import java.util.Stack;

class Main {

    public static void main(String[] args) {

        int size = 20;

        Chrono timerToGenerate = new Chrono();
        GameState solution;
        GameState g = new GameState(size);
        GameState originalGame = new GameState(g);

        System.out.println("GAME:\n" + originalGame.toString());
        Chrono timerToSolve = new Chrono();
        solution = BST(g);

        if (solution != null) {
            System.out.println("SOLUTION:\n" + solution.toString());
        } else {
            System.out.println("THERE IS NO SOLUTION TO THIS PUZZLE\n");
        }

        System.out.println("To create it, it took " + ((int) (timerToGenerate.getSeconds()*1000 - timerToSolve.getSeconds()*1000) ) + " milliseconds.");
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
                continue;
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
