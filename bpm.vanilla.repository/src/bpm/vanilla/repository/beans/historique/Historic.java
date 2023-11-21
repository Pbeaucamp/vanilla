package bpm.vanilla.repository.beans.historique;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_historique")
public class Historic {

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int Id;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "model_id")
	private Integer modelId;
	
	@Column(name = "item_id")
	private Integer directoryItemId;
	
	@Column(name = "date_histo")
	private Date dateHistoric = new Date();

	public Integer getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(Integer itemId) {
		this.directoryItemId = itemId;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	public Date getDateHistoric() {
		return dateHistoric;
	}

	public void setDateHistoric(Date dateHistoric) {
		this.dateHistoric = dateHistoric;
	}

}
