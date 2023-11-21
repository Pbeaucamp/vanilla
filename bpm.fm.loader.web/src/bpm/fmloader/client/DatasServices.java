package bpm.fmloader.client;

import java.util.Date;
import java.util.List;

import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fmloader.client.dto.DTO;
import bpm.fmloader.client.dto.MetricDTO;
import bpm.fmloader.client.dto.ValuesDTO;
import bpm.fmloader.client.infos.InfosUser;
import bpm.gwt.commons.shared.InfoShare;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("DatasServices")
public interface DatasServices extends RemoteService {

	public InfosUser getValues(List<Integer> metricIds, Date selectedDate, Date endDate, boolean first) throws Exception;
	
	public InfosUser getMetrics(InfosUser infos) throws Exception;
	
	public void updateValues(LoaderDataContainer values, InfosUser infos) throws Exception;
	
	public List<MetricDTO> getMetricsForValueInfos() throws Exception;
	
	public void createEvoChart(DTO value, String filename, String url, String metricName, Date selectedDate) throws Exception;
	
	public void cleanJsps() throws Exception;

	public ValuesDTO getCompteurValueInformations(ValuesDTO value, String filename, String hostPageBaseURL, Date selectedDate) throws Exception;
	
	public void exportMetricValues(InfoShare infoShare, String metricName, int metricId, Date selectedDate, Date endDate) throws Exception;
}
