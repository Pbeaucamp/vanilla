package bpm.gateway.core.veolia;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Access(AccessType.FIELD)
@Table(name = "xml_log")
public class LogInsertXML implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "xml_insert_etl_log_id_seq")
	@SequenceGenerator(name = "xml_insert_etl_log_id_seq", sequenceName = "xml_insert_etl_log_id_seq", allocationSize = 1)
	@Column(name = "id")
	private int id;
	@Column(name = "table_name")
	private String tableName;
	@Column(name = "contract_name")
	private String contractName;
	@Column(name = "load_date")
	private Date dateChargement;
	@Column(name = "lines_in")
	private int nbLigneIn;
	@Column(name = "lines_out")
	private int nbLigneOut;
	@Column(name = "lines_error")
	private int nbLigneError;
	@Column(name = "id_chg")
	private Integer idChg;

	public LogInsertXML() {
		dateChargement = new Date();
	}

	public LogInsertXML(String tableName, String contractName, int idChg) {
		this();
		this.tableName = tableName;
		this.contractName = contractName;
		this.idChg = idChg;
	}

	public int getId() {
		return id;
	}

	public String getTableName() {
		return tableName;
	}

	public String getContractName() {
		return contractName;
	}

	public Date getDateChargement() {
		return dateChargement;
	}

	public int getNbLigneIn() {
		return nbLigneIn;
	}

	public void incrementIn() {
		this.nbLigneIn++;
	}

	public int getNbLigneOut() {
		return nbLigneOut;
	}

	public void incrementOut() {
		this.nbLigneOut++;
	}

	public int getNbLigneError() {
		return nbLigneError;
	}

	public void incrementError() {
		this.nbLigneError++;
	}

	public Integer getIdChg() {
		return idChg;
	}
}
