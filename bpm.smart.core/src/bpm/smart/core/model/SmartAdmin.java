package bpm.smart.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table (name = "smart_admin")
public class SmartAdmin implements Serializable {

	
	private static final long serialVersionUID = -3139482210351330543L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "idUser")
	private int idUser;
	
	@Column (name = "idMirror")
	private int idMirror;
	
	@Transient
	private MirrorCran mirror;
	
	public SmartAdmin() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public int getIdMirror() {
		return idMirror;
	}

	public void setIdMirror(int idMirror) {
		this.idMirror = idMirror;
	}

	public MirrorCran getMirror() {
		return mirror;
	}

	public void setMirror(MirrorCran mirror) {
		this.mirror = mirror;
	}
	
	

}
