package bpm.fm.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "fm_fact_table_objectives")
public class FactTableObjectives implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "fact_table_id")
	private int factTableId;
	
	@Column (name = "objective_column")
	private String objectiveColumn;
	
	@Column (name = "table_name")
	private String tableName;
	
	@Column (name = "min_column")
	private String minColumn;
	
	@Column (name = "max_column")
	private String maxColumn;
	
	@Column (name = "date_column")
	private String dateColumn;
	
	@Column (name = "tolerance")
	private String tolerance;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFactTableId() {
		return factTableId;
	}

	public void setFactTableId(int factTableId) {
		this.factTableId = factTableId;
	}

	public String getObjectiveColumn() {
		return objectiveColumn;
	}

	public void setObjectiveColumn(String objectiveColumn) {
		this.objectiveColumn = objectiveColumn;
	}

	public String getMinColumn() {
		return minColumn;
	}

	public void setMinColumn(String minColumn) {
		this.minColumn = minColumn;
	}

	public String getMaxColumn() {
		return maxColumn;
	}

	public void setMaxColumn(String maxColumn) {
		this.maxColumn = maxColumn;
	}

	public String getDateColumn() {
		return dateColumn;
	}

	public void setDateColumn(String dateColumn) {
		this.dateColumn = dateColumn;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTolerance() {
		return tolerance;
	}

	public void setTolerance(String tolerance) {
		this.tolerance = tolerance;
	}
	
}
