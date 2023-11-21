package bpm.fmloader.client;

import java.util.Date;
import java.util.List;

import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fmloader.client.dto.DTO;
import bpm.fmloader.client.dto.MetricDTO;
import bpm.fmloader.client.dto.ValuesDTO;
import bpm.fmloader.client.infos.InfosUser;
import bpm.gwt.commons.shared.InfoShare;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DatasServicesAsync {

	public void getValues(List<Integer> metricIds, Date selectedDate, Date endDate, boolean first, AsyncCallback<InfosUser> asyncCallback);
	
	public void getMetrics(InfosUser infos, AsyncCallback<InfosUser> asyncCallback);
	
	public void updateValues(LoaderDataContainer values, InfosUser infos, AsyncCallback<Void> asyncCallback);

	void getMetricsForValueInfos(AsyncCallback<List<MetricDTO>> callback);

	void createEvoChart(DTO value, String filename, String url, String metricName, Date selectedDate, AsyncCallback<Void> callback);

	void cleanJsps(AsyncCallback<Void> callback);

	public void getCompteurValueInformations(ValuesDTO value, String filename, String hostPageBaseURL, Date selectedDate, AsyncCallback<ValuesDTO> asyncCallback);
	
	public void exportMetricValues(InfoShare infoShare, String metricName, int metricId, Date selectedDate, Date endDate, AsyncCallback<Void> asyncCallback);
}
