import java.util.Random;
import java.lang.Comparable;

public class QueenChromosome implements Comparable<QueenChromosome> {
    private int[] genes; // genes[i] = columna de la reina en la fila i
    private int fitness;

    public QueenChromosome(int n,Random rand) {
        this.genes = new int[n];
        randomize(rand);
        evaluateFitness();
    }

    public QueenChromosome(int[] genes) {
        this.genes = genes.clone();
        evaluateFitness();
    }

    public void randomize(Random rand) {
        for (int i = 0; i < genes.length; i++) {
            genes[i] = rand.nextInt(genes.length);
        }
        evaluateFitness();
    }

    public int[] getGenes() {
        return genes.clone();
    }

    public int getFitness() {
        return fitness;
    }

    public void evaluateFitness() {
        int conflicts = 0;
        int n = genes.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (genes[i] == genes[j] || Math.abs(genes[i] - genes[j]) == Math.abs(i - j)) {
                    conflicts++;
                }
            }
        }
        this.fitness = -conflicts; // Fitness negativo: menos conflictos es mejor
    }

    // Añadir este metodo dentro de la clase QueenChromosome.java
    public void setGenes(int[] genes) {
        this.genes = genes.clone();
        evaluateFitness(); // ¡Importante! Recalcular el fitness después de la mutación.
    }

    @Override
    public int compareTo(QueenChromosome other) {
        return Integer.compare(other.fitness, this.fitness); // Mayor fitness es mejor
    }
}