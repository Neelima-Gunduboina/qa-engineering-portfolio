package com.javabasics.collections;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Covers: ArrayList, LinkedList, HashSet, TreeSet, HashMap, TreeMap,
 *         LinkedHashMap, ArrayDeque (Queue/Stack), Collections utility class.
 */
public class CollectionsDemo {

    public static void main(String[] args) {
        arrayListDemo();
        linkedListDemo();
        setDemo();
        mapDemo();
        queueAndStackDemo();
        collectionsUtilityDemo();
    }

    // ── ArrayList ─────────────────────────────────────────────────────────────
    static void arrayListDemo() {
        System.out.println("=== ArrayList ===");
        List<String> names = new ArrayList<>(Arrays.asList("Neelima", "Alice", "Bob"));

        names.add("Charlie");           // add at end
        names.add(1, "Zara");           // add at index
        names.remove("Bob");            // remove by value
        names.remove(0);                // remove by index

        System.out.println("List  : " + names);
        System.out.println("Size  : " + names.size());
        System.out.println("Get(0): " + names.get(0));
        System.out.println("Contains Alice: " + names.contains("Alice"));

        // Iterate
        System.out.print("Loop  : ");
        for (String name : names) System.out.print(name + " ");
        System.out.println();

        // Sort
        Collections.sort(names);
        System.out.println("Sorted: " + names);
    }

    // ── LinkedList ────────────────────────────────────────────────────────────
    static void linkedListDemo() {
        System.out.println("\n=== LinkedList ===");
        LinkedList<Integer> ll = new LinkedList<>(Arrays.asList(10, 20, 30));

        ll.addFirst(5);    // O(1) at head
        ll.addLast(40);    // O(1) at tail
        ll.add(2, 15);     // O(n) at index

        System.out.println("LinkedList : " + ll);
        System.out.println("First      : " + ll.getFirst());
        System.out.println("Last       : " + ll.getLast());

        ll.removeFirst();
        ll.removeLast();
        System.out.println("After remove first/last: " + ll);
    }

    // ── Set (HashSet, LinkedHashSet, TreeSet) ────────────────────────────────
    static void setDemo() {
        System.out.println("\n=== Set ===");

        // HashSet — no order, no duplicates
        Set<String> hashSet = new HashSet<>(Arrays.asList("Banana", "Apple", "Cherry", "Apple"));
        System.out.println("HashSet (no dupes, no order): " + hashSet);

        // LinkedHashSet — insertion order preserved
        Set<String> linked = new LinkedHashSet<>(Arrays.asList("Banana", "Apple", "Cherry", "Apple"));
        System.out.println("LinkedHashSet (insertion order): " + linked);

        // TreeSet — sorted natural order
        Set<String> tree = new TreeSet<>(Arrays.asList("Banana", "Apple", "Cherry", "Apple"));
        System.out.println("TreeSet (sorted): " + tree);

        // Set operations
        Set<Integer> a = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        Set<Integer> b = new HashSet<>(Arrays.asList(3, 4, 5, 6, 7));

        Set<Integer> union = new HashSet<>(a);
        union.addAll(b);
        System.out.println("Union       : " + new TreeSet<>(union));

        Set<Integer> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        System.out.println("Intersection: " + new TreeSet<>(intersection));

        Set<Integer> difference = new HashSet<>(a);
        difference.removeAll(b);
        System.out.println("Difference  : " + new TreeSet<>(difference));
    }

    // ── Map (HashMap, LinkedHashMap, TreeMap) ────────────────────────────────
    static void mapDemo() {
        System.out.println("\n=== Map ===");

        // HashMap
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Neelima", 95);
        scores.put("Alice",   88);
        scores.put("Bob",     72);
        scores.put("Charlie", 91);

        System.out.println("Neelima's score: " + scores.get("Neelima"));
        System.out.println("Contains Alice : " + scores.containsKey("Alice"));
        System.out.println("Size           : " + scores.size());

        // Iterate entries
        System.out.println("All scores:");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.printf("  %-10s → %d%n", entry.getKey(), entry.getValue());
        }

        // getOrDefault
        System.out.println("Unknown key: " + scores.getOrDefault("Zara", 0));

        // putIfAbsent
        scores.putIfAbsent("Alice", 100);   // won't overwrite
        System.out.println("Alice after putIfAbsent: " + scores.get("Alice"));

        // TreeMap — sorted by key
        Map<String, Integer> sorted = new TreeMap<>(scores);
        System.out.println("TreeMap (sorted keys): " + sorted);
    }

    // ── Queue & Stack (ArrayDeque) ────────────────────────────────────────────
    static void queueAndStackDemo() {
        System.out.println("\n=== Queue (FIFO) with ArrayDeque ===");
        Queue<String> queue = new ArrayDeque<>();
        queue.offer("Task1");
        queue.offer("Task2");
        queue.offer("Task3");

        System.out.println("Peek (front): " + queue.peek());
        while (!queue.isEmpty()) {
            System.out.println("Processed: " + queue.poll());
        }

        System.out.println("\n=== Stack (LIFO) with ArrayDeque ===");
        Deque<String> stack = new ArrayDeque<>();
        stack.push("Page1");
        stack.push("Page2");
        stack.push("Page3");

        System.out.println("Top: " + stack.peek());
        while (!stack.isEmpty()) {
            System.out.println("Popped: " + stack.pop());
        }
    }

    // ── Collections utility methods ───────────────────────────────────────────
    static void collectionsUtilityDemo() {
        System.out.println("\n=== Collections Utility ===");
        List<Integer> nums = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));

        System.out.println("Original : " + nums);
        Collections.sort(nums);
        System.out.println("Sorted   : " + nums);
        Collections.reverse(nums);
        System.out.println("Reversed : " + nums);
        Collections.shuffle(nums, new Random(42));
        System.out.println("Shuffled : " + nums);
        System.out.println("Min      : " + Collections.min(nums));
        System.out.println("Max      : " + Collections.max(nums));
        System.out.println("Frequency of 1: " + Collections.frequency(nums, 1));

        List<String> immutable = Collections.unmodifiableList(Arrays.asList("a", "b", "c"));
        System.out.println("Immutable list: " + immutable);
        try {
            immutable.add("d");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify immutable list!");
        }
    }
}
