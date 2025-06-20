import java.text.MessageFormat;
import java.util.Arrays;

public class BacktrackingCore {
    public BacktrackingCore(int n){
        TimeMeter profiler = new TimeMeter();
        Board board = new Board(n);

        profiler.start();

        BacktrackingSolver solver = new BacktrackingSolver();
        boolean found = solver.solve(board);

        profiler.stop();

        if (found) {
            System.out.println("Solución encontrada: ");
            board.printBoard();
        }
        else{
            System.out.println(MessageFormat.format("No existe solución para un tablero de {0}x{0} con {0} reinas.",n));
        }

        System.out.println("Posición de columna de reina según su fila:\n" + Arrays.toString(board.getQueens()));
        System.out.println(solver.getMetrics());
        profiler.printReport();
    }
}