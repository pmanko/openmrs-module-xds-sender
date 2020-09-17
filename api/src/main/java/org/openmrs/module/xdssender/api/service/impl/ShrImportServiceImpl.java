package org.openmrs.module.xdssender.api.service.impl;

import java.io.IOException;

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.dcm4chee.xds2.common.exception.XDSException;
import org.hl7.fhir.r4.model.Bundle;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.errorhandling.CcdErrorHandlingService;
import org.openmrs.module.xdssender.api.errorhandling.ErrorHandlingService;
import org.openmrs.module.xdssender.api.errorhandling.RetrieveAndSaveCcdParameters;
import org.openmrs.module.xdssender.api.service.XdsImportService;
import org.openmrs.module.xdssender.api.xds.ShrRetriever;
import org.openmrs.module.xdssender.api.xds.XdsRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("xdsSender.ShrImportService")
public class ShrImportServiceImpl implements XdsImportService {

	private static final Log LOGGER = LogFactory.getLog(ShrImportServiceImpl.class);

	private static final String ECID_NAME = "ECID";

	@Autowired
	private XdsSenderConfig config;

	@Autowired
	private ShrRetriever shrRetriever;

    private FhirContext fhirContext = FhirContext.forR4();

	@Override
	public Ccd retrieveCCD(Patient patient) throws XDSException {
		Ccd ccd = null;

		String patientEcid = extractPatientEcid(patient);

		try {
			Bundle result = shrRetriever.sendRetrieveCCD(patientEcid);

			ccd = new Ccd();
			ccd.setPatient(patient);
			ccd.setDocument(fhirContext.newJsonParser().encodeResourceToString(result));

		} catch (Exception e) {
			LOGGER.error("Unable to load CCD content", e);
		}

		return ccd;
	}

	private String extractPatientEcid(Patient patient) {
		String patientEcid = null;
		for (PatientIdentifier patientIdentifier : patient.getIdentifiers()) {
			if (patientIdentifier.getIdentifierType().getName().equals(ECID_NAME)) {
				patientEcid = patientIdentifier.getIdentifier();
			}
		}
		return patientEcid;
	}
}
