# 04 — Collections Framework

The Java Collections Framework provides ready-made data structures.

---

## Overview

```
Iterable
└── Collection
    ├── List       → ordered, allows duplicates    → ArrayList, LinkedList
    ├── Set        → no duplicates                 → HashSet, TreeSet, LinkedHashSet
    └── Queue      → FIFO / LIFO                   → ArrayDeque, PriorityQueue
Map (separate)     → key-value pairs               → HashMap, TreeMap, LinkedHashMap
```

---

## List

Ordered collection. Index-based access. Allows duplicates.

```java
List<String> list = new ArrayList<>();
list.add("Apple");
list.add("Banana");
list.add(0, "Mango");        // insert at index 0
list.get(1);                  // "Apple"
list.remove("Banana");        // by value
list.remove(0);               // by index
list.size();                  // 1
Collections.sort(list);       // sort in place
```

**ArrayList vs LinkedList:**

| | ArrayList | LinkedList |
|-|-----------|------------|
| Random access | O(1) | O(n) |
| Insert/delete at ends | O(n) | O(1) |
| Memory | Less (array) | More (nodes) |
| **Use when** | Read-heavy | Frequent insert/delete |

---

## Set

No duplicates. Does not guarantee order (unless TreeSet/LinkedHashSet).

```java
Set<String> set = new HashSet<>();
set.add("Apple");
set.add("Apple");    // ignored — already present
set.size();          // 1

// TreeSet — sorted
Set<Integer> sorted = new TreeSet<>(List.of(5,2,8,1));
// → [1, 2, 5, 8]

// Set operations
setA.retainAll(setB);   // intersection
setA.addAll(setB);      // union
setA.removeAll(setB);   // difference
```

---

## Map

Key-value pairs. Keys are unique.

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 88);
scores.get("Alice");                      // 95
scores.getOrDefault("Zara", 0);           // 0 (key not present)
scores.containsKey("Bob");                // true
scores.putIfAbsent("Alice", 100);         // won't overwrite

// Iterate
for (Map.Entry<String, Integer> e : scores.entrySet()) {
    System.out.println(e.getKey() + " → " + e.getValue());
}

// TreeMap — keys sorted alphabetically
Map<String, Integer> sorted = new TreeMap<>(scores);
```

---

## Queue & Stack

```java
// Queue — FIFO (First In, First Out)
Queue<String> queue = new ArrayDeque<>();
queue.offer("Task1");    // enqueue
queue.peek();            // look at front without removing
queue.poll();            // dequeue (remove from front)

// Stack — LIFO (Last In, First Out)
Deque<String> stack = new ArrayDeque<>();
stack.push("Page1");     // push to top
stack.peek();            // look at top
stack.pop();             // remove from top
```

---

## Choosing the right collection

| Need | Use |
|------|-----|
| Ordered list with duplicates | `ArrayList` |
| Fast insert/delete at ends | `LinkedList` |
| Unique items, order doesn't matter | `HashSet` |
| Unique items, sorted | `TreeSet` |
| Unique items, insertion order | `LinkedHashSet` |
| Key-value lookup | `HashMap` |
| Key-value, sorted by key | `TreeMap` |
| FIFO queue | `ArrayDeque` as `Queue` |
| LIFO stack | `ArrayDeque` as `Deque` |

---

## File Reference
[CollectionsDemo.java](../src/main/java/com/javabasics/collections/CollectionsDemo.java)
