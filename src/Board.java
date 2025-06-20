public class Board {
    private final int[] queens; // queens[i] = columna de la reina en la fila i

    public Board(int n) {
        this.queens = new int[n];
        for (int i = 0; i < n; i++) {
            queens[i] = -1;
        }
    }

    public Board(int[] queens) {
        this.queens = queens.clone();
    }

    public int size() {
        return queens.length;
    }

    public int[] getQueens() {
        return queens.clone();
    }

    public void setQueen(int row, int col) {
        queens[row] = col;
    }

    public void removeQueen(int row) {
        queens[row] = -1;
    }

    public boolean isSafe(int row, int column,Counters counters) {
        counters.manualBits++;
        for (int position = 0; position < row; position++) {

            counters.comparisons++;/* cada comparación true del for */

            int queenColumn = queens[position];
            counters.manualBits++;
            counters.assignments++;

            if (queenColumn == column || Math.abs(queenColumn - column) == Math.abs(position - row)) {
                counters.prunningCounter++;
                return false;
            }counters.comparisons+=3;/* 1.queenColumn == column 2.Math.abs(queenColumn - column) == Math.abs(position - row)) 3.(1. || 2.)    */
        }counters.assignments++;/*int position = 0 del for*/
        counters.comparisons++;/*comparación false del for*/
        return true;
    }

    public void printBoard() {
        for (int row = 0; row < size(); row++) {
            for (int column = 0; column < size(); column++) {
                System.out.print(queens[row] == column ? "♕ " : "▯ ");
            }
            System.out.println();
        }
    }
}