import java.util.*;

public class GeneticSolver {
    private final int n;
    private final int populationSize;
    private final int checkpointInterval; // Renombrado de 'generations'
    private final double mutationRate;
    private final int elitismCount;

    public GeneticSolver(
            int n,
            int populationSize,
            int checkpointInterval,
            double mutationRate,
            int elitismCount
    ) {
        this.n = n;
        this.populationSize = populationSize;
        this.checkpointInterval = checkpointInterval;
        this.mutationRate = mutationRate;
        this.elitismCount = elitismCount;
    }

    /**
     * Ejecuta el algoritmo genético de forma continua hasta encontrar una
     * solución válida.
     * @return Un objeto GeneticSolverResult que contiene la solución final y el
     *         historial.
     */
    public GeneticSolverResult solve() {
        List<QueenChromosome> population = initializePopulation();
        // Usamos una LinkedList para gestionar fácilmente el historial de 2 elementos.
        LinkedList<QueenChromosome> checkpointHistory = new LinkedList<>();
        int generationCounter = 0;

        while (true) {
            generationCounter++;
            Collections.sort(population);

            // --- CONDICIÓN DE PARADA ÚNICA ---
            // Si el mejor individuo tiene fitness 0, hemos terminado.
            if (population.get(0).getFitness() == 0) {
                System.out.println(
                        "\n¡Solución VÁLIDA encontrada en la generación total " +
                                generationCounter +
                                "!"
                );
                return new GeneticSolverResult(
                        checkpointHistory,
                        population.get(0),
                        generationCounter
                );
            }

            // --- LÓGICA DE CHECKPOINT ---
            // Se activa cada vez que se completa un ciclo de 'checkpointInterval' generaciones.
            if (generationCounter > 0 && generationCounter % this.checkpointInterval == 0) {
                QueenChromosome bestAtCheckpoint = population.get(0);
                System.out.println(
                        "\n--- CHECKPOINT ALCANZADO (Generación " +
                                generationCounter +
                                ") ---"
                );
                System.out.println(
                        "Mejor fitness en este checkpoint: " +
                                bestAtCheckpoint.getFitness()
                );

                // Añadir la solución al historial
                checkpointHistory.add(bestAtCheckpoint);

                // Mantener el historial con un máximo de 2 soluciones
                if (checkpointHistory.size() > 2) {
                    checkpointHistory.removeFirst(); // Elimina la más antigua
                }
            }

            // Crear la siguiente generación para continuar la búsqueda
            population = createNextGeneration(population);
        }
    }

    // Los métodos initializePopulation, createNextGeneration, selectParent,
    // crossover y mutate permanecen exactamente iguales que en la versión anterior.
    // (Los incluyo aquí por completitud, pero no tienen cambios)

    private List<QueenChromosome> createNextGeneration(
            List<QueenChromosome> currentPopulation
    ) {
        List<QueenChromosome> newPopulation = new ArrayList<>();
        for (int i = 0; i < this.elitismCount; i++) {
            newPopulation.add(currentPopulation.get(i));
        }
        while (newPopulation.size() < this.populationSize) {
            QueenChromosome parent1 = selectParent(currentPopulation);
            QueenChromosome parent2 = selectParent(currentPopulation);
            QueenChromosome child = crossover(parent1, parent2);
            mutate(child);
            newPopulation.add(child);
        }
        return newPopulation;
    }

    private List<QueenChromosome> initializePopulation() {
        List<QueenChromosome> population = new ArrayList<>();
        for (int i = 0; i < this.populationSize; i++) {
            population.add(new QueenChromosome(n));
        }
        return population;
    }

    private QueenChromosome selectParent(List<QueenChromosome> population) {
        int tournamentSize = 5;
        List<QueenChromosome> tournament = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = rand.nextInt(population.size());
            tournament.add(population.get(randomIndex));
        }
        return Collections.max(tournament);
    }

    private QueenChromosome crossover(QueenChromosome p1, QueenChromosome p2) {
        int[] parent1Genes = p1.getGenes();
        int[] parent2Genes = p2.getGenes();
        int[] childGenes = new int[n];
        Random rand = new Random();
        int crossoverPoint = rand.nextInt(n);
        for (int i = 0; i < n; i++) {
            if (i < crossoverPoint) {
                childGenes[i] = parent1Genes[i];
            } else {
                childGenes[i] = parent2Genes[i];
            }
        }
        return new QueenChromosome(childGenes);
    }

    private void mutate(QueenChromosome chromosome) {
        Random rand = new Random();
        if (rand.nextDouble() < this.mutationRate) {
            int row = rand.nextInt(n);
            int col = rand.nextInt(n);
            int[] genes = chromosome.getGenes();
            genes[row] = col;
            chromosome.setGenes(genes);
        }
    }
}