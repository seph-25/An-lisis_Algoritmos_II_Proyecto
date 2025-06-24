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

        for (int gen = 0; gen < params.generations(); gen++) {
            Collections.sort(population);

            if (population.getFirst().getFitness() == 0) {
                System.out.println("Solución encontrada en la generación: " + gen);
                return new ArrayList<>(population.subList(0, Math.min(3, population.size())));
            }
            population = createNextGeneration(population);
        }

        Collections.sort(population);
        return new ArrayList<>(population.subList(0, Math.min(3, population.size())));
    }

    private List<QueenChromosome> createNextGeneration(List<QueenChromosome> currentPopulation) {
        List<QueenChromosome> newPopulation = new ArrayList<>();

        for (int i = 0; i < params.elitismCount(); i++) {
            newPopulation.add(currentPopulation.get(i));
        }

        while (newPopulation.size() < params.populationSize()) {
            QueenChromosome[] parents = selectParents(currentPopulation);
            QueenChromosome[] children = crossover(parents[0], parents[1]);
            mutate(children[0]);
            mutate(children[1]);

            String cruceInfo =
                "Padre1 " + Arrays.toString(parents[0].getGenes()) + " puntuación " + parents[0].getFitness() + "\n" +
                "Padre2 " + Arrays.toString(parents[1].getGenes()) + " puntuación " + parents[1].getFitness() + "\n" +
                "Hijo1  " + Arrays.toString(children[0].getGenes()) + " puntuación " + children[0].getFitness() + "\n" +
                "Hijo2  " + Arrays.toString(children[1].getGenes()) + " puntuación " + children[1].getFitness() + "\n";
            System.out.println(cruceInfo);

            newPopulation.add(children[0]);
            newPopulation.add(children[1]);
        }
        System.out.println("Población al final de la generación:");
        for (QueenChromosome c : newPopulation) {
            System.out.println(Arrays.toString(c.getGenes()) + " -> " + c.getFitness());
        }
        System.out.println("----");
        return newPopulation;
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
        QueenChromosome parent1 = Collections.max(tournament1);

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

        QueenChromosome parent2 = Collections.max(tournament2);

        return new QueenChromosome[]{parent1, parent2};
    }

    /*
    ¿En qué estuve trabajando?

    Estuve optimizando el metodo para seleccionar los padres para mejorar
    la diversidad pero evitar candidatos repetidos a la hora de escoger los padres
    y que sea robusto y evite ciclos infinitos, implementé el metodo que está abajo
    de este comentario el cual asegura que no hayan padres duplicados, sin embargo, no
    favorece a los mejores candidatos limitando la evolucion, el metodo de arriba de este comentario
    fue una propuesta híbrida de mi metodo y el anterior que utilizaba torneos, parece prometer
    robustez, diversidad y exclusividad de padres sin embargo aun no se ha probado.

    Pendiente:
    -Comprobar eficiencia de metodo selectParents
    -Agregar medidores
    -Documentación
    Fecha de comentario: 24/06/2025 12:35 am muriéndome de sueño
    */

    private QueenChromosome[] selectParents(List<QueenChromosome> population) {
        QueenChromosome parent1 = null ,parent2 = null;
        Set<Integer> usedIndexes = new HashSet<>();
        while (parent1 == null || parent2 == null){
            int idx = this.rand.nextInt(population.size());
            if (!usedIndexes.contains(idx)){
                usedIndexes.add(idx);
                if (parent1 == null){
                    parent1 = population.get(idx);
                }else {
                    parent2 = population.get(idx);
                    break;
                }
            }
        }
        return new QueenChromosome[]{parent1, parent2};
    }

    private QueenChromosome[] selectParent(List<QueenChromosome> population) {
        int tournamentSize = Math.min(5, population.size());

        //Torneo para e; primer padre (sin repetidos)
        List<QueenChromosome> tournament1 = new ArrayList<>();
        Set<Integer> usedIndexes = new HashSet<>();
        while (tournament1.size() < tournamentSize) {
            int idx = this.rand.nextInt(population.size());
            if (!usedIndexes.contains(idx)){
                tournament1.add(population.get(idx));
                usedIndexes.add(idx);
            }
        }
        QueenChromosome parent1 = Collections.min(tournament1);

        tournamentSize -= 1;

        //Torneo para segundo padre (sin repetidos y excluyendo el parent1)
        List<QueenChromosome> tournament2 = new ArrayList<>();
        usedIndexes = new HashSet<>();
        while (tournament2.size() < tournamentSize) {
            int idx = rand.nextInt(population.size());
            QueenChromosome candidate = population.get(idx);
            if (!usedIndexes.contains(idx) && !Arrays.equals(candidate.getGenes(), parent1.getGenes())) {
                tournament2.add(candidate);
                usedIndexes.add(idx);
            }
        }
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

    private void mutateRandom(QueenChromosome chromosome) {
        int row = rand.nextInt(params.n_size());
        int col = rand.nextInt(params.n_size());
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