public class Main {
    public static void main(String[] args) {
        int n = 5; // Cambia el tamaño según lo necesites

        // Backtracking
        Board board = new Board(n);
        BacktrackingSolver backSolver = new BacktrackingSolver();
        if (backSolver.solve(board, 0)) {
            System.out.println("Solución Backtracking:");
            board.printBoard();
        } else {
            System.out.println("No hay solución.");
        }

        //Genético
        int populationSize = 150;
        int checkpointInterval = 500; // Cada 10 000 generaciones es un checkpoint
        double mutationRate = 0.3;
        int elitismCount = 20;

        GeneticSolver genSolver = new GeneticSolver(
                n,
                populationSize,
                checkpointInterval,
                mutationRate,
                elitismCount
        );

        System.out.println("\nIniciando Búsqueda Continua con Algoritmo Genético para N=" + n);
        System.out.println("La búsqueda se detendrá únicamente al encontrar una solución con fitness = 0.");

        // Ejecutar el solver y obtener el resultado
        GeneticSolverResult result = genSolver.solve();

        // --- IMPRESIÓN DE RESULTADOS FINALES ---

        // 1. Imprimir el historial de checkpoints
        System.out.println("\n-------------------------------------------------");
        System.out.println("--- HISTORIAL DE LAS ÚLTIMAS SOLUCIONES (CHECKPOINTS) ---");
        System.out.println("-------------------------------------------------");

        if (result.checkpointSolutions().isEmpty()) {
            System.out.println(
                    "No se completó ningún ciclo de checkpoint antes de encontrar la solución."
            );
        } else {
            int checkpointNum = 1;
            for (QueenChromosome ch : result.checkpointSolutions()) {
                System.out.println(
                        "\n>> Solución de Checkpoint #" +
                                checkpointNum +
                                " (Fitness: " +
                                ch.getFitness() +
                                ")"
                );
                Board checkpointBoard = new Board(ch.getGenes());
                checkpointBoard.printBoard();
                checkpointNum++;
            }
        }

        // 2. Imprimir la solución final válida
        System.out.println("\n-------------------------------------------------");
        System.out.println("--- SOLUCIÓN FINAL VÁLIDA ENCONTRADA ---");
        System.out.println("-------------------------------------------------");
        System.out.println(
                "\n>> Solución Final (Fitness: " +
                        result.finalSolution().getFitness() +
                        ")"
        );
        System.out.println(
                "Encontrada después de " + result.totalGenerations() + " generaciones totales."
        );
        Board finalBoard = new Board(result.finalSolution().getGenes());
        finalBoard.printBoard();
    }
}