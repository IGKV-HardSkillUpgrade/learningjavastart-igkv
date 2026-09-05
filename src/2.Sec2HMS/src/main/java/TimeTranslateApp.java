import java.util.InputMismatchException;
import java.util.Scanner;

public class TimeTranslateApp {
    public static void main (String[] args){
        try (Scanner scanner = new Scanner(System.in)){
            int seconds = readSeconds(scanner);
            if (seconds < 0) {
                System.out.println("Incorrect time");
            } else {
                String parsTime = timeTranslate(seconds);
                printNewTimeFormat(parsTime);
            }
        }
    }

    static int readSeconds (Scanner scanner) {
            while (true) {
                try {
                    return scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Could not parse a number. Please, try again");
                    scanner.nextLine();
                }
            }
    }
    static String timeTranslate(int seconds){
        int hour = seconds / 3600;
        int minute = (seconds - (hour * 3600)) / 60;
        int secondsResult = seconds - (hour * 3600) - (minute * 60);
        return String.format("%02d:%02d:%02d", hour, minute, secondsResult);
    }

    static void printNewTimeFormat(String resultTime){
        System.out.printf(resultTime);
    }
}
