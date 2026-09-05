package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SortNumbersArrayTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    void setUp() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        Locale.setDefault(Locale.US);
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Сортировка пустого массива")
    void testSortingEmptyArray() {
        float[] input = {};
        float[] result = SortNumbersArray.sortingNumbers(input);

        assertAll(
                () -> assertEquals(0, result.length),
                () -> assertArrayEquals(new float[]{}, result)
        );
    }

    @Test
    @DisplayName("Сортировка массива из одного элемента")
    void testSortingSingleElementArray() {
        float[] input = {42.5f};
        float[] result = SortNumbersArray.sortingNumbers(input);

        assertAll(
                () -> assertEquals(1, result.length),
                () -> assertEquals(42.5f, result[0], 0.001f)
        );
    }

    @Test
    @DisplayName("Сортировка уже отсортированного массива")
    void testSortingAlreadySortedArray() {
        float[] input = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        float[] expected = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        float[] result = SortNumbersArray.sortingNumbers(input);

        assertArrayEquals(expected, result, 0.001f);
    }

    @Test
    @DisplayName("Сортировка массива в обратном порядке")
    void testSortingReverseOrderArray() {
        float[] input = {5.0f, 4.0f, 3.0f, 2.0f, 1.0f};
        float[] expected = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
        float[] result = SortNumbersArray.sortingNumbers(input);

        assertArrayEquals(expected, result, 0.001f);
    }

    @Test
    @DisplayName("Сортировка массива с дубликатами")
    void testSortingArrayWithDuplicates() {
        float[] input = {3.0f, 1.0f, 3.0f, 2.0f, 1.0f, 2.0f};
        float[] expected = {1.0f, 1.0f, 2.0f, 2.0f, 3.0f, 3.0f};
        float[] result = SortNumbersArray.sortingNumbers(input);

        assertArrayEquals(expected, result, 0.001f);
    }

    @Test
    @DisplayName("Сортировка массива с отрицательными числами")
    void testSortingArrayWithNegativeNumbers() {
        float[] input = {-5.5f, 3.2f, -1.1f, 0.0f, 2.2f, -3.3f};
        float[] expected = {-5.5f, -3.3f, -1.1f, 0.0f, 2.2f, 3.2f};
        float[] result = SortNumbersArray.sortingNumbers(input);

        assertArrayEquals(expected, result, 0.001f);
    }

    @Test
    @DisplayName("Сортировка массива с экстремальными значениями")
    void testSortingArrayWithExtremeValues() {
        float[] input = {
                Float.MAX_VALUE,
                Float.MIN_VALUE,
                -Float.MAX_VALUE,
                0.0f,
                Float.NEGATIVE_INFINITY,
                Float.POSITIVE_INFINITY
        };
        float[] result = SortNumbersArray.sortingNumbers(input);

        assertAll(
                () -> assertEquals(Float.NEGATIVE_INFINITY, result[0]),
                () -> assertEquals(-Float.MAX_VALUE, result[1]),
                () -> assertEquals(0.0f, result[2]),
                () -> assertEquals(Float.MIN_VALUE, result[3]),
                () -> assertEquals(Float.MAX_VALUE, result[4]),
                () -> assertEquals(Float.POSITIVE_INFINITY, result[5])
        );
    }

    @Test
    @DisplayName("Сортировка массива с NaN")
    void testSortingArrayWithNaN() {
        float[] input = {3.0f, Float.NaN, 1.0f, 2.0f};
        float[] result = SortNumbersArray.sortingNumbers(input);

        // NaN не сравнивается корректно, просто проверяем что нет исключений
        assertEquals(4, result.length);
    }

    @Test
    @DisplayName("Сортировка большого массива")
    void testSortingLargeArray() {
        int size = 1000;
        float[] input = new float[size];
        for (int i = 0; i < size; i++) {
            input[i] = size - i;
        }

        float[] result = SortNumbersArray.sortingNumbers(input);

        assertAll(
                () -> assertEquals(size, result.length),
                () -> assertEquals(1.0f, result[0], 0.001f),
                () -> assertEquals(500.0f, result[499], 0.001f),
                () -> assertEquals(1000.0f, result[999], 0.001f)
        );
    }

    @Test
    @DisplayName("Сортировка не изменяет размер массива")
    void testSortingPreservesArraySize() {
        float[] input = {3.0f, 1.0f, 2.0f};
        float[] result = SortNumbersArray.sortingNumbers(input);

        assertEquals(input.length, result.length);
    }

    @Test
    @DisplayName("Сортировка работает с нулевыми значениями")
    void testSortingWithZeroValues() {
        float[] input = {0.0f, -0.0f, 0.0f, -0.0f};
        float[] result = SortNumbersArray.sortingNumbers(input);

        assertEquals(4, result.length);
        assertEquals(0.0f, result[0], 0.001f);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 'Input error. Size <= 0'",
            "-1, 'Input error. Size <= 0'",
            "-100, 'Input error. Size <= 0'"
    })
    @DisplayName("Тест обработки некорректного размера")
    void testInvalidSizeHandling(String input, String expectedOutput) {
        provideInput(input);
        SortNumbersArray.main(new String[]{});
        assertEquals(expectedOutput + System.lineSeparator(), testOut.toString());
    }

    @ParameterizedTest
    @CsvSource({
            "4 100.0 50.0 60.0 10.0, '10.0 50.0 60.0 100.0'",
            "3 1.5 2.5 0.5, '0.5 1.5 2.5'",
            "1 42.0, '42.0'",
            "5 -1.0 -2.0 -3.0 -4.0 -5.0, '-5.0 -4.0 -3.0 -2.0 -1.0'"
    })
    @DisplayName("Тест полного цикла ввода-вывода")
    void testFullInputOutputCycle(String input, String expectedOutput) {
        provideInput(input);
        SortNumbersArray.main(new String[]{});
        assertEquals(expectedOutput + System.lineSeparator(), testOut.toString());
    }



    @Test
    @DisplayName("Тест scanSizeArray с корректным вводом")
    void testScanSizeArrayValidInput() {
        String input = "5";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        int result = SortNumbersArray.scanSizeArray(scanner);

        assertEquals(5, result);
    }

    @Test
    @DisplayName("Тест scanSizeArray с отрицательным числом")
    void testScanSizeArrayNegativeNumber() {
        String input = "-1";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> SortNumbersArray.scanSizeArray(scanner)
        );

        assertEquals("Input error. Size <= 0", exception.getMessage());
    }

    @Test
    @DisplayName("Тест scanSizeArray с нулем")
    void testScanSizeArrayZero() {
        String input = "0";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> SortNumbersArray.scanSizeArray(scanner)
        );

        assertEquals("Input error. Size <= 0", exception.getMessage());
    }

    @Test
    @DisplayName("Тест scanSizeArray с некорректным вводом")
    void testScanSizeArrayInvalidInput() {
        String input = "abc\n5";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        int result = SortNumbersArray.scanSizeArray(scanner);

        assertEquals(5, result);
        assertTrue(testOut.toString().contains("Could not parse a number. Please, try again"));
    }

    @Test
    @DisplayName("Тест scanArrayNumbers с корректным вводом")
    void testScanArrayNumbersValidInput() {
        String input = "1.0 2.0 3.0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        float[] result = SortNumbersArray.scanArrayNumbers(scanner, 3);

        assertArrayEquals(new float[]{1.0f, 2.0f, 3.0f}, result, 0.001f);
    }

    @Test
    @DisplayName("Тест scanArrayNumbers с некорректным вводом")
    void testScanArrayNumbersInvalidInput() {
        String input = "abc\n2.0\ndef\n3.0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        float[] result = SortNumbersArray.scanArrayNumbers(scanner, 2);

        assertArrayEquals(new float[]{2.0f, 3.0f}, result, 0.001f);
        assertTrue(testOut.toString().contains("Could not parse a number. Please, try again"));
    }

    @Test
    @DisplayName("Тест printResult с пустым массивом")
    void testPrintResultEmptyArray() {
        SortNumbersArray.printResult(new float[]{});
        assertEquals("There are no such elements", testOut.toString().trim());
    }

    @Test
    @DisplayName("Тест printResult с массивом")
    void testPrintResultArray() {
        SortNumbersArray.printResult(new float[]{1.0f, 2.0f, 3.0f});
        assertEquals("1.0 2.0 3.0", testOut.toString().trim());
    }

    @Test
    @DisplayName("Тест printResult с массивом из одного элемента")
    void testPrintResultSingleElement() {
        SortNumbersArray.printResult(new float[]{42.0f});
        assertEquals("42.0", testOut.toString().trim());
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }
}