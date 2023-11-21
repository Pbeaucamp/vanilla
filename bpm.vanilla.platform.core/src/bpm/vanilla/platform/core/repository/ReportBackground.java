package bpm.vanilla.platform.core.repository;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.User;

@Entity
@Table(name = "rpy_report_background")
public class ReportBackground implements IRepositoryObject {

	private static final long serialVersionUID = 4575157541160639952L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;
	
	@Column(name = "item_id")
	private int itemId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "outputFormat")
	private String outputFormat;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "group_id")
	private int groupId;

	@Column(name = "creation_date")
	private Date creationDate;

	@Column(name = "task_key")
	private String taskKey;

	@Column(name = "task_id")
	private Integer taskId;

	@Column(name = "readed")
	private boolean readed;

	@Column(name = "running")
	private boolean running;

	@Column(name = "path")
	private String path;

	@Column(name = "size")
	private Long size;

	@Transient
	private User user;

	public ReportBackground() {
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}
	
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getTaskKey() {
		return taskKey;
	}
	
	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}
	
	public Integer getTaskId() {
		return taskId;
	}
	
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public boolean isNew() {
		return !readed && !isFailed() && !isRunning();
	}
	
	public boolean isFailed() {
		return !isRunning() && (path == null || path.isEmpty());
	}
}
