package bpm.smart.core.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "statdatacolumn")
public class StatDataColumn implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;

	@Column (name = "idDatacolumn")
	private int idDatacolumn;

	@Column (name = "nameDatacolumn")
	private String nameDatacolumn;

	@Column (name = "minStat")
	private String minStat;

	@Column (name = "maxStat")
	private String maxStat;

	@Column (name = "averageStat")
	private String average;

	@Column (name = "deviationStat")
	private String deviation;

	@Column (name = "repartitionStat", length = 10000000)
	private String repartitionToString;

	@Transient
	private List<String> repartition;

	public StatDataColumn() {
		super();
	}
	
	public StatDataColumn(int idDatacolumn, String nameDatacolumn, String min, String max, String average,
			String deviation, List<String> repartition) {
		super();
		this.idDatacolumn = idDatacolumn;
		this.nameDatacolumn = nameDatacolumn;
		this.minStat = min;
		this.maxStat = max;
		this.average = average;
		this.deviation = deviation;
		this.repartition = repartition;
		
		String build = "";
		for(String rep : this.repartition){
			build += rep + ";";
		}
		this.repartitionToString = build.substring(0, build.length()-1);
	}

	public StatDataColumn(int idDatacolumn, String nameDatacolumn, String min, String max, String average,
			String deviation, String repartitionToString) {
		super();
		this.idDatacolumn = idDatacolumn;
		this.nameDatacolumn = nameDatacolumn;
		this.minStat = min;
		this.maxStat = max;
		this.average = average;
		this.deviation = deviation;
		this.repartitionToString = repartitionToString;
		
		this.repartition = Arrays.asList(repartitionToString.split(";"));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdDatacolumn() {
		return idDatacolumn;
	}

	public void setIdDatacolumn(int idDatacolumn) {
		this.idDatacolumn = idDatacolumn;
	}

	public String getMin() {
		return minStat;
	}

	public void setMin(String min) {
		this.minStat = min;
	}

	public String getMax() {
		return maxStat;
	}

	public void setMax(String max) {
		this.maxStat = max;
	}

	public String getAverage() {
		return average;
	}

	public void setAverage(String average) {
		this.average = average;
	}

	public String getDeviation() {
		return deviation;
	}

	public void setDeviation(String deviation) {
		this.deviation = deviation;
	}

	public List<String> getRepartition() {
		return repartition;
	}

	public void setRepartition(List<String> repartition) {
		this.repartition = repartition;
		
		String build = "";
		for(String rep : this.repartition){
			build += rep + ";";
		}
		this.repartitionToString = build.substring(0, build.length()-1);
	}

	public String getNameDatacolumn() {
		return nameDatacolumn;
	}

	public void setNameDatacolumn(String nameDatacolumn) {
		this.nameDatacolumn = nameDatacolumn;
	}

	public String getRepartitionToString() {
		return repartitionToString;
	}

	public void setRepartitionToString(String repartitionToString) {
		this.repartitionToString = repartitionToString;
		this.repartition = Arrays.asList(repartitionToString.split(";"));
	}

	
}
