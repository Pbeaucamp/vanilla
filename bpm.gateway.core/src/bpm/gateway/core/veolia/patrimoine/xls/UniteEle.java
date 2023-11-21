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
@Table (name = PatrimoineDAO.UNITE_ELE)
public class UniteEle {

	@Id
	@Column(name = "uniteele_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Transient
	private String uniteeleUnitesocleId;

	//TODO: Liste
	@Transient
	private String uniteeleTypecontrat;

	@Transient
	private String uniteeleLocalisationtransfo;

	@Transient
	private String uniteeleLocalisationtransfosec;

	@Transient
	private Double uniteelePuissancesouscrite;

	@Transient
	private String uniteeleNumcontrat;

	@Transient
	private String uniteelePostesource1;

	@Transient
	private String uniteelePostesource2;

	@Transient
	private Double uniteelePuisouscritep;

	@Transient
	private Double uniteelePuisouscritehph;

	@Transient
	private Double uniteelePuisouscritehch;

	@Transient
	private Double uniteelePuisouscritehpdemisaison;

	@Transient
	private Double uniteelePuisouscritehcdemisaison;

	@Transient
	private Double uniteelePuisouscritehpete;

	@Transient
	private Double uniteelePuisouscritehcete;

	@Transient
	private Double uniteelePuisouscriteja;

	@Transient
	private String uniteeleLocalisationarmoirecommande;

	@Column(name = "id_chg")
	private Integer idChg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_unitesocle_id")
	public String getUniteeleUnitesocleId() {
		if (uniteeleUnitesocleId == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_unitesocle_id - Valeur 'Null' non permise.");
		}
		return uniteeleUnitesocleId;
	}

	public void setUniteeleUnitesocleId(String uniteeleUnitesocleId) {
		this.uniteeleUnitesocleId = uniteeleUnitesocleId;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_typecontrat")
	public String getUniteeleTypecontrat() {
		if (uniteeleTypecontrat == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_typecontrat - Valeur 'Null' non permise.");
		}
		return uniteeleTypecontrat;
	}

	public void setUniteeleTypecontrat(String uniteeleTypecontrat) {
		this.uniteeleTypecontrat = uniteeleTypecontrat;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_localisationtransfo")
	public String getUniteeleLocalisationtransfo() {
		if (uniteeleLocalisationtransfo == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_localisationtransfo - Valeur 'Null' non permise.");
		}
		return uniteeleLocalisationtransfo;
	}

	public void setUniteeleLocalisationtransfo(String uniteeleLocalisationtransfo) {
		this.uniteeleLocalisationtransfo = uniteeleLocalisationtransfo;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_localisationtransfosec")
	public String getUniteeleLocalisationtransfosec() {
		if (uniteeleLocalisationtransfosec == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_localisationtransfosec - Valeur 'Null' non permise.");
		}
		return uniteeleLocalisationtransfosec;
	}

	public void setUniteeleLocalisationtransfosec(String uniteeleLocalisationtransfosec) {
		this.uniteeleLocalisationtransfosec = uniteeleLocalisationtransfosec;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_puissancesouscrite")
	public Double getUniteelePuissancesouscrite() {
		if (uniteelePuissancesouscrite == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_puissancesouscrite - Valeur 'Null' non permise.");
		}
		return uniteelePuissancesouscrite;
	}

	public void setUniteelePuissancesouscrite(Double uniteelePuissancesouscrite) {
		this.uniteelePuissancesouscrite = uniteelePuissancesouscrite;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_numcontrat")
	public String getUniteeleNumcontrat() {
		if (uniteeleNumcontrat == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_numcontrat - Valeur 'Null' non permise.");
		}
		return uniteeleNumcontrat;
	}

	public void setUniteeleNumcontrat(String uniteeleNumcontrat) {
		this.uniteeleNumcontrat = uniteeleNumcontrat;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_postesource1")
	public String getUniteelePostesource1() {
		if (uniteelePostesource1 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_postesource1 - Valeur 'Null' non permise.");
		}
		return uniteelePostesource1;
	}

	public void setUniteelePostesource1(String uniteelePostesource1) {
		this.uniteelePostesource1 = uniteelePostesource1;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_postesource2")
	public String getUniteelePostesource2() {
		if (uniteelePostesource2 == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_postesource2 - Valeur 'Null' non permise.");
		}
		return uniteelePostesource2;
	}

	public void setUniteelePostesource2(String uniteelePostesource2) {
		this.uniteelePostesource2 = uniteelePostesource2;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_puisouscritep")
	public Double getUniteelePuisouscritep() {
		if (uniteelePuisouscritep == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_puisouscritep - Valeur 'Null' non permise.");
		}
		return uniteelePuisouscritep;
	}

	public void setUniteelePuisouscritep(Double uniteelePuisouscritep) {
		this.uniteelePuisouscritep = uniteelePuisouscritep;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_puisouscritehph")
	public Double getUniteelePuisouscritehph() {
		if (uniteelePuisouscritehph == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_puisouscritehph - Valeur 'Null' non permise.");
		}
		return uniteelePuisouscritehph;
	}

	public void setUniteelePuisouscritehph(Double uniteelePuisouscritehph) {
		this.uniteelePuisouscritehph = uniteelePuisouscritehph;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_puisouscritehch")
	public Double getUniteelePuisouscritehch() {
		if (uniteelePuisouscritehch == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_puisouscritehch - Valeur 'Null' non permise.");
		}
		return uniteelePuisouscritehch;
	}

	public void setUniteelePuisouscritehch(Double uniteelePuisouscritehch) {
		this.uniteelePuisouscritehch = uniteelePuisouscritehch;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_puisouscritehpdemisaison")
	public Double getUniteelePuisouscritehpdemisaison() {
		if (uniteelePuisouscritehpdemisaison == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_puisouscritehpdemisaison - Valeur 'Null' non permise.");
		}
		return uniteelePuisouscritehpdemisaison;
	}

	public void setUniteelePuisouscritehpdemisaison(Double uniteelePuisouscritehpdemisaison) {
		this.uniteelePuisouscritehpdemisaison = uniteelePuisouscritehpdemisaison;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_puisouscritehcdemisaison")
	public Double getUniteelePuisouscritehcdemisaison() {
		if (uniteelePuisouscritehcdemisaison == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_puisouscritehcdemisaison - Valeur 'Null' non permise.");
		}
		return uniteelePuisouscritehcdemisaison;
	}

	public void setUniteelePuisouscritehcdemisaison(Double uniteelePuisouscritehcdemisaison) {
		this.uniteelePuisouscritehcdemisaison = uniteelePuisouscritehcdemisaison;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_puisouscritehpete")
	public Double getUniteelePuisouscritehpete() {
		if (uniteelePuisouscritehpete == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_puisouscritehpete - Valeur 'Null' non permise.");
		}
		return uniteelePuisouscritehpete;
	}

	public void setUniteelePuisouscritehpete(Double uniteelePuisouscritehpete) {
		this.uniteelePuisouscritehpete = uniteelePuisouscritehpete;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_puisouscritehcete")
	public Double getUniteelePuisouscritehcete() {
		if (uniteelePuisouscritehcete == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_puisouscritehcete - Valeur 'Null' non permise.");
		}
		return uniteelePuisouscritehcete;
	}

	public void setUniteelePuisouscritehcete(Double uniteelePuisouscritehcete) {
		this.uniteelePuisouscritehcete = uniteelePuisouscritehcete;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_puisouscriteja")
	public Double getUniteelePuisouscriteja() {
		if (uniteelePuisouscriteja == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_puisouscriteja - Valeur 'Null' non permise.");
		}
		return uniteelePuisouscriteja;
	}

	public void setUniteelePuisouscriteja(Double uniteelePuisouscriteja) {
		this.uniteelePuisouscriteja = uniteelePuisouscriteja;
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "uniteele_localisationarmoirecommande")
	public String getUniteeleLocalisationarmoirecommande() {
		if (uniteeleLocalisationarmoirecommande == null) {
			throw new IllegalArgumentException("Table " + PatrimoineDAO.UNITE_ELE + " - Champs uniteele_localisationarmoirecommande - Valeur 'Null' non permise.");
		}
		return uniteeleLocalisationarmoirecommande;
	}

	public void setUniteeleLocalisationarmoirecommande(String uniteeleLocalisationarmoirecommande) {
		this.uniteeleLocalisationarmoirecommande = uniteeleLocalisationarmoirecommande;
	}

	public Integer getIdChg() {
		return idChg;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}
	
	
}
