package bpm.vanilla.platform.core.beans.meta;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_meta_link")
public class MetaLink implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TypeMetaLink {
		ARCHITECT
	}
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private TypeMetaLink type;
	
	@Column(name = "item_id")
	private int itemId;

	@Column(name = "meta_id")
	private int metaId;
	
	@Transient
	private Meta meta;
	
	@Transient
	private MetaValue value;
	
	public MetaLink() {
	}
	
	public MetaLink(Meta meta) {
		this.meta = meta;
		setMetaId(meta.getId());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TypeMetaLink getType() {
		return type;
	}

	public void setType(TypeMetaLink type) {
		this.type = type;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getMetaId() {
		return metaId;
	}

	public void setMetaId(int metaId) {
		this.metaId = metaId;
	}
	
	public Meta getMeta() {
		return meta;
	}
	
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	
	public MetaValue getValue() {
		return value;
	}
	
	public void setValue(MetaValue value) {
		this.value = value;
	}
}
