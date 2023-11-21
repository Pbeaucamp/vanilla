package bpm.vanilla.platform.core.beans.data;

import java.io.Serializable;

public class D4CTypes implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private boolean facette;
	private boolean facetteMultiple;
	private boolean tableau;
	private boolean infobulle;
	private boolean tri;
	private boolean datePonctuelle;
	private boolean dateDebut;
	private boolean dateFin;
	private boolean images;
	private boolean nuageDeMot;
	private boolean nuageDeMotNombre;
	private boolean dateHeure;
	private boolean friseLibelle;
	private boolean friseDescription;
	private boolean friseDate;
	private boolean displayPopup = true;

	public boolean isFacette() {
		return facette;
	}

	public void setFacette(boolean facette) {
		this.facette = facette;
	}

	public boolean isFacetteMultiple() {
		return facetteMultiple;
	}

	public void setFacetteMultiple(boolean facetteMultiple) {
		this.facetteMultiple = facetteMultiple;
	}

	public boolean isTableau() {
		return tableau;
	}

	public void setTableau(boolean tableau) {
		this.tableau = tableau;
	}

	public boolean isInfobulle() {
		return infobulle;
	}

	public void setInfobulle(boolean infobulle) {
		this.infobulle = infobulle;
	}

	public boolean isTri() {
		return tri;
	}

	public void setTri(boolean tri) {
		this.tri = tri;
	}

	public boolean isDatePonctuelle() {
		return datePonctuelle;
	}

	public void setDatePonctuelle(boolean datePonctuelle) {
		this.datePonctuelle = datePonctuelle;
	}

	public boolean isDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(boolean dateDebut) {
		this.dateDebut = dateDebut;
	}

	public boolean isDateFin() {
		return dateFin;
	}

	public void setDateFin(boolean dateFin) {
		this.dateFin = dateFin;
	}

	public boolean isImages() {
		return images;
	}

	public void setImages(boolean images) {
		this.images = images;
	}

	public boolean isNuageDeMot() {
		return nuageDeMot;
	}

	public void setNuageDeMot(boolean nuageDeMot) {
		this.nuageDeMot = nuageDeMot;
	}

	public boolean isNuageDeMotNombre() {
		return nuageDeMotNombre;
	}

	public void setNuageDeMotNombre(boolean nuageDeMotNombre) {
		this.nuageDeMotNombre = nuageDeMotNombre;
	}

	public boolean isDateHeure() {
		return dateHeure;
	}

	public void setDateHeure(boolean dateHeure) {
		this.dateHeure = dateHeure;
	}

	public boolean isFriseLibelle() {
		return friseLibelle;
	}

	public void setFriseLibelle(boolean friseLibelle) {
		this.friseLibelle = friseLibelle;
	}

	public boolean isFriseDescription() {
		return friseDescription;
	}

	public void setFriseDescription(boolean friseDescription) {
		this.friseDescription = friseDescription;
	}

	public boolean isFriseDate() {
		return friseDate;
	}

	public void setFriseDate(boolean friseDate) {
		this.friseDate = friseDate;
	}
	
	public boolean isDisplayPopup() {
		return displayPopup;
	}
	
	public void setDisplayPopup(boolean displayPopup) {
		this.displayPopup = displayPopup;
	}
}
