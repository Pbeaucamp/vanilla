package bpm.es.web.shared.beans;

import java.util.Date;

public class Person {

	private String id;
	private Dossier dossier;
	
	private Parameter type;

	private String lastName;
	private String firstName;

	private Date birthDate;
	private Parameter sexe;
	private Parameter civilite;

	private Parameter familySituation;
	private Parameter studyLevel;
	private Parameter studyState;
	private Parameter diploma;

	private String comment;

	private Family family;

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
	
	public Parameter getType() {
		return type;
	}
	
	public void setType(Parameter type) {
		this.type = type;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Parameter getSexe() {
		return sexe;
	}

	public void setSexe(Parameter sexe) {
		this.sexe = sexe;
	}

	public Parameter getCivilite() {
		return civilite;
	}

	public void setCivilite(Parameter civilite) {
		this.civilite = civilite;
	}

	public Parameter getFamilySituation() {
		return familySituation;
	}

	public void setFamilySituation(Parameter familySituation) {
		this.familySituation = familySituation;
	}

	public Parameter getStudyLevel() {
		return studyLevel;
	}

	public void setStudyLevel(Parameter studyLevel) {
		this.studyLevel = studyLevel;
	}

	public Parameter getStudyState() {
		return studyState;
	}

	public void setStudyState(Parameter studyState) {
		this.studyState = studyState;
	}

	public Parameter getDiploma() {
		return diploma;
	}

	public void setDiploma(Parameter diploma) {
		this.diploma = diploma;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

}
