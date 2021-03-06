package com.sample;

import java.util.*;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import org.kie.api.definition.type.FactType;
import org.kie.api.KieBase;


public class DroolsRulesApplier {
	
    private static KieSession KIE_SESSION;
    private static KieContainer CONTAINER;
    private Object msg;

    public DroolsRulesApplier() throws Exception {
        KIE_SESSION = DroolsSessionFactory.createDroolsSession();
        CONTAINER =  DroolsSessionFactory.createKieContainer();
        FactType msgFactType = msgFactType(CONTAINER.getKieBase());
        msg = makeMsgApplicant(msgFactType);
        KIE_SESSION.insert(msg);
    }

    public void runRules(Object kpi, String group){
        KIE_SESSION.getAgenda().getAgendaGroup(group).setFocus();
        KIE_SESSION.insert(kpi);
        KIE_SESSION.fireAllRules();
    }

    public void runRules(ArrayList<Object> objs, String group){
        for(Object obj: objs){
            KIE_SESSION.insert(obj);
        }
        runRules(group);
    }

    public void runRules(String group){
        System.out.println("start to run rules");
        KIE_SESSION.getAgenda().getAgendaGroup(group).setFocus();
        KIE_SESSION.fireAllRules();
        System.out.println("end to run rules");

        FactType msgFactType = msgFactType(CONTAINER.getKieBase());
        String message = (String)msgFactType.get(msg, "str");
        int times = (Integer)msgFactType.get(msg, "times");
        String content = (String)msgFactType.get(msg, "content");
        System.out.println("times:" + times);
        System.out.println("content:" + content);
        System.out.println("str:" + message);
        
    }

    /**
     * Applies the loaded Drools rules to a given String.
     *
     * @param value the String to which the rules should be applied
     * @return the String after the rule has been applied
     * @throws Exception 
     */
    public String applyKpiFilterRule(String value) {
    	System.out.println("start to apply rule with fact: " + value);
        try{
            FactType factType = factType(CONTAINER.getKieBase());
            Object kpi = makeApplicant(factType, value);
            runRules(kpi, "kpi-filter");
            boolean skipped = (Boolean)factType.get(kpi, "skipped");
            
            System.out.println("skipped："+ skipped);
            if(skipped){
                return null;
            }else{
                return factType.get(kpi, "group") + " " + factType.get(kpi, "name") + " " + factType.get(kpi, "value"); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public String applyKpiComputeRule(String value) {
        try{
            System.out.println("start to apply compute rule with fact: " + value);
            FactType factType = factType(CONTAINER.getKieBase());
            Object kpi = makeApplicant(factType, value);
            runRules(kpi, "kpi-compute");
            return factType.get(kpi, "group") + " " + factType.get(kpi, "name") + " " + factType.get(kpi, "value");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public String applyNotificationRule(String value) {
        try{
            System.out.println("start to apply notification rule with fact: " + value);
            FactType factType = factType(CONTAINER.getKieBase());
            Object event = makeApplicant(factType, value);
            ArrayList<Object> objs = new ArrayList<Object>();
            objs.add(event);
            runRules(objs, "notification");
            //FactHandle eventFactHandle = KIE_SESSION.getFactHandle(event);
            //KIE_SESSION.delete(eventFactHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMsg() {
        try{

            FactType msgFactType = msgFactType(CONTAINER.getKieBase());
            //boolean ready = (boolean)msgFactType.get(msg, "ready");
            String message = (String)msgFactType.get(msg, "str");
            String content = (String)msgFactType.get(msg, "content");
            if(message != null && (message.length() > 0) && (content != null) && (content.length()>0)) {
                FactHandle msgFactHandle = KIE_SESSION.getFactHandle(msg);
                msgFactType.set(msg, "str", "");
                msgFactType.set(msg, "content", "");
                KIE_SESSION.update(msgFactHandle, msg);
            }
            runRules("notification");
            return message;
        } catch  (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    
    private static Object makeApplicant(FactType factType, String input) throws Exception{
        Object kpi = factType.newInstance();
        String[] components = input.split(" ");
        factType.set(kpi, "group", components[0]);
        factType.set(kpi, "name", components[1]);
        factType.set(kpi, "value", Double.valueOf(components[2]));
        factType.set(kpi, "skipped", false);
        return kpi;
    }
    protected static FactType factType(KieBase base) {
        FactType factType = base.getFactType("uk.co.hadoopathome.kafka.kafka_streams_drools", "EVENT");
        return factType;
    }

    private static Object makeMsgApplicant(FactType factType) throws Exception{
        Object msg = factType.newInstance();
        factType.set(msg, "content", "");
        factType.set(msg, "str", "");
        return msg;
    }

    protected static FactType msgFactType(KieBase base) {
        FactType factType = base.getFactType("uk.co.hadoopathome.kafka.kafka_streams_drools", "MSG");
        return factType;
    }

}

