package strategyForGrade;

import java.util.Iterator;

import domainModel.Grade;

public class WeightedGradeAverageStrategy implements GradeAverageStrategy {

	@Override
	public double getAverage(Iterator<Grade> grades) {
		double sum = 0;
		int total = 0;
		Grade grade;
		while (grades.hasNext()) {
			grade = grades.next();
			sum = sum + grade.getValue() * grade.getWeight();
			total = total + grade.getWeight();
		}
		return total != 0 ? sum / total : 0;
	}

}
