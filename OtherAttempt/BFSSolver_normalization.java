package solver;

import base.IPegSolitaireSolver;

import java.util.*;

public class BFSSolver_normalization implements IPegSolitaireSolver {
    private int goalPebbels;
    HashSet<Long> visitedMap = new HashSet<>(8000000);
    private long normalizedGoalBoard;
    private int rotation;

    @Override
    public long[] solve(long initialBoard, long goalBoard) {
        goalPebbels = Long.bitCount(goalBoard);

        var goalRotation = UTILS.normalizeBoard(goalBoard);
        this.rotation = goalRotation.rotation();
        this.normalizedGoalBoard = goalRotation.normalizedBoard();

        var initialRotation = UTILS.normalizeBoard(initialBoard);
        long normalizedInitialBoard = initialRotation.normalizedBoard();


        // Debug prints
        System.out.println("Initial Board: " + Long.toBinaryString(initialBoard));
        System.out.println("Normalized Initial: " + Long.toBinaryString(normalizedInitialBoard));
        System.out.println("Goal Board: " + Long.toBinaryString(goalBoard));
        System.out.println("Normalized Goal: " + Long.toBinaryString(normalizedGoalBoard));

        if (normalizedInitialBoard == normalizedGoalBoard) {
            return new long[]{goalBoard}; // Solution is trivial
        }

        long[] path = solveBFS(normalizedInitialBoard);
        // The path returned from BFS is now "lifted" using the inverse transformations stored in each node.
        return path;
    }

    public long[] solveBFS(long startBoard) {
        visitedMap.clear();

        // Mark normalized version as visited
        var norm = UTILS.normalizeBoard(startBoard);
        visitedMap.add(norm.normalizedBoard());

        // Enqueue the RAW start board
        Deque<GameTree> queue = new ArrayDeque<>();
        queue.add(new GameTree(startBoard, norm.normalizedBoard(), norm.rotation(), null));

        while (!queue.isEmpty()) {
            GameTree node = queue.poll();
            long board = node.originalBoard; // raw orientation

            // If (normalized) board == (normalized) goal
            long normOfThis = UTILS.normalizeBoard(board).normalizedBoard();
            if (normOfThis == normalizedGoalBoard) {
                return UTILS.reconstructPath(node);
            }

            var newBoards = UTILS.getAllValidMoves(board);  // Moves on *raw* board
            for (long childBoard : newBoards) {
                if (Long.bitCount(childBoard) < goalPebbels) continue;

                // Check visited in normalized form
                var childNorm = UTILS.normalizeBoard(childBoard);
                if (!visitedMap.contains(childNorm.normalizedBoard())) {
                    visitedMap.add(childNorm.normalizedBoard());
                    queue.add(new GameTree(childBoard, childNorm.normalizedBoard(), childNorm.rotation(), node));
                }
            }
        }
        return new long[0];
    }








    @Override
    public String[] personalData() {
        return new String[] { "Nicolas", "Dyhrman" };
    }
}
