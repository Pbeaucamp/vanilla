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
@Table (name = "date_chargement")
public class DateChargement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="date_chargement_id_seq")
    @SequenceGenerator(name="date_chargement_id_seq", sequenceName="date_chargement_id_seq", allocationSize=1)
	@Column(name = "datechg_id")
	private int id;
	@Column(name = "datechg_nometl")
	private String etlName;
	@Column(name = "datechg_fichiersource")
	private String fileName;
	@Column(name = "datechg_tabledestination")
	private String destinationTable;
	@Column(name = "datechg_date")
	private Date loadingDate;
	
	public DateChargement(String etlName, String fileName, String destinationTable) {
		this.etlName = etlName;
		this.fileName = fileName;
		this.destinationTable = destinationTable;
		this.loadingDate = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEtlName() {
		return etlName;
	}

	public void setEtlName(String etlName) {
		this.etlName = etlName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDestinationTable() {
		return destinationTable;
	}

	public void setDestinationTable(String destinationTable) {
		this.destinationTable = destinationTable;
	}

	public Date getLoadingDate() {
		return loadingDate;
	}

	public void setLoadingDate(Date loadingDate) {
		this.loadingDate = loadingDate;
	}
}
