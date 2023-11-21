package bpm.es.web.shared.beans;

import java.util.Date;

public class Demand {

	private String id;

	private Dossier dossier;
	private Person manager;

	private int nbDemand;

	private Date creationDate;
	private Date admissionDate;
	private Date releaseDate;

	private Person person;
	private Parameter ageRange;

	private String geoOrigin;
	private String geoOriginDetail;

	private Parameter prescriptorType;
	private Parameter mesureType;
	private String mesureDetail;

	private String typeOrigin;
	private String typeOriginDetail;

	private String knownSituation;
	private String knownSituationDetail;

	private Parameter reasonRequest;

	private Parameter response;
	private String responseDetail;
	private String responseDetailComplement;
	private String observation;

	private Parameter orientation;
	private String orientationDetail;

	private int presenceDuration;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Dossier getDossier() {
		return dossier;
	}

	public void setDossier(Dossier dossier) {
		this.dossier = dossier;
	}

	public Person getManager() {
		return manager;
	}

	public void setManager(Person manager) {
		this.manager = manager;
	}

	public int getNbDemand() {
		return nbDemand;
	}

	public void setNbDemand(int nbDemand) {
		this.nbDemand = nbDemand;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Parameter getAgeRange() {
		return ageRange;
	}

	public void setAgeRange(Parameter ageRange) {
		this.ageRange = ageRange;
	}

	public String getGeoOrigin() {
		return geoOrigin;
	}

	public void setGeoOrigin(String geoOrigin) {
		this.geoOrigin = geoOrigin;
	}

	public String getGeoOriginDetail() {
		return geoOriginDetail;
	}

	public void setGeoOriginDetail(String geoOriginDetail) {
		this.geoOriginDetail = geoOriginDetail;
	}

	public Parameter getPrescriptorType() {
		return prescriptorType;
	}

	public void setPrescriptorType(Parameter prescriptorType) {
		this.prescriptorType = prescriptorType;
	}

	public Parameter getMesureType() {
		return mesureType;
	}

	public void setMesureType(Parameter mesureType) {
		this.mesureType = mesureType;
	}

	public String getMesureDetail() {
		return mesureDetail;
	}

	public void setMesureDetail(String mesureDetail) {
		this.mesureDetail = mesureDetail;
	}

	public String getTypeOrigin() {
		return typeOrigin;
	}

	public void setTypeOrigin(String typeOrigin) {
		this.typeOrigin = typeOrigin;
	}

	public String getTypeOriginDetail() {
		return typeOriginDetail;
	}

	public void setTypeOriginDetail(String typeOriginDetail) {
		this.typeOriginDetail = typeOriginDetail;
	}

	public String getKnownSituation() {
		return knownSituation;
	}

	public void setKnownSituation(String knownSituation) {
		this.knownSituation = knownSituation;
	}

	public String getKnownSituationDetail() {
		return knownSituationDetail;
	}

	public void setKnownSituationDetail(String knownSituationDetail) {
		this.knownSituationDetail = knownSituationDetail;
	}

	public Parameter getReasonRequest() {
		return reasonRequest;
	}

	public void setReasonRequest(Parameter reasonRequest) {
		this.reasonRequest = reasonRequest;
	}

	public Parameter getResponse() {
		return response;
	}

	public void setResponse(Parameter response) {
		this.response = response;
	}

	public String getResponseDetail() {
		return responseDetail;
	}

	public void setResponseDetail(String responseDetail) {
		this.responseDetail = responseDetail;
	}

	public String getResponseDetailComplement() {
		return responseDetailComplement;
	}

	public void setResponseDetailComplement(String responseDetailComplement) {
		this.responseDetailComplement = responseDetailComplement;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public Parameter getOrientation() {
		return orientation;
	}

	public void setOrientation(Parameter orientation) {
		this.orientation = orientation;
	}

	public String getOrientationDetail() {
		return orientationDetail;
	}

	public void setOrientationDetail(String orientationDetail) {
		this.orientationDetail = orientationDetail;
	}

	public int getPresenceDuration() {
		return presenceDuration;
	}

	public void setPresenceDuration(int presenceDuration) {
		this.presenceDuration = presenceDuration;
	}
}
