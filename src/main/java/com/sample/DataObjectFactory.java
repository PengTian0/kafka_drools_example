package com.sample;

import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;


public class DataObjectFactory {


		public static Object makeInstance(FactType factType, String value) throws Exception{
			Object instance01 = factType.newInstance();
			
			factType.set(instance01, "content", value);
			return instance01;
		}
		public static FactType get(KieBase base) {
			FactType factType = base.getFactType("uk.co.hadoopathome.kafka.kafka_streams_drools", "Message");
			return factType;
		}
	
}

