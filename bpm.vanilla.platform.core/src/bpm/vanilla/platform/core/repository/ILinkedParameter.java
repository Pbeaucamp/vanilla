package bpm.vanilla.platform.core.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_linked_parameters")
public class ILinkedParameter {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "parameter_position")
	private int internalParameterPosition;
	
	@Column(name = "provided_parameter_id")
	private int providedParameterId;

	@Column(name = "provider_parameter_id")
	private int providerParameterId;
	
	@Transient
	private String providerParameterName;
	
	@Transient
	private String internalParameterName;

	@Transient
	private Parameter parent;

	public ILinkedParameter() {
	}

	public Parameter getParent() {
		return parent;
	}

	public void setParent(Parameter parent) {
		this.parent = parent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProviderParameterName() {
		return providerParameterName;
	}

	public void setProviderParameterName(String providerParameterName) {
		this.providerParameterName = providerParameterName;
	}

	public int getProviderParameterId() {
		return providerParameterId;
	}

	public void setProviderParameterId(int providerParameterId) {
		this.providerParameterId = providerParameterId;
	}

	public String getInternalParameterName() {
		return internalParameterName;
	}

	public void setInternalParameterName(String internalParameterName) {
		this.internalParameterName = internalParameterName;
	}

	public int getInternalParameterPosition() {
		return internalParameterPosition;
	}

	public void setInternalParameterPosition(int internalParameterPosition) {
		this.internalParameterPosition = internalParameterPosition;
	}

	public int getProvidedParameterId() {
		return providedParameterId;
	}

	public void setProvidedParameterId(int providedParameterId) {
		this.providedParameterId = providedParameterId;
	}

}
