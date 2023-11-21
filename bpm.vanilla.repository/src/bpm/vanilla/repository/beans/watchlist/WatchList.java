package bpm.vanilla.repository.beans.watchlist;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.repository.RepositoryItem;

@Entity
@Table (name = "rpy_watchlist")
public class WatchList {

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "creation_date")
	private Date creationDate = Calendar.getInstance().getTime();
	
	@Column(name = "creator_id")
	private Integer creatorId;
	
	@Column(name = "item_id")
	private Integer directoryItemId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public Integer getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(Integer directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

	public String getXml(int dirItId, RepositoryItem it, String creatorName) {
		StringBuffer buf = new StringBuffer();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		buf.append("    <watchList-item>\n");
		buf.append("        <id>" + id + "</id>\n");
		buf.append("        <directoryItem-id>" + dirItId + "</directoryItem-id>\n");
		buf.append("        <item-name>" + it.getItemName() + "</item-name>\n");
		buf.append("        <creator-name>" + creatorName + "</creator-name>\n");

		if (it.getDateModification() == null) {
			buf.append("        <lastUpdate>" + sdf.format(it.getDateCreation()) + "</lastUpdate>\n");
		}
		else {
			buf.append("        <lastUpdate>" + sdf.format(it.getDateModification()) + "</lastUpdate>\n");
		}

		buf.append("    </watchList-item>\n");

		return buf.toString();
	}
}
