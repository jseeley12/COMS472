package edu.iastate.cs472.proj2;
/**
 * @author Josh Seeley
 */
import java.util.ArrayList;
import java.util.Arrays;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                board[i][j] = EMPTY;

        for (int r = 0; r <= 2; r++) {
            for (int c = 0; c < 8; c++) {
                if (r % 2 != c % 2)
                    board[r][c] = BLACK;
            }
        }

        for (int r = 5; r <= 7; r++) {
            for (int c = 0; c < 8; c++) {
                if (r % 2 != c % 2)
                    board[r][c] = RED;
            }
        }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     *
     */
    void makeMove(CheckersMove move) {
        int l = move.rows.size();
        for(int i = 0; i < l-1; i++)
            makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i+1), move.cols.get(i+1));
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        int piece = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;
        board[toRow][toCol] = piece;

        if (Math.abs(fromRow - toRow) == 2 && Math.abs(fromCol - toCol) == 2) {
            int capRow = (fromRow + toRow) / 2;
            int capCol = (fromCol + toCol) / 2;
            board[capRow][capCol] = EMPTY;
        }

        if (piece == RED && toRow == 0)
            board[toRow][toCol] = RED_KING;
        else if (piece == BLACK && toRow == 7)
            board[toRow][toCol] = BLACK_KING;
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) {
        if (player != RED && player != BLACK)
            return null;

        ArrayList<CheckersMove> moves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int p = pieceAt(r, c);
                if (p == EMPTY) continue;
                if (player == RED && (p == RED || p == RED_KING)) {
                    CheckersMove[] jm = getLegalJumpsFrom(player, r, c);
                    if (jm != null)
                        for (CheckersMove m : jm) moves.add(m);
                } else if (player == BLACK && (p == BLACK || p == BLACK_KING)) {
                    CheckersMove[] jm = getLegalJumpsFrom(player, r, c);
                    if (jm != null)
                        for (CheckersMove m : jm) moves.add(m);
                }
            }
        }

        if (!moves.isEmpty())
            return moves.toArray(new CheckersMove[0]);

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int p = pieceAt(r, c);
                if (p == EMPTY) continue;
                if (player == RED && (p == RED || p == RED_KING)) {
                    int[] dr = {-1, -1};
                    int[] dc = {-1, 1};
                    if (p == RED_KING) {
                        dr = new int[]{-1, -1, 1, 1};
                        dc = new int[]{-1, 1, -1, 1};
                    }
                    for (int i = 0; i < dr.length; i++) {
                        int nr = r + dr[i];
                        int nc = c + dc[i];
                        if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && board[nr][nc] == EMPTY) {
                            moves.add(new CheckersMove(r, c, nr, nc));
                        }
                    }
                } else if (player == BLACK && (p == BLACK || p == BLACK_KING)) {
                    int[] dr = {1, 1};
                    int[] dc = {-1, 1};
                    if (p == BLACK_KING) {
                        dr = new int[]{-1, -1, 1, 1};
                        dc = new int[]{-1, 1, -1, 1};
                    }
                    for (int i = 0; i < dr.length; i++) {
                        int nr = r + dr[i];
                        int nc = c + dc[i];
                        if (nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && board[nr][nc] == EMPTY) {
                            moves.add(new CheckersMove(r, c, nr, nc));
                        }
                    }
                }
            }
        }

        if (moves.isEmpty()) return null;
        return moves.toArray(new CheckersMove[0]);
    }


    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps. 
     * Each move returned in the array represents a sequence of jumps 
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        int piece = pieceAt(row, col);
        if (piece == EMPTY) return null;

        ArrayList<CheckersMove> jumps = new ArrayList<>();

        CheckersMove start = new CheckersMove(row, col, row, col); 


        findJumps(player, row, col, piece, start, jumps, board);

        if (jumps.isEmpty()) return null;
        return jumps.toArray(new CheckersMove[0]);
    }

   /**
    * Finds jump seqeunces for pieces
    * @param player The player of the current jump, red or black
    * @param r the index of the row 
    * @param c the index of the column
    * @param piece the piece type at (r,c)
    * @param current the current move
    * @param accum the accumulated jump moves
    * @param stateBoard the current board state
    */
    private void findJumps(int player, int r, int c, int piece, CheckersMove current, ArrayList<CheckersMove> accum, int[][] stateBoard) {
        boolean isKing = (piece == RED_KING || piece == BLACK_KING);
        boolean foundJump = false;

        int[] drs, dcs;
        if (isKing) {
            drs = new int[]{-1, -1, 1, 1};
            dcs = new int[]{-1, 1, -1, 1};
        } else if (player == RED) {
            drs = new int[]{-1, -1};
            dcs = new int[]{-1, 1};
        } else {
            drs = new int[]{1, 1};
            dcs = new int[]{-1, 1};
        }

        for (int i = 0; i < drs.length; i++) {
            int midR = r + drs[i];
            int midC = c + dcs[i];
            int toR = r + 2 * drs[i];
            int toC = c + 2 * dcs[i];
            if (toR < 0 || toR >= 8 || toC < 0 || toC >= 8) continue;
            if (midR < 0 || midR >= 8 || midC < 0 || midC >= 8) continue;
            int midPiece = stateBoard[midR][midC];
            int destPiece = stateBoard[toR][toC];
            if (destPiece != EMPTY) continue;
            if (player == RED && (midPiece == BLACK || midPiece == BLACK_KING)) {
              
                int[][] newBoard = copyBoardArray(stateBoard);
                newBoard[r][c] = EMPTY;
                newBoard[midR][midC] = EMPTY;
                newBoard[toR][toC] = piece;
                CheckersMove next = current.clone();
                
                if (next.rows.size() == 2 && next.rows.get(0).intValue()==next.rows.get(1).intValue() && next.cols.get(0).intValue()==next.cols.get(1).intValue()) {
                    next.rows.set(1, toR);
                    next.cols.set(1, toC);
                } else {
                    next.addMove(toR, toC);
                }
                foundJump = true;
                if (piece == RED && toR == 0) {
                    accum.add(next);
                } else {
                    findJumps(player, toR, toC, piece, next, accum, newBoard);
                }
            } else if (player == BLACK && (midPiece == RED || midPiece == RED_KING)) {
                int[][] newBoard = copyBoardArray(stateBoard);
                newBoard[r][c] = EMPTY;
                newBoard[midR][midC] = EMPTY;
                newBoard[toR][toC] = piece;
                CheckersMove next = current.clone();
                if (next.rows.size() == 2 && next.rows.get(0).intValue()==next.rows.get(1).intValue() && next.cols.get(0).intValue()==next.cols.get(1).intValue()) {
                    next.rows.set(1, toR);
                    next.cols.set(1, toC);
                } else {
                    next.addMove(toR, toC);
                }
                foundJump = true;
                if (piece == BLACK && toR == 7) {
                    accum.add(next);
                } else {
                    findJumps(player, toR, toC, piece, next, accum, newBoard);
                }
            }
        }

        if (!foundJump) {
           
            if (!(current.rows.size() == 2 && current.rows.get(0).intValue()==current.rows.get(1).intValue() && current.cols.get(0).intValue()==current.cols.get(1).intValue())) {
                accum.add(current);
            }
        }
    }
/**
 * Copies the board Array
 * @param src The source board array
 * @return The copied new board array
 */
    private int[][] copyBoardArray(int[][] src) {
        int[][] nb = new int[8][8];
        for (int i = 0; i < 8; i++) System.arraycopy(src[i], 0, nb[i], 0, 8);
        return nb;
    }

}
