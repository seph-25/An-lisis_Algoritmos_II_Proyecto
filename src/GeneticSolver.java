import java.util.*;

/**
 * Resuelve el problema de las N-Reinas usando un Algoritmo Genético.
 * Esta versión incluye correcciones críticas en la mutación y la
 * implementación de elitismo para asegurar la convergencia.
 */
public class GeneticSolver {
    // Parámetros configurables para mayor flexibilidad
    private final int n;
    private final int populationSize;
    private final int generations;
    private final double mutationRate;
    private final int elitismCount;

    public GeneticSolver(
            int n,
            int populationSize,
            int generations,
            double mutationRate,
            int elitismCount
    ) {
        this.n = n;
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.elitismCount = elitismCount;
    }

    public QueenChromosome solve() {
        List<QueenChromosome> population = initializePopulation();

        for (int gen = 0; gen < generations; gen++) {
            // Ordenar la población por fitness (el mejor primero)
            Collections.sort(population);

            // Condición de parada: si encontramos una solución perfecta, terminamos.
            if (population.get(0).getFitness() == 0) {
                System.out.println("Solución encontrada en la generación: " + gen);
                return population.get(0);
            }

            population = createNextGeneration(population);
        }

        // Si no se encontró solución perfecta, devolver la mejor encontrada
        Collections.sort(population);
        return population.get(0);
    }

    private List<QueenChromosome> createNextGeneration(
            List<QueenChromosome> currentPopulation
    ) {
        List<QueenChromosome> newPopulation = new ArrayList<>();

        // --- IMPLEMENTACIÓN DE ELITISMO ---
        // Los 'elitismCount' mejores individuos pasan directamente a la siguiente generación.
        for (int i = 0; i < this.elitismCount; i++) {
            newPopulation.add(currentPopulation.get(i));
        }

        // Rellenar el resto de la población con nuevos hijos
        while (newPopulation.size() < this.populationSize) {
            QueenChromosome parent1 = selectParent(currentPopulation);
            QueenChromosome parent2 = selectParent(currentPopulation);

            // Solo se genera un hijo por cruce para simplificar
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
        // Selección por Torneo: más eficiente y efectiva
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
        // Cruce de un solo punto (genera un solo hijo)
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

    // --- CORRECCIÓN DE MUTACIÓN ---
    private void mutate(QueenChromosome chromosome) {
        Random rand = new Random();
        // La mutación solo ocurre si se cumple la probabilidad
        if (rand.nextDouble() < this.mutationRate) {
            int row = rand.nextInt(n);
            int col = rand.nextInt(n);
            int[] genes = chromosome.getGenes();
            genes[row] = col;
            // Se utiliza el nuevo método para modificar el cromosoma y recalcular su fitness
            chromosome.setGenes(genes);
        }
    }
}