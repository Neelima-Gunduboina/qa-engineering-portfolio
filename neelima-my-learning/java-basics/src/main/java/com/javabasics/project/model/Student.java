package com.javabasics.project.model;

import java.util.Objects;

/**
 * Student entity — encapsulates student data with validation.
 */
public class Student {

    public enum Grade { A, B, C, D, F }

    private final int    id;
    private       String name;
    private       String email;
    private       int    age;
    private       double gpa;
    private       String major;

    public Student(int id, String name, String email, int age, double gpa, String major) {
        if (id <= 0)                         throw new IllegalArgumentException("ID must be positive");
        if (name == null || name.isBlank())  throw new IllegalArgumentException("Name cannot be blank");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
        if (age < 16 || age > 100)           throw new IllegalArgumentException("Age must be 16-100");
        if (gpa < 0.0 || gpa > 4.0)         throw new IllegalArgumentException("GPA must be 0.0-4.0");

        this.id    = id;
        this.name  = name;
        this.email = email;
        this.age   = age;
        this.gpa   = gpa;
        this.major = major;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int    getId()    { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
    public int    getAge()   { return age; }
    public double getGpa()   { return gpa; }
    public String getMajor() { return major; }

    // ── Setters (id is immutable) ─────────────────────────────────────────────
    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be blank");
        this.name = name;
    }
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid email");
        this.email = email;
    }
    public void setAge(int age)      { this.age = age; }
    public void setGpa(double gpa)   { this.gpa = gpa; }
    public void setMajor(String m)   { this.major = m; }

    // ── Derived property ──────────────────────────────────────────────────────
    public Grade getLetterGrade() {
        if (gpa >= 3.7) return Grade.A;
        if (gpa >= 3.0) return Grade.B;
        if (gpa >= 2.0) return Grade.C;
        if (gpa >= 1.0) return Grade.D;
        return Grade.F;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        return id == ((Student) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("Student{id=%d, name='%s', email='%s', age=%d, gpa=%.2f, major='%s', grade=%s}",
                id, name, email, age, gpa, major, getLetterGrade());
    }
}
