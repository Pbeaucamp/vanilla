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
@Table (name = "fm_complex_axis")
public class ComplexMapAxis implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "idMap")
	private int idMap;
	
	@Column (name = "idAxis")
	private int idAxis;
	
	@Transient
	private Axis axis;

	public ComplexMapAxis() {
		super();
	}
	
	public ComplexMapAxis(Axis axis) {
		super();
		this.axis = axis;
		this.idAxis = axis.getId();
	}

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis;
		this.idAxis = axis.getId();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdMap() {
		return idMap;
	}

	public void setIdMap(int idMap) {
		this.idMap = idMap;
	}

	public int getIdAxis() {
		return idAxis;
	}

	public void setIdAxis(int idAxis) {
		this.idAxis = idAxis;
	}
	
	
	
}
