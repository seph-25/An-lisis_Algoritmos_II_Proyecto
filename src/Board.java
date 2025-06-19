public class Board {
    private int[] queens; // queens[i] = columna de la reina en la fila i

    public Board(int n) {
        this.queens = new int[n];
        for (int i = 0; i < n; i++) {
            queens[i] = -1; // -1 indica que no hay reina en la fila i
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

    public boolean isSafe(int row, int col) {
        for (int i = 0; i < row; i++) {
            int qCol = queens[i];
            if (qCol == col || Math.abs(qCol - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }

    public void printBoard() {
        for (int row = 0; row < size(); row++) {
            for (int col = 0; col < size(); col++) {
                System.out.print(queens[row] == col ? "Q " : ". ");
            }
            System.out.println();
        }
    }
}