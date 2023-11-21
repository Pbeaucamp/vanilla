package bpm.document.management.core.model.aklademat;

import java.io.Serializable;

public class FournisseurCegid implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String codeTiers;
	private String raisonSociale;
	private String siret;
	private int typeTiers;
	
	public FournisseurCegid() {
		super();
	}

	public FournisseurCegid(String codeTiers, String raisonSociale, String siret, int typeTiers) {
		super();
		this.codeTiers = codeTiers;
		this.raisonSociale = raisonSociale;
		this.siret = siret;
		this.typeTiers = typeTiers;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodeTiers() {
		return codeTiers;
	}

	public void setCodeTiers(String codeTiers) {
		this.codeTiers = codeTiers;
	}

	public String getRaisonSociale() {
		return raisonSociale;
	}

	public void setRaisonSociale(String raisonSociale) {
		this.raisonSociale = raisonSociale;
	}

	public String getSiret() {
		return siret;
	}

	public void setSiret(String siret) {
		this.siret = siret;
	}

	public int getTypeTiers() {
		return typeTiers;
	}

	public void setTypeTiers(int typeTiers) {
		this.typeTiers = typeTiers;
	}

	
	
}
