package bpm.fm.api.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "fm_fact_table_axis")
public class FactTableAxis implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "fact_table_id")
	private int factTableId;
	
	@Column (name = "axis_id")
	private int axisId;

	@Column (name = "column_id")
	private String columnId;
	
	@Column (name = "objective_column_Id")
	private String objectiveColumnId;
	
	@Transient
	private Axis axis;

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

	public int getAxisId() {
		return axisId;
	}

	public void setAxisId(int axisId) {
		this.axisId = axisId;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis;
	}

	public String getObjectiveColumnId() {
		return objectiveColumnId;
	}

	public void setObjectiveColumnId(String objectiveColumnId) {
		this.objectiveColumnId = objectiveColumnId;
	}

}
