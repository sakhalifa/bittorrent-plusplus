package fr.ystat.tracker.criterions;

public class FilesizeCriterion implements ICriterion{
	private final int fileSize;
	private final ComparisonType comparisonType;

	public FilesizeCriterion(int fileSize, ComparisonType comparisonType) {
		this.fileSize = fileSize;
		this.comparisonType = comparisonType;
	}

	@Override
	public String toString() {
		String compStr = "";
		switch(comparisonType){
			case EQ:
				compStr = "=";
				break;
			case LT:
				compStr = "<";
				break;
			case GT:
				compStr = ">";
				break;
			case GTE:
				compStr = ">=";
				break;
			case LTE:
				compStr = "<=";
		}
		return String.format("filesize%s\"%d\"", compStr, fileSize);
	}

	public enum ComparisonType{
		EQ,
		GTE,
		LTE,
		GT,
		LT
	}

}
