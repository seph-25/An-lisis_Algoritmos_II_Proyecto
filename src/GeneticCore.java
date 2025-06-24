import java.util.Arrays;
import java.util.List;

public class GeneticCore {

    public GeneticCore(int n, int crossoverType,int mutationType){
        int[] generations = new int[]{1,10,15};

        for (int gen : generations) {
            // 3 corridas requeridas
            System.out.println("\n===  GENERACIONES: " + gen + "  ===");

            GeneticsParams params = new GeneticsParams(
                    n,crossoverType,mutationType,gen);

            TimeMeter profiler = new TimeMeter();
            profiler.start();

            GeneticSolver solver = new GeneticSolver(params);
            List<QueenChromosome> best3 = solver.solve();

            profiler.stop();
            QueenChromosome best = best3.getFirst();
            System.out.println("Mejor fitness " + best.getFitness());
            new Board(best.getGenes()).printBoard();

            for (int i=1;i<best3.size();i++)
                System.out.println("Sol " + (i+1)+": "
                        + Arrays.toString(best3.get(i).getGenes())
                        + " -> " + best3.get(i).getFitness());

            profiler.printReport();
        }
    }
}