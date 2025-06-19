public class Main {
    public static void main(String[] args) {
        int n = 8; // Cambia el tamaño según lo necesites

        // Backtracking
        Board board = new Board(n);
        BacktrackingSolver backSolver = new BacktrackingSolver();
        if (backSolver.solve(board, 0)) {
            System.out.println("Solución Backtracking:");
            board.printBoard();
        } else {
            System.out.println("No hay solución.");
        }

        // --- Genético con parámetros ---
        int populationSize = 1500;
        int generations = 5000;
        double mutationRate = 0.05; // 10% de probabilidad de mutar
        int elitismCount = 15; // El 10% de la población es élite

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