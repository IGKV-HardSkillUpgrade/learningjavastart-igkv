package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckingNumberSequenceTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    private String getOutput() {
        return outContent.toString().trim();
    }

    @Test
    void testEmptyInput() {
        provideInput("");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("Input error", getOutput());
    }

    @Test
    void testOnlyWhitespace() {
        provideInput("   \n\t   ");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("Input error", getOutput());
    }

    @Test
    void testSingleNumber() {
        provideInput("42");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is ordered in ascending order", getOutput());
    }

    @Test
    void testTwoNumbersIncreasing() {
        provideInput("1 2");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is ordered in ascending order", getOutput());
    }

    @Test
    void testTwoNumbersNotIncreasing() {
        provideInput("5 3");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is not ordered from the ordinal number of the number 1", getOutput());
    }

    @Test
    void testMultipleNumbersViolationInMiddle() {
        provideInput("1 2 5 3 7");
        // нарушение на 3-м числе (5 >= 3)
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is not ordered from the ordinal number of the number 3", getOutput());
    }

    @Test
    void testViolationAtLastComparison() {
        provideInput("10 20 30 25");
        // нарушение на 3-м числе (30 >= 25)
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is not ordered from the ordinal number of the number 3", getOutput());
    }

    @Test
    void testEqualNumbers() {
        provideInput("1 2 2 3");
        // нарушение на 2-м числе (2 >= 2)
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is not ordered from the ordinal number of the number 2", getOutput());
    }

    @Test
    void testNegativeNumbersStrictlyIncreasing() {
        provideInput("-5 -3 -1 0");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is ordered in ascending order", getOutput());
    }

    @Test
    void testMixedNumbersIncreasing() {
        provideInput("-100 0 100");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is ordered in ascending order", getOutput());
    }

    @Test
    void testNonIntegerFirstToken() {
        provideInput("abc 1 2 3");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("Input error", getOutput());
    }

    @Test
    void testNonIntegerAfterValidNumbers() {
        // согласно текущей логике, нечисловой токен прерывает чтение,
        // и последовательность из прочитанных чисел считается упорядоченной
        provideInput("1 2 3 abc");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is ordered in ascending order", getOutput());
    }

    @Test
    void testInputWithNewlinesAndSpaces() {
        provideInput("1\n2\t3 4");
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is ordered in ascending order", getOutput());
    }

    @Test
    void testVeryLargeSequence() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 1000; i++) {
            sb.append(i).append(" ");
        }
        provideInput(sb.toString());
        CheckingNumberSequence.main(new String[]{});
        assertEquals("The sequence is ordered in ascending order", getOutput());
    }
}