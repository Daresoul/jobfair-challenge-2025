package solver;

public class GameTree {
    public long board;      // The board state (as a long bitmask)
    public GameTree parent; // Parent pointer for path reconstruction

    public GameTree(long board, GameTree parent) {
        this.board = board;
        this.parent = parent;
    }
}
