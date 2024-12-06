package domainModel;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;

import decorator.Component;
import decorator.DescriptionDecorator;
import decorator.HeaderDecorator;
import decorator.SubissionDateDecorator;
import decorator.TimespanDecorator;
import domainModel.Annotation;
import domainModel.TeachingAssignment;
import domainModel.SchoolClass;
import domainModel.Teacher;
import domainModel.AnnotationFacade;

public class TestAnnotationFacade {

    private SchoolClass schoolClass;
    private Teacher teacher;
    private TeachingAssignment teachingAssignment;
    private LocalDate date;
    private String description;
    private LocalDate submissionDate;
    private LocalTime startHour;
    private LocalTime endHour;

    @Before
    public void setup() {
        schoolClass = new SchoolClass("5F");
        teacher = new Teacher("Mario", "Rossi", 1);
        teachingAssignment = new TeachingAssignment(0, "Math", teacher, schoolClass);

        date = LocalDate.now();
        description = "Lezione bella";
        submissionDate = LocalDate.of(2024, 12, 1);
        startHour = LocalTime.of(9, 0);
        endHour = LocalTime.of(11, 0);
    }

    @Test
    public void testGetHomework() {
        Component homework = AnnotationFacade.getHomework(0, teachingAssignment, date, description, submissionDate);

        assertThat(homework).isNotNull();
        assertThat(homework.info()).isEqualTo("Homework:\nClass: 5F\nTeacher: Mario Rossi\nSubject: Math\nDate: "
                + date + "\nSubission date: " + submissionDate.toString() + "\nDescription: Lezione bella");
    }

    @Test
    public void testGetLesson() {
        Component lesson = AnnotationFacade.getLesson(0, teachingAssignment, date, description, startHour, endHour);

        assertThat(lesson).isNotNull();
        assertThat(lesson.info()).isEqualTo("Lesson:\nClass: 5F\nTeacher: Mario Rossi\nSubject: Math\nDate: "
                + date + "\nTimespan: " + startHour + " - " + endHour + "\nDescription: Lezione bella");
    }
}
