package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MailSearch implements Serializable {

	private static final long serialVersionUID = -7215000245745473777L;
	
	private String keyword;
	private List<SpecificSearch> specificSearchs;
	private Date entryDate, validationDate, sendDate;

	public MailSearch() { }
	
	public MailSearch(String keyword, List<SpecificSearch> specificSearchs, Date entryDate, Date validationDate, Date sendDate) {
		this.keyword = keyword;
		this.specificSearchs = specificSearchs;
		this.entryDate = entryDate;
		this.validationDate = validationDate;
		this.sendDate = sendDate;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public List<SpecificSearch> getSpecificSearchs() {
		return specificSearchs;
	}
	
	public Date getEntryDate() {
		return entryDate;
	}
	
	public Date getValidationDate() {
		return validationDate;
	}
	
	public Date getSendDate() {
		return sendDate;
	}
}
