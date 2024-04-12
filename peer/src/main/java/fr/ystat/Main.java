package fr.ystat;

import fr.ystat.config.DummyConfigurationManager;
import fr.ystat.config.IConfigurationManager;
import lombok.Getter;

public class Main {
	@Getter
	private static IConfigurationManager configurationManager;

	public static void main(String[] args) {
		configurationManager = new DummyConfigurationManager(); // TODO: better config xd
	}
}
