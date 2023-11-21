package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;

public class DocName implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String rootName;
	private boolean date;
	private int increment;
	private int base;
	private String type;
	private int folderId;
	private boolean enable;
	private Date resetDate = new Date();
	private String automaticName;
	private boolean fromAdmin;
	private boolean global;

	public DocName() {
		// TODO Auto-generated constructor stub
	}

	public DocName(String rootName, boolean date, int increment, String type, int folderId, boolean enable, String automaticName) {
		super();
		this.rootName = rootName;
		this.date = date;
		this.increment = increment;
		this.type = type;
		this.folderId = folderId;
		this.enable = enable;
		this.automaticName = automaticName;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public int getFolderId() {
		return folderId;
	}

	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isDate() {
		return date;
	}

	public void setDate(boolean date) {
		this.date = date;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRootName() {
		return rootName;
	}

	public void setRootName(String rootName) {
		this.rootName = rootName;
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public Date getResetDate() {
		return resetDate;
	}

	public void setResetDate(Date resetDate) {
		this.resetDate = resetDate;
	}

	public boolean isFromAdmin() {
		return fromAdmin;
	}

	public void setFromAdmin(boolean fromAdmin) {
		this.fromAdmin = fromAdmin;
	}

	public String getAutomaticName() {
		return automaticName;
	}

	public void setAutomaticName(String automaticName) {
		this.automaticName = automaticName;
	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

}
