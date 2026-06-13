package com.javabasics.project.service;

import com.javabasics.project.model.Student;
import com.javabasics.project.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Business logic layer — orchestrates operations on students.
 * Depends on StudentRepository for persistence.
 */
public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    // ── Add ───────────────────────────────────────────────────────────────────
    public Student addStudent(String name, String email, int age, double gpa, String major) {
        // Guard: duplicate email
        if (repo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("A student with email '" + email + "' already exists.");
        }
        int id = repo.generateNextId();
        Student student = new Student(id, name, email, age, gpa, major);
        return repo.save(student);
    }

    // ── Get ───────────────────────────────────────────────────────────────────
    public Student getStudentById(int id) {
        return repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Student not found with id: " + id));
    }

    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    // ── Update ────────────────────────────────────────────────────────────────
    public Student updateGpa(int id, double newGpa) {
        Student student = getStudentById(id);
        student.setGpa(newGpa);
        return repo.update(student);
    }

    public Student updateMajor(int id, String newMajor) {
        Student student = getStudentById(id);
        student.setMajor(newMajor);
        return repo.update(student);
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    public boolean removeStudent(int id) {
        if (!repo.existsById(id)) {
            throw new NoSuchElementException("Cannot delete — student id " + id + " not found.");
        }
        return repo.deleteById(id);
    }

    // ── Search & Filter ───────────────────────────────────────────────────────
    public List<Student> getStudentsByMajor(String major) {
        return repo.findByMajor(major);
    }

    public List<Student> getTopStudents(double minGpa) {
        return repo.findByMinGpa(minGpa).stream()
                .sorted(Comparator.comparingDouble(Student::getGpa).reversed())
                .collect(Collectors.toList());
    }

    public List<Student> getStudentsSortedByName() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(Student::getName))
                .collect(Collectors.toList());
    }

    // ── Statistics ────────────────────────────────────────────────────────────
    public double getAverageGpa() {
        return repo.findAll().stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
    }

    public Optional<Student> getHighestGpaStudent() {
        return repo.findAll().stream()
                .max(Comparator.comparingDouble(Student::getGpa));
    }

    public Map<String, Long> getStudentCountByMajor() {
        return repo.findAll().stream()
                .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
    }

    public Map<Student.Grade, List<Student>> groupByGrade() {
        return repo.findAll().stream()
                .collect(Collectors.groupingBy(Student::getLetterGrade));
    }

    public int getTotalStudents() {
        return repo.count();
    }
}
