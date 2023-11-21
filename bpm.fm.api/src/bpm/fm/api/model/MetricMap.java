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
@Table (name = "fm_metric_map")
public class MetricMap implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String TYPE_OSM = "OpenStreetMap";
	public static final String TYPE_WMS = "WMS";
	
	public static List<String> TYPES;
	
	static {
		TYPES = new ArrayList<String>();
		TYPES.add(TYPE_OSM);
		TYPES.add(TYPE_WMS);
	}

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "map_name")
	private String name;
	
	@Column(name = "map_desc")
	private String desc;
	
	@Column(name = "map_type")
	private String type;
	
	@Column(name = "metric_id")
	private int metricId;
	
	@Column(name = "datasource_id")
	private int datasourceId;
	
	@Column(name = "zone_column")
	private String columnZone;
	
	@Column(name = "latitude_column")
	private String columnLatitude;
	
	@Column(name = "longitude_column")
	private String columnLongitude;
	
	@Column(name = "table_name")
	private String tableName;
	
	@Column(name = "level_id")
	private int levelId;
	
	@Transient
	private Level level;
	
	@Transient
	private Datasource datasource;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMetricId() {
		return metricId;
	}

	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}

	public int getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(int datasourceId) {
		this.datasourceId = datasourceId;
	}

	public String getColumnZone() {
		return columnZone;
	}

	public void setColumnZone(String columnZone) {
		this.columnZone = columnZone;
	}

	public String getColumnLatitude() {
		return columnLatitude;
	}

	public void setColumnLatitude(String columnLatitude) {
		this.columnLatitude = columnLatitude;
	}

	public String getColumnLongitude() {
		return columnLongitude;
	}

	public void setColumnLongitude(String columnLongitude) {
		this.columnLongitude = columnLongitude;
	}

	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getLevelId() {
		return levelId;
	}

	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
}
