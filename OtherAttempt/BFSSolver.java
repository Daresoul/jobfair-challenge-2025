package solver;

import base.IPegSolitaireSolver;

import java.util.*;

/**
 * An example BFS solver that merges symmetrical states
 * but still preserves each orientation in BFS.
 */
public class BFSSolver implements IPegSolitaireSolver {

    private int goalPebbels;
    private long goalBoard;

    /**
     * Instead of a HashSet, we keep a map from
     *   normalizedBoard -> bitmask of visited orientations
     * where orientation i is stored in the i-th bit of the byte.
     */
    private HashMap<Long, Byte> visitedMap = new HashMap<>(80000000);

    @Override
    public long[] solve(long initialBoard, long goalBoard) {
        this.goalBoard = goalBoard;
        this.goalPebbels = Long.bitCount(goalBoard);

        // If the start is already the goal, trivial solution:
        if (initialBoard == goalBoard) {
            return new long[] { goalBoard };
        }

        long[] path = solveBFS(initialBoard);
        return path;
    }

    private long[] solveBFS(long startBoard) {
        visitedMap.clear();

        // Compute normalized representation of the start board
        var startNorm = UTILS.normalizeBoard(startBoard);
        long startNormBoard = startNorm.normalizedBoard();
        int startOrientation = startNorm.rotation();  // which transform gave that normalizedBoard

        // Mark that (startNormBoard, startOrientation) is visited
        visitedMap.put(startNormBoard, (byte)(1 << startOrientation));

        // Create the BFS queue; each node holds the "raw" board plus
        // some normalization info for visited checks and path reconstruction.
        Deque<GameTree> queue = new ArrayDeque<>();
        queue.add(new GameTree(
                /* rawBoard      = */ startBoard,
                /* normBoard     = */ startNormBoard,
                /* transformIndex= */ startOrientation,
                /* parent        = */ null
        ));

        // Standard BFS loop
        while (!queue.isEmpty()) {
            GameTree node = queue.poll();
            long board = node.originalBoard; // the raw orientation at this node

            // Check if we've literally reached the final board orientation
            if (board == goalBoard) {
                // Reconstruct the path from the BFS tree
                return UTILS.reconstructPath(node);
            }

            // Get all valid moves from 'board' (the raw orientation)
            var children = UTILS.getAllValidMoves(board);
            for (long childBoard : children) {
                // Optional: prune boards that have too few pegs to match goal
                if (Long.bitCount(childBoard) < goalPebbels) {
                    continue;
                }

                // Normalize the child's raw orientation
                var childNorm = UTILS.normalizeBoard(childBoard);
                long childNormBoard = childNorm.normalizedBoard();
                int childOrientation = childNorm.rotation();

                // Check if we've visited this (normalizedBoard, orientation)
                byte visitedOrients = visitedMap.getOrDefault(childNormBoard, (byte)0);
                byte mask = (byte)(1 << childOrientation);

                // If this orientation bit is already set, skip
                if ((visitedOrients & mask) != 0) {
                    continue;
                }

                // Otherwise, record that we've now visited it
                visitedMap.put(childNormBoard, (byte)(visitedOrients | mask));

                // Enqueue the child
                GameTree childNode = new GameTree(
                        childBoard,
                        childNormBoard,
                        childOrientation,
                        node
                );
                queue.add(childNode);
            }
        }

        return new long[0];
    }

    @Override
    public String[] personalData() {
        return new String[] { "FirstName", "LastName" };
    }
}
