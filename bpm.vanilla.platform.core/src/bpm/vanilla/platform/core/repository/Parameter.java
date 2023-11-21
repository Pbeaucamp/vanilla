package bpm.vanilla.platform.core.repository;

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

@Entity
@Table (name = "rpy_parameter")
public class Parameter implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "parameter_name")
	private String name;
	
	@Column(name = "instance_name")
	private String instanceName;

	@Column(name = "dataprovider_id")
	private int dataProviderId;
	
	@Column(name = "dataprovider_name")
	private String dataProviderName;

	@Column(name = "value_column_index")
	private int valueColumnIndex;
	
	@Column(name = "value_column_name")
	private String valueColumnName;

	@Column(name = "label_column_index")
	private int labelColumnIndex;
	
	@Column(name = "label_column_name")
	private String labelColumnName;

	@Column(name = "item_id")
	private int directoryItemId;

	@Column(name = "PRM_DATAPROVIDER_MULTIPLE_VALUES")
	private boolean allowMultipleValues;

	@Column(name = "default_value")
	private String defaultValue;

	@Transient
	private List<ILinkedParameter> requestedParameters = new ArrayList<ILinkedParameter>();

	public Parameter() {
	}

	/**
	 * @return the labelColumnIndex
	 */
	public int getLabelColumnIndex() {
		if (labelColumnIndex <= 0 && getValueColumnIndex() >= 0) {
			return getValueColumnIndex();
		}
		return labelColumnIndex;
	}

	/**
	 * @param labelColumnIndex
	 *            the labelColumnIndex to set
	 */
	public void setLabelColumnIndex(int labelColumnIndex) {
		this.labelColumnIndex = labelColumnIndex;
	}

	/**
	 * @return the labelColumnName
	 */
	public String getLabelColumnName() {
		return labelColumnName != null ? labelColumnName : "";
	}

	/**
	 * @param labelColumnName
	 *            the labelColumnName to set
	 */
	public void setLabelColumnName(String labelColumnName) {
		this.labelColumnName = labelColumnName;
	}

	public List<ILinkedParameter> getRequestecParameters() {
		return requestedParameters;
	}

	public void addRequestedParameter(ILinkedParameter p) {
		requestedParameters.add(p);
		p.setParent(this);
		p.setProvidedParameterId(getId());
	}

	public void removeRequestedParameter(ILinkedParameter p) {
		requestedParameters.remove(p);
		p.setParent(null);
		p.setProvidedParameterId(0);
	}
	
	public void setLinkedParameters(List<ILinkedParameter> lParams) {
		if(lParams != null) {
			requestedParameters = lParams;
			for(ILinkedParameter p : requestedParameters) {
				p.setParent(this);
				p.setProvidedParameterId(getId());
			}
		}
	}

	public int getDataProviderId() {
		return dataProviderId;
	}

	public void setDataProviderId(int dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	public String getDataProviderName() {
		return dataProviderName != null ? dataProviderName : "";
	}

	public void setDataProviderName(String dataProviderName) {
		this.dataProviderName = dataProviderName;
	}

	public int getValueColumnIndex() {
		return valueColumnIndex;
	}

	public void setValueColumnIndex(int valueColumnIndex) {
		this.valueColumnIndex = valueColumnIndex;
	}

	public String getValueColumnName() {
		return valueColumnName != null ? valueColumnName : "";
	}

	public void setValueColumnName(String valueColumnName) {
		this.valueColumnName = valueColumnName;
	}

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

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return the allowMultipleValues
	 */
	public boolean isAllowMultipleValues() {
		return allowMultipleValues;
	}

	/**
	 * @param allowMultipleValues
	 *            the allowMultipleValues to set
	 */
	public void setAllowMultipleValues(boolean allowMultipleValues) {
		this.allowMultipleValues = allowMultipleValues;
	}

	public int getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

}
