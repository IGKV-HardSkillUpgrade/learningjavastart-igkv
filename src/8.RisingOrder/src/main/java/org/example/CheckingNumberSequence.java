package org.example;

import java.util.Scanner;

public class CheckingNumberSequence {
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)) {
            if (!scanner.hasNextInt()){
                System.out.println("Input error");
                return;
            }

            int previous = scanner.nextInt();
            int position = 1;

            while (scanner.hasNextInt()){
                int current = scanner.nextInt();

                if (previous >= current){
                    System.out.println("The sequence is not ordered from the ordinal number of the number " + position);
                    return;
                }
                previous = current;
                position ++;
            }

            System.out.println("The sequence is ordered in ascending order");
        }
    }

}
