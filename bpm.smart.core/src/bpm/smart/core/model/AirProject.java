package bpm.smart.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.data.Dataset;

@Entity
@Table (name = "air_project")
public class AirProject implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column (name = "name")
	private String name;
	
	@Column (name = "date")
	private Date date;
	
	@Column (name = "author")
	private String author;
	
	@Column (name = "icon")
	private String avatar;
	
	@Column (name = "subject")
	private String subject;
	
	@Column (name = "idUserCreator")
	private int idUserCreator;
	
	@Column (name = "isPrivate")
	private boolean isPrivate;
	
	@Column (name = "linkedDatasets")
	private String linkedDatasets;
	
	@Transient
	private List<RScript> scripts;
	
	@Transient
	private Dataset dataset;
	
	@Transient
	private String urlIcon;
	
	
	public AirProject() {
		super();
	}
	
	public AirProject(String name, Date date, String author, String avatar,
			String subject) {
		super();
		this.name = name;
		this.date = date;
		this.author = author;
		this.avatar = avatar;
		this.subject = subject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<RScript> getScripts() {
		return scripts;
	}

	public void setScripts(List<RScript> scripts) {
		this.scripts = scripts;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public String getUrlIcon() {
		return urlIcon;
	}

	public void setUrlIcon(String urlIcon) {
		this.urlIcon = urlIcon;
	}

	public int getIdUserCreator() {
		return idUserCreator;
	}

	public void setIdUserCreator(int idUserCreator) {
		this.idUserCreator = idUserCreator;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof AirProject) ) return false;

		final AirProject other = (AirProject) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public String getLinkedDatasets() {
		return linkedDatasets;
	}

	public void setLinkedDatasets(String linkedDatasets) {
		this.linkedDatasets = linkedDatasets;
	}
	
}
