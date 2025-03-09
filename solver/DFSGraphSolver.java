package solver;

import base.IPegSolitaireSolver;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DFSGraphSolver implements IPegSolitaireSolver {
    private final HashSet<Long> visited = new HashSet<>(8000000);

    @Override
    public long[] solve(long initialBoard, long goalBoard) {
        int goalPebbels = Long.bitCount(goalBoard);
        int initialPebbels = Long.bitCount(initialBoard);
        int totalMovesRequired = initialPebbels - goalPebbels;

        visited.clear();

        var initialNode = new GameTree(initialBoard, null);

        var result = solveDFS(initialBoard, goalBoard, totalMovesRequired, initialNode);

        if (result == null) {
            return new long[0];
        }
        return reconstructPath(result);
    }

    private GameTree solveDFS(long currentBoard, long goalBoard, int movesRemaining, GameTree parent) {
        if (movesRemaining == 0) {
            if (currentBoard == goalBoard) {
                return new GameTree(currentBoard, parent);
            } else {
                return null;
            }
        }

        if (Long.bitCount(currentBoard) < Long.bitCount(goalBoard)) {
            return null;
        }

        if (visited.contains(currentBoard)) {
            return null;
        }
        visited.add(currentBoard);

        List<Long> validMoves = UTILS.getAllValidMoves(currentBoard);
        if (validMoves.isEmpty()) {
            return null;
        }

        var currentNode = new GameTree(currentBoard, parent);

        for (long newState : validMoves) {
            GameTree subsequentSolution = solveDFS(newState, goalBoard, movesRemaining - 1, currentNode);
            if (subsequentSolution != null) {
                return subsequentSolution;
            }
        }

        return null;
    }

    @Override
    public String[] personalData() {
        return new String[] { "Nicolas", "Dyhrman" };
    }

    private long[] reconstructPath(GameTree currentNode) {
        ArrayList<Long> path = new ArrayList<>();

        while (currentNode.parent != null) {
            path.add(currentNode.board);
            currentNode = currentNode.parent;
        }

        return path.reversed().stream().mapToLong(Long::longValue).toArray();
    }

}
