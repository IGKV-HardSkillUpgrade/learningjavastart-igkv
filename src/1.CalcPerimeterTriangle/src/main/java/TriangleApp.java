import java.util.Scanner;

public class TriangleApp {

    public static void main(String[] args){
        try(Scanner scanner = new Scanner(System.in)){
            double x1 = scanDoc(scanner);
            double y1 = scanDoc(scanner);
            double x2 = scanDoc(scanner);
            double y2 = scanDoc(scanner);
            double x3 = scanDoc(scanner);
            double y3 = scanDoc(scanner);

            double edgeA = distanceTo(x1, y1, x2, y2);
            double edgeB = distanceTo(x2, y2, x3, y3);
            double edgeC = distanceTo(x3, y3, x1, y1);
            if (!isValidateTriangle(edgeA, edgeB, edgeC)) {
                System.out.println("It's not a triangle");
            }else {
                System.out.printf("Perimeter: %.3f", trianglePerimeter(edgeA, edgeB, edgeC));
            }

        }
    }


    static double scanDoc(Scanner scanner){
        while (true){
            if (!scanner.hasNextDouble()) {
                System.out.println("Could not parse a number. Please, try again");
                scanner.next();
                continue;
            }
            return scanner.nextDouble();
        }
    }

    static double trianglePerimeter (double edgeA, double edgeB, double edgeC){
        return edgeA + edgeB + edgeC;
    }
    static boolean isValidateTriangle (double edgeA, double edgeB, double edgeC){
        return (edgeA + edgeB > edgeC) &&
                (edgeA + edgeC > edgeB) &&
                (edgeB + edgeC > edgeA);
    }
    static double distanceTo(double x1, double y1, double x2, double y2){
        double dx = x1 - x2;
        double dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
