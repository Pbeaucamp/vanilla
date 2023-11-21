package bpm.fm.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.data.Datasource;

@Entity
@Table (name = "fm_fact_table")
public class FactTable extends AbstractFactTable implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String PERIODICITY_YEARLY = "Yearly";
	public static final String PERIODICITY_BIANNUAL = "Biannual";
	public static final String PERIODICITY_QUARTERLY = "Quarterly";
	public static final String PERIODICITY_MONTHLY = "Monthly";
	public static final String PERIODICITY_WEEKLY = "Weekly";
	public static final String PERIODICITY_DAILY = "Daily";
	
	public static final String PERIODICITY_HOURLY = "Hourly";
	public static final String PERIODICITY_MINUTE = "Minute";
	
	public static final String[] PERIODICITIES = {
		PERIODICITY_YEARLY, 
//		PERIODICITY_BIANNUAL, 
//		PERIODICITY_QUARTERLY, 
		PERIODICITY_MONTHLY, 
//		PERIODICITY_WEEKLY, 
		PERIODICITY_DAILY,
		PERIODICITY_HOURLY,
		PERIODICITY_MINUTE};
	
	public static List<String> PERIODICITIES_LIST;
	
	static {
		PERIODICITIES_LIST = new ArrayList<String>();
		PERIODICITIES_LIST.add(PERIODICITY_YEARLY);
		PERIODICITIES_LIST.add(PERIODICITY_MONTHLY);
		PERIODICITIES_LIST.add(PERIODICITY_DAILY);
		PERIODICITIES_LIST.add(PERIODICITY_HOURLY);
		PERIODICITIES_LIST.add(PERIODICITY_MINUTE);
	}
	 
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "metric_id")
	private int metricId;
	
	@Column (name = "table_name")
	private String tableName;
	
	@Column (name = "value_column")
	private String valueColumn;
	
	@Column (name = "datasource_id")
	private int datasourceId;
	
	@Column (name = "date_column")
	private String dateColumn;
	
	@Column (name = "periodicity")
	private String periodicity = PERIODICITY_YEARLY;
	
	@Transient
	private List<FactTableAxis> factTableAxis;
	
	@Transient
	private FactTableObjectives objectives;
	
	@Transient
	private Datasource datasource;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMetricId() {
		return metricId;
	}

	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getValueColumn() {
		return valueColumn;
	}

	public void setValueColumn(String valueColumn) {
		this.valueColumn = valueColumn;
	}

	public int getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(int datasourceId) {
		this.datasourceId = datasourceId;
	}

	public String getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}

	public List<FactTableAxis> getFactTableAxis() {
		if(factTableAxis == null) {
			factTableAxis = new ArrayList<FactTableAxis>();
		}
		return factTableAxis;
	}

	public void setFactTableAxis(List<FactTableAxis> factTableAxis) {
		this.factTableAxis = factTableAxis;
	}

	public String getDateColumn() {
		return dateColumn;
	}

	public void setDateColumn(String dateColumn) {
		this.dateColumn = dateColumn;
	}

	public FactTableObjectives getObjectives() {
		return objectives;
	}

	public void setObjectives(FactTableObjectives objectives) {
		this.objectives = objectives;
	}

	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

}
