package strategyForGrade;

import java.util.Iterator;

import domainModel.Grade;

public interface GradeAverageStrategy {
	
	public double getAverage(Iterator<Grade> grades);
	
}