import java.text.MessageFormat;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int[] sizes = {5, 10, 15, 20, 25, 27, 30, 33};
        for (int n : sizes) {
            System.out.println(MessageFormat.format("Tamaño {0}x{0}\n", n));

            System.out.println("Backtracking:(Esto puede tardar unos minutos)\nn: " + n);
            new BacktrackingCore(n);
            next("continuar con el algoritmo Genético (Cruce de un punto)");

            System.out.println("\nGenético (Cruce de un punto):\nn: " + n);
            new GeneticCore(n, 1, 1);
            next("continuar con el algoritmo Genético (Cruce uniforme)");

            System.out.println("\nGenético (Cruce uniforme):\nn: " + n);
            new GeneticCore(n, 2, 2);

            System.out.println('\n' + String.valueOf('☰').repeat(50) + '\n');
            next("continuar con el próximo valor de n");
        }
    }

    /* Agrega un bloque de caracteres a la consola para encontrar la nueva ejeccuion para un nuevo n */
    public static void cls() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            builder.append(String.valueOf('☰').repeat(50)).append("\n");
        }
        System.out.print(builder+"\n");
    }

    public static void next(String message){
        Scanner sc = new Scanner(System.in);
        System.out.println("Presione Enter para "+message);
        sc.nextLine();
        cls();
    }
}



