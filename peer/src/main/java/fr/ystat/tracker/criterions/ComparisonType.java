package fr.ystat.tracker.criterions;

public enum ComparisonType {
	EQ,
//	GTE,
//	LTE,
	GT,
	LT;

	@Override
	public String toString() {
		switch (this) {
			case EQ:
				return "=";
			case LT:
				return "<";
			case GT:
				return ">";
//			case GTE:
//				return ">=";
//			case LTE:
//				return "<=";
			default:
				throw new RuntimeException("Unknown comparison type"); // Should NEVER happen
		}
	}
}
