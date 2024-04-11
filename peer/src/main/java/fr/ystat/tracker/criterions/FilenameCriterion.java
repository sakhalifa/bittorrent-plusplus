package fr.ystat.tracker.criterions;

public class FilenameCriterion implements ICriterion{
	private final String name;

	public FilenameCriterion(String name){
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("filename=\"%s\"", name);
	}
}
