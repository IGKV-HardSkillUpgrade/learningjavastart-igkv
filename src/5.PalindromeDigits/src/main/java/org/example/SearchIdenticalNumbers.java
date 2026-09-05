package org.example;

import java.util.InputMismatchException;
import java.util.Scanner;

public class SearchIdenticalNumbers {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)){
            int[] arrayNumbers = scanArrayNumbers (scanner, scanSizeArray(scanner));
            int[] matchingArray = searchMatchingNumbers(arrayNumbers);
            printResult(matchingArray);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }

    static void printResult (int[] matchingArray) {
        if (matchingArray.length == 0) {
            System.out.println("There are no such elements");
            return;
        }

        int i = 0;
        while (i < matchingArray.length){
            if(i == 0){
                System.out.print(matchingArray[i]);

            } else {
                System.out.print(" " + matchingArray[i]);
            }
            i++;
        }
        System.out.println();
    }

    static int[] searchMatchingNumbers (int[] arrayNumbers){
        int i = 0, countIndex = 0;
        while ( i < arrayNumbers.length ){
            if(equallyFirstLast(arrayNumbers[i])){
                countIndex++;
            }
            i++;
        }
        int[] resultArray = new int[countIndex];

        int j = 0, index = 0;
        while ( j < arrayNumbers.length ){
            if(equallyFirstLast(arrayNumbers[j])){
                resultArray[index++] = arrayNumbers[j];
            }
            j++;
        }
        return resultArray;
    }

    static boolean equallyFirstLast(int arrayNumber) {
        if (arrayNumber >= -9 && arrayNumber <= 9) {
            return true;
        }

        int first, last;
        long devisor = 1;
        long temp = Math.abs((long) arrayNumber);

        while (temp > 9) {
            devisor *= 10;
            temp /= 10;
        }

        first = (int) (arrayNumber / devisor);
        last = arrayNumber % 10;

        // Сравниваем модули обеих цифр
        return Math.abs(first) == Math.abs(last);
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


}
