package com.javabasics.project;

import com.javabasics.project.model.Student;
import com.javabasics.project.repository.StudentRepository;
import com.javabasics.project.service.StudentService;

import java.util.List;
import java.util.Map;

/**
 * Entry point for the Student Management System.
 * Demonstrates the full CRUD + analytics flow.
 */
public class StudentManagementApp {

    public static void main(String[] args) {
        StudentRepository repo    = new StudentRepository();
        StudentService    service = new StudentService(repo);

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Student Management System  v1.0    ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        // ── Seed data ─────────────────────────────────────────────────────────
        service.addStudent("Neelima Gunduboina", "neelima@uni.edu",  22, 3.9, "Computer Science");
        service.addStudent("Alice Johnson",      "alice@uni.edu",    20, 3.5, "Mathematics");
        service.addStudent("Bob Martinez",       "bob@uni.edu",      21, 2.8, "Physics");
        service.addStudent("Charlie Brown",      "charlie@uni.edu",  19, 3.1, "Computer Science");
        service.addStudent("Diana Prince",       "diana@uni.edu",    23, 3.7, "Mathematics");
        service.addStudent("Evan Williams",      "evan@uni.edu",     20, 1.9, "Physics");
        service.addStudent("Fiona Green",        "fiona@uni.edu",    22, 2.4, "Chemistry");

        // ── Display all ───────────────────────────────────────────────────────
        printSeparator("All Students");
        service.getAllStudents().forEach(System.out::println);

        // ── Fetch by id ───────────────────────────────────────────────────────
        printSeparator("Fetch student id=1");
        System.out.println(service.getStudentById(1));

        // ── Update GPA ────────────────────────────────────────────────────────
        printSeparator("Update Bob's GPA to 3.2");
        service.updateGpa(3, 3.2);
        System.out.println(service.getStudentById(3));

        // ── Delete a student ──────────────────────────────────────────────────
        printSeparator("Delete Evan (id=6)");
        service.removeStudent(6);
        System.out.println("Total students after delete: " + service.getTotalStudents());

        // ── Filter by major ───────────────────────────────────────────────────
        printSeparator("Computer Science students");
        service.getStudentsByMajor("Computer Science").forEach(System.out::println);

        // ── Top students (GPA ≥ 3.5) ──────────────────────────────────────────
        printSeparator("Top students (GPA ≥ 3.5)");
        service.getTopStudents(3.5).forEach(s ->
                System.out.printf("  %-25s GPA=%.2f%n", s.getName(), s.getGpa()));

        // ── Statistics ────────────────────────────────────────────────────────
        printSeparator("Statistics");
        System.out.printf("Average GPA   : %.2f%n", service.getAverageGpa());
        service.getHighestGpaStudent().ifPresent(s ->
                System.out.println("Highest GPA   : " + s.getName() + " (" + s.getGpa() + ")"));

        printSeparator("Students per major");
        service.getStudentCountByMajor()
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  %-20s → %d students%n", e.getKey(), e.getValue()));

        printSeparator("Students grouped by grade");
        service.groupByGrade().forEach((grade, students) -> {
            System.out.print("  Grade " + grade + ": ");
            students.forEach(s -> System.out.print(s.getName() + "  "));
            System.out.println();
        });

        // ── Sorted by name ────────────────────────────────────────────────────
        printSeparator("All students sorted by name");
        service.getStudentsSortedByName()
                .forEach(s -> System.out.printf("  %-25s %s%n", s.getName(), s.getMajor()));

        // ── Duplicate email guard ─────────────────────────────────────────────
        printSeparator("Duplicate email guard");
        try {
            service.addStudent("Duplicate", "neelima@uni.edu", 20, 3.0, "Art");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println("\n✓ Demo complete.");
    }

    static void printSeparator(String title) {
        System.out.println("\n── " + title + " " + "─".repeat(Math.max(0, 40 - title.length())));
    }
}
