public class Main {
    public static void main(String[] args) {
        int n = 8;
            //Backtracking
        Board board = new Board(n);
        BacktrackingSolver solver = new BacktrackingSolver();

        boolean found = solver.solve(board);
        System.out.println("¿Solución encontrada? " + found);
        if (found) {
            board.printBoard();
        }
        System.out.println(solver.getMetrics());

            // --- Genético con parámetros ---
        int populationSize = 100;
        int generations = 1000;
        double mutationRate = 0.1; // 10% de probabilidad de mutar
        int elitismCount = 10; // El 10% de la población es élite

        GeneticSolver genSolver = new GeneticSolver(
                n,
                populationSize,
                generations,
                mutationRate,
                elitismCount
        );

        QueenChromosome best = genSolver.solve();
        System.out.println(
                "\nMejor solución genética (fitness " + best.getFitness() + "):"
        );
        // Usamos la clase Board para imprimir el resultado del genético
        Board genBoard = new Board(best.getGenes());
        genBoard.printBoard();
    }
}