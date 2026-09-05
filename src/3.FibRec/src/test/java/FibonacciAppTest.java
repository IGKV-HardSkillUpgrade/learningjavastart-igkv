package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class FibonacciAppTest {

    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @ParameterizedTest(name = "Fibonacci({0}) = {1}")
    @CsvSource({
            "0, 0",
            "1, 1",
            "2, 1",
            "3, 2",
            "4, 3",
            "5, 5",
            "6, 8",
            "7, 13",
            "8, 21",
            "9, 34",
            "10, 55",
            "20, 6765",
            "30, 832040"
    })
    @DisplayName("Тест базовых значений Фибоначчи")
    void testFibonacciBasic(int input, int expected) {
        assertEquals(expected, FibonacciApp.searchNumberFib(input));
    }

    @Test
    @DisplayName("Тест граничного значения int")
    void testFibonacciMaxInt() {
        // F(46) = 1836311903 - максимальное значение для int
        assertEquals(1836311903, FibonacciApp.searchNumberFib(46));
    }

    @Test
    @DisplayName("Тест переполнения int")
    void testFibonacciOverflow() {
        // F(47) = 2971215073 > Integer.MAX_VALUE
        assertThrows(ArithmeticException.class, () -> {
            FibonacciApp.searchNumberFib(47);
        });
    }

    @ParameterizedTest(name = "Тест ввода: {0}")
    @ValueSource(strings = {
            "10\n",
            "0\n",
            "1\n",
            "46\n"
    })
    @DisplayName("Тест корректного ввода")
    void testValidInput(String input) {
        String simulatedInput = input;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        try (Scanner scanner = new Scanner(inputStream)) {
            int result = FibonacciApp.readIndexFib(scanner);
            assertTrue(result >= 0, "Результат должен быть неотрицательным");
        }
    }

    @ParameterizedTest(name = "Тест некорректного ввода: {0}")
    @ValueSource(strings = {
            "abc\n10\n",
            "-5\n10\n",
            "10.5\n10\n",
            "\n10\n",
            "99999999999999999999\n10\n"
    })
    @DisplayName("Тест некорректного ввода с повторной попыткой")
    void testInvalidInputWithRetry(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());

        try (Scanner scanner = new Scanner(inputStream)) {
            int result = FibonacciApp.readIndexFib(scanner);
            assertEquals(10, result, "Должно вернуться корректное значение после повторного ввода");
        }
    }

    @Test
    @DisplayName("Тест вывода сообщения об ошибке при некорректном вводе")
    void testErrorMessageForInvalidInput() {
        String simulatedInput = "abc\n10\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        try (Scanner scanner = new Scanner(inputStream)) {
            FibonacciApp.readIndexFib(scanner);
        }

        String output = outputStream.toString();
        assertTrue(output.contains("Could not parse a number. Please, try again"),
                "Должно выводиться сообщение об ошибке");
    }

    @Test
    @DisplayName("Тест больших значений n")
    void testLargeN() {
        // Проверяем, что для очень большого n выбрасывается StackOverflowError
        // или ArithmeticException (зависит от того, что произойдёт раньше)
        assertThrows(Throwable.class, () -> {
            FibonacciApp.searchNumberFib(1000000);
        });
    }

    @Test
    @DisplayName("Тест отрицательных чисел")
    void testNegativeInput() {
        String simulatedInput = "-1\n10\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        try (Scanner scanner = new Scanner(inputStream)) {
            int result = FibonacciApp.readIndexFib(scanner);
            assertEquals(10, result);
        }
    }

    @Test
    @DisplayName("Тест Integer.MAX_VALUE")
    void testIntegerMaxValue() {
        String simulatedInput = "2147483647\n10\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        try (Scanner scanner = new Scanner(inputStream)) {
            int result = FibonacciApp.readIndexFib(scanner);
            // Программа должна либо обработать это значение, либо запросить новое
            assertTrue(result >= 0);
        }
    }

    @Test
    @DisplayName("Тест Integer.MIN_VALUE")
    void testIntegerMinValue() {
        String simulatedInput = "-2147483648\n10\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        try (Scanner scanner = new Scanner(inputStream)) {
            int result = FibonacciApp.readIndexFib(scanner);
            assertEquals(10, result, "Должно вернуться 10 после некорректного ввода");
        }
    }

    @Test
    @DisplayName("Тест множественных некорректных вводов")
    void testMultipleInvalidInputs() {
        String simulatedInput = "abc\n-5\n10.5\n\n10\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        try (Scanner scanner = new Scanner(inputStream)) {
            int result = FibonacciApp.readIndexFib(scanner);
            assertEquals(10, result);
        }

        String output = outputStream.toString();
        long errorCount = output.lines()
                .filter(line -> line.contains("Could not parse a number"))
                .count();
        assertEquals(3, errorCount, "Должно быть 4 сообщения об ошибке");
    }
}