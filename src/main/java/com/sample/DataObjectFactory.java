package com.sample;

import java.util.ArrayList;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;

public class DataObjectFactory {
    private static DataObjectFactory instance = null;
    private static String packageName;
    private DataObjectFactory(PropertiesConfiguration properties) {
        packageName = properties.getString("group") + "." + properties.getString("artifact");
    }

    public static DataObjectFactory getInstance(PropertiesConfiguration properties) {
        if (instance == null) {
            synchronized (DataObjectFactory.class) {
                if (instance == null) {
                    instance =  new DataObjectFactory(properties);
                }
            }
        }
        return instance;
    }

    public Object makeEventApplicant(FactType factType, String input) throws Exception{
        Object kpi = factType.newInstance();
        String[] components = input.split(" ");
        factType.set(kpi, "group", components[0]);
        factType.set(kpi, "name", components[1]);
        factType.set(kpi, "value", Double.valueOf(components[2]));
        factType.set(kpi, "skipped", false);
        return kpi;
    }
    public FactType eventFactType(KieBase base) {
        FactType factType = base.getFactType(packageName, "EVENT");
        return factType;
    }

    public Object makeNotificationsApplicant(FactType factType) throws Exception{
        Object notifications = factType.newInstance();
        factType.set(notifications, "messages", new ArrayList<String>());
        return notifications;
    }

    public FactType notificationsFactType(KieBase base) {
        FactType factType = base.getFactType(packageName, "NOTIFICATIONS");
        return factType;
    }
}

