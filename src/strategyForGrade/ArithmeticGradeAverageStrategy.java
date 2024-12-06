package strategyForGrade;

import java.util.Iterator;

import domainModel.Grade;

public class ArithmeticGradeAverageStrategy implements GradeAverageStrategy {

	@Override
	public double getAverage(Iterator<Grade> grades) {
		double sum = 0;
		int total = 0;
		while (grades.hasNext()) {
			sum = sum + grades.next().getValue();
			total++;
		}
		return total != 0 ? sum / total : 0;
	}

}
