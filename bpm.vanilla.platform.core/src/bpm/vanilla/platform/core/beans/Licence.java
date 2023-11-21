package bpm.vanilla.platform.core.beans;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Licence {

	private Date limitDate;

	public Licence() {
	}

	public Licence(Date limitDate) {
		this.limitDate = limitDate;
	}
	
	public Date getLimitDate() {
		return limitDate;
	}

	@JsonIgnore
	public boolean isDateLimitReach() {
		return limitDate != null ? new Date().after(limitDate) : false;
	}
}
