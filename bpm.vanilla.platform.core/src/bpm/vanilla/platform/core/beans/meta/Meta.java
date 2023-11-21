package bpm.vanilla.platform.core.beans.meta;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_meta")
public class Meta implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum TypeMeta {
		STRING,
		DATE,
		VALIDATION
	}

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private TypeMeta type;
	
	@Column(name = "meta_key")
	private String key;
	
	@Column(name = "label")
	private String label;
	
	@Column(name = "schema_definition")
	private String schemaDefinition;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TypeMeta getType() {
		return type;
	}

	public void setType(TypeMeta type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getSchemaDefinition() {
		return schemaDefinition;
	}
	
	public void setSchemaDefinition(String schemaDefinition) {
		this.schemaDefinition = schemaDefinition;
	}
}
