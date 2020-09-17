package org.openmrs.module.xdssender.api.xds;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.Bundle;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("xdssender.ShrRetriever")
public class ShrRetriever {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShrRetriever.class);

	@Autowired
	private XdsSenderConfig config;

	@Autowired
	@Qualifier("fhirContext")
	private FhirContext fhirContext;

	public Bundle sendRetrieveCCD(String patientEcid)  {
		try {
			// TODO use client.
			IGenericClient client = fhirContext.getRestfulClientFactory().newGenericClient(config.getExportCcdEndpoint());

			return client.search().byUrl("/Patient?_id=645196&_revinclude=MedicationStatement:subject&_include:iterate=MedicationStatement:medication&_revinclude=AllergyIntolerance:patient&_revinclude=Condition:subject&_revinclude=Immunization:patient&_revinclude=Procedure:subject&_id=8c12ad7e-4379-4f7d-9f16-db9374a3b7e9").returnBundle(Bundle.class).execute();
		} catch (Exception ex) {
			LOGGER.error("Error when fetching ccd", ex);
			return null;
		}
	}
}
