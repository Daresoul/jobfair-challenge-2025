package solver;

public class GameTree {
    long originalBoard;    // raw orientation
    long normBoard;        // canonical orientation
    int transformIndex;    // which transform created normBoard
    GameTree parent;       // back-pointer for path reconstruction

    GameTree(long raw, long norm, int ti, GameTree parent) {
        this.originalBoard = raw;
        this.normBoard = norm;
        this.transformIndex = ti;
        this.parent = parent;
    }
}
