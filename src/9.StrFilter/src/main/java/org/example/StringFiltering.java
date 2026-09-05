package org.example;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class StringFiltering {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            List<String> listString = scanArrayList(scanner);
            String filter = scanFilter(scanner);
            List<String> resultList = stringsComparison(listString, filter);
            printResult(resultList);
        }

    }

    static List<String> scanArrayList(Scanner scanner) {
            List<String> copyListString = new ArrayList<>();
            while(true){
                try {
                    int indexes = scanner.nextInt();
                    scanner.nextLine();
                    for (int i = 0; i < indexes; i++) {
                        copyListString.add(scanner.nextLine());
                    }
                    return copyListString;
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                }
            }
    }

    static String scanFilter(Scanner scanner){
        return scanner.nextLine();
    }

    static List<String> stringsComparison (List<String> listString, String filter){

        List<String> sortlistString = new ArrayList<>();
        for (String item : listString){
            if (item.contains(filter)){
                sortlistString.add(item);
            }
        }
        return sortlistString;
    }

    static void printResult (List<String> resultList){
        if (resultList.isEmpty()) {
            return;
        }
        System.out.print(String.join(", ", resultList));
    }
}
