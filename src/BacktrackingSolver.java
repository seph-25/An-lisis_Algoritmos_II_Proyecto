public class BacktrackingSolver {
    Counters counters = new Counters();

    public BacktrackingSolver() {
        resetMetrics();
    }

    private void resetMetrics() {
        counters.manualBits = 0;
        counters.assignments = 0;
        counters.comparisons = 0;
        counters.prunningCounter = 0;
    }

    public boolean solve(Board board) {
        resetMetrics();
        boolean found = solveRow(board, 0);

        // Cálculo manual de memoria:
        int n = board.size();
        int bitsBoard    = Integer.SIZE * n;       // 32·N
        //int bitsStack    = Integer.SIZE * 3 * n;   // (row, n, col) × N
        //int bitsIsSafe   = Integer.SIZE * 4 * n;       // (row, col, position, qCol)
        counters.manualBits = bitsBoard; // + bitsStack + bitsIsSafe;
        return found;
    }

    private boolean solveRow(Board board, int row) {
        int n = board.size();
        counters.manualBits+=Integer.SIZE;
        counters.assignments++;/* int n = board.size(); */
        if (row == n) {
            return true;
        }counters.comparisons++;/* if (row == n) */
        counters.manualBits++;
        for (int column = 0; column < n; column++) {
            counters.comparisons++; /* column < n cada que entra al ciclo*/

            if (!board.isSafe(row, column,counters)) {
                continue;
            }counters.comparisons++;/*(!board.isSafe(row, column,counters)) comparado con true o false*/

            board.setQueen(row, column);
            counters.assignments++;/* board.setQueen(row, column) */

            if (solveRow(board, row + 1)) {
                return true;
            }counters.comparisons++;/* (solveRow(board, row + 1)) */

            counters.prunningCounter++;
            board.removeQueen(row);
            counters.assignments++;
        }
        counters.comparisons++;/*Comparación false de for (int column = 0; column < n; column++)*/
        counters.assignments++;/*Asignación de int column en for (int column = 0; column < n; column++)*/
        return false;
    }

    public String getMetrics() {
        int totalInstructions = counters.assignments + counters.comparisons;
        return String.format(
                "Instrucciones ejecutadas: %d%n" +
                "Asignaciones: %d%n" +
                "Comparaciones: %d%n" +
                "Podas: %d%n"+
                "Memoria de variables: %d bits (%d Bytes)",
                totalInstructions,
                counters.assignments,
                counters.comparisons,
                counters.prunningCounter,
                counters.manualBits,
                counters.manualBits/8
        );
    }
}