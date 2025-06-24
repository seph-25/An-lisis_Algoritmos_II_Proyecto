import java.text.MessageFormat;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int[] sizes = {5,10,15,20,25,27,30,33};
        for (int n : sizes) {
            System.out.println(MessageFormat.format("Tamaño {0}x{0}\n",n));

            System.out.println("Backtracking:");
            new BacktrackingCore(n);

            System.out.println("\nGenético (Cruce de un punto):");
            new GeneticCore(n, 1,1);

            System.out.println("\nGenético (Cruce uniforme):");
            new GeneticCore(n, 2,2);

            System.out.println('\n'+String.valueOf('☰').repeat(50)+'\n');
            Scanner sc = new Scanner(System.in);
            System.out.println("Presione Enter para continuar con el próximo valor de n");
            sc.nextLine();
        }
    }
}

