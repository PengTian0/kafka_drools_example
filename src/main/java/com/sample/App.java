package com.sample;


import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.runtime.StatelessKieSession;


public class App {
    public static void main(String[] args) throws Exception{
	//ReleaseIdImpl releaseId = new ReleaseIdImpl("com.pachiraframework","driving-license","1.0");

        String groupId = "com.maglev.ruleengine";
        String artifactId = "Drools-dynamic-test";
        String version = "LATEST";

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);
        KieContainer container = ks.newKieContainer(releaseId);

		//ReleaseIdImpl releaseId = new ReleaseIdImpl("com.maglev.ruleengine","Drools-dynamic-test","LATEST");
		//KieServices ks = KieServices.Factory.get();
		//KieContainer container = ks.newKieContainer(releaseId);
		KieScanner scanner = ks.newKieScanner(container);
		scanner.start(1000);
		StatelessKieSession session = container.newStatelessKieSession();
		//StatelessKieSession session = DroolsSessionFactory.createDroolsSession();
		//KieContainer container =  DroolsSessionFactory.createKieContainer();
		for(int i = 0;i < 100;i++) {
			//FactType factType = DataObjectFactory.get(container.getKieBase());
			//Object applicant = DataObjectFactory.makeInstance(factType, "abc");
			//applicant= DroolsSessionFactory.runRule(applicant);
			FactType factType = factType(container.getKieBase());
			Object applicant = makeApplicant(factType);
			session.execute(applicant);
			System.out.println("message："+factType.get(applicant, "content"));
			
			Thread.sleep(5000);
		}
	}
	private static Object makeApplicant(FactType factType) throws Exception{
		Object message = factType.newInstance();
		
		factType.set(message, "content", "abc");
		return message;
	}
	protected static FactType factType(KieBase base) {
		FactType factType = base.getFactType("com.maglev.ruleengine.drools_dynamic_test", "Message");
		return factType;
	}
}

