package bpm.aklabox.workflow.core.model.resources;

import bpm.aklabox.workflow.core.model.activities.IMailServer;

public class MailServer extends Resource implements IMailServer {

	private static final long serialVersionUID = 1L;

	private int id;
	private String description;
	private int port;
	private String fromWho = "";
	private String toWho = "";
	private String object = "";
	private String content = "";
	private String alias = "";
	private boolean attachBirt;

	private String reportId;


	public MailServer(String name, int userId, String address, String userName, String password, String description, int port, String fromWho, String toWho, String object, String content, String alias) {
		this.name = name;
		this.userId = userId;
		this.address = address;
		this.userName = userName;
		this.password = password;
		this.description = description;
		this.port = port;
		this.fromWho = fromWho;
		this.toWho = toWho;
		this.object = object;
		this.content = content;
		this.alias = alias;
	}

	public MailServer() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFromWho() {
		return fromWho;
	}

	public void setFromWho(String fromWho) {
		this.fromWho = fromWho;
	}

	public String getToWho() {
		return toWho;
	}

	public void setToWho(String toWho) {
		this.toWho = toWho;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public int getMailServerId() {
		return id;
	}

	@Override
	public void setMailServerId(int mailServerId) {
		this.id = mailServerId;
	}

	@Override
	public boolean isAttachOrbeonPdf() {
		return false;
	}

	@Override
	public void setAttachOrbeonPdf(boolean attachOrbeonPdf) {}

	@Override
	public boolean isAttachBirt() {
		return attachBirt;
	}

	@Override
	public void setAttachBirt(boolean attachBirt) {
		this.attachBirt = attachBirt;
	}

	@Override
	public String getReportId() {
		return reportId;
	}

	@Override
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	
}
