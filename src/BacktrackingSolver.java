public class BacktrackingSolver {
    private int solutionsFound;
    private int assignments;
    private int comparisons;
    private int prunningCounter = 0;

    public BacktrackingSolver() {
        resetMetrics();
    }

    private void resetMetrics() {
        this.solutionsFound = 0;
        this.assignments    = 0;
        this.comparisons    = 0;
        this.prunningCounter = 0;
    }

    public boolean solve(Board board) {
        resetMetrics();
        return solveRow(board, 0);
    }

    private boolean solveRow(Board board, int row) {
        int n = board.size();
        if (row == n) {
            solutionsFound++;
            return true;
        }
        for (int col = 0; col < n; col++) {
            comparisons++;
            if (!board.isSafe(row, col)) {
                continue;
            }
            board.setQueen(row, col);
            assignments++;
            if (solveRow(board, row + 1)) {
                return true;
            }
            board.removeQueen(row);
        }
        return false;
    }

    // Getters para usar después en Main o donde necesites
    public int getSolutionsFound() { return solutionsFound; }
    public int getAssignments()    { return assignments; }
    public int getComparisons()    { return comparisons; }

    public String getMetrics() {
        return String.format(
                "Soluciones encontradas: %d, Asignaciones: %d, Comparaciones: %d",
                solutionsFound, assignments, comparisons
        );
    }
}