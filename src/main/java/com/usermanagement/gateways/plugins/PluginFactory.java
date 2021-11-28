package com.usermanagement.gateways.plugins;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PluginFactory {
    private static Properties props = new Properties();

    static{
        try{
            InputStream input = new FileInputStream("src/main/java/resources/sendVerification.properties");
            props.load(input);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Object getPlugin(Class iface) {

        String impName = props.getProperty(iface.getName());
        if (impName == null) {
            throw new RuntimeException("implementation not specified for " +
                    iface.getName() + " in PluginFactory properties");
        }
        try {
            return Class.forName(impName).newInstance();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException("factory unable to construct instance of " +
                    iface.getName());
        }
        return null;
    }

}
