package bpm.fmloader.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.LoaderMetricValue;
import bpm.fmloader.client.DatasServices;
import bpm.fmloader.client.dto.DTO;
import bpm.fmloader.client.dto.MetricDTO;
import bpm.fmloader.client.dto.ValuesDTO;
import bpm.fmloader.client.infos.InfosUser;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareArchitect;
import bpm.gwt.commons.shared.InfoShareD4C;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.utils.CkanHelper;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DatasServicesImpl extends RemoteServiceServlet implements DatasServices {

	private FMLoaderSession getSession() throws Exception {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), FMLoaderSession.class);
	}

	private static final long serialVersionUID = 5499382415346514331L;

	public InfosUser getValues(List<Integer> metricIds, Date selectedDate, Date endDate, boolean first) throws Exception {

		getSession().setSelectedDate(selectedDate);

		List<LoaderDataContainer> res = getSession().getManager().getValuesForLoader(metricIds, selectedDate, endDate);

		InfosUser.getInstance().setValues(res);

		return InfosUser.getInstance();
	}

	public InfosUser getMetrics(InfosUser infos) throws Exception {
		FMLoaderSession session = getSession();
		IFreeMetricsManager manager = session.getManager();

		infos.setApplications(manager.getAxis());

		List<Metric> metrics = manager.getMetrics();

		List<Metric> result = new ArrayList<Metric>();
		for (Metric m : metrics) {
			if (m.getFactTable() instanceof FactTable) {
				if (((FactTable) m.getFactTable()).getDatasource().getType() == DatasourceType.JDBC) {
					result.add(m);
				}
			}
		}

		infos.setMetrics(result);

		return infos;
	}

	public void updateValues(LoaderDataContainer values, InfosUser infos) throws Exception {

		try {
			getSession().getManager().updateValuesFromLoader(values, getSession().getSelectedDate());
		} catch (Exception e) {

		}
	}

	public void createEvoChart(DTO value, String filename, String url, String metricName, Date selectedDate) throws Exception {

		// TODO

		// FMLoaderSession session =
		// (FMLoaderSession)getThreadLocalRequest().getSession().getAttribute("_session_");
		// MetricValues metricVal = null;
		// boolean isCompteur;
		// if(value instanceof IndicatorValuesDTO) {
		// metricVal =
		// session.getManager().getMetricValueById(((IndicatorValuesDTO)
		// value).getId());
		// isCompteur = false;
		// }
		// else {
		// metricVal = session.getManager().getMetricValueById(((ValuesDTO)
		// value).getId());
		// isCompteur = true;
		// }
		//
		// File file = new File(getServletContext().getRealPath(File.separator)
		// + File.separator + filename);
		// session.getJsps().add(getServletContext().getRealPath(File.separator)
		// + File.separator + filename);
		// try {
		//
		//
		//
		//
		// // FileWriter fw = new FileWriter(file);
		//
		// Charset charset = Charset.forName("UTF-8");
		// Writer writer = new OutputStreamWriter(new FileOutputStream(file),
		// charset);
		// writer.write(createEvoChartJsp(metricVal, url, metricName,
		// isCompteur, selectedDate));
		// writer.flush();
		// writer.close();
		//
		// // fw.write(createEvoChartJsp(metricVal, url, metricName, isCompteur,
		// selectedDate));
		// // fw.flush();
		// // fw.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	private String createEvoChartJsp(MetricValues value, String url, String metricName, boolean isCompteur, Date selectedDate) throws Exception {

		// TODO

		// FMLoaderSession session =
		// (FMLoaderSession)getThreadLocalRequest().getSession().getAttribute("_session_");
		// String path = getServletContext().getRealPath(File.separator);
		//
		// IManager manager = session.getManager();
		//
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		//
		// StringBuffer buf = new StringBuffer();
		//
		// buf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"
		// + "<HTML>"
		// + "<HEAD>"
		// +
		// "<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>"
		// +
		// "<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></HEAD>");
		//
		// buf.append("<%{\n");
		//
		// buf.append("java.util.Properties fmMetricProp = new java.util.Properties();\n");
		// String p = System.getProperty("bpm.vanilla.configurationFile");
		//
		// Properties prop = new Properties();
		// try {
		// prop.load(new FileInputStream(p));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// /*
		// * connection DataBase information
		// */
		// buf.append("fmMetricProp.setProperty(\"driverClassName\", \"" +
		// prop.getProperty("bpm.vanilla.freemetrics.database.driverClassName")
		// + "\");\n");
		// buf.append("fmMetricProp.setProperty(\"url\", \"" +
		// prop.getProperty("bpm.vanilla.freemetrics.database.jdbcUrl") +
		// "\");\n");
		// buf.append("fmMetricProp.setProperty(\"username\", \"" +
		// prop.getProperty("bpm.vanilla.freemetrics.database.userName") +
		// "\");\n");
		//
		// String encPass =
		// prop.getProperty("bpm.vanilla.freemetrics.database.password").replace("ENC(",
		// "");
		// if(encPass.substring(encPass.length() - 1).equals(")")) {
		// encPass = encPass.substring(0,encPass.length() - 1);
		// }
		//
		// String pass = FactoryManager.decript(encPass, "biplatform");
		// buf.append("fmMetricProp.setProperty(\"password\", \"" + pass +
		// "\");\n");
		//
		// //metric informations
		// //find the metric, application and date
		// Assoc_Application_Metric assoc = null;
		// try {
		// assoc =
		// manager.getAssoc_Territoire_Metric_ById(value.getMvGlAssoc_ID());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// Metric metric = manager.getMetricById(assoc.getMetr_ID());
		//
		// if(!isCompteur) {
		// List<Metric> cpt = manager.getAssciatedCompteur(metric);
		// LOOOK:for(Metric m : cpt) {
		// if(m.getName().equals(metricName)) {
		// LOOK:for(Assoc_Application_Metric as :
		// manager.getAssoc_Application_MetricByMetricId(m.getId())) {
		// List<Application> apps =
		// manager.findApplicationsByAssoId(as.getId());
		// List<Application> appsInd =
		// manager.findApplicationsByAssoId(assoc.getId());
		// boolean finded = false;
		// for(Application app : apps) {
		// finded = false;
		// for(Application appInd : appsInd) {
		// if(appInd.getId() == appInd.getId()) {
		// finded = true;
		// break;
		// }
		// }
		// if(!finded) {
		// continue LOOK;
		// }
		// }
		// if(finded) {
		// assoc = as;
		// break LOOOK;
		// }
		// }
		// }
		// }
		// }
		//
		// // Application app = manager.getApplicationById(assoc.getApp_ID());
		//
		// // buf.append("fmMetricProp.setProperty(\"applicationName\", \"" +
		// app.getName() + "\");\n");
		// buf.append("fmMetricProp.setProperty(\"assocId\", \"" + assoc.getId()
		// + "\");\n");
		// buf.append("fmMetricProp.setProperty(\"metricName\", \"" + metricName
		// + "\");\n");
		// buf.append("fmMetricProp.setProperty(\"iscompteur\", \"" + isCompteur
		// + "\");\n");
		//
		// String metricDate = df.format(selectedDate);
		// // String[] da = metric.getValue().getDate().split("\\/");
		// // GregorianCalendar cal = new
		// GregorianCalendar(Integer.parseInt(da[2]), Integer.parseInt(da[1]),
		// Integer.parseInt(da[0]));
		// // String date = ""+cal.get(Calendar.YEAR);
		// // date += "-" + cal.get(Calendar.MONTH);
		// // date += "-" + cal.get(Calendar.DAY_OF_MONTH);
		// buf.append("fmMetricProp.setProperty(\"metricDate\", \"" + metricDate
		// + "\");\n");
		//
		// //find begin and end dates
		// // int assoId = assoc.getId();
		// // List<MetricValues> values =
		// session.getManager().getValuesForAssocId(assoId);
		// Date dateDebut = null;
		// Date dateFin = null;
		//
		// if(metric.getMdCalculationTimeFrame().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_YEARLY]))
		// {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.YEAR, - 6);
		// calendar.set(Calendar.MONTH,
		// calendar.getActualMinimum(Calendar.MONTH));
		// calendar.set(Calendar.DATE,
		// calendar.getActualMinimum(Calendar.DATE));
		// dateDebut = calendar.getTime();
		//
		// calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.YEAR, - 1);
		// calendar.set(Calendar.MONTH,
		// calendar.getActualMaximum(Calendar.MONTH));
		// calendar.set(Calendar.DATE,
		// calendar.getActualMaximum(Calendar.DATE));
		// dateFin = calendar.getTime();
		// }
		//
		// else
		// if(metric.getMdCalculationTimeFrame().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_BIANNUAL]))
		// {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.MONTH, - (6 * 6));
		// calendar.set(Calendar.DATE,
		// calendar.getActualMinimum(Calendar.DATE));
		// dateDebut = calendar.getTime();
		//
		// calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.MONTH, - (1 * 6));
		// calendar.set(Calendar.DATE,
		// calendar.getActualMaximum(Calendar.DATE));
		// dateFin = calendar.getTime();
		// }
		//
		// else
		// if(metric.getMdCalculationTimeFrame().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_QUARTERLY]))
		// {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.MONTH, - (6 * 3));
		// calendar.set(Calendar.DATE,
		// calendar.getActualMinimum(Calendar.DATE));
		// dateDebut = calendar.getTime();
		//
		// calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.MONTH, - (1 * 3));
		// calendar.set(Calendar.DATE,
		// calendar.getActualMaximum(Calendar.DATE));
		// dateFin = calendar.getTime();
		// }
		//
		// else
		// if(metric.getMdCalculationTimeFrame().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_MONTHLY]))
		// {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.MONTH, - 6);
		// calendar.set(Calendar.DATE,
		// calendar.getActualMinimum(Calendar.DATE));
		// dateDebut = calendar.getTime();
		//
		// calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.MONTH, - 1);
		// calendar.set(Calendar.DATE,
		// calendar.getActualMaximum(Calendar.DATE));
		// dateFin = calendar.getTime();
		// }
		//
		// else
		// if(metric.getMdCalculationTimeFrame().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_WEEKLY]))
		// {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.DATE, - (6 * 7));
		// calendar.set(Calendar.HOUR_OF_DAY,
		// calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		// calendar.set(Calendar.DAY_OF_WEEK,
		// calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
		// dateDebut = calendar.getTime();
		//
		// calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.DATE, - (1 * 7));
		// calendar.set(Calendar.HOUR_OF_DAY,
		// calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		// calendar.set(Calendar.DAY_OF_WEEK,
		// calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
		// dateFin = calendar.getTime();
		// }
		//
		// else
		// if(metric.getMdCalculationTimeFrame().equalsIgnoreCase(IConstants.PERIODS[IConstants.PERIOD_DAYLY]))
		// {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.DATE, - 6);
		// calendar.set(Calendar.HOUR_OF_DAY,
		// calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		// dateDebut = calendar.getTime();
		//
		// calendar = Calendar.getInstance();
		// calendar.setTime(selectedDate);
		// calendar.add(Calendar.DATE, - 1);
		// calendar.set(Calendar.HOUR_OF_DAY,
		// calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		// dateFin = calendar.getTime();
		// }
		//
		// String ddeb = df.format(dateDebut);
		// String dfin = df.format(dateFin);
		//
		// buf.append("fmMetricProp.setProperty(\"metricDateMin\", \"" + ddeb +
		// "\");\n");
		//
		// buf.append("fmMetricProp.setProperty(\"metricDateMax\", \"" + dfin +
		// "\");\n");
		//
		// /*
		// * freemetricsLogin informations
		// */
		// buf.append("fmMetricProp.setProperty(\"fmLogin\", \"" +
		// session.getUsername() + "\");\n");
		// buf.append("fmMetricProp.setProperty(\"fmPassword\", \"" +
		// session.getPassword() + "\");\n");
		// buf.append("fmMetricProp.setProperty(\"isencrypted\", \"" +
		// session.isEncrypted() + "\");\n");
		//
		// buf.append("java.util.Properties _p = new java.util.Properties();\n");
		//
		// buf.append("String zz = \"\";\n");
		// buf.append("try{\n");
		// buf.append("    zz = bpm.fmloader.server.chart.FreemetricHelper.getMetricEvolutionXml(fmMetricProp, _p);\n");
		// buf.append("}catch(Exception e){}\n");
		// buf.append("%>\n");
		//
		// // buf.append("<html>\n");
		// buf.append("    <header>\n");
		// buf.append("        <script language=\"Javascript\" src=\"" + url +
		// "FusionCharts/FusionCharts.js\"></script>\n");
		// buf.append("		<script type=\"text/javascript\" src=\"" + url +
		// "FusionCharts/swfobject.js\"></script>\n");
		// buf.append("    </header>\n");
		// buf.append("    <body>\n");
		//
		// buf.append("<table>\n" +
		// "<tr>\n" +
		// "<td valign=\"top\">\n");
		// buf.append("<p id=\"" + "ddd" + "\">\n");
		// buf.append("    <script type=\"text/javascript\">\n");
		//
		// //test
		// // buf.append("alert(<%= zz %>);");
		// //test
		// // buf.append("alert(<%= \"\\\"\" +zz + \"\\\"\"%>);");
		// buf.append("        var chart_" + "ddd" + " = new FusionCharts(\"" +
		// url +
		// "FusionCharts/MSLine.swf?ChartNoDataText=There are no previous values for this metric\", "
		// + "\"Id_" + "ddd" + "\", \""
		// +460+"\", \""+230+"\", \"0\", \"0\");\n");
		// buf.append("        chart_" + "ddd" +
		// ".setDataXML(<%= \"\\\"\" +zz + \"\\\"\"%>);\n");
		// buf.append("        chart_" + "ddd" + ".render(\"" + "ddd" +
		// "\");\n");
		// buf.append("    </script>\n");
		// buf.append("</p>\n");
		// buf.append("</td>");
		// buf.append("</tr>");
		// buf.append("</table>\n");
		//
		// buf.append("</body>\n");
		// buf.append("</html>");
		//
		// buf.append("<%}%>\n");
		//
		// return buf.toString();

		return null;
	}

	public List<MetricDTO> getMetricsForValueInfos() throws Exception {
		return (List<MetricDTO>) getThreadLocalRequest().getSession().getAttribute("_infosmetrics_");
	}

	public void cleanJsps() throws Exception {
		FMLoaderSession session = (FMLoaderSession) getThreadLocalRequest().getSession().getAttribute("_session_");
		if (session != null) {
			for (String jsp : session.getJsps()) {
				File f = new File(jsp);
				f.delete();
			}
		}
	}

	public ValuesDTO getCompteurValueInformations(ValuesDTO value, String filename, String url, Date periodeDate) throws Exception {

		// TODO

		// FMLoaderSession session =
		// (FMLoaderSession)getThreadLocalRequest().getSession().getAttribute("_session_");
		//
		// IManager manager = session.getManager();
		//
		// MetricValues metricVal = manager.getMetricValueById(value.getId());
		//
		// Assoc_Application_Metric assoc = null;
		// try {
		// assoc = manager.getAssoc_Territoire_Metric_ById(value.getAssoId());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// Metric metric = manager.getMetricById(assoc.getMetr_ID());
		//
		// MetricValues val = null;
		// if(metricVal != null) {
		// val = manager.getPreviousMetricValue(metricVal,
		// metric.getMdCalculationTimeFrame());
		// }
		// else {
		// //find the previous value
		// List<MetricValues> values =
		// manager.getValuesForAssocId(assoc.getId());
		// for(MetricValues v : values) {
		// if(val == null && v.getMvPeriodDate().before(periodeDate)) {
		// val = v;
		// }
		// else if(v.getMvPeriodDate().before(periodeDate) &&
		// v.getMvPeriodDate().after(val.getMvPeriodDate())) {
		// val = v;
		// }
		// }
		// }
		//
		// ValuesDTO cptValue = null;
		//
		// if(val != null) {
		// cptValue = new ValuesDTO();
		//
		// cptValue.setId(val.getId());
		//
		// cptValue.setDate(val.getMvPeriodDate());
		//
		// if(metricVal != null) {
		// cptValue.setNextDate(metricVal.getMvPeriodDate());
		// }
		// else {
		// cptValue.setNextDate(periodeDate);
		// }
		//
		// cptValue.setValue(String.valueOf(val.getMvValue()));
		//
		// cptValue.setAssoId(val.getMvGlAssoc_ID());
		// }
		//
		// if(metricVal != null) {
		// createEvoChart(value, filename, url, metric.getName(), periodeDate);
		// }
		// else if(cptValue != null ) {
		// createEvoChart(cptValue, filename, url, metric.getName(),
		// periodeDate);
		// }
		//
		// return cptValue;

		return null;
	}

	@Override
	public void exportMetricValues(InfoShare infoShare, String metricName, int metricId, Date selectedDate, Date endDate) throws Exception {
		try {
			List<Integer> metricIds = new ArrayList<Integer>();
			metricIds.add(metricId);

			InfosUser result = getValues(metricIds, selectedDate, endDate, false);
			LoaderDataContainer loader = result.getValues().get(0);

			ByteArrayInputStream is = buildCsv(metricName, loader.getAxisInfos(), loader.getValues(), ";");

			if (infoShare.getTypeShare() == TypeShare.CKAN) {
				InfoShareD4C infoShareD4C = (InfoShareD4C) infoShare;
				CkanPackage pack = infoShareD4C.getPack();

				CkanHelper ckanHelper = new CkanHelper();
				ckanHelper.uploadCkanFile(pack.getSelectedResource().getName(), pack, is);
			}
			else if (infoShare.getTypeShare() == TypeShare.ARCHITECT) {
				InfoShareArchitect infoShareArch = (InfoShareArchitect) infoShare;
				Contract contract = infoShareArch.getContract();
				
				String documentName = infoShare.getItemName();
				String format = infoShare.getFormat();
				
				FMLoaderSession session = getSession();
				
				int userId = session.getUser().getId();
				
				int repositoryId = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_PUBLIC_REPOSITORY_ID));
				try {					
					GedDocument doc = null;
					if (contract.getDocId() != null) {
						doc = session.getGedComponent().getDocumentDefinitionById(contract.getDocId());
					}

					if (doc != null) {
						session.getGedComponent().addVersionToDocumentThroughServlet(doc.getId(), format, is);
					}
					else {
						List<Integer> groupIds = session.getMdmRemote().getSupplierSecurity(contract.getParent().getId());

						doc = session.getGedComponent().createDocumentThroughServlet(documentName, format, userId, groupIds, repositoryId, is);
						contract.setFileVersions(doc);
					}

					contract.setVersionId(null);
					session.getMdmRemote().addContract(contract);
					
					session.getMdmRemote().launchAssociatedItems(contract.getId(), session.getRepositoryConnection().getContext(), session.getUser());
				} catch (Exception e) {
					e.printStackTrace();
					throw new ServiceException("Unable to add a new version: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private ByteArrayInputStream buildCsv(String metricName, List<AxisInfo> axisInfo, List<LoaderMetricValue> metricValues, String separator) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		List<String> columnNames = new ArrayList<>();
		columnNames.add("Nom");
//		columnNames.add("Statut");
		columnNames.add("Valeur");
		columnNames.add("Objectif");
		columnNames.add("Minimum");
		columnNames.add("Maximum");
		for(final AxisInfo axis : axisInfo) {
			columnNames.add(axis.getAxis().getName());
		}

		StringBuffer firstLine = new StringBuffer();
		for (int i = 0; i < columnNames.size(); i++) {
			if (i != 0) {
				firstLine.append(separator);
			}
			firstLine.append(columnNames.get(i));
		}
		firstLine.append("\n");

		try {
			os.write(firstLine.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (LoaderMetricValue value : metricValues) {
			StringBuffer line = new StringBuffer();
			line.append(metricName);
			line.append(separator);
			line.append(String.valueOf(value.getValue()));
			line.append(separator);
			line.append(String.valueOf(value.getObjective()));
			line.append(separator);
			line.append(String.valueOf(value.getMinimum()));
			line.append(separator);
			line.append(String.valueOf(value.getMaximum()));
			
			for(int i=0; i<axisInfo.size(); i++) {
				line.append(separator);
				line.append(value.getMembers().get(i).getLabel() + " : " + value.getMembers().get(i).getValue());
			}
				
			line.append("\n");

			try {
				os.write(line.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(os.toByteArray());
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteArrayIs;
	}
}
