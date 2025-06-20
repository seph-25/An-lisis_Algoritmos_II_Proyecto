import java.text.MessageFormat;

public class Main {
    public static void main(String[] args) {
        int[] sizes = {5,10,15,20,25,27,30,33};
        for (int n : sizes) {
            System.out.println(MessageFormat.format("Tamaño {0}x{0}\n",n));

            System.out.println("Backtracking:");
            new BacktrackingCore(n);


            System.out.println("\nGenético:");
            new GeneticCore(n);


            System.out.println('\n'+String.valueOf('☰').repeat(30)+'\n');
        }
    }
}
