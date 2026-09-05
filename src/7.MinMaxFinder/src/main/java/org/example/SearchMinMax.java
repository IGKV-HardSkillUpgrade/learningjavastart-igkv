package org.example;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
//src\main\resources\file1.txt
public class SearchMinMax {
    public static void main(String[] args) {
        try (Scanner scan = new Scanner(System.in)) {
            Path pathScan = scanPathFile(scan);
            float[] numbers = scanFileInArr(pathScan);
            writeConsoleResultArr(numbers);
            float[] resultArr = findMinMax(numbers);
            writeResult(resultArr, pathScan);
        } catch (IOException | InputMismatchException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void writeConsoleResultArr(float[] numbers) {
        System.out.println(numbers.length);
        for (int i = 0; i < numbers.length; i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(numbers[i]);
        }
        System.out.println();
    }

    public static void writeResult(float[] resultArr, Path origPath) throws IOException {
        Path outPath = origPath.getParent().resolve("result.txt");
        try (PrintWriter write = new PrintWriter(outPath.toFile())) {
            write.print(resultArr[0] + " ");
            write.print(resultArr[1]);
        }
        System.out.println("Saving min and max values in file");
    }

    public static Path scanPathFile(Scanner scan) throws IOException {
        Path pathScan = Paths.get(scan.nextLine());
        if (Files.notExists(pathScan)) {
            throw new IOException("Input error. File doesn't exist");
        }
        return pathScan;
    }

    public static float[] scanFileInArr(Path pathScan) throws IOException {
        try (Scanner reader = new Scanner(pathScan)) {
            reader.useLocale(Locale.US);
            reader.useDelimiter("\\s+");

            if (!reader.hasNextInt()) {
                throw new InputMismatchException("Input error. Insufficient number of elements");
            }

            int size = reader.nextInt();

            if (size <= 0) {
                throw new IllegalArgumentException("Input error. Size <= 0");
            }

            float[] numbers = new float[size];
            int position = 0;

            while (position < size && reader.hasNext()) {
                if (reader.hasNextFloat()) {
                    numbers[position] = reader.nextFloat();
                    position++;
                } else {
                    reader.next();
                }
            }

            if (position < size) {
                throw new IllegalArgumentException("Input error. Insufficient number of elements");
            }
            return numbers;
        }
    }
    public static float[] findMinMax(float[] arr) {
        float min = arr[0];
        float max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) min = arr[i];
            if (arr[i] > max) max = arr[i];
        }
        return new float[]{min, max};
    }
}