package solver;

import OtherAttempt.GameTree;
import OtherAttempt.RotationNormal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UTILS {
    public static final List<Integer> OFFSETS = List.of(1, -1, 7, -7);
    public static final long INVALID_FIELDS = 438808218710499L;

    public static RotationNormal normalizeBoard(long board) {
        if (board == 0) return new RotationNormal(0, 0);

        long[] transformations = {
                board,
                flipVertical(board), flipHorizontal(board),
                rotate90(board), rotate180(board), rotate270(board),
                flipVertical(rotate90(board)), flipHorizontal(rotate90(board))
        };

        RotationNormal min = new RotationNormal(0, board);

        for (int i = 1; i < transformations.length; i++) {
            if (transformations[i] != 0 && transformations[i] < min.normalizedBoard()) {
                min = new RotationNormal(i, transformations[i]);
            }
        }

        //System.out.println("✅ Final Normalized Board: " + Long.toBinaryString(min.normalizedBoard()));
        return min;
    }




    public static long flipVertical(long x) {
        // A 7-bit mask: 0b1111111 = 0x7F.
        long mask7 = (1L << 7) - 1; // 0x7F

        // Extract each row (row0 = bottom, row6 = top)
        long row0 =  x             & mask7;             // bits 0-6
        long row1 = (x >>> 7)       & mask7;             // bits 7-13
        long row2 = (x >>> 14)      & mask7;             // bits 14-20
        long row3 = (x >>> 21)      & mask7;             // bits 21-27
        long row4 = (x >>> 28)      & mask7;             // bits 28-34
        long row5 = (x >>> 35)      & mask7;             // bits 35-41
        long row6 = (x >>> 42)      & mask7;             // bits 42-48

        // Reassemble with rows flipped vertically:
        // New bottom row (row0) = old top row (row6)
        // New row1 = old row5, new row2 = old row4, row3 stays the same,
        // New row4 = old row2, new row5 = old row1, new top (row6) = old row0.
        long flipped = (row6)         |
                (row5 << 7)    |
                (row4 << 14)   |
                (row3 << 21)   |
                (row2 << 28)   |
                (row1 << 35)   |
                (row0 << 42);

        return flipped;
    }






    public static long reverse7Bits(long row) {
        long rev = 0;
        for (int i = 0; i < 7; i++) {
            rev = (rev << 1) | (row & 1);
            row >>= 1;
        }
        return rev;
    }

    // Flip horizontally for a 7x7 board by reversing each 7-bit row.
    public static long flipHorizontal(long x) {
        long mask7 = (1L << 7) - 1; // 0x7F
        long result = 0;
        for (int r = 0; r < 7; r++) {
            long row = (x >>> (r * 7)) & mask7;
            long revRow = reverse7Bits(row);
            result |= (revRow << (r * 7));
        }
        return result;
    }

    public static long flipDiag(long x) {
        long t;
        // Build masks for cells above the diagonal for each difference d.
        long k1 = (1L << 1)  | (1L << 9)  | (1L << 17) | (1L << 25) | (1L << 33) | (1L << 41);
        long k2 = (1L << 2)  | (1L << 10) | (1L << 18) | (1L << 26) | (1L << 34);
        long k3 = (1L << 3)  | (1L << 11) | (1L << 19) | (1L << 27);
        long k4 = (1L << 4)  | (1L << 12) | (1L << 20);
        long k5 = (1L << 5)  | (1L << 13);
        long k6 = (1L << 6);

        // For d = 1 (shift = 6)
        t = (x ^ (x >> 6)) & k1;
        x = x ^ t ^ (t << 6);

        // For d = 2 (shift = 12)
        t = (x ^ (x >> 12)) & k2;
        x = x ^ t ^ (t << 12);

        // For d = 3 (shift = 18)
        t = (x ^ (x >> 18)) & k3;
        x = x ^ t ^ (t << 18);

        // For d = 4 (shift = 24)
        t = (x ^ (x >> 24)) & k4;
        x = x ^ t ^ (t << 24);

        // For d = 5 (shift = 30)
        t = (x ^ (x >> 30)) & k5;
        x = x ^ t ^ (t << 30);

        // For d = 6 (shift = 36)
        t = (x ^ (x >> 36)) & k6;
        x = x ^ t ^ (t << 36);

        return x;
    }





    public static long flipAntiDiag(long x) {
        long t;
        final long k1 = 0x9249240000000000L; // Adjusted for 7x7
        final long k2 = 0xC3C3000000000000L; // Adjusted for 7x7
        final long k4 = 0xF0F0000000000000L; // Adjusted for 7x7

        t  =       x ^ (x << 24);
        x ^= k4 & (t ^ (x >>> 24));
        t  = k2 & (x ^ (x << 12));
        x ^=       t ^ (t >>> 12);
        t  = k1 & (x ^ (x << 6));
        x ^=       t ^ (t >>> 6);

        return x;
    }



    public static long rotate90 (long x) {
        return flipVertical (flipDiag (x) );
    }




    public static long rotate180(long x) {
        return flipHorizontal (flipVertical (x) );
    }

    public static long rotate270(long x) {
        return flipDiag (flipVertical (x) );
    }


    public static long[] adjustPathToGoalOrientation(long[] path, int goalTrans) {
        long[] adjusted = new long[path.length];
        for (int i = 0; i < path.length; i++) {
            adjusted[i] = adjustToGoalOrientation(path[i], goalTrans);
        }
        return adjusted;
    }


    public static long adjustToGoalOrientation(long board, int transformation) {
        return switch (transformation) {
            case 0 -> board;                            // Identity
            case 1 -> flipVertical(board);              // flipVertical is self-inverse
            case 2 -> flipHorizontal(board);            // flipHorizontal is self-inverse
            case 3 -> rotate270(board);                 // Inverse of rotate90
            case 4 -> rotate180(board);                 // Self-inverse
            case 5 -> rotate90(board);                  // Inverse of rotate270
            case 6 -> rotate270(flipVertical(board));   // Inverse of flipVertical(rotate90)
            case 7 -> rotate270(flipHorizontal(board)); // Inverse of flipHorizontal(rotate90)
            default -> board;
        };
    }

    public static long[] reconstructPath(GameTree goalNode) {
        List<Long> path = new ArrayList<>();
        for (GameTree cur = goalNode; cur != null; cur = cur.parent) {
            path.add(cur.originalBoard);
        }
        Collections.reverse(path);

        System.out.println("Final path:");
        for (var p : path) {
            System.out.println(Long.toBinaryString(p));
        }

        return path.stream().mapToLong(Long::longValue).toArray();
    }

    public static List<Long> getAllValidMoves(long state) {
        List<Long> validMoves = new ArrayList<>();

        for (int i = 0; i < 49; i++) {
            if ((state & (1L << i)) != 0) {
                // try a move if it is valid
                for (int offset : OFFSETS) {
                    long move = move(state, i, offset);
                    if (move != -1) {
                        validMoves.add(move);
                    }
                }
            }
        }
        return validMoves;
    }

    private static long move(long currentBoard, int position, int offset) {
        int jumpedPos = position + offset; // Position of the jumped peg
        int landingPos = position + 2 * offset; // Position where the peg lands

        // If move goes out of bounds
        if (jumpedPos < 0 || jumpedPos >= 64 || landingPos < 0 || landingPos >= 64) {
            return -1;
        }

        // Check if there's a peg at the starting position
        if ((currentBoard & (1L << position)) == 0) {
            return -1;
        }

        // Check if there's a peg at the jumped position
        if ((currentBoard & (1L << jumpedPos)) == 0) {
            return -1;
        }

        // Check if the landing position is empty
        if ((currentBoard & (1L << landingPos)) != 0) {
            return -1;
        }

        // Perform the move: remove starting peg, remove jumped peg, add landing peg
        currentBoard &= ~(1L << position); // Remove starting peg
        currentBoard &= ~(1L << jumpedPos); // Remove jumped peg
        currentBoard |= (1L << landingPos); // Add landing peg

        // if a peg is on an invalid field
        if ((currentBoard & 438808218710499L) != 0) {
            return -1L;
        }

        return currentBoard;

    }


}
