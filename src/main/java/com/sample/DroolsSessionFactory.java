package com.sample;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieSession;
import org.drools.compiler.kie.builder.impl.KieServicesImpl;

public class DroolsSessionFactory {
    private static KieSession session;
    private static KieContainer container;
    private static KieServices ks = KieServices.Factory.get();
	
    protected static KieContainer createKieContainer(PropertiesConfiguration properties) throws Exception{
        if(container == null) {
            String groupId = properties.getString("group");
            String artifactId = properties.getString("artifact");
            String version = properties.getString("rulesVersion");
            ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);
            container = ks.newKieContainer(releaseId);
        }
        return container;
    }

    protected static KieSession createDroolsSession(KieContainer container) throws Exception{
        //container = createKieContainer();
        KieScanner kscanner = ks.newKieScanner(container);
        kscanner.start(5000);
        KieSession ksession = container.newKieSession();
        return ksession;
    }	
}

