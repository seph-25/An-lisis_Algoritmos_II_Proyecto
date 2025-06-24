import java.util.List;

public class GeneticCore {

    public GeneticCore(int n, int crossoverType,int mutationType){
        TimeMeter profiler = new TimeMeter();
        GeneticsParams geneticsParams = new GeneticsParams(
                n,
                crossoverType,
                mutationType
        );

        profiler.start();

        GeneticSolver genSolver = new GeneticSolver(geneticsParams);
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
