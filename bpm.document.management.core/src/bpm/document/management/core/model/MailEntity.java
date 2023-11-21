package bpm.document.management.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailEntity implements IAdminDematObject {

	private static final long serialVersionUID = 1L;

	public enum MailStatus {
		NEW, IGNORED, TREADTED;
	}

	private AkladematAdminEntity<MailEntity> parent;

	private int mailServerId;

	private String subject;
	private Date mailDate;
	private String from;
	private List<String> attachments = new ArrayList<>();

	private MailStatus status = MailStatus.NEW;

	@SuppressWarnings("unchecked")
	@Override
	public void setParent(AkladematAdminEntity<? extends IAdminDematObject> entity) {
		this.parent = (AkladematAdminEntity<MailEntity>) entity;
	}

	public int getMailServerId() {
		return mailServerId;
	}

	public void setMailServerId(int mailServerId) {
		this.mailServerId = mailServerId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getMailDate() {
		return mailDate;
	}

	public void setMailDate(Date mailDate) {
		this.mailDate = mailDate;
	}

	public MailStatus getStatus() {
		return status;
	}

	public void setStatus(MailStatus status) {
		this.status = status;
	}

	public boolean isSameMail(MailEntity mailEntity) {
		if(subject.equals(mailEntity.getSubject()) && mailDate.equals(mailEntity.getMailDate())) {
			return true;
		}
		return false;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

}
