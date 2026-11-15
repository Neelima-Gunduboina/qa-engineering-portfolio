package com.javabasics.basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Number programs")
class NumberProgramsTest {

    @Test
    @DisplayName("Fibonacci: first 10 numbers")
    void fibonacciFirstTen() {
        assertEquals(List.of(0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L), NumberPrograms.fibonacci(10));
    }

    @Test
    @DisplayName("Fibonacci: boundaries (0, 1, negative)")
    void fibonacciBoundaries() {
        assertEquals(List.of(), NumberPrograms.fibonacci(0));
        assertEquals(List.of(0L), NumberPrograms.fibonacci(1));
        assertThrows(IllegalArgumentException.class, () -> NumberPrograms.fibonacci(-1));
    }

    @ParameterizedTest(name = "sumOfDigits({0}) = {1}")
    @CsvSource({"343, 10", "0, 0", "9, 9", "1001, 2", "-343, 10", "2147483647, 46"})
    void sumOfDigits(int input, int expected) {
        assertEquals(expected, NumberPrograms.sumOfDigits(input));
    }

    @ParameterizedTest(name = "reverse({0}) = {1}")
    @CsvSource({"1234, 4321", "100, 1", "7, 7", "-123, -321", "0, 0"})
    void reverse(int input, int expected) {
        assertEquals(expected, NumberPrograms.reverse(input));
    }

    @ParameterizedTest(name = "{0} is a palindrome")
    @ValueSource(ints = {0, 7, 121, 1221, 12321})
    void palindromes(int n) {
        assertTrue(NumberPrograms.isPalindrome(n));
    }

    @ParameterizedTest(name = "{0} is not a palindrome")
    @ValueSource(ints = {10, 123, -121})
    void notPalindromes(int n) {
        assertFalse(NumberPrograms.isPalindrome(n));
    }

    @ParameterizedTest(name = "{0} is prime")
    @ValueSource(ints = {2, 3, 17, 97, 7919})
    void primes(int n) {
        assertTrue(NumberPrograms.isPrime(n));
    }

    @ParameterizedTest(name = "{0} is not prime")
    @ValueSource(ints = {-7, 0, 1, 4, 100, 7917})
    void notPrimes(int n) {
        assertFalse(NumberPrograms.isPrime(n));
    }

    @Test
    @DisplayName("Factorial: values and invalid input")
    void factorial() {
        assertEquals(1, NumberPrograms.factorial(0));
        assertEquals(120, NumberPrograms.factorial(5));
        assertEquals(2_432_902_008_176_640_000L, NumberPrograms.factorial(20));
        assertThrows(IllegalArgumentException.class, () -> NumberPrograms.factorial(-1));
        assertThrows(IllegalArgumentException.class, () -> NumberPrograms.factorial(21));
    }
}
