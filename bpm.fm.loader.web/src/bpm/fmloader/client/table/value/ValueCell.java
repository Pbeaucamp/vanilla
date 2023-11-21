package bpm.fmloader.client.table.value;

import bpm.fmloader.client.dto.AssoMetricAppDTO;
import bpm.fmloader.client.dto.DTO;

public interface ValueCell {

	public DTO getValue();
	
	public AssoMetricAppDTO getAssoc();
	
}
