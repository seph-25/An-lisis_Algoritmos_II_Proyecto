public class TimeMeter {
    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        endTime = System.nanoTime();
    }

    public long getElapsedTimeMillis() {
        return (endTime - startTime) / 1_000_000;
    }

    public void printReport() {
        System.out.printf("Tiempo transcurrido: %d ms%n", getElapsedTimeMillis());
    }
}
