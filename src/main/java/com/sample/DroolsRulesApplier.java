package com.sample;

import java.util.*;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import org.kie.api.definition.type.FactType;
import org.kie.api.KieBase;
import org.kie.api.runtime.rule.FactHandle;

public class DroolsRulesApplier {
	
    private static KieSession KIE_SESSION;
    private static KieContainer CONTAINER;
    private static DataObjectFactory factory;
    private FactType eventFactType;
    private Object notifications;
    private FactHandle notificationsHandle;
    private FactType notificationsFactType;

    public DroolsRulesApplier(PropertiesConfiguration properties) throws Exception {
        CONTAINER =  DroolsSessionFactory.createKieContainer(properties);
        KIE_SESSION = DroolsSessionFactory.createDroolsSession(CONTAINER);

        factory = DataObjectFactory.getInstance(properties);
        eventFactType = factory.eventFactType(CONTAINER.getKieBase());
        notificationsFactType = factory.notificationsFactType(CONTAINER.getKieBase());
        notifications = factory.makeNotificationsApplicant(notificationsFactType);
        notificationsHandle = KIE_SESSION.insert(notifications);
    }

    public void runRules(Object obj, String group){
        KIE_SESSION.getAgenda().getAgendaGroup(group).setFocus();
        KIE_SESSION.insert(obj);
        KIE_SESSION.fireAllRules();
    }

    public void runRules(ArrayList<Object> objs, String group){
        KIE_SESSION.getAgenda().getAgendaGroup(group).setFocus();
        for(Object obj: objs){
            KIE_SESSION.insert(obj);
        }
        KIE_SESSION.fireAllRules();
    }

    public void runRules(String group){
        KIE_SESSION.getAgenda().getAgendaGroup(group).setFocus();
        KIE_SESSION.fireAllRules();
    }

    public void resetNotifications() {
        KIE_SESSION.retract(notificationsHandle);
        notificationsFactType.set(notifications, "messages", new ArrayList<String>());
        notificationsHandle = KIE_SESSION.insert(notifications);
    }

    public String applyNotificationRule(String value) {
        try{
            System.out.println("start to apply notification rule with fact: " + value);
            Object event = factory.makeEventApplicant(eventFactType, value);
            System.out.println("start to run rules");
            runRules(event, "notification");
            System.out.println("end to run rules");
            /*ArrayList<String> messages = getNotificationRequest();
            if(messages.isEmpty()) {
                return null;
            }
            return messages.get(messages.size()-1);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getNotificationRequest() {
        try{
            ArrayList<String> messages = (ArrayList<String>)notificationsFactType.get(notifications, "messages");
            return messages;
        } catch  (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    private static Object makeEventApplicant(FactType factType, String input) throws Exception{
        Object kpi = factType.newInstance();
        String[] components = input.split(" ");
        factType.set(kpi, "group", components[0]);
        factType.set(kpi, "name", components[1]);
        factType.set(kpi, "value", Double.valueOf(components[2]));
        factType.set(kpi, "skipped", false);
        return kpi;
    }
    protected static FactType eventFactType(KieBase base) {
        FactType factType = base.getFactType("com.dell.mars.pacs.notify", "EVENT");
        return factType;
    }

    private static Object makeNotificationsApplicant(FactType factType) throws Exception{
        System.out.println("start to make notifications applicant");
        System.out.println(factType);
        Object notifications = factType.newInstance();
        factType.set(notifications, "messages", new ArrayList<String>());
        return notifications;
    }

    protected static FactType notificationsFactType(KieBase base) {
        FactType factType = base.getFactType("com.dell.mars.pacs.notify", "NOTIFICATIONS");
        return factType;
    }*/

}

