public class TimeMemoryProfilerResource {
    private long startTime;
    private long startMemory;
    private long endTime;
    private long endMemory;

    public void start() {
        System.gc(); // Opcional: para limpiar memoria antes de medir
        Runtime runtime = Runtime.getRuntime();
        startMemory = runtime.totalMemory() - runtime.freeMemory();
        startTime = System.nanoTime();
    }

    public void stop() {
        Runtime runtime = Runtime.getRuntime();
        endTime = System.nanoTime();
        endMemory = runtime.totalMemory() - runtime.freeMemory();
    }

    public long getElapsedTimeMillis() {
        return (endTime - startTime) / 1_000_000;
    }

    public long getMemoryUsedBytes() {
        return endMemory - startMemory;
    }

    public void printReport() {
        System.out.printf("Tiempo transcurrido: %d ms%n", getElapsedTimeMillis());
        System.out.printf("Memoria usada: %d bytes%n", getMemoryUsedBytes());
    }
}