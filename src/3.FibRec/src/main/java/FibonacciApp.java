import java.util.InputMismatchException;
import java.util.Scanner;

public class FibonacciApp {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)){
            int numberFib = searchNumberFib(readIndexFib(scanner));
            System.out.println(numberFib);
        } catch (ArithmeticException | StackOverflowError e) {
            System.out.println("Too large n");
        }
    }

    static int readIndexFib(Scanner scanner){
        while (true){
            try {
                int n = scanner.nextInt();
                if (n < 0) {
                    throw new InputMismatchException();
                }
                return n;
            } catch (InputMismatchException e) {
                System.out.println("Could not parse a number. Please, try again");
                scanner.nextLine();
            }
        }
    }

    static int searchNumberFib(int n) {
        if (n == 0 || n == 1) {
            return n;
        }
        return Math.addExact(searchNumberFib(n - 1), searchNumberFib(n - 2));
    }
}
