package bpm.vanilla.platform.core.beans.meta;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_meta_value")
public class MetaValue implements Serializable {
	
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "meta_link_id")
	private int metaLinkId;
	
	@Column(name = "value")
	private String value;
	
	@Column(name = "modify_date")
	private Date modifyDate;

	@Transient
	private String metaKey;
	
	public MetaValue() {
	}
	
	public MetaValue(String metaKey, String value) {
		this.metaKey = metaKey;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMetaLinkId() {
		return metaLinkId;
	}

	public void setMetaLinkId(int metaLinkId) {
		this.metaLinkId = metaLinkId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getMetaKey() {
		return metaKey;
	}
}
