package bpm.aklabox.workflow.core.model.activities;

public interface IMailServer {

	public String getObject();

	public void setObject(String object);

	public String getContent();

	public void setContent(String content);

	public String getFromWho();

	public void setFromWho(String fromWho);

	public String getToWho();

	public void setToWho(String toWho);

	public int getMailServerId();

	public void setMailServerId(int mailServerId);

	public void setAlias(String alias);

	public String getAlias();
	
	public boolean isAttachOrbeonPdf();

	public void setAttachOrbeonPdf(boolean attachOrbeonPdf);
	
	public boolean isAttachBirt();

	public void setAttachBirt(boolean attachBirt);
	
	public String getReportId();

	public void setReportId(String reportId);

}
