package solver;

import base.IPegSolitaireSolver;


import java.util.HashSet;
import java.util.List;

public class DFSSolver implements IPegSolitaireSolver {
    private final HashSet<Long> visited = new HashSet<>(8000000);

    @Override
    public long[] solve(long initialBoard, long goalBoard) {
        int goalPebbels = Long.bitCount(goalBoard);
        int initialPebbels = Long.bitCount(initialBoard);
        int totalMovesRequired = initialPebbels - goalPebbels;

        visited.clear();

        long[] result = solveDFS(initialBoard, goalBoard, totalMovesRequired);
        return result;
    }

    private long[] solveDFS(long currentBoard, long goalBoard, int movesRemaining) {
        if (movesRemaining == 0) {
            if (currentBoard == goalBoard) {
                return new long[]{currentBoard};
            } else {
                return new long[0];
            }
        }

        if (Long.bitCount(currentBoard) < Long.bitCount(goalBoard)) {
            return new long[0];
        }

        if (visited.contains(currentBoard)) {
            return new long[0];
        }
        visited.add(currentBoard);

        List<Long> validMoves = UTILS.getAllValidMoves(currentBoard);
        if (validMoves.isEmpty()) {
            return new long[0];
        }

        for (long newState : validMoves) {
            long[] subsequentSolution = solveDFS(newState, goalBoard, movesRemaining - 1);
            if (subsequentSolution.length > 0) {
                long[] result = new long[subsequentSolution.length + 1];
                result[0] = currentBoard;
                System.arraycopy(subsequentSolution, 0, result, 1, subsequentSolution.length);
                return result;
            }
        }

        return new long[0];
    }

    @Override
    public String[] personalData() {
        return new String[] { "Nicolas", "Dyhrman" };
    }

}
