package fr.ystat.config;

public class GlobalConfiguration {

    private GlobalConfiguration(){}
    public static void setConfiguration(IConfigurationManager newConfig){
        INSTANCE = newConfig;
    }


    public static IConfigurationManager get(){
        return INSTANCE;
    }

    private static IConfigurationManager INSTANCE = new DummyConfigurationManager();


}
