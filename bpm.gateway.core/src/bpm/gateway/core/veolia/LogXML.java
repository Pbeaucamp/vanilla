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
@Table (name = "etl_log")
public class LogXML implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="xml_etl_log_id_seq")
    @SequenceGenerator(name="xml_etl_log_id_seq", sequenceName="xml_etl_log_id_seq", allocationSize=1)
	@Column(name = "id")
	private int id;
	@Column(name = "table_name")
	private String tableName;
	@Column(name = "contract_name")
	private String contractName;
	@Column(name = "load_date")
	private Date dateChargement;
	@Column(name = "line_id")
	private String lineId;
	@Column(name = "error_messages", length = 10000000)
	private String errorMessage;
	@Column(name = "id_chg")
	private Integer idChg;
	
	public LogXML() {
		dateChargement = new Date();
	}
	
	public LogXML(String tableName, String contractName, String lineId, String errorMessage, Integer idChg) {
		this();
		this.tableName = tableName;
		this.contractName = contractName;
		this.lineId = lineId;
		this.errorMessage = errorMessage;
		this.idChg = idChg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public Date getDateChargement() {
		return dateChargement;
	}

	public void setDateChargement(Date dateChargement) {
		this.dateChargement = dateChargement;
	}
	
	public String getLineId() {
		return lineId;
	}
	
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setIdChg(Integer idChg) {
		this.idChg = idChg;
	}

}
