package com.javabasics.project;

import com.javabasics.project.model.Student;
import com.javabasics.project.repository.StudentRepository;
import com.javabasics.project.service.StudentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentService using JUnit 5.
 * Covers: CRUD, validation, search, statistics, edge cases.
 */
@DisplayName("StudentService Tests")
class StudentServiceTest {

    private StudentRepository repo;
    private StudentService    service;

    // Runs before EACH test — fresh state every time
    @BeforeEach
    void setUp() {
        repo    = new StudentRepository();
        service = new StudentService(repo);
    }

    // ── Add Student ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("addStudent()")
    class AddStudentTests {

        @Test
        @DisplayName("should add a valid student and return it with a generated id")
        void addValidStudent() {
            Student s = service.addStudent("Neelima", "n@uni.edu", 22, 3.9, "CS");

            assertNotNull(s);
            assertTrue(s.getId() > 0);
            assertEquals("Neelima", s.getName());
            assertEquals("n@uni.edu", s.getEmail());
            assertEquals(22, s.getAge());
            assertEquals(3.9, s.getGpa(), 0.001);
            assertEquals("CS", s.getMajor());
        }

        @Test
        @DisplayName("should throw when email is duplicate")
        void rejectDuplicateEmail() {
            service.addStudent("Alice", "alice@uni.edu", 20, 3.5, "Math");

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    service.addStudent("Another Alice", "alice@uni.edu", 21, 3.0, "Physics"));

            assertTrue(ex.getMessage().contains("alice@uni.edu"));
        }

        @Test
        @DisplayName("should throw when name is blank")
        void rejectBlankName() {
            assertThrows(IllegalArgumentException.class, () ->
                    service.addStudent("  ", "x@uni.edu", 20, 3.0, "CS"));
        }

        @ParameterizedTest(name = "invalid GPA = {0}")
        @ValueSource(doubles = {-0.1, 4.1, 5.0, -1.0})
        @DisplayName("should throw on invalid GPA values")
        void rejectInvalidGpa(double gpa) {
            assertThrows(IllegalArgumentException.class, () ->
                    service.addStudent("Test", "t@uni.edu", 20, gpa, "CS"));
        }

        @ParameterizedTest(name = "invalid age = {0}")
        @ValueSource(ints = {0, 10, 15, 101, 200})
        @DisplayName("should throw on invalid age values")
        void rejectInvalidAge(int age) {
            assertThrows(IllegalArgumentException.class, () ->
                    service.addStudent("Test", "t@uni.edu", age, 3.0, "CS"));
        }
    }

    // ── Get Student ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("getStudentById()")
    class GetStudentTests {

        @Test
        @DisplayName("should return correct student for known id")
        void getById() {
            Student added = service.addStudent("Bob", "bob@uni.edu", 21, 3.0, "Physics");
            Student found = service.getStudentById(added.getId());
            assertEquals(added.getId(), found.getId());
            assertEquals("Bob", found.getName());
        }

        @Test
        @DisplayName("should throw NoSuchElementException for unknown id")
        void throwForUnknownId() {
            assertThrows(NoSuchElementException.class, () -> service.getStudentById(999));
        }
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("update operations")
    class UpdateTests {

        @Test
        @DisplayName("updateGpa() should update and persist new GPA")
        void updateGpa() {
            Student s = service.addStudent("Charlie", "c@uni.edu", 20, 2.5, "CS");
            Student updated = service.updateGpa(s.getId(), 3.8);

            assertEquals(3.8, updated.getGpa(), 0.001);
            // confirm persisted
            assertEquals(3.8, service.getStudentById(s.getId()).getGpa(), 0.001);
        }

        @Test
        @DisplayName("updateMajor() should update and persist new major")
        void updateMajor() {
            Student s = service.addStudent("Diana", "d@uni.edu", 22, 3.6, "Math");
            service.updateMajor(s.getId(), "Data Science");
            assertEquals("Data Science", service.getStudentById(s.getId()).getMajor());
        }

        @Test
        @DisplayName("updateGpa() should throw for non-existent id")
        void updateGpaNotFound() {
            assertThrows(NoSuchElementException.class, () -> service.updateGpa(999, 3.0));
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("removeStudent()")
    class DeleteTests {

        @Test
        @DisplayName("should delete existing student and reduce count")
        void deleteExisting() {
            Student s = service.addStudent("Eve", "e@uni.edu", 20, 3.2, "Art");
            int before = service.getTotalStudents();

            boolean removed = service.removeStudent(s.getId());

            assertTrue(removed);
            assertEquals(before - 1, service.getTotalStudents());
            assertThrows(NoSuchElementException.class, () -> service.getStudentById(s.getId()));
        }

        @Test
        @DisplayName("should throw when deleting non-existent student")
        void deleteNotFound() {
            assertThrows(NoSuchElementException.class, () -> service.removeStudent(999));
        }
    }

    // ── Search & Filter ───────────────────────────────────────────────────────
    @Nested
    @DisplayName("search and filter")
    class SearchTests {

        @BeforeEach
        void seed() {
            service.addStudent("S1", "s1@u.edu", 20, 3.9, "CS");
            service.addStudent("S2", "s2@u.edu", 21, 3.5, "Math");
            service.addStudent("S3", "s3@u.edu", 22, 2.8, "CS");
            service.addStudent("S4", "s4@u.edu", 20, 1.5, "Physics");
        }

        @Test
        @DisplayName("getStudentsByMajor() should return correct students")
        void filterByMajor() {
            List<Student> cs = service.getStudentsByMajor("CS");
            assertEquals(2, cs.size());
            assertTrue(cs.stream().allMatch(s -> s.getMajor().equals("CS")));
        }

        @Test
        @DisplayName("getStudentsByMajor() should be case-insensitive")
        void filterByMajorCaseInsensitive() {
            assertEquals(2, service.getStudentsByMajor("cs").size());
            assertEquals(2, service.getStudentsByMajor("CS").size());
        }

        @Test
        @DisplayName("getTopStudents() should return students with GPA >= threshold, sorted desc")
        void topStudents() {
            List<Student> top = service.getTopStudents(3.5);
            assertEquals(2, top.size());
            // verify sorted descending
            for (int i = 0; i < top.size() - 1; i++) {
                assertTrue(top.get(i).getGpa() >= top.get(i + 1).getGpa());
            }
        }

        @Test
        @DisplayName("getStudentsSortedByName() should return alphabetically sorted list")
        void sortedByName() {
            List<Student> sorted = service.getStudentsSortedByName();
            for (int i = 0; i < sorted.size() - 1; i++) {
                assertTrue(sorted.get(i).getName().compareTo(sorted.get(i + 1).getName()) <= 0);
            }
        }
    }

    // ── Statistics ────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("statistics")
    class StatisticsTests {

        @BeforeEach
        void seed() {
            service.addStudent("A", "a@u.edu", 20, 4.0, "CS");
            service.addStudent("B", "b@u.edu", 21, 3.0, "Math");
            service.addStudent("C", "c@u.edu", 22, 2.0, "CS");
        }

        @Test
        @DisplayName("getAverageGpa() should return correct average")
        void averageGpa() {
            assertEquals(3.0, service.getAverageGpa(), 0.001);
        }

        @Test
        @DisplayName("getHighestGpaStudent() should return student with max GPA")
        void highestGpa() {
            Student top = service.getHighestGpaStudent().orElseThrow();
            assertEquals("A", top.getName());
            assertEquals(4.0, top.getGpa(), 0.001);
        }

        @Test
        @DisplayName("getStudentCountByMajor() should group correctly")
        void countByMajor() {
            Map<String, Long> counts = service.getStudentCountByMajor();
            assertEquals(2L, counts.get("CS"));
            assertEquals(1L, counts.get("Math"));
        }

        @Test
        @DisplayName("getAverageGpa() should return 0.0 when no students")
        void averageGpaEmpty() {
            StudentService emptyService = new StudentService(new StudentRepository());
            assertEquals(0.0, emptyService.getAverageGpa(), 0.001);
        }
    }

    // ── Grade assignment ──────────────────────────────────────────────────────
    @Nested
    @DisplayName("letter grade calculation")
    class GradeTests {

        @ParameterizedTest(name = "GPA {0} → Grade {1}")
        @CsvSource({
            "4.0, A",
            "3.7, A",
            "3.5, B",
            "3.0, B",
            "2.5, C",
            "2.0, C",
            "1.5, D",
            "1.0, D",
            "0.5, F",
            "0.0, F"
        })
        @DisplayName("should assign correct letter grade for each GPA")
        void gradeMapping(double gpa, String expectedGrade) {
            Student s = service.addStudent("Test" + gpa, gpa + "@u.edu", 20, gpa, "CS");
            assertEquals(Student.Grade.valueOf(expectedGrade), s.getLetterGrade());
        }
    }
}
