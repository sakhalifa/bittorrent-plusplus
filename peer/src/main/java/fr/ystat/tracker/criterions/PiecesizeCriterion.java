package fr.ystat.tracker.criterions;

public class PiecesizeCriterion extends AbstractSizeCriterion{
	public PiecesizeCriterion(long size, ComparisonType comparisonType) {
		super(size, comparisonType);
	}

	@Override
	protected String getFieldName() {
		return "piecesize";
	}
}
