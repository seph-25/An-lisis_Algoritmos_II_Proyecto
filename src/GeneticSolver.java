import java.util.*;

public class GeneticSolver {
    private final int n;
    private final int populationSize;
    private final int generations;
    private final double mutationRate;
    private final int elitismCount;
    private final int crossoverType;
    private final int mutationType;

    public GeneticSolver(
            int n,
            int populationSize,
            int generations,
            double mutationRate,
            int elitismCount,
            int crossoverType,
            int mutationType
    ) {
        this.n = n;
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.elitismCount = elitismCount;
        this.crossoverType = crossoverType;
        this.mutationType = mutationType;
    }

    public QueenChromosome solve() {
        List<QueenChromosome> population = initializePopulation();

        for (int gen = 0; gen < generations; gen++) {
            Collections.sort(population);// Ordenar la población por fitness (el mejor primero)

            if (population.get(0).getFitness() == 0) {// Condición de parada: si encontramos una solución perfecta, terminamos.
                System.out.println("Solución encontrada en la generación: " + gen);
                return population.get(0);
            }

            population = createNextGeneration(population);
        }

        Collections.sort(population);// Si no se encontró solución perfecta, devolver la mejor encontrada
        return population.get(0);
    }

    private List<QueenChromosome> createNextGeneration(List<QueenChromosome> currentPopulation) {
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

    /**
     * Inicializar población y evitar cromosomas duplicados en la población inicial.
     */
    private List<QueenChromosome> initializePopulation() {
        List<QueenChromosome> population = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int attempts = 0;
        while (population.size() < this.populationSize) {
            QueenChromosome candidate = new QueenChromosome(n);
            String key = Arrays.toString(candidate.getGenes());
            if (!seen.contains(key)) {
                population.add(candidate);
                seen.add(key);
            }
            // Evitar bucle infinito si la población es muy grande para N pequeño
            attempts++;
            if (attempts > this.populationSize * 100) {
                System.out.println("Advertencia: No se pudo generar una población inicial completamente única.");
                break;
            }
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

    private QueenChromosome crossoverOnePoint(QueenChromosome p1, QueenChromosome p2) {
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

    private QueenChromosome crossoverUniform(QueenChromosome p1, QueenChromosome p2) {
        int[] parent1Genes = p1.getGenes();
        int[] parent2Genes = p2.getGenes();
        int[] childGenes = new int[n];
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            if (rand.nextBoolean()) {
                childGenes[i] = parent1Genes[i];
            } else {
                childGenes[i] = parent2Genes[i];
            }
        }
        return new QueenChromosome(childGenes);
    }

    private QueenChromosome crossover(QueenChromosome p1, QueenChromosome p2) {
        if (crossoverType == 1) {
            return crossoverOnePoint(p1, p2);
        } else {
            return crossoverUniform(p1, p2);
        }
    }

    private void mutate(QueenChromosome chromosome) {
        Random rand = new Random();
        if (rand.nextDouble() < this.mutationRate) {
            if (mutationType == 1) {
                mutateRandom(chromosome);
            } else {
                mutateSwapIfBetter(chromosome);
            }
        }
    }

    private void mutateRandom(QueenChromosome chromosome) {
        Random rand = new Random();
        int row = rand.nextInt(n);
        int col = rand.nextInt(n);
        int[] genes = chromosome.getGenes();
        int oldValue = genes[row];
        genes[row] = col;
        chromosome.setGenes(genes);
        // Si no mejora el fitness, revierte
        if (chromosome.getFitness() < 0) { // fitness negativo es peor
            genes[row] = oldValue;
            chromosome.setGenes(genes);
        }
    }

    private void mutateSwapIfBetter(QueenChromosome chromosome) {
        Random rand = new Random();
        int[] genes = chromosome.getGenes();
        int i = rand.nextInt(n);
        int j = rand.nextInt(n);
        while (j == i) j = rand.nextInt(n);

        int oldFitness = chromosome.getFitness();
        // Intercambia dos posiciones
        int temp = genes[i];
        genes[i] = genes[j];
        genes[j] = temp;
        chromosome.setGenes(genes);

        // Solo aplica si mejora el fitness
        if (chromosome.getFitness() < oldFitness) {
            // Si no mejora, revierte
            temp = genes[i];
            genes[i] = genes[j];
            genes[j] = temp;
            chromosome.setGenes(genes);
        }
    }

}