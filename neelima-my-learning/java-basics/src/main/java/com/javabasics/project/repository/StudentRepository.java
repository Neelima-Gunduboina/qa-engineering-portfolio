package com.javabasics.project.repository;

import com.javabasics.project.model.Student;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory data store for Students.
 * Acts as the data layer — all CRUD operations live here.
 */
public class StudentRepository {

    // id → Student map as the backing store
    private final Map<Integer, Student> store = new HashMap<>();
    private int nextId = 1;

    // ── Create ────────────────────────────────────────────────────────────────
    public Student save(Student student) {
        if (store.containsKey(student.getId())) {
            throw new IllegalArgumentException("Student with id " + student.getId() + " already exists.");
        }
        store.put(student.getId(), student);
        return student;
    }

    // ── Read ──────────────────────────────────────────────────────────────────
    public Optional<Student> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Student> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Student> findByMajor(String major) {
        return store.values().stream()
                .filter(s -> s.getMajor().equalsIgnoreCase(major))
                .collect(Collectors.toList());
    }

    public List<Student> findByMinGpa(double minGpa) {
        return store.values().stream()
                .filter(s -> s.getGpa() >= minGpa)
                .collect(Collectors.toList());
    }

    public Optional<Student> findByEmail(String email) {
        return store.values().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    // ── Update ────────────────────────────────────────────────────────────────
    public Student update(Student updated) {
        if (!store.containsKey(updated.getId())) {
            throw new NoSuchElementException("Student id " + updated.getId() + " not found.");
        }
        store.put(updated.getId(), updated);
        return updated;
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    public boolean deleteById(int id) {
        return store.remove(id) != null;
    }

    public void deleteAll() {
        store.clear();
    }

    // ── Utility ───────────────────────────────────────────────────────────────
    public boolean existsById(int id) {
        return store.containsKey(id);
    }

    public int count() {
        return store.size();
    }

    public int generateNextId() {
        return nextId++;
    }
}
