package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SearchMinMaxTest {

    @TempDir
    Path tempDir;

    private Path testFile;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
    }

    // ===== Тесты для findMinMax =====

    @Test
    @DisplayName("findMinMax: обычный массив")
    void testFindMinMax_NormalArray() {
        float[] input = {100.0f, 50.0f, 60.0f, 10.0f};
        float[] result = SearchMinMax.findMinMax(input);

        assertAll("Проверка min и max",
                () -> assertEquals(10.0f, result[0], 0.001f, "Минимум должен быть 10.0"),
                () -> assertEquals(100.0f, result[1], 0.001f, "Максимум должен быть 100.0")
        );
    }

    @Test
    @DisplayName("findMinMax: массив с одним элементом")
    void testFindMinMax_SingleElement() {
        float[] input = {42.5f};
        float[] result = SearchMinMax.findMinMax(input);

        assertAll("Проверка min и max для одного элемента",
                () -> assertEquals(42.5f, result[0], 0.001f, "Минимум должен быть 42.5"),
                () -> assertEquals(42.5f, result[1], 0.001f, "Максимум должен быть 42.5")
        );
    }

    @Test
    @DisplayName("findMinMax: массив с отрицательными числами")
    void testFindMinMax_NegativeNumbers() {
        float[] input = {-100.0f, -50.0f, -60.0f, -10.0f};
        float[] result = SearchMinMax.findMinMax(input);

        assertAll("Проверка min и max с отрицательными числами",
                () -> assertEquals(-100.0f, result[0], 0.001f, "Минимум должен быть -100.0"),
                () -> assertEquals(-10.0f, result[1], 0.001f, "Максимум должен быть -10.0")
        );
    }

    @Test
    @DisplayName("findMinMax: смешанные положительные и отрицательные")
    void testFindMinMax_MixedNumbers() {
        float[] input = {-5.5f, 0.0f, 5.5f, -10.5f, 10.5f};
        float[] result = SearchMinMax.findMinMax(input);

        assertAll("Проверка min и max со смешанными числами",
                () -> assertEquals(-10.5f, result[0], 0.001f, "Минимум должен быть -10.5"),
                () -> assertEquals(10.5f, result[1], 0.001f, "Максимум должен быть 10.5")
        );
    }

    @Test
    @DisplayName("findMinMax: все одинаковые числа")
    void testFindMinMax_AllSame() {
        float[] input = {25.0f, 25.0f, 25.0f, 25.0f};
        float[] result = SearchMinMax.findMinMax(input);

        assertAll("Проверка min и max для одинаковых чисел",
                () -> assertEquals(25.0f, result[0], 0.001f, "Минимум должен быть 25.0"),
                () -> assertEquals(25.0f, result[1], 0.001f, "Максимум должен быть 25.0")
        );
    }

    @ParameterizedTest
    @CsvSource({
            "0.0, 1.0, 2.0, 0.0, 2.0",
            "-1.5, -2.5, -3.5, -3.5, -1.5",
            "100.0, 100.0, 100.0, 100.0, 100.0"
    })
    @DisplayName("findMinMax: параметризованные тесты")
    void testFindMinMax_Parameterized(float a, float b, float c, float expectedMin, float expectedMax) {
        float[] input = {a, b, c};
        float[] result = SearchMinMax.findMinMax(input);

        assertAll("Проверка min и max",
                () -> assertEquals(expectedMin, result[0], 0.001f),
                () -> assertEquals(expectedMax, result[1], 0.001f)
        );
    }

    // ===== Тесты для scanFileInArr =====

    @Test
    @DisplayName("scanFileInArr: корректный файл с 4 числами")
    void testScanFileInArr_CorrectFile() throws IOException {
        Path file = tempDir.resolve("test1.txt");
        Files.writeString(file, "4  100.0 50.0 60.0 10.0");

        float[] result = SearchMinMax.scanFileInArr(file);

        assertAll("Проверка массива",
                () -> assertEquals(4, result.length, "Должно быть 4 элемента"),
                () -> assertEquals(100.0f, result[0], 0.001f),
                () -> assertEquals(50.0f, result[1], 0.001f),
                () -> assertEquals(60.0f, result[2], 0.001f),
                () -> assertEquals(10.0f, result[3], 0.001f)
        );
    }

    @Test
    @DisplayName("scanFileInArr: файл с некорректными данными (буквы пропускаются)")
    void testScanFileInArr_InvalidDataSkipped() throws IOException {
        Path file = tempDir.resolve("test2.txt");
        Files.writeString(file, "5  20.0 50.0 f 60.0 g 10.0 1.0");

        float[] result = SearchMinMax.scanFileInArr(file);

        assertAll("Проверка пропуска некорректных данных",
                () -> assertEquals(5, result.length, "Должно быть 5 элементов"),
                () -> assertEquals(20.0f, result[0], 0.001f),
                () -> assertEquals(50.0f, result[1], 0.001f),
                () -> assertEquals(60.0f, result[2], 0.001f),
                () -> assertEquals(10.0f, result[3], 0.001f),
                () -> assertEquals(1.0f, result[4], 0.001f)
        );
    }

    @Test
    @DisplayName("scanFileInArr: размер 0")
    void testScanFileInArr_SizeZero() throws IOException {
        Path file = tempDir.resolve("test3.txt");
        Files.writeString(file, "0  100.0 50.0 60.0 10.0");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> SearchMinMax.scanFileInArr(file)
        );

        assertEquals("Input error. Size <= 0", exception.getMessage());
    }

    @Test
    @DisplayName("scanFileInArr: отрицательный размер")
    void testScanFileInArr_NegativeSize() throws IOException {
        Path file = tempDir.resolve("test4.txt");
        Files.writeString(file, "-5  100.0 50.0 60.0 10.0");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> SearchMinMax.scanFileInArr(file)
        );

        assertEquals("Input error. Size <= 0", exception.getMessage());
    }

    @Test
    @DisplayName("scanFileInArr: недостаточное количество чисел")
    void testScanFileInArr_InsufficientElements() throws IOException {
        Path file = tempDir.resolve("test5.txt");
        Files.writeString(file, "10  100.0 50.0 60.0 10.0");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> SearchMinMax.scanFileInArr(file)
        );

        assertEquals("Input error. Insufficient number of elements", exception.getMessage());
    }

    @Test
    @DisplayName("scanFileInArr: пустой файл")
    void testScanFileInArr_EmptyFile() throws IOException {
        Path file = tempDir.resolve("test6.txt");
        Files.writeString(file, "");

        assertThrows(
                InputMismatchException.class,
                () -> SearchMinMax.scanFileInArr(file)
        );
    }

    @Test
    @DisplayName("scanFileInArr: только размер без чисел")
    void testScanFileInArr_OnlySize() throws IOException {
        Path file = tempDir.resolve("test7.txt");
        Files.writeString(file, "3");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> SearchMinMax.scanFileInArr(file)
        );

        assertEquals("Input error. Insufficient number of elements", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "1  42.5, 1",
            "2  -100.0 200.0, 2",
            "3  10.0 20.0 30.0, 3"
    })
    @DisplayName("scanFileInArr: параметризованные тесты с разным количеством")
    void testScanFileInArr_Parameterized(String content, int expectedLength) throws IOException {
        Path file = tempDir.resolve("param_test.txt");
        Files.writeString(file, content);

        float[] result = SearchMinMax.scanFileInArr(file);

        assertEquals(expectedLength, result.length);
    }

    // ===== Тесты для scanPathFile =====

    @Test
    @DisplayName("scanPathFile: существующий файл")
    void testScanPathFile_ExistingFile() throws IOException {
        Path file = tempDir.resolve("existing.txt");
        Files.writeString(file, "test");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(file.toString().getBytes());
        Scanner scanner = new Scanner(inputStream);

        Path result = SearchMinMax.scanPathFile(scanner);

        assertEquals(file, result);
        scanner.close();
    }

    @Test
    @DisplayName("scanPathFile: несуществующий файл")
    void testScanPathFile_NonExistingFile() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("nonexistent.txt".getBytes());
        Scanner scanner = new Scanner(inputStream);

        IOException exception = assertThrows(
                IOException.class,
                () -> SearchMinMax.scanPathFile(scanner)
        );

        assertEquals("Input error. File doesn't exist", exception.getMessage());
        scanner.close();
    }

    // ===== Тесты для writeResult =====

    @Test
    @DisplayName("writeResult: корректная запись в файл")
    void testWriteResult_CorrectWrite() throws IOException {
        float[] result = {10.0f, 100.0f};
        Path sourceFile = tempDir.resolve("source.txt");
        Files.writeString(sourceFile, "test");

        SearchMinMax.writeResult(result, sourceFile);

        Path resultFile = tempDir.resolve("result.txt");
        assertTrue(Files.exists(resultFile), "result.txt должен существовать");

        String content = Files.readString(resultFile);
        assertEquals("10.0 100.0", content, "Содержимое файла должно быть '10.0 100.0'");
    }

    @Test
    @DisplayName("writeResult: проверка вывода в консоль")
    void testWriteResult_ConsoleOutput() throws IOException {
        float[] result = {5.5f, 6.6f};
        Path sourceFile = tempDir.resolve("source2.txt");
        Files.writeString(sourceFile, "test");

        SearchMinMax.writeResult(result, sourceFile);

        assertTrue(outputStreamCaptor.toString().contains("Saving min and max values in file"));
    }

    // ===== Тесты для writeConsoleResultArr =====

    @Test
    @DisplayName("writeConsoleResultArr: корректный вывод")
    void testWriteConsoleResultArr_CorrectOutput() {
        float[] numbers = {100.0f, 50.0f, 60.0f, 10.0f};

        SearchMinMax.writeConsoleResultArr(numbers);

        String output = outputStreamCaptor.toString();
        assertAll("Проверка вывода",
                () -> assertTrue(output.contains("4"), "Должно быть выведено количество элементов"),
                () -> assertTrue(output.contains("100.0 50.0 60.0 10.0"), "Должны быть выведены числа")
        );
    }

    @Test
    @DisplayName("writeConsoleResultArr: пустой массив")
    void testWriteConsoleResultArr_EmptyArray() {
        float[] numbers = {};

        SearchMinMax.writeConsoleResultArr(numbers);

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("0"), "Должно быть выведено 0");
    }

    @Test
    @DisplayName("writeConsoleResultArr: массив с одним элементом")
    void testWriteConsoleResultArr_SingleElement() {
        float[] numbers = {42.5f};

        SearchMinMax.writeConsoleResultArr(numbers);

        String output = outputStreamCaptor.toString();
        assertAll("Проверка вывода одного элемента",
                () -> assertTrue(output.contains("1"), "Должно быть выведено 1"),
                () -> assertTrue(output.contains("42.5"), "Должно быть выведено число 42.5")
        );
    }

    @ParameterizedTest
    @CsvSource({
            "1.5 2.5 3.5, 3, 1.5 2.5 3.5",
            "-1.0 -2.0, 2, -1.0 -2.0",
            "100.0, 1, 100.0"
    })
    @DisplayName("writeConsoleResultArr: параметризованные тесты")
    void testWriteConsoleResultArr_Parameterized(String numbersStr, int expectedCount, String expectedNumbers) {
        String[] parts = numbersStr.split(" ");
        float[] numbers = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            numbers[i] = Float.parseFloat(parts[i]);
        }

        SearchMinMax.writeConsoleResultArr(numbers);

        String output = outputStreamCaptor.toString();
        assertAll("Проверка вывода",
                () -> assertTrue(output.contains(String.valueOf(expectedCount))),
                () -> assertTrue(output.contains(expectedNumbers))
        );
    }

    // ===== Интеграционные тесты =====

    @Test
    @DisplayName("Интеграционный тест: полный цикл с корректным файлом")
    void testIntegration_CorrectFile() throws IOException {
        Path file = tempDir.resolve("integration1.txt");
        Files.writeString(file, "4  100.0 50.0 60.0 10.0");

        float[] numbers = SearchMinMax.scanFileInArr(file);
        float[] result = SearchMinMax.findMinMax(numbers);
        SearchMinMax.writeResult(result, file);

        assertAll("Интеграционная проверка",
                () -> assertEquals(4, numbers.length),
                () -> assertEquals(10.0f, result[0], 0.001f),
                () -> assertEquals(100.0f, result[1], 0.001f),
                () -> assertTrue(Files.exists(tempDir.resolve("result.txt")))
        );
    }

    @Test
    @DisplayName("Интеграционный тест: полный цикл с некорректными данными")
    void testIntegration_InvalidData() throws IOException {
        Path file = tempDir.resolve("integration2.txt");
        Files.writeString(file, "5  20.0 50.0 f 60.0 g 10.0 1.0");

        float[] numbers = SearchMinMax.scanFileInArr(file);
        float[] result = SearchMinMax.findMinMax(numbers);
        SearchMinMax.writeResult(result, file);

        assertAll("Интеграционная проверка с некорректными данными",
                () -> assertEquals(5, numbers.length),
                () -> assertEquals(1.0f, result[0], 0.001f),
                () -> assertEquals(60.0f, result[1], 0.001f),
                () -> assertTrue(Files.exists(tempDir.resolve("result.txt")))
        );
    }

    @Test
    @DisplayName("Интеграционный тест: ошибка при некорректном размере")
    void testIntegration_InvalidSize() throws IOException {
        Path file = tempDir.resolve("integration3.txt");
        Files.writeString(file, "0  100.0 50.0 60.0 10.0");

        assertThrows(
                IllegalArgumentException.class,
                () -> SearchMinMax.scanFileInArr(file)
        );
    }
}