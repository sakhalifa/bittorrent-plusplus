package fr.ystat.tracker.criterions;

public class HashCriterion implements ICriterion{
	private final String hash;

	public HashCriterion(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return String.format("key=\"%s\"", hash);
	}
}
