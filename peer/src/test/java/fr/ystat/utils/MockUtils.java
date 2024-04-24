package fr.ystat.utils;

import fr.ystat.Main;
import fr.ystat.config.DummyConfigurationManager;

import java.lang.reflect.Field;

public class MockUtils {

	public static void mockMain() throws Exception{
		Field configManagerField = Main.class.getDeclaredField("configurationManager");
		configManagerField.setAccessible(true);
		configManagerField.set(null, new DummyConfigurationManager());
	}
}
