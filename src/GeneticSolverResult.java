import java.util.List;

/**
 * Contiene el resultado de la ejecución del GeneticSolver.
 *
 * @param checkpointSolutions La lista de las dos últimas mejores soluciones
 *                            encontradas en los checkpoints.
 * @param finalSolution       La solución válida final (fitness = 0).
 * @param totalGenerations    El número total de generaciones que tomó
 *                            encontrar la solución.
 */
public record GeneticSolverResult(
        List<QueenChromosome> checkpointSolutions,
        QueenChromosome finalSolution,
        int totalGenerations
) {}