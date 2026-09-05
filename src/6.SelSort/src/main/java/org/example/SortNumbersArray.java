package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class SortNumbersArray {
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)){
            float[] arrayNumbers = scanArrayNumbers (scanner, scanSizeArray(scanner));
            float[] arraySortingNumbers = sortingNumbers(arrayNumbers);
            printResult(arraySortingNumbers);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }

    static void printResult(float[] arrayResult){
        if (arrayResult.length == 0) {
            System.out.println("There are no such elements");
            return;
        }

        int i = 0;
        while (i < arrayResult.length){
            if(i == 0){
                System.out.print(arrayResult[i]);

            } else {
                System.out.print(" " + arrayResult[i]);
            }
            i++;
        }
        System.out.println();
    }

    static float[] sortingNumbers (float[] arrayNumbers){
        for (int i = 0; i < arrayNumbers.length; i++){
            int minIndex = i;
            for (int j = i + 1; j < arrayNumbers.length; j++){
                if (arrayNumbers[j] < arrayNumbers[minIndex]){
                    minIndex = j;
                }
            }
            if (minIndex != i){
                float temp = arrayNumbers[i];
                arrayNumbers[i] = arrayNumbers[minIndex];
                arrayNumbers[minIndex] = temp;
            }
        }
        return arrayNumbers;
    }

    static float[] scanArrayNumbers (Scanner scan, int sizeArray) {
        float[] arrayNumbers = new float[sizeArray];
        for (int i = 0; i < sizeArray; i++){
            while(true){
                try {
                    arrayNumbers[i] = scan.nextFloat();
                    break;
                } catch (InputMismatchException e) {
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

}