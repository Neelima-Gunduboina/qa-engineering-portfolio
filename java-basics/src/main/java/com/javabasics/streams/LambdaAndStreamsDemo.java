package com.javabasics.streams;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Covers: lambda expressions, method references, built-in functional interfaces,
 *         Stream API (filter, map, reduce, collect, sorted, distinct, flatMap, groupBy).
 */
public class LambdaAndStreamsDemo {

    public static void main(String[] args) {
        lambdaBasics();
        builtInFunctionalInterfaces();
        methodReferences();
        streamOperations();
        streamCollectors();
        optionalDemo();
    }

    // ── Lambda Basics ─────────────────────────────────────────────────────────
    static void lambdaBasics() {
        System.out.println("=== Lambda Basics ===");

        // Old way: anonymous class
        Runnable oldWay = new Runnable() {
            @Override public void run() { System.out.println("Old: anonymous class"); }
        };

        // Lambda
        Runnable lambda = () -> System.out.println("Lambda: concise!");

        oldWay.run();
        lambda.run();

        // Lambda with parameters
        Comparator<String> byLength  = (a, b) -> a.length() - b.length();
        Comparator<String> byAlpha   = (a, b) -> a.compareTo(b);

        List<String> words = new ArrayList<>(Arrays.asList("banana", "kiwi", "apple", "fig"));
        words.sort(byLength);
        System.out.println("By length : " + words);
        words.sort(byAlpha);
        System.out.println("By alpha  : " + words);
    }

    // ── Built-in Functional Interfaces ───────────────────────────────────────
    static void builtInFunctionalInterfaces() {
        System.out.println("\n=== Built-in Functional Interfaces ===");

        // Predicate<T> — test(T) → boolean
        Predicate<Integer> isEven   = n -> n % 2 == 0;
        Predicate<String>  longWord = s -> s.length() > 5;
        System.out.println("isEven(4)      : " + isEven.test(4));
        System.out.println("isEven(7)      : " + isEven.test(7));
        System.out.println("longWord(\"Java\")  : " + longWord.test("Java"));
        System.out.println("Composed (even AND >3): " + isEven.and(n -> n > 3).test(8));

        // Function<T,R> — apply(T) → R
        Function<String, Integer> strLength  = String::length;
        Function<Integer, String> intToStr   = i -> "Number: " + i;
        Function<String, String>  composed   = strLength.andThen(intToStr);
        System.out.println("strLength(\"hello\") : " + strLength.apply("hello"));
        System.out.println("composed(\"hello\")  : " + composed.apply("hello"));

        // Consumer<T> — accept(T) → void
        Consumer<String> printer  = System.out::println;
        Consumer<String> upper    = s -> System.out.println(s.toUpperCase());
        Consumer<String> both     = printer.andThen(upper);
        both.accept("neelima");

        // Supplier<T> — get() → T
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        newList.add("item");
        System.out.println("Supplier made: " + newList);

        // BiFunction<T,U,R>
        BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);
        System.out.println("repeat(\"Ha\",3) = " + repeat.apply("Ha", 3));
    }

    // ── Method References ─────────────────────────────────────────────────────
    static void methodReferences() {
        System.out.println("\n=== Method References ===");

        List<String> names = Arrays.asList("Neelima", "Alice", "Bob", "Charlie");

        // Static method reference
        names.stream().map(String::toLowerCase).forEach(System.out::println);

        // Instance method reference on a specific instance
        String prefix = "Hello, ";
        Function<String, String> greet = prefix::concat;
        System.out.println(greet.apply("World"));

        // Constructor reference
        Function<String, StringBuilder> sbMaker = StringBuilder::new;
        System.out.println("SB: " + sbMaker.apply("Built!"));
    }

    // ── Stream Operations ─────────────────────────────────────────────────────
    static void streamOperations() {
        System.out.println("\n=== Stream Operations ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // filter — keep elements matching predicate
        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("Evens      : " + evens);

        // map — transform each element
        List<Integer> squares = numbers.stream()
                .map(n -> n * n)
                .collect(Collectors.toList());
        System.out.println("Squares    : " + squares);

        // filter + map + collect
        List<String> result = numbers.stream()
                .filter(n -> n > 5)
                .map(n -> "Num-" + n)
                .collect(Collectors.toList());
        System.out.println("Filtered+Mapped: " + result);

        // reduce — aggregate to single value
        int sum = numbers.stream().reduce(0, Integer::sum);
        System.out.println("Sum        : " + sum);

        int product = numbers.stream().reduce(1, (a, b) -> a * b);
        System.out.println("Product    : " + product);

        // sorted, distinct, limit, skip
        List<Integer> mixed = Arrays.asList(5, 3, 8, 1, 9, 2, 3, 5, 7);
        System.out.println("Distinct sorted top-5: " +
                mixed.stream().distinct().sorted().limit(5).collect(Collectors.toList()));

        // flatMap — flatten nested lists
        List<List<Integer>> nested = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5),
                Arrays.asList(6, 7, 8, 9));
        List<Integer> flat = nested.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        System.out.println("flatMap    : " + flat);

        // anyMatch, allMatch, noneMatch
        System.out.println("anyMatch >8 : " + numbers.stream().anyMatch(n -> n > 8));
        System.out.println("allMatch >0 : " + numbers.stream().allMatch(n -> n > 0));
        System.out.println("noneMatch <0: " + numbers.stream().noneMatch(n -> n < 0));

        // min, max, count
        System.out.println("Min: " + numbers.stream().min(Integer::compareTo).orElse(-1));
        System.out.println("Max: " + numbers.stream().max(Integer::compareTo).orElse(-1));
        System.out.println("Count evens: " + numbers.stream().filter(n -> n % 2 == 0).count());
    }

    // ── Stream Collectors ─────────────────────────────────────────────────────
    static void streamCollectors() {
        System.out.println("\n=== Stream Collectors ===");

        List<String> names = Arrays.asList("Alice", "Bob", "Anna", "Charlie", "Brian", "Amy");

        // joining
        String joined = names.stream().collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Joined: " + joined);

        // groupingBy
        Map<Character, List<String>> byFirstLetter = names.stream()
                .collect(Collectors.groupingBy(s -> s.charAt(0)));
        byFirstLetter.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.println("'" + e.getKey() + "' → " + e.getValue()));

        // counting per group
        Map<Character, Long> countByLetter = names.stream()
                .collect(Collectors.groupingBy(s -> s.charAt(0), Collectors.counting()));
        System.out.println("Count by letter: " + new TreeMap<>(countByLetter));

        // partitioningBy
        Map<Boolean, List<String>> partition = names.stream()
                .collect(Collectors.partitioningBy(s -> s.length() > 3));
        System.out.println("Long names  : " + partition.get(true));
        System.out.println("Short names : " + partition.get(false));

        // toMap
        Map<String, Integer> nameLengths = names.stream()
                .collect(Collectors.toMap(s -> s, String::length));
        System.out.println("Name lengths: " + new TreeMap<>(nameLengths));
    }

    // ── Optional ──────────────────────────────────────────────────────────────
    static void optionalDemo() {
        System.out.println("\n=== Optional ===");

        Optional<String> present = Optional.of("Neelima");
        Optional<String> empty   = Optional.empty();

        System.out.println("present.isPresent() : " + present.isPresent());
        System.out.println("empty.isPresent()   : " + empty.isPresent());
        System.out.println("get()               : " + present.get());
        System.out.println("orElse(empty)       : " + empty.orElse("Default"));
        System.out.println("map to upper        : " + present.map(String::toUpperCase).orElse(""));

        // Safe chaining — no NPE even with nulls in the chain
        Optional<String> result = Optional.<String>ofNullable(null)
                .map(String::trim)
                .filter(s -> !s.isEmpty());
        System.out.println("Safe chain on null  : " + result.orElse("fallback"));
    }
}
