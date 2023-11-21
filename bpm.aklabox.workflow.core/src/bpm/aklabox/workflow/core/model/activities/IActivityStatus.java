package bpm.aklabox.workflow.core.model.activities;

import java.util.Date;

public interface IActivityStatus {

	enum Status {
		FAILURE, SUCCESS, WARNING
	}

	public void setDateStarted(Date date);

	public Date getDateStarted();

	public void setMessage(String message);

	public String getMessage();

	public void setStatus(Status status);

	public Status getStatus();

}
