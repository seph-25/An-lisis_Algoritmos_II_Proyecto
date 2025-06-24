import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GeneticSolver {
    private final GeneticsParams params;
    private final Random rand = new Random();
    private final Counters counters = new Counters();

    public GeneticSolver(GeneticsParams params) {
        this.params = params;
    }

    public List<QueenChromosome> solve() {
        List<QueenChromosome> population = initializePopulation();
        for (int g=0; g<params.runsGenerations(); g++) {
            Collections.sort(population);
            System.out.println("Generación " + (g+1) +
                    "  mejor fitness = " +
                    population.getFirst().getFitness());
            if (population.getFirst().getFitness()==0) break;
            population = createNextGeneration(population);
        }
        Collections.sort(population);
        return new ArrayList<>(population.subList(0,Math.min(3,population.size())));
    }

    /* EXACTO número de hijos = popSize*2 */
    private List<QueenChromosome> createNextGeneration(List<QueenChromosome> curr){
        List<QueenChromosome> next = new ArrayList<>(curr.subList(0,params.elitismCount()));

        int childrenNeeded = params.childrenPerGeneration();
        List<QueenChromosome> children = new ArrayList<>();

        while (children.size() < childrenNeeded) {
            QueenChromosome[] parents = selectParents(curr);
            QueenChromosome[] offspring = crossover(parents[0],parents[1]);
            mutate(offspring[0]);  mutate(offspring[1]);

            children.add(offspring[0]);
            if (children.size() < childrenNeeded) children.add(offspring[1]);

            String cruceInfo =
                    "Padre1 " + Arrays.toString(parents[0].getGenes()) + " fitness " + parents[0].getFitness() + "\n" +
                    "Padre2 " + Arrays.toString(parents[1].getGenes()) + " fitness " + parents[1].getFitness() + "\n" +
                    "Hijo1  " + Arrays.toString(offspring[0].getGenes()) + " fitness " + offspring[0].getFitness() + "\n" +
                    "Hijo2  " + Arrays.toString(offspring[1].getGenes()) + " fitness " + offspring[1].getFitness() + "\n";
            System.out.println(cruceInfo);

        }
        curr.addAll(children);
        Collections.sort(curr);
        while (next.size() < params.populationSize())
            next.add(curr.get(next.size()));

        return next;
    }

    /**
     * Inicializar población y evitar cromosomas duplicados en la población inicial.
     */
    private List<QueenChromosome> initializePopulation() {
        List<QueenChromosome> population = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int attempts = 0;
        while (population.size() < params.populationSize()) {
            QueenChromosome candidate = new QueenChromosome(params.n_size(),rand);
            String key = Arrays.toString(candidate.getGenes());
            if (!seen.contains(key)) {
                population.add(candidate);
                seen.add(key);
            }
            attempts++;
            if (attempts > params.populationSize() * 100) {
                System.out.println("Advertencia: No se pudo generar una población inicial completamente única.");
                break;
            }
        }
        System.out.println("Población inicial generada con " + population.size() + " cromosomas.");
        for (QueenChromosome chromosome : population) {
            System.out.println(Arrays.toString(chromosome.getGenes()) + " -> " + chromosome.getFitness());
        }
        System.out.println(String.valueOf('☰').repeat(50));
        return population;
    }

    private QueenChromosome[] selectParents(List<QueenChromosome> population) {
        int tournamentSize = Math.min(5, population.size());

        // Mezcla la población para asegurar aleatoriedad
        List<QueenChromosome> shuffled = new ArrayList<>(population);
        Collections.shuffle(shuffled, rand);

        // Torneo 1: selecciona el mejor de los primeros 'tournamentSize'
        List<QueenChromosome> tournament1 = shuffled.subList(0, tournamentSize);
        QueenChromosome parent1 = Collections.min(tournament1);

        // Torneo 2: selecciona el mejor de los siguientes 'tournamentSize', excluyendo parent1
        List<QueenChromosome> tournament2 = new ArrayList<>();
        for (QueenChromosome c : shuffled) {
            if (!Arrays.equals(c.getGenes(), parent1.getGenes())) {
                tournament2.add(c);
                if (tournament2.size() == tournamentSize) break;
            }
        }
        // Si no hay suficientes, rellena con cualquier otro distinto, o repite parent1 si no hay opción
        if (tournament2.isEmpty()) tournament2.add(parent1);

        QueenChromosome parent2 = Collections.min(tournament2);

        return new QueenChromosome[]{parent1, parent2};
    }

    private QueenChromosome[] crossoverOnePoint(QueenChromosome p1, QueenChromosome p2) {
        int[] parent1Genes = p1.getGenes();
        int[] parent2Genes = p2.getGenes();
        int[] child1Genes = new int[params.n_size()];
        int[] child2Genes = new int[params.n_size()];
        int crossoverPoint = rand.nextInt(params.n_size());

        for (int i = 0; i < params.n_size(); i++) {
            if (i < crossoverPoint) {
                child1Genes[i] = parent1Genes[i];
                child2Genes[i] = parent2Genes[i];
            } else {
                child1Genes[i] = parent2Genes[i];
                child2Genes[i] = parent1Genes[i];
            }
        }
        return new QueenChromosome[]{
                new QueenChromosome(child1Genes),
                new QueenChromosome(child2Genes)
        };
    }

    private QueenChromosome[] crossoverUniform(QueenChromosome p1, QueenChromosome p2) {
        int[] parent1Genes = p1.getGenes();
        int[] parent2Genes = p2.getGenes();
        int[] child1Genes = new int[params.n_size()];
        int[] child2Genes = new int[params.n_size()];

        for (int i = 0; i < params.n_size(); i++) {
            if (rand.nextBoolean()) {
                child1Genes[i] = parent1Genes[i];
                child2Genes[i] = parent2Genes[i];
            } else {
                child1Genes[i] = parent2Genes[i];
                child2Genes[i] = parent1Genes[i];
            }
        }
        return new QueenChromosome[]{
                new QueenChromosome(child1Genes),
                new QueenChromosome(child2Genes)
        };
    }

    private QueenChromosome[] crossover(QueenChromosome p1, QueenChromosome p2) {
        if (params.crossoverType() == 1) {
            return crossoverOnePoint(p1, p2);
        } else {
            return crossoverUniform(p1, p2);
        }
    }

    private void mutate(QueenChromosome chromosome) {
        if (rand.nextDouble() < params.mutationRate()) {
            if (params.mutationType() == 1) {
                mutateRandom(chromosome);
            } else {
                mutateSwapIfBetter(chromosome);
            }
        }
    }

    /* MUTACIÓN aleatoria con reversión si empeora */
    private void mutateRandom(QueenChromosome chrom){
        int prevFit = chrom.getFitness();
        int row = rand.nextInt(params.n_size());
        int col = rand.nextInt(params.n_size());
        int[] g = chrom.getGenes();
        int old = g[row];
        g[row]=col;
        chrom.setGenes(g);               // recalcula fitness
        if (chrom.getFitness() < prevFit){ // ← revierte si PEOR
            g[row]=old;
            chrom.setGenes(g);
        }
    }

    private void mutateSwapIfBetter(QueenChromosome chromosome) {
        int[] genes = chromosome.getGenes();
        int i = rand.nextInt(params.n_size());
        int j = rand.nextInt(params.n_size());
        while (j == i) j = rand.nextInt(params.n_size());

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

    public String getMetrics() {
//        int totalInstructions = counters.assignments + counters.comparisons;
//        return String.format(
//                "Instrucciones ejecutadas: %d%n" +
//                        "Asignaciones: %d%n" +
//                        "Comparaciones: %d%n" +
//                        "Podas: %d%n"+
//                        "Memoria de variables: %d bits (%d bytes)",
//                totalInstructions,
//                counters.assignments,
//                counters.comparisons,
//                counters.prunningCounter,
//                counters.manualBits,
//                counters.manualBits/8
//        );
        return "";
    }

}