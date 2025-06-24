import java.util.Arrays;

public final class MutationLogger {
    private MutationLogger() {}

    public static void log(int[] before, int beforeFit,
                           int[] after , int  afterFit) {
        System.out.println("Individuo (antes)  "
                + Arrays.toString(before) + " fitness " + beforeFit);
        System.out.println("Mutación  (después) "
                + Arrays.toString(after)  + " fitness " + afterFit);
        System.out.println("------------------------------------------------");
    }
}