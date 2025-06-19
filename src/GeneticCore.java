
public class GeneticCore {

    public GeneticCore(int n){
        GeneticsParams geneticsParams = new GeneticsParams(
                100,
                1000,
                0.1,
                10
        );
        GeneticSolver genSolver = new GeneticSolver(
                n,
                geneticsParams.populationSize(),
                geneticsParams.generations(),
                geneticsParams.mutationRate(),
                geneticsParams.elitismCount()
        );
        QueenChromosome best = genSolver.solve();
        System.out.println(
                "\nMejor solución genética (fitness " + best.getFitness() + "):"
        );
        // Usamos la clase Board para imprimir el resultado del genético
        Board genBoard = new Board(best.getGenes());
        genBoard.printBoard();
    }
}
