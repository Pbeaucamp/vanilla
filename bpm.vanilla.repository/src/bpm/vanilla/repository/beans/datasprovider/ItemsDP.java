package bpm.vanilla.repository.beans.datasprovider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table (name = "rpy_items_datasprovider")
public class ItemsDP {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "item_id")
	private int itemId;
	
	@Column(name = "data_provider_id")
	private int datasProviderId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = new Integer(id);
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = new Integer(itemId);
	}

	public int getDatasProviderId() {
		return datasProviderId;
	}

	public void setDatasProviderId(int datasProviderId) {
		this.datasProviderId = datasProviderId;
	}

	public void setDatasProviderId(String datasProviderId) {
		this.datasProviderId = new Integer(datasProviderId);
	}

}
