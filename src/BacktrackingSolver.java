public class BacktrackingSolver {
    private int solutionsFound = 0;
    private int assignments = 0;
    private int comparisons = 0;

    public boolean solve(Board board, int row) {
        int n = board.size();
        if (row == n) {
            solutionsFound++;
            return true; // Si solo quieres una solución, retorna aquí
        }
        for (int col = 0; col < n; col++) {
            comparisons++;
            if (board.isSafe(row, col)) {
                board.setQueen(row, col);
                assignments++;
                if (solve(board, row + 1)) {
                    return true; // Si solo quieres una solución, retorna aquí
                }
                board.removeQueen(row);
            }
        }
        return false;
    }

    public int getAssignments() {
        return assignments;
    }

    public int getComparisons() {
        return comparisons;
    }

    public int getSolutionsFound() {
        return solutionsFound;
    }
}