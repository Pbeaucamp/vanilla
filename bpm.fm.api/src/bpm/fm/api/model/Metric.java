package bpm.fm.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.alerts.Alert;

@Entity
@Table (name = "fm_metric")
public class Metric implements Serializable, HasItemLinked {

	public final static int AGGREGATOR_SUM = 0;
	public final static int AGGREGATOR_COUNT = 1;
	public final static int AGGREGATOR_DISTINCT_COUNT = 2;
	public final static int AGGREGATOR_AVERAGE = 3;
	public final static int AGGREGATOR_MINIMUM = 4;
	public final static int AGGREGATOR_MAXIMUM = 5;
	
	public final static int DIRECTION_TOP = 0;
	public final static int DIRECTION_BOTTOM = 1;
	public final static int DIRECTION_MIDDLE = 2;
	
	public final static int TYPE_CLASSIC = 0;
	public final static int TYPE_CALCULATED = 1;
	public final static int TYPE_KAPLAN = 2;
	
	public final static int KAPLAN_ONE = 0;
	public final static int KAPLAN_ALL = 1;
	public final static int KAPLAN_MAJ = 2;
	
	public static List<String> AGGREGATORS;
	
	static {
		AGGREGATORS = new ArrayList<String>();
		AGGREGATORS.add("sum");
		AGGREGATORS.add("count");
		AGGREGATORS.add("distinct count");
		AGGREGATORS.add("average");
		AGGREGATORS.add("minimum");
		AGGREGATORS.add("maximum");
	}
	
	public static List<String> DIRECTIONS;
	
	static {
		
		DIRECTIONS = new ArrayList<String>();
		DIRECTIONS.add("Top");
		DIRECTIONS.add("Bottom");
		DIRECTIONS.add("Middle");
	}
	
	public static List<String> METRIC_TYPES;
	
	static {
		METRIC_TYPES = new ArrayList<String>();
		METRIC_TYPES.add("Classic");
		METRIC_TYPES.add("Calculated");
		METRIC_TYPES.add("Kaplan");
	}
	
	public static List<String> KAPLAN_TYPES;
	
	static {
		KAPLAN_TYPES = new ArrayList<String>();
		KAPLAN_TYPES.add("ONE");
		KAPLAN_TYPES.add("ALL");
		KAPLAN_TYPES.add("MAJORITY");
	}
	 
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "metric_name")
	private String name;
	
	@Column (name = "creation_date")
	private Date creationDate = new Date();
	
	@Column (name = "metric_description")
	private String description;
	
	@Column (name = "etl_name")
	private String etlName;
	
	@Column (name = "owner")
	private String owner;
	
	@Column (name = "responsible")
	private String responsible;
	
	@Column (name = "report_id")
	private String reportName;
	
	@Transient
	private AbstractFactTable factTable;
	
	@Transient
	private List<Alert> alerts;
	
	@Transient
	private List<MetricAction> metricActions;
	
	@Transient
	private List<MetricMap> maps;
	
	@Column (name = "metric_operator")
	private String operator = "sum";

	@Column (name = "metric_direction")
	private String direction = DIRECTIONS.get(DIRECTION_TOP);
	
	@Column (name = "metric_type")
	private String metricType = METRIC_TYPES.get(TYPE_CLASSIC);
	
	@Column (name = "linked_item_id")
	private Integer linkedItemId;
	
	@Transient
	private List<MetricLinkedItem> linkedItems;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public AbstractFactTable getFactTable() {
		return factTable;
	}

	public void setFactTable(AbstractFactTable factTable) {
		this.factTable = factTable;
	}

	public List<Alert> getAlerts() {
		if(alerts == null) {
			alerts = new ArrayList<Alert>();
		}
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEtlName() {
		return etlName;
	}

	public void setEtlName(String etlName) {
		this.etlName = etlName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == ((Metric)obj).getId();
	}

	public String getOperator() {
		if(operator == null) {
			operator = "sum";
		}
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getMetricType() {
		if(metricType == null || metricType.isEmpty()) {
			metricType = METRIC_TYPES.get(TYPE_CLASSIC);
		}
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public String getEtlItemName() {
		try {
			String[] part = etlName.split(";");
			return part[1];
		} catch(Exception e) {
			
		}
		return etlName;
	}
	
	public int getEtlId() {
		try {
			String[] part = etlName.split(";");
			return Integer.parseInt(part[0]);
		} catch(Exception e) {
			
		}
		return 0;
	}

	public boolean isClassic() {
		if(metricType.equals(Metric.METRIC_TYPES.get(Metric.TYPE_CLASSIC))) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	public List<MetricAction> getMetricActions() {
		if(metricActions == null) {
			metricActions = new ArrayList<MetricAction>();
		}
		return metricActions;
	}

	public void setMetricActions(List<MetricAction> metricActions) {
		this.metricActions = metricActions;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public List<MetricMap> getMaps() {
		if(maps == null) {
			maps = new ArrayList<MetricMap>();
		}
		return maps;
	}

	public void setMaps(List<MetricMap> maps) {
		this.maps = maps;
	}

	public List<MetricLinkedItem> getLinkedItems() {
		if(linkedItems == null) {
			linkedItems = new ArrayList<MetricLinkedItem>();
		}
		return linkedItems;
	}

	public void setLinkedItems(List<MetricLinkedItem> linkedItems) {
		this.linkedItems = linkedItems;
	}
	
	public List<Axis> getLinkedAxis() {
		List<Axis> result = new ArrayList<Axis>();
		if(factTable instanceof FactTable) {
			for(FactTableAxis a : ((FactTable)factTable).getFactTableAxis()) {
				result.add(a.getAxis());
			}
		}
		
		return result;
	}
	
	@Override
	public Integer getLinkedItemId() {
		return linkedItemId;
	}
	
	@Override
	public void setLinkedItemId(Integer linkedItemId) {
		this.linkedItemId = linkedItemId;
	}
}
