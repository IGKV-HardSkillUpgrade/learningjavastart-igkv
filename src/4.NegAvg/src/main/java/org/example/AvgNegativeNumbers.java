package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class AvgNegativeNumbers {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)){
            int[] arrayNumbers = scanArrayNumbers (scanner, scanSizeArray(scanner));
            int avg = findAvgNegativNumb (arrayNumbers);
            System.out.println(avg);
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }

    static int[] scanArrayNumbers (Scanner scan, int sizeArray) {
        int[] arrayNumbers = new int[sizeArray];
        for (int i = 0; i < sizeArray; i++){
            while(true){
                try {
                    arrayNumbers[i] = scan.nextInt();
                    break;
                }catch (InputMismatchException e) {
                    System.out.println("Could not parse a number. Please, try again");
                    scan.nextLine();
                }
            }
        }
        return arrayNumbers;
    }

    static int scanSizeArray (Scanner scan){
        while (true){
            try {
                int size = scan.nextInt();
                if (size <= 0) {
                    throw new IllegalArgumentException("Input error. Size <= 0");
                }
                return size;
            }catch (InputMismatchException e) {
                System.out.println("Could not parse a number. Please, try again");
                scan.nextLine();
            }
        }
    }

    public static int findAvgNegativNumb (int[] arrayNumbers){
        int count = 0;
        int sumNegativeNumb = 0;
        for (int arrayNumber : arrayNumbers) {
            if (arrayNumber < 0) {
                count++;
                sumNegativeNumb += arrayNumber;
            }
        }
        if (count == 0) {
            throw new IllegalArgumentException("There are no negative elements");
        }
        return  sumNegativeNumb / count;
    }
}