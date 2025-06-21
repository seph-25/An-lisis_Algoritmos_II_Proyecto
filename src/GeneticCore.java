import java.util.List;

public class GeneticCore {

    public GeneticCore(int n, int crossoverType,int mutationType){
        TimeMeter profiler = new TimeMeter();
        GeneticsParams geneticsParams = new GeneticsParams(
                30,
                60,
                0.1,
                10
        );

        profiler.start();

        GeneticSolver genSolver = new GeneticSolver(
                n,
                geneticsParams.populationSize(),
                geneticsParams.generations(),
                geneticsParams.mutationRate(),
                geneticsParams.elitismCount(),
                crossoverType,
                mutationType
        );
        List<QueenChromosome> bestList = genSolver.solve();

        profiler.stop();

        QueenChromosome best = bestList.getFirst();
        System.out.println("Mejor solución genética (fitness " + best.getFitness() + "):");
        Board genBoard = new Board(best.getGenes());
        genBoard.printBoard();
        System.out.println("Arreglo queens: " + java.util.Arrays.toString(best.getGenes()));

        for (int i = 1; i < bestList.size(); i++) {
            QueenChromosome other = bestList.get(i);
            System.out.println("Solución genética #" + (i+1) + " (fitness " + other.getFitness() + "):");
            System.out.println("Arreglo queens: " + java.util.Arrays.toString(other.getGenes()));
        }
        System.out.println(genSolver.getMetrics());
        profiler.printReport();
    }
}
