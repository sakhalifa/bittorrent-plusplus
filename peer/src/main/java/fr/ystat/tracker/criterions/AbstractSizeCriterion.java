package fr.ystat.tracker.criterions;

abstract class AbstractSizeCriterion implements ICriterion{
	private final long size;
	private final ComparisonType comparisonType;

	public AbstractSizeCriterion(long size, ComparisonType comparisonType) {
		this.size = size;
		this.comparisonType = comparisonType;
	}

	protected abstract String getFieldName();

	@Override
	public String toString() {
		return String.format("%s%s\"%d\"", this.getFieldName(), comparisonType.toString(), size);
	}
}
