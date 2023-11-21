package bpm.vanilla.portal.shared.repository;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LinkedDocumentDTO implements IsSerializable {
	private int id;
	private String name;
	private String format;
	private String comment;
	private String relativePath;

	public LinkedDocumentDTO() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

}
