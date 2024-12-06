package strategyForGrade;

import java.util.Iterator;

import domainModel.Grade;

public class GeometricGradeAverageStrategy implements GradeAverageStrategy {

	@Override
	public double getAverage(Iterator<Grade> grades) {
		double product = 1;
		int total = 0;
		while (grades.hasNext()) {
			product = product * grades.next().getValue();
			total++;
		}
		return total != 0 ? Math.pow(product, 1.0 / total) : 0;
	}

}
