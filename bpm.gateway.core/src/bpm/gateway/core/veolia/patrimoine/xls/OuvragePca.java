package bpm.gateway.core.veolia.patrimoine.xls;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import bpm.gateway.core.veolia.patrimoine.PatrimoineDAO;

@Entity
@Access(AccessType.FIELD)
@Table (name = PatrimoineDAO.OUVRAGE_PCA)
public class OuvragePca {

	@Id
	@Column(name = "ouvragepca_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String ouvragepcaOuvragesocleId;

	//TODO: Liste
	@Transient
	private String ouvragepcaTypeposte;

	@Column(name = "ouvragepca_commentaire")
	private String ouvragepcaCommentaire;

	@Column(name = "ouvragepca_planassocie")
	private String ouvragepcaPlanassocie;
	
	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepca_ouvragesocle_id")
	public String getOuvragepcaOuvragesocleId() {
		if (ouvragepcaOuvragesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PCA + " - Champs ouvragepca_ouvragesocle_id - Valeur 'Null' non permise.");
		}
		return ouvragepcaOuvragesocleId;
	}

	public void setOuvragepcaOuvragesocleId(String ouvragepcaOuvragesocleId) {
		this.ouvragepcaOuvragesocleId = ouvragepcaOuvragesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "ouvragepca_typeposte")
	public String getOuvragepcaTypeposte() {
		if (ouvragepcaTypeposte == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.OUVRAGE_PCA + " - Champs ouvragepca_typeposte - Valeur 'Null' non permise.");
		}
		return ouvragepcaTypeposte;
	}

	public void setOuvragepcaTypeposte(String ouvragepcaTypeposte) {
		this.ouvragepcaTypeposte = ouvragepcaTypeposte;
	}

	public String getOuvragepcaCommentaire() {
		return ouvragepcaCommentaire;
	}

	public void setOuvragepcaCommentaire(String ouvragepcaCommentaire) {
		this.ouvragepcaCommentaire = ouvragepcaCommentaire;
	}
	
	public String getOuvragepcaPlanassocie() {
		return ouvragepcaPlanassocie;
	}
	
	public void setOuvragepcaPlanassocie(String ouvragepcaPlanassocie) {
		this.ouvragepcaPlanassocie = ouvragepcaPlanassocie;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
}
