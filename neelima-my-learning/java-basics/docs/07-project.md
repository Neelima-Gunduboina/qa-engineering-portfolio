# 07 — Student Management System

A small layered application that applies everything learned so far.

---

## Architecture

```
StudentManagementApp   ← entry point / UI layer
        │
        ▼
StudentService         ← business logic layer
        │
        ▼
StudentRepository      ← data access layer (in-memory HashMap)
        │
        ▼
Student                ← domain model
```

This is a **3-layer architecture** (common in real Java apps):
- **Model** — represents the data
- **Repository** — manages persistence (here: in-memory, could swap to a database)
- **Service** — contains business rules, orchestrates repository calls

---

## Student Model

```java
Student s = new Student(1, "Neelima", "n@uni.edu", 22, 3.9, "Computer Science");
s.getLetterGrade();   // Grade.A
```

| Field | Type | Validation |
|-------|------|-----------|
| id | int | > 0, immutable |
| name | String | not blank |
| email | String | must contain `@` |
| age | int | 16–100 |
| gpa | double | 0.0–4.0 |
| major | String | any |

Grade is derived:

| GPA | Grade |
|-----|-------|
| ≥ 3.7 | A |
| ≥ 3.0 | B |
| ≥ 2.0 | C |
| ≥ 1.0 | D |
| < 1.0 | F |

---

## StudentRepository (CRUD)

```java
repo.save(student)           // Create
repo.findById(1)             // Read — returns Optional<Student>
repo.findAll()               // Read all
repo.findByMajor("CS")       // Filter
repo.findByMinGpa(3.5)       // Filter
repo.update(student)         // Update
repo.deleteById(1)           // Delete
repo.count()                 // Total count
```

---

## StudentService (Business Logic)

```java
// CRUD
service.addStudent("Neelima", "n@uni.edu", 22, 3.9, "CS");
service.getStudentById(1);
service.updateGpa(1, 4.0);
service.updateMajor(1, "Data Science");
service.removeStudent(1);

// Search
service.getStudentsByMajor("CS");
service.getTopStudents(3.5);          // sorted by GPA desc
service.getStudentsSortedByName();

// Statistics
service.getAverageGpa();
service.getHighestGpaStudent();       // returns Optional<Student>
service.getStudentCountByMajor();     // Map<String, Long>
service.groupByGrade();               // Map<Grade, List<Student>>
service.getTotalStudents();
```

---

## How to run

```bash
mvn compile exec:java -Dexec.mainClass="com.javabasics.project.StudentManagementApp"
```

### Expected output (excerpt)
```
╔══════════════════════════════════════╗
║   Student Management System  v1.0    ║
╚══════════════════════════════════════╝

── All Students ──────────────────────────
Student{id=1, name='Neelima Gunduboina', ...grade=A}
...

── Statistics ────────────────────────────
Average GPA   : 3.06
Highest GPA   : Neelima Gunduboina (3.9)
```

---

## Java concepts used

| Concept | Where |
|---------|-------|
| Encapsulation | `Student` — private fields + validation |
| Collections | `HashMap` in repository, `List` returns |
| Streams | All filter/sort/statistics in service |
| Optional | `findById`, `findByEmail`, `getHighestGpaStudent` |
| Lambdas | All stream operations |
| Exceptions | Validation in model + service guards |
| Enum | `Student.Grade` |

---

## File Reference
[Student.java](../src/main/java/com/javabasics/project/model/Student.java) | [StudentRepository.java](../src/main/java/com/javabasics/project/repository/StudentRepository.java) | [StudentService.java](../src/main/java/com/javabasics/project/service/StudentService.java) | [StudentManagementApp.java](../src/main/java/com/javabasics/project/StudentManagementApp.java)
