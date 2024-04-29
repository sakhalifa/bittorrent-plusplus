package fr.ystat.tracker.criterions;

public class KeyCriterion implements ICriterion{
	private final String hash;

	public KeyCriterion(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return String.format("key=\"%s\"", hash);
	}
}
