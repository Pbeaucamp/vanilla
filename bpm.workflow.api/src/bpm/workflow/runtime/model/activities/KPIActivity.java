package bpm.workflow.runtime.model.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.manager.IManager;
import bpm.freemetrics.api.organisation.group.Group;
import bpm.freemetrics.api.organisation.metrics.MetricValues;
import bpm.freemetrics.api.organisation.relations.appl_metric.Assoc_Application_Metric;
import bpm.freemetrics.api.security.FmUser;
import bpm.freemetrics.api.utils.Tools;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IFreeMetricsServer;
import bpm.workflow.runtime.model.IParameters;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.FreemetricServer;
import bpm.workflow.runtime.resources.servers.ListServer;

/**
 * Check the value of a metrics and make a comparison
 * 
 * @author Charles MARTIN
 * 
 */
public class KPIActivity extends AbstractActivity implements IFreeMetricsServer, IParameters, IComment, IConditionnable {
	private FreemetricServer server;
	private String comment;
	public int associd = -1;
	public String comparator;
	public List<String> listeParam;
	public ActivityVariables varSucceed;
	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	private static int number = 0;

	private HashMap<String, String> mapping = new HashMap<String, String>();
	
	/**
	 * 
	 * @return the operator of the comparison
	 */
	public String getComparator() {
		return comparator;
	}

	/**
	 * Set the operator of the comparisons
	 * 
	 * @param comparator
	 */
	public void setComparator(String comparator) {
		this.comparator = comparator;
	}

	public KPIActivity() {
		listeParam = new ArrayList<String>();
		listeParam.add("value");
		listeParam.add("date");
		number++;
	}

	public String getSuccessVariableSuffix() {
		return "_result";
	}

	/**
	 * 
	 * @return the id : the chosen association
	 */
	public int getAssocid() {
		return associd;
	}

	/**
	 * Set the id : the chosen association
	 * 
	 * @param associd
	 */
	public void setAssocid(String associd) {
		this.associd = Integer.parseInt(associd);
	}

	/**
	 * Create a KPI Activity with the specified name
	 * 
	 * @param name
	 */
	public KPIActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		listeParam = new ArrayList<String>();
		listeParam.add("value");
		listeParam.add("date");
	}

	public IActivity copy() {
		KPIActivity a = new KPIActivity();
		if(server != null) {
			a.setServer(server);
		}
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		return a;
	}

	/**
	 * Set the id : the chosen association
	 * 
	 * @param id
	 */
	public void addAssociationId(int id) {
		this.associd = id;
	}

	/**
	 * Remove the id : the chosen association
	 * 
	 * @param id
	 */
	public void removeAssociationId(int id) {
		this.associd = 0;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if(server == null) {
			buf.append("For activity " + name + ", the freemetrics server is not set.\n");
		}
		if(associd == -1) {
			buf.append("For activity " + name + ", the association is not set.\n");
		}
		if(comparator == null) {
			buf.append("For activity " + name + ", the comparator is not set.\n");
		}

		return buf.toString();
	}

	public IResource getServer() {

		return server;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("KPIActivity");

		if(comment != null) {
			e.addElement("comment").setText(comment);
		}

		if(server != null) {
			e.addElement("serverRefName").setText(server.getName());
		}
		if(!(Integer.toString(associd)).equalsIgnoreCase("")) {
			e.addElement("idAssoc").setText(Integer.toString(associd));
		}
		if(comparator != null) {
			e.addElement("comparator").setText(comparator);
		}

		return e;
	}

	public Class<?> getServerClass() {

		return FreemetricServer.class;
	}

	public void setServer(IResource server) {
		this.server = (FreemetricServer) server;

	}

	public void setServer(String name) {
		server = (FreemetricServer) ListServer.getInstance().getServer(name);
	}

	public List<String> getParameters(IRepositoryApi sock) {
		List<String> params = new ArrayList<String>();
		params.add("date");
		return params;
	}

	public List<ActivityVariables> getVariables() {
		listeVar.add(varSucceed);

		return listeVar;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}

	/**
	 * not implemented
	 */
	public void addParameterLink(String activityParamName, String formParamName) {
		mapping.put(activityParamName, formParamName);
	}

	public void decreaseNumber() {
		number--;
	}

	@Override
	public HashMap<String, String> getMappings() {
		return mapping;
	}

	@Override
	public void removeParameterLink(String act, String orb) {}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			FmUser user = null;
			IManager fmMgr = null;
			boolean assocFound = false;
			boolean dateFound = false;
			int idAssocString = getAssocid();
			float valueref = 0;
			String date = workflowInstance.parseString(mapping.get("date"));
			try {

				FactoryManager.init("", Tools.OS_TYPE_WINDOWS);

				fmMgr = FactoryManager.getManager();
				
				valueref = fmMgr.getValuesForAssocIdPeridDate(idAssocString, new SimpleDateFormat().parse(date)).getMvValue();

				user = fmMgr.getUserByNameAndPass(server.getFmLogin(), server.getFmPassword());

				for(Group g : fmMgr.getGroupsForUser(user.getId())) {

					for(bpm.freemetrics.api.organisation.application.Application a : fmMgr.getApplicationsForGroup(g.getId())) {

						for(Assoc_Application_Metric ass : fmMgr.getAssoc_Application_MetricsByAppId(a.getId())) {

							if(ass.getId() == idAssocString) {

								assocFound = true;

								Logger.getLogger(getClass()).info("Assoc name found : " + ass.getName());

								try {
									List<MetricValues> liste = fmMgr.getValuesForAssocId((int) idAssocString);

									for(MetricValues value : liste) {

										if(value.getMvPeriodDate().toString().equalsIgnoreCase(date)) {

											dateFound = true;

											Logger.getLogger(getClass()).info("Date found for the association : " + date);

											float recupInt = value.getMvValue();

											if(comparator.equalsIgnoreCase("<")) {
												if(valueref < recupInt) {
													activityResult = true;
												}
												else {
													activityResult = false;
												}
											}
											if(comparator.equalsIgnoreCase(">")) {
												if(valueref > recupInt) {
													activityResult = true;
												}
												else {
													activityResult = false;
												}
											}
											if(comparator.equalsIgnoreCase("=")) {
												if(valueref == recupInt) {
													activityResult = true;

												}
												else {
													activityResult = false;
												}
											}
											break;
										}

									}

								} catch(Exception e) {
									Logger.getLogger(getClass()).error(e.getMessage(), e);
									throw e;

								}

							}

						}

					}
				}


				if(assocFound && dateFound) {
					Logger.getLogger(getClass()).info("Launch the comparaison");
				}
	
				else if(!assocFound) {
					Logger.getLogger(getClass()).info("Association not found");

				}

				else if(!dateFound) {
					Logger.getLogger(getClass()).info("Date not found");

				}
				
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}

			super.finishActivity();
		}

	}

}
