package bpm.smart.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table (name = "AirCube")
public class AirCube  implements Serializable{
	
private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "idUser")
	private int idUser;
	
	@Column (name = "title")
	private String title;
	
	@Column (name = "idDataset")
	private int idDataset;
	
	@Column (name = "xmlModel", length = 10000000)
	private String xmlModel;

	public AirCube() {
		super();
	}

	public AirCube(String title, int idDataset, String xmlModel) {
		super();
		this.title = title;
		this.idDataset = idDataset;
		this.xmlModel = xmlModel;
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

	public String getXmlModel() {
		return xmlModel;
	}

	public void setXmlModel(String xmlModel) {
		this.xmlModel = xmlModel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIdDataset() {
		return idDataset;
	}

	public void setIdDataset(int idDataset) {
		this.idDataset = idDataset;
	}

}
