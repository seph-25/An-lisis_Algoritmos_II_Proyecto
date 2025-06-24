public class GeneticsParams {
        private final int n_size;
        private  int populationSize;
        private  int generations;
        private double mutationRate;
        private int elitismCount;
        private final int crossoverType;
        private final int mutationType;

    public GeneticsParams(
            int n_size,
            int crossoverType,
            int mutationType
        ) {
        this.n_size = n_size;
        this.crossoverType = crossoverType;
        this.mutationType = mutationType;

        switch (this.n_size) {
            case 5:
                this.populationSize = 3;
                this.generations = 6;
                this.elitismCount = 1;
                this.mutationRate = 0.30;//Valor optimo probado 0.50 se estanca en fitness -2
                break;
            case 10:
                this.populationSize = 5;
                this.generations = 10;
                this.elitismCount = 1;
                this.mutationRate = 0.22;
                break;
            case 15:
                this.populationSize = 8;
                this.generations = 16;
                this.elitismCount = 2;
                this.mutationRate = 0.20;
                break;
            case 20:
                this.populationSize = 10;
                this.generations = 20;
                this.elitismCount = 2;
                this.mutationRate = 0.19;
                break;
            case 25:
                this.populationSize = 13;
                this.generations = 26;
                this.elitismCount = 2;
                this.mutationRate = 0.18;
                break;
            case 27:
                this.populationSize = 14;
                this.generations = 28;
                this.elitismCount = 2;
                this.mutationRate = 0.18;
                break;
            case 30:
                this.populationSize = 15;
                this.generations = 30;
                this.elitismCount = 2;
                this.mutationRate = 0.17;
                break;
            case 33:
                this.populationSize = 17;
                this.generations = 34;
                this.elitismCount = 3;
                this.mutationRate = 0.17;
            default:
                this.populationSize = Math.max(3, n_size / 2);
                this.generations = n_size * 2;
                this.elitismCount = Math.max(1, this.populationSize / 10);
                this.mutationRate = 0.15;
        }
    }

    public int n_size() {
        return n_size;
    }

    public int populationSize() {
        return populationSize;
    }

    public int generations() {
        return generations;
    }

    public double mutationRate() {
        return mutationRate;
    }

    public int elitismCount() {
        return elitismCount;
    }

    public int crossoverType() {
        return crossoverType;
    }

    public int mutationType() {
        return mutationType;
    }
}
