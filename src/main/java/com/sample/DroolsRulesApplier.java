package com.sample;

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


public class DroolsRulesApplier {
	
	private static KieSession KIE_SESSION;
	private static KieContainer CONTAINER;

    public DroolsRulesApplier() throws Exception {
        //System.out.println(sessionName);
        KIE_SESSION = DroolsSessionFactory.createDroolsSession();
        CONTAINER =  DroolsSessionFactory.createKieContainer();
        //KIE_SESSION = DroolsUtil.getStatefulKnowledgeSession(sessionName);
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
            KIE_SESSION.getAgenda().getAgendaGroup("kpi-filter").setFocus();
            KIE_SESSION.insert(kpi);
            KIE_SESSION.fireAllRules();
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
        System.out.println("start to apply compute rule with fact: " + value);
        try{
            FactType factType = factType(CONTAINER.getKieBase());
            Object kpi = makeApplicant(factType, value);
            KIE_SESSION.getAgenda().getAgendaGroup("kpi-compute").setFocus(); 
            KIE_SESSION.insert(kpi);
            KIE_SESSION.fireAllRules();
            return factType.get(kpi, "group") + " " + factType.get(kpi, "name") + " " + factType.get(kpi, "value");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
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
                FactType factType = base.getFactType("com.maglev.ruleengine.drools_dynamic_test", "KPI");
                return factType;
        }

}

