package testDomainModel;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.Before;
import org.junit.Test;

import decorator.*;
import domainModel.Annotation;
import domainModel.SchoolClass;
import domainModel.Teacher;
import domainModel.TeachingAssignment;

public class TestAnnotation {

	private SchoolClass schoolClass;
	private Teacher teacher;
	private TeachingAssignment teachingAssignment;
	private Annotation annotation;
	private String expectedAnnotationInfo;

	@Before
	public void setup() {
		schoolClass = new SchoolClass("5F");
		teacher = new Teacher("Mario", "Rossi", 1);

		teachingAssignment = new TeachingAssignment(0, "Math", teacher, schoolClass);

		annotation = new Annotation(teachingAssignment, LocalDate.now(), 0);

		expectedAnnotationInfo = "Class: 5F" + "\nTeacher: Mario Rossi" + "\nSubject: Math" + "\nDate: "
				+ LocalDate.now().toString();
	}

	@Test
	public void testAnnotationInfo() {
		assertThat(annotation.info()).isEqualTo(expectedAnnotationInfo);
	}

	@Test
	public void testDescriptionDecorator() {
		Component c = new DescriptionDecorator(annotation, "Lezione bella");

		assertThat(c.info()).isEqualTo(expectedAnnotationInfo + "\nDescription: Lezione bella");
	}

	@Test
	public void testHomeworkDecorator() {
		Component c = new HeaderDecorator(annotation, "Homework:");

		assertThat(c.info()).isEqualTo("Homework:\n" + expectedAnnotationInfo);
	}

	@Test
	public void testSubissionDateDecorator() {
		Component c = new SubissionDateDecorator(annotation, LocalDate.now());

		assertThat(c.info()).isEqualTo(expectedAnnotationInfo + "\nSubission date: " + LocalDate.now().toString());
	}

	@Test
	public void testTimespanDecorator() {
		Component c = new TimespanDecorator(annotation, LocalTime.of(10, 0), LocalTime.of(11, 0));

		assertThat(c.info()).isEqualTo(expectedAnnotationInfo + "\nTimespan: " + LocalTime.of(10, 0).toString() + " - "
				+ LocalTime.of(11, 0).toString());
	}

	@Test
	public void testExampleOfDecorator() {
		Component c = new DescriptionDecorator(
				new HeaderDecorator(new TimespanDecorator(annotation, LocalTime.of(10, 0), LocalTime.of(11, 0)),
						"Homework:"),
				"Lezione bella");

		assertThat(c.info())
				.isEqualTo("Homework:\n" + expectedAnnotationInfo + "\nTimespan: " + LocalTime.of(10, 0).toString()
						+ " - " + LocalTime.of(11, 0).toString() + "\nDescription: Lezione bella");
	}

}
