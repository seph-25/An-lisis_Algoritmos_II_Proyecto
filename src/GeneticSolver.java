import java.util.*;

public class GeneticSolver {
    private final GeneticsParams params;
    private final Random rand = new Random();
    private final Counters counters = new Counters();

    public GeneticSolver(GeneticsParams params) {
        this.params = params;
        counters.manualBits += 32;
    }

    public List<QueenChromosome> solve() {
        List<QueenChromosome> population = initializePopulation();
        counters.manualBits += 32 + 32L * params.n_size();/*Peso de clase QueenChromosome*/
        counters.assignments++;

        counters.assignments++;
        counters.comparisons++;
        for (int g=0; g<params.runsGenerations(); g++) {
            counters.assignments++;
            counters.comparisons++;
            Collections.sort(population);
            System.out.println("Generación " + (g+1) +
                    "  mejor fitness = " +
                    population.getFirst().getFitness());
            if (population.getFirst().getFitness()==0) {
                counters.comparisons++;
                break;
            }
            population = createNextGeneration(population);
            counters.assignments++;
        }
        Collections.sort(population);
        return new ArrayList<>(population.subList(0,Math.min(3,population.size())));
    }

    private List<QueenChromosome> createNextGeneration(List<QueenChromosome> curr) {

        int childrenNeeded = params.offspringGenerations();
        counters.assignments++;
        counters.manualBits += 32;
        List<QueenChromosome> children = new ArrayList<>();
        counters.manualBits += 32 + 32L * params.n_size();/*Peso de clase QueenChromosome*/

        while (children.size() < childrenNeeded) {
            QueenChromosome[] p  = selectParents(curr);
            QueenChromosome[] ch = crossover(p[0], p[1]);

            mutate(ch[0]);  mutate(ch[1]);

            for (QueenChromosome c : ch) {
                if (containsGenes(curr, c) || containsGenes(children, c))
                    forceMutate(c);
                children.add(c);
                if (children.size() == childrenNeeded) break;
            }

            System.out.println(
                    "Padre1 "+Arrays.toString(p[0].getGenes())+" fit "+p[0].getFitness());
            System.out.println(
                    "Padre2 "+Arrays.toString(p[1].getGenes())+" fit "+p[1].getFitness());
            System.out.println(
                    "Hijo1  "+Arrays.toString(ch[0].getGenes())+" fit "+ch[0].getFitness());
            System.out.println(
                    "Hijo2  "+Arrays.toString(ch[1].getGenes())+" fit "+ch[1].getFitness()+"\n");
        }

        List<QueenChromosome> pool = new ArrayList<>();
        pool.addAll(curr);
        pool.addAll(children);
        Collections.sort(pool);

        Set<String> seen = new HashSet<>();
        List<QueenChromosome> next = new ArrayList<>();
        for (QueenChromosome q : pool) {
            if (seen.add(Arrays.toString(q.getGenes())))
                next.add(q);
            if (next.size() == params.populationSize()) break;
        }
        return next;
    }

    /**
     * Inicializar población y evitar cromosomas duplicados en la población inicial.
     */
    private List<QueenChromosome> initializePopulation() {
        counters.assignments+=3;
        List<QueenChromosome> population = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int attempts = 0;
        counters.comparisons++;
        while (population.size() < params.populationSize()) {
            counters.comparisons++;
            QueenChromosome candidate = new QueenChromosome(params.n_size(),rand);
            String key = Arrays.toString(candidate.getGenes());
            counters.assignments+=2;
            if (!seen.contains(key)) {
                counters.comparisons++;
                counters.assignments+=2;
                population.add(candidate);
                seen.add(key);
            }
            attempts++;
            counters.assignments++;
            if (attempts > params.populationSize() * 100) {
                counters.comparisons++;
                System.out.println("Advertencia: No se pudo generar una población inicial completamente única.");
                break;
            }
        }
        System.out.println("Población inicial generada con " + population.size() + " cromosomas.");
        for (QueenChromosome chromosome : population) {
            counters.comparisons++;
            System.out.println(Arrays.toString(chromosome.getGenes()) + " -> " + chromosome.getFitness());
        }counters.comparisons++;
        System.out.println(String.valueOf('☰').repeat(50));
        return population;
    }

    private QueenChromosome[] selectParents(List<QueenChromosome> pop) {

        int tournamentSize = Math.min(2, pop.size());
        Collections.shuffle(pop, rand);

        QueenChromosome parent1 = Collections.min(pop.subList(0, tournamentSize));

        QueenChromosome parent2 = parent1;

        counters.assignments+=3;
        counters.comparisons++;
        for (QueenChromosome c : pop) {
            counters.comparisons++;
            if (!Arrays.equals(c.getGenes(), parent1.getGenes())) {
                counters.comparisons++;
                counters.assignments++;
                parent2 = c;
                break;
            }
        }
        return new QueenChromosome[]{parent1, parent2};
    }

    private QueenChromosome[] crossoverOnePoint(QueenChromosome p1, QueenChromosome p2) {
        counters.assignments+=6;
        int[] parent1Genes = p1.getGenes();
        int[] parent2Genes = p2.getGenes();
        int[] child1Genes = new int[params.n_size()];
        int[] child2Genes = new int[params.n_size()];
        int crossoverPoint = rand.nextInt(params.n_size());

        counters.comparisons++;
        for (int i = 0; i < params.n_size(); i++) {
            counters.comparisons++;
            if (i < crossoverPoint) {
                counters.comparisons++;
                counters.assignments+=2;
                child1Genes[i] = parent1Genes[i];
                child2Genes[i] = parent2Genes[i];
            } else {
                counters.comparisons++;
                counters.assignments+=2;
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
        counters.assignments+=5;
        int[] parent1Genes = p1.getGenes();
        int[] parent2Genes = p2.getGenes();
        int[] child1Genes = new int[params.n_size()];
        int[] child2Genes = new int[params.n_size()];
        counters.comparisons++;
        for (int i = 0; i < params.n_size(); i++) {
            counters.comparisons++;
            if (rand.nextBoolean()) {
                counters.comparisons++;
                counters.assignments+=2;
                child1Genes[i] = parent1Genes[i];
                child2Genes[i] = parent2Genes[i];
            } else {
                counters.comparisons++;
                counters.assignments+=2;
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
            counters.comparisons++;
            return crossoverOnePoint(p1, p2);
        } else {
            return crossoverUniform(p1, p2);
        }
    }

    private void mutate(QueenChromosome chrom) {

        if (rand.nextDouble() >= params.mutationRate()) {
            counters.comparisons++;
            return;
        }

        int[]  beforeGenes = chrom.getGenes().clone();
        int    beforeFit   = chrom.getFitness();
        counters.assignments+=2;

        if (params.mutationType() == 1) {
            counters.comparisons++;
            mutateRandom(chrom);
        }
        else {
            mutateSwapIfBetter(chrom);
        }

        if (!Arrays.equals(beforeGenes, chrom.getGenes())) {
            counters.comparisons++;
            counters.mutationCounter++;
            MutationLogger.log(beforeGenes, beforeFit,
                    chrom.getGenes(),  chrom.getFitness());
        }
    }

    private void mutateRandom(QueenChromosome chrom){
        counters.assignments+=2;
        int row = rand.nextInt(params.n_size());
        int col = rand.nextInt(params.n_size());

        if (chrom.getGenes()[row] == col) {
            counters.comparisons++;
            return;
        };

        int[] g = chrom.getGenes();
        int old = g[row];
        g[row]  = col;
        chrom.setGenes(g);
        counters.assignments+=4;

        if (chrom.getFitness() < 0 && rand.nextDouble() < 0.60) {
            counters.comparisons++;
            counters.assignments+=2;
            g[row] = old;
            chrom.setGenes(g);
        }
    }

    private void mutateSwapIfBetter(QueenChromosome chrom){
        counters.assignments+=2;
        int i = rand.nextInt(params.n_size());
        int j = rand.nextInt(params.n_size());
        if (i == j) {
            counters.comparisons++;
            return;
        }

        counters.assignments++;
        int[] g = chrom.getGenes();
        if (g[i] == g[j]) {
            counters.comparisons++;
            return;
        }

        int oldFit = chrom.getFitness();
        int tmp = g[i]; g[i] = g[j]; g[j] = tmp;
        chrom.setGenes(g);
        counters.assignments+=5;

        /* revierte si empeora */
        if (chrom.getFitness() < oldFit) {
            counters.comparisons++;
            counters.assignments+=4;
            tmp = g[i]; g[i] = g[j]; g[j] = tmp;
            chrom.setGenes(g);
        }
    }

    private void forceMutate(QueenChromosome chrom){
        counters.assignments+=4;
        int row = rand.nextInt(params.n_size());
        int col = rand.nextInt(params.n_size());
        chrom.getGenes()[row] = col;
        chrom.setGenes(chrom.getGenes());
    }

    private boolean containsGenes(List<QueenChromosome> list, QueenChromosome q){
        String key = Arrays.toString(q.getGenes());
        counters.assignments++;
        counters.comparisons++;
        for (QueenChromosome x : list) {
            counters.comparisons++;
            if (key.equals(Arrays.toString(x.getGenes()))){
                counters.comparisons++;
                return true;
            }
        }
        return false;
    }

    public String getMetrics() {
        long totalInstructions = counters.assignments + counters.comparisons;
        return String.format(
                "Instrucciones ejecutadas: %d%n" +
                "Asignaciones: %d%n" +
                "Comparaciones: %d%n" +
                "Mutaciones: %d%n"+
                "Memoria de variables: %d bits (%d bytes)",
                totalInstructions,
                counters.assignments,
                counters.comparisons,
                counters.mutationCounter,
                counters.manualBits,
                counters.manualBits/8
        );
    }
}