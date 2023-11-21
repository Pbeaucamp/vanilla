package bpm.aklabox.workflow.core.model.activities;

public class MailActivity extends Activity implements IMailServer {

	private static final long serialVersionUID = 1L;

	private String fromWho = "";
	private String toWho = "";
	private String object = "";
	private String content = "";
	private String alias = "";
	private int mailServerId = 0;
	private boolean attachOrbeonPdf = false;

	private boolean attachBirt;

	private String reportId;

	public MailActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String fromWho, String toWho, String object, String content, int mailServerId) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.fromWho = fromWho;
		this.toWho = toWho;
		this.object = object;
		this.content = content;
		this.mailServerId = mailServerId;
	}

	public MailActivity() {
		this.activityId = "mail";
		this.activityName = "Mail";
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

	public int getMailServerId() {
		return mailServerId;
	}

	public void setMailServerId(int mailServerId) {
		this.mailServerId = mailServerId;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	public boolean isAttachOrbeonPdf() {
		return attachOrbeonPdf;
	}

	public void setAttachOrbeonPdf(boolean attachOrbeonPdf) {
		this.attachOrbeonPdf = attachOrbeonPdf;
	}

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
