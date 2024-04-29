package fr.ystat.tracker.criterions;

public class FilesizeCriterion extends AbstractSizeCriterion{

	public FilesizeCriterion(long size, ComparisonType comparisonType) {
		super(size, comparisonType);
	}

	@Override
	protected String getFieldName() {
		return "filesize";
	}
}
