
public class GeneticCore {

    public GeneticCore(int n){
        TimeMemoryProfilerResource profiler = new TimeMemoryProfilerResource();
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
                geneticsParams.elitismCount()
        );
        QueenChromosome best = genSolver.solve();

        profiler.stop();

        System.out.println(
                "Mejor solución genética (fitness " + best.getFitness() + "):"
        );

        Board genBoard = new Board(best.getGenes());
        genBoard.printBoard();
        profiler.printReport();
    }
}
