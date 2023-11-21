package bpm.fd.runtime.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.DrillDrivenComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.maps.openlayers.ComponentOsmMap;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.datas.odaconsumer.DriverManager;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DrillDrivenStackableCell;
import bpm.fd.api.core.model.structure.Folder;
import bpm.fd.api.core.model.structure.FolderPage;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

public class DashInstance {
	private Group group;
	private User user;
	private DashBoard parent;
	private DashState state;
	private String uuid;

	private String localeLanguage;

	/**
	 * This list maintain the component that need to be rendered because a parameter value has changed Once a parameter is set, all the component that need it are added to this list. Once a component has been rendered, the component is removed from the list
	 */
	private List<String> dirtyComponents = new ArrayList<String>();

	public DashInstance(DashBoard parent, Group group, User user, String localeLanguage) {
		this.group = group;
		this.user = user;
		this.parent = parent;
		this.state = new DashState(this);
		this.uuid = UUID.randomUUID().toString();
		this.localeLanguage = localeLanguage;
		initDefaultParameterValues();
	}

	private void initDefaultParameterValues() {
		for(ComponentRuntime compRun : parent.getComponents()) {
			if(compRun.getElement() instanceof IComponentDefinition) {
				IComponentDefinition def = (IComponentDefinition) compRun.getElement();
				for(ComponentParameter p : def.getParameters()) {
					try {
						String provider = getDashBoard().getDesignParameterProvider(p).getName();
						String defaultVal = p.getDefaultValue();
						if(defaultVal != null && !defaultVal.isEmpty()) {
							state.setComponentValue(provider, defaultVal);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public String getRelativeUrl() {
		return getDashBoard().getRelativeUrl() + "?uuid=" + getUuid();
	}

	public User getUser() {
		return user;
	}

	public Group getGroup() {
		return group;
	}

	public List<String> setParameter(String provider, String value) throws Exception {
		
		if(provider.equals("_superusers_")) {
			state.setComponentValue("_superusers_", value);
			return new ArrayList<String>();
		}
		
		if(provider.equals("_defaultfolder_")) {
			state.setComponentValue("folder", value);
			return new ArrayList<String>();
		}
		
		//XXX
		if(provider.startsWith("Filter_Sections")) {
			state.setComponentValue("Filter_Sections_1", value);
			state.setComponentValue("Filter_Sections_2", value);
			state.setComponentValue("Filter_Sections_3", value);
		}
		else{
			state.setComponentValue(provider, value);
		}
		//XXX
		
		if(provider.endsWith("_colcolors")) {
			List<String> l = new ArrayList<String>();
			l.add(provider.substring(0, provider.indexOf("_colcolors")));
			return l;
		}
		
		ComponentRuntime component = getDashBoard().getComponent(provider);
		
		List<String> l = new ArrayList<String>();
		
		//trick for the map osm drill
		//it looks if it's a drill on the map itself or not
		if(component.getElement() instanceof ComponentOsmMap) {
			if(value.contains(";")) {
				l.add(component.getName());
				return l;
			}
		}
		
		
		List<ComponentRuntime> targets = new ArrayList<ComponentRuntime>();

		try {
			if(provider.equals("folder")) {
				for(IBaseElement p : ((Folder)component.getElement()).getContent()) {
					FolderPage page = (FolderPage) p;
					FdModel model = (FdModel) page.getContent().get(0);
					if(model.getName().equals(value) || page.getName().equals(value)) {
						for(IBaseElement elem : model.getContent()) {
							if(elem instanceof StackableCell) {
								for(IBaseElement e : ((StackableCell)elem).getContent()) {
									l.add(e.getName());
									targets.add(parent.getComponent(e.getName()));
								}
								l.add(elem.getName());
								targets.add(parent.getComponent(elem.getName()));
							}
							else if(elem instanceof Cell) {
								
								l.add(((Cell)elem).getContent().get(0).getName());
								targets.add(parent.getComponent(((Cell)elem).getContent().get(0).getName()));
							}
							
						}
						break;
					}
				}
			}
		} catch(Exception e1) {
		}
		
		targets.addAll(component.getTargets(true));
		

		//XXX
		try {
			boolean add = true;
			for(ComponentRuntime t : targets) {
				

				if (t instanceof ComponentContainer && ((ComponentContainer) t).getComponentDefinition() instanceof DrillDrivenStackableCell) {

					if (!provider.equals("folder")) {
						String newProvider = provider;
						if(provider.equals("Toolbar_Filter_Direction") || 
								provider.equals("Toolbar_Filter_DGA") || 
								provider.equals("Toolbar_Filter_Collectivites") || 
								provider.equals("Toolbar_Filter_Budget") || 
								provider.equals("Filter_Exercices") || 
								provider.equals("Filter_Sections")) {
							String direction = state.getComponentValue("Toolbar_Filter_Direction");
							if(direction == null || direction.equals("%")) {
								String dga = state.getComponentValue("Toolbar_Filter_DGA");
								if(dga == null || dga.equals("%")) {
									state.setComponentValue(t.getName(), "Toolbar_Filter_Collectivites");
									ComponentRuntime comp = getDashBoard().getComponent("Toolbar_Filter_Collectivites");
									newProvider = "Toolbar_Filter_Collectivites";
									for(ComponentRuntime ta : comp.getTargets(true)) {
										l.add(ta.getName());
									}
								}
								else {
									state.setComponentValue(t.getName(), "Toolbar_Filter_DGA");
									ComponentRuntime comp = getDashBoard().getComponent("Toolbar_Filter_DGA");
									newProvider = "Toolbar_Filter_DGA";
									for(ComponentRuntime ta : comp.getTargets(true)) {
										l.add(ta.getName());
									}
								}
							}
							else {
								state.setComponentValue(t.getName(), "Toolbar_Filter_Direction");
								ComponentRuntime comp = getDashBoard().getComponent("Toolbar_Filter_Direction");
								newProvider = "Toolbar_Filter_Direction";
								for(ComponentRuntime ta : comp.getTargets(true)) {
									l.add(ta.getName());
								}
							}
						}
						
						else if(provider.equals("FilterCollectivites") || 
								provider.equals("FilterDGA") || 
								provider.equals("FilterDirection") || 
								provider.equals("FilterAnnee")) {
							String direction = state.getComponentValue("FilterDirection");
							if(direction == null || direction.equals("%")) {
								String dga = state.getComponentValue("FilterDGA");
								if(dga == null || dga.equals("%")) {
									state.setComponentValue(t.getName(), "FilterCollectivites");
									ComponentRuntime comp = getDashBoard().getComponent("FilterCollectivites");
									newProvider = "FilterCollectivites";
									for(ComponentRuntime ta : comp.getTargets(true)) {
										l.add(ta.getName());
									}
								}
								else {
									state.setComponentValue(t.getName(), "FilterDGA");
									ComponentRuntime comp = getDashBoard().getComponent("FilterDGA");
									newProvider = "FilterDGA";
									for(ComponentRuntime ta : comp.getTargets(true)) {
										l.add(ta.getName());
									}
								}
							}
							else {
								state.setComponentValue(t.getName(), "FilterDirection");
								ComponentRuntime comp = getDashBoard().getComponent("FilterDirection");
								newProvider = "FilterDirection";
								for(ComponentRuntime ta : comp.getTargets(true)) {
									l.add(ta.getName());
								}
							}
						}
						else {
							add = true;
						}
////						// XXX for nimes
////						if (!state.getComponentValue("_superusers_").contains(group.getName()) && (provider.equals("Toolbar_Filter_Collectivites") || provider.equals("FilterCollectivites") || provider.equals("Filter_Exercices") || provider.equals("FilterAnnee"))) {
////							String dga = state.getComponentValue("Toolbar_Filter_DGA");
////							if (dga == null || dga.isEmpty()) {
////								dga = state.getComponentValue("FilterDGA");
////							}
	////
////							if (dga != null && !dga.equals("%")) {
////								if (provider.equals("Toolbar_Filter_Collectivites") || provider.equals("Filter_Exercices")) {
////									state.setComponentValue(t.getName(), "Toolbar_Filter_DGA");
////								} else if (provider.equals("FilterCollectivites") || provider.equals("FilterAnnee")) {
////									state.setComponentValue(t.getName(), "FilterDGA");
////								}
////								add = false;
////							}
////						}
	////
						if (add) {
							state.setComponentValue(t.getName(), newProvider);
						}
					}
					if (add) {
						dirtyComponents.add(t.getName());
						l.add(t.getName());
					}
				} else {
					dirtyComponents.add(t.getName());
					l.add(t.getName());
				}
			}
		} catch(Exception e1) {
		}
		
		//XXX
		
		
//		for(ComponentRuntime t : targets) {
//			l.add(t.getName());
//
//			if(t instanceof ComponentContainer && ((ComponentContainer) t).getComponentDefinition() instanceof DrillDrivenStackableCell && !provider.equals("folder")) {
//				state.setComponentValue(t.getName(), provider);
//			}
//		}
		
		for(ComponentRuntime t : targets) {
			if(t instanceof ComponentContainer && ((ComponentContainer) t).getComponentDefinition() instanceof DrillDrivenStackableCell) {
				String compName = state.getComponentValue(t.getName());
				if(compName != null) {
					try {
						for(IComponentDefinition conf : ((HashMap<IComponentDefinition, ComponentConfig>)((DrillDrivenStackableCell)((ComponentContainer) t).getComponentDefinition()).getConfigurations()).keySet()) {
							DrillDrivenComponentConfig c = (DrillDrivenComponentConfig) ((HashMap<IComponentDefinition, ComponentConfig>)((DrillDrivenStackableCell)((ComponentContainer) t).getComponentDefinition()).getConfigurations()).get(conf);
							if(!c.getController().getName().equals(compName)) {
								l.remove(conf.getName());
								dirtyComponents.remove(conf.getName());
							}
						}
					} catch(Exception e) {
					}
				}
			}
		}
		
		try {
			String pageName = state.getComponentValue("folder");
			if(pageName != null) {
				ComponentRuntime folder = parent.getComponent("folder");
				for(IBaseElement p : ((Folder)folder.getElement()).getContent()) {
					FolderPage page = (FolderPage) p;
					FdModel model = (FdModel) page.getContent().get(0);
					if(!page.getName().equals(pageName)) {
						for(IBaseElement elem : model.getContent()) {
							if(elem instanceof StackableCell) {
								for(IBaseElement e : ((StackableCell)elem).getContent()) {
									l.remove(e.getName());
								}
								l.remove(elem.getName());
							}
							else if(elem instanceof Cell) {					
								l.remove(((Cell)elem).getContent().get(0).getName());
							}			
						}
					}
				}
			}
		} catch(Exception e) {
		}
		//XXX
		Set<String> set = new LinkedHashSet<String>(l);
		l = new ArrayList<String>(set);
		
		Collections.sort(l, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if(o1.equals("FilterDGA") || o1.equals("Toolbar_Filter_DGA") 
						|| o1.equals("FilterDirection") || o1.equals("Toolbar_Filter_Direction")) {
					return -1;
				}
				else if(o2.equals("FilterDGA") || o2.equals("Toolbar_Filter_DGA") 
						|| o2.equals("FilterDirection") || o2.equals("Toolbar_Filter_Direction")) {
					return 1;
				}
				else if(o2.startsWith("DrillDrivenStackableCell")) {
					return -1;
				}
				else if(o1.startsWith("DrillDrivenStackableCell")) {
					return -1;
				}
				else if(o2.equals("folder")) {
					return -1;
				}
				else if(o1.equals("folder")) {
					return -1;
				}
				return 0;
			}
			
		});
		//XXX
		return l;

	}

	/**
	 * used by charts with drillTYpe = Dimension it returns the Chart Xml
	 * 
	 * @param provider
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public String drillDown(String chartName, String value) throws Exception {
		Component component = (Component) getDashBoard().getComponent(chartName);
		DrillState drill = state.getDrillState(chartName);

		if(drill.drillDown(value)) {
			dirtyComponents.add(component.getName());
			return chartName;
		}
		else {
			return null;
		}

	}

	public DashBoard getDashBoard() {
		return parent;
	}

	public String getUuid() {
		return uuid;
	}

	/**
	 * execute IQuery of the component and return iits full HTML this method should only be called when rendering the Dashboard for the first time.
	 * 
	 * @param componentName
	 * @return
	 * @throws Exception
	 */
	public String renderComponent(String componentName, String printParam) throws Exception {
		System.out.println("Checking " + componentName + " " + printParam);
		
		boolean print = false;
		if (printParam != null && Boolean.parseBoolean(printParam)) {
			print = true;
		}
		return getRenderingContent(componentName, false, print);
	}

	/**
	 * refresh the datas of the Component with the given name if needed and return the HTML code to display for the Component. The HTML generated will be the part that can change, not the fixed one
	 * 
	 * @param componentName
	 * @return
	 * @throws Exception
	 */
	public String refreshComponent(String componentName) throws Exception {
		return getRenderingContent(componentName, true, false);
	}

	private String getRenderingContent(String componentName, boolean refresh, boolean print) throws Exception {
		ComponentRuntime component = getDashBoard().getComponent(componentName);
		IConnection connection = null;
		IQuery query = null;
		IResultSet resultSet = null;
		if(component instanceof Component) {
			
			//try to make something work for this postgresql pile of shit
			//the thing can't use the same connection twice
			//so we'll to use a new connection for each query
			try {
				DataSource datasource = getDashBoard().getQueryProvider().getDatasource(((Component) component).getDataSet());
				IDriver odaDriver = DriverManager.getOdaDriver(datasource);
				connection = odaDriver.getConnection(datasource.getOdaExtensionDataSourceId());
				connection.open(datasource.getProperties());
				
				try {
					query = connection.newQuery(((Component) component).getDataSet().getOdaExtensionDataSetId());
				} catch(Exception e2) {
					query = connection.newQuery("");
				}
				
				query.setAppContext(group);
				query.prepare(((Component) component).getDataSet().getQueryText());
				
				if(query != null) {
					boolean b = ((Component) component).prepareQuery(query, state);
					if(b) {
						
						resultSet = query.executeQuery();
						resultSet = ((Component) component).adapt(state, resultSet);
					}
				}
				
				//We set the print option
				state.setComponentValue("print", String.valueOf(print));
			
			
				dirtyComponents.remove(componentName);
				
				String result = getDashBoard().getRenderer().renderComponent(component, state, resultSet, refresh).toString();
				return result;
			} catch(Exception e2) {
//				e2.printStackTrace();
			} finally {
				if(resultSet != null) {
					resultSet.close();
				}
				if(query != null) {
					query.close();
				}
				if(connection != null) {
					connection.close();
				}
			}
		}
		
		//We set the print option
		state.setComponentValue("print", String.valueOf(print));
		
		String result = getDashBoard().getRenderer().renderComponent(component, state, resultSet, refresh).toString();
		return result;
			
//			/*
//			 * create a query and set its parameters from the state
//			 */
//
//			try {
//				
// 				System.out.println("Executing query for component : " + componentName);
//				
//				query = getDashBoard().getQueryProvider().getQuery(group, ((Component) component).getDataSet());
//				
//				if(query != null) {
//				
//					System.out.println("Rendering component : " + componentName);
//					boolean b = false;
//					try {
//						b = ((Component) component).prepareQuery(query, state);
//					} catch (Exception e1) {
//						try {
//							query = getDashBoard().getQueryProvider().getNewQuery(group, ((Component) component).getDataSet());
//							b = ((Component) component).prepareQuery(query, state);
//						} catch (Exception e) {
//							e.printStackTrace();
//							throw e;
//						}
//					}
//					
//					if(query != null && b) {
//						try {
//							resultSet = query.executeQuery();
//						} catch (Exception e) {
//							
//							
//							query = getDashBoard().getQueryProvider().getNewQuery(group, ((Component) component).getDataSet());
//							b = ((Component) component).prepareQuery(query, state);
//							resultSet = query.executeQuery();
//						}
//						resultSet = ((Component) component).adapt(state, resultSet);
//					}
//				}
//
//			} catch(Exception ex) {
//				ex.printStackTrace();
//				Logger.getLogger(getClass()).warn("Failed to execute queyr for Component " + componentName, ex);
//			} finally {}
//		}
//		dirtyComponents.remove(componentName);
//		try {
//			
//			return getDashBoard().getRenderer().renderComponent(component, state, resultSet, refresh).toString();
//		} 
//		catch(Exception e) {
//			e.printStackTrace();
//			throw e;
//		}
//		finally {
//			if(query != null) {
//				query.close();
//			}
//			if(resultSet != null) {
//				resultSet.close();
//			}
//		}

	}

	public String getLocale() {
		return localeLanguage;
	}

	public DashState getState() {
		return state;
	}

	public List<String> getDirtyComponents() {
		return new ArrayList<String>(dirtyComponents);
	}

	public String drillUp(String componentName) throws Exception {
		DrillState drill = state.getDrillState(componentName);

		if(drill != null && drill.drillUp()) {
			return componentName;
		}
		else {
			return null;
		}
	}

}
