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
        //logCountersHeader();
        return new ArrayList<>(population.subList(0,Math.min(3,population.size())));
    }

    /* EXACTO número de hijos = popSize*2 */
    private List<QueenChromosome> createNextGeneration(List<QueenChromosome> curr) {

        int childrenNeeded = params.offspringGenerations();
        List<QueenChromosome> children = new ArrayList<>();

        while (children.size() < childrenNeeded) {
            QueenChromosome[] p  = selectParents(curr);
            QueenChromosome[] ch = crossover(p[0], p[1]);

            mutate(ch[0]);  mutate(ch[1]);

            /* si el hijo ya existe, forzar mutación para diversificar */
            for (QueenChromosome c : ch) {
                if (containsGenes(curr, c) || containsGenes(children, c))
                    forceMutate(c);
                children.add(c);
                if (children.size() == childrenNeeded) break;
            }

            /* impresión del cruce (padres e hijos) */
            System.out.println(
                    "Padre1 "+Arrays.toString(p[0].getGenes())+" fit "+p[0].getFitness());
            System.out.println(
                    "Padre2 "+Arrays.toString(p[1].getGenes())+" fit "+p[1].getFitness());
            System.out.println(
                    "Hijo1  "+Arrays.toString(ch[0].getGenes())+" fit "+ch[0].getFitness());
            System.out.println(
                    "Hijo2  "+Arrays.toString(ch[1].getGenes())+" fit "+ch[1].getFitness()+"\n");
        }

        /* --- construir nueva población sin duplicados --- */
        List<QueenChromosome> pool = new ArrayList<>();
        pool.addAll(curr);
        pool.addAll(children);
        Collections.sort(pool);                    // mejor → primero

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
        counters.assignments += 2;                // candidate + key
        counters.manualBits += Integer.SIZE*2;

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

    private QueenChromosome[] selectParents(List<QueenChromosome> pop) {

        int tournamentSize = Math.min(2, pop.size()); // ↓ presión selectiva
        Collections.shuffle(pop, rand);

        QueenChromosome parent1 = Collections.min(pop.subList(0, tournamentSize));

        QueenChromosome parent2 = parent1;
        for (QueenChromosome c : pop)                    // busca otro distinto
            if (!Arrays.equals(c.getGenes(), parent1.getGenes())) {
                parent2 = c; break;
            }
        if (parent2 == parent1) parent2 = parent1;       // si población=1 (caso ext.)

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
        int[] before = chrom.getGenes();
        int beforeFitness  = chrom.getFitness();

        counters.mutationCounter++;          // count mutation attempt
        counters.assignments += 3;
        counters.manualBits  += Integer.SIZE*3;

        int row = rand.nextInt(params.n_size());
        int col = rand.nextInt(params.n_size());
        int[] g  = chrom.getGenes(); int old = g[row];
        g[row] = col; chrom.setGenes(g);

        if (chrom.getFitness() < beforeFitness && rand.nextDouble() < .60){
            g[row] = old; chrom.setGenes(g);
        }
        else {
            MutationLogger.log(before,beforeFitness,chrom.getGenes(),chrom.getFitness());
        }
    }

    private void mutateSwapIfBetter(QueenChromosome c){
        int[] before = c.getGenes(); int beforeF = c.getFitness();
        counters.mutationCounter++;

        int i = rand.nextInt(params.n_size());
        int j = rand.nextInt(params.n_size());
        while(j==i) j = rand.nextInt(params.n_size());

        int[] g = c.getGenes();
        int tmp = g[i]; g[i] = g[j]; g[j] = tmp;
        c.setGenes(g);

        if (c.getFitness() < beforeF){          // revert if worse
            tmp = g[i]; g[i] = g[j]; g[j] = tmp;
            c.setGenes(g);
        }else{
            MutationLogger.log(before,beforeF,c.getGenes(),c.getFitness());
        }

    }

    /*  mutación “fuerte” obligatoria (sólo se usa para romper duplicados) */
    private void forceMutate(QueenChromosome c){
        int row = rand.nextInt(params.n_size());
        int col = rand.nextInt(params.n_size());
        c.getGenes()[row] = col;
        c.setGenes(c.getGenes());          // recalcula fitness
    }

    private boolean containsGenes(List<QueenChromosome> list, QueenChromosome q){
        String key = Arrays.toString(q.getGenes());
        for (QueenChromosome x : list)
            if (key.equals(Arrays.toString(x.getGenes())))
                return true;
        return false;
    }

    public String getMetrics() {
        int totalInstructions = counters.assignments + counters.comparisons;
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