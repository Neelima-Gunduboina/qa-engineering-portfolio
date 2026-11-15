package com.javabasics.basics;

import java.util.ArrayList;
import java.util.List;

/**
 * Classic beginner / interview programs, written as small testable methods.
 * (Previously Basicjavacode.java, Fibonacci.java and Sumofdigits.java.)
 */
public final class NumberPrograms {

    private NumberPrograms() {
    }

    /** First {@code count} Fibonacci numbers: 0, 1, 1, 2, 3, 5, ... */
    public static List<Long> fibonacci(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative: " + count);
        }
        List<Long> series = new ArrayList<>();
        long a = 0;
        long b = 1;
        for (int i = 0; i < count; i++) {
            series.add(a);
            long next = a + b;
            a = b;
            b = next;
        }
        return series;
    }

    /** Sum of the decimal digits, e.g. 343 -> 10. Negative numbers use their absolute value. */
    public static int sumOfDigits(int number) {
        int n = Math.abs(number);
        int sum = 0;
        while (n > 0) {
            sum += n % 10;
            n /= 10;
        }
        return sum;
    }

    /** Reverses the digits, e.g. 1234 -> 4321. */
    public static int reverse(int number) {
        int n = Math.abs(number);
        int reversed = 0;
        while (n > 0) {
            reversed = reversed * 10 + n % 10;
            n /= 10;
        }
        return number < 0 ? -reversed : reversed;
    }

    /** True if the number reads the same backwards, e.g. 121. */
    public static boolean isPalindrome(int number) {
        return number >= 0 && reverse(number) == number;
    }

    /** True if the number is prime. */
    public static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        for (int i = 2; (long) i * i <= number; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /** n! for 0 <= n <= 20 (the largest that fits in a long). */
    public static long factorial(int n) {
        if (n < 0 || n > 20) {
            throw new IllegalArgumentException("n must be between 0 and 20: " + n);
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("Hello GitHub");
        System.out.println("Fibonacci(10)   = " + fibonacci(10));
        System.out.println("Sum of digits   = " + sumOfDigits(343));
        System.out.println("Reverse(1234)   = " + reverse(1234));
        System.out.println("121 palindrome? = " + isPalindrome(121));
        System.out.println("17 prime?       = " + isPrime(17));
        System.out.println("5!              = " + factorial(5));
    }
}
