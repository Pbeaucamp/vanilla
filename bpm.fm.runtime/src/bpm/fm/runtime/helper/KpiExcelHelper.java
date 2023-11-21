package bpm.fm.runtime.helper;

import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.LoaderMetricValue;
import bpm.fm.runtime.FreeMetricsManagerComponent;
import bpm.fm.runtime.utils.LoaderValueUpdater;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.VanillaConstantsForFMDT;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;




public class KpiExcelHelper {

	private static IVanillaAPI vanillaApi;
	private IFreeMetricsManager component;
	
	public KpiExcelHelper(FreeMetricsManagerComponent componentProvider){
		this.component = componentProvider;
	}
	
	
public  String loadGroups(String user, String pass, Session session) throws Exception {
		
		IVanillaAPI api = getVanillaApi();
		
		User vanillaUser = api.getVanillaSecurityManager().authentify("", user, pass, false);
		
	//	List<Repository> repositories = api.getVanillaRepositoryManager().getUserRepositories(user);
		List<Group> groups = api.getVanillaSecurityManager().getGroups(vanillaUser);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(VanillaConstantsForFMDT.REQUEST_LOADGRPREPANDREPOSITORIES);
		
		
		Element elemGrps = root.addElement("groups");
		for(Group grp : groups) {
			Element elemGrp = elemGrps.addElement("group");
			elemGrp.addElement("name").setText(grp.getName());
			elemGrp.addElement("id").setText(grp.getId()+"");
		}
		
		session.setUser(user);
		session.setPassword(pass);
		
		return doc.asXML();
	}



public  String loadObservatories(String groupId, Session session) throws Exception {	
	
	IVanillaAPI api = getVanillaApi();
	
	List<Observatory> observatories= component.getObservatoriesByGroup(Integer.parseInt(groupId));
	
	//session.setGroup(grp);
	//session.setObservatory(null);
	//session.setTheme(null);

	Document doc = DocumentHelper.createDocument();
	Element root = doc.addElement(KpiExcelConstants.REQUEST_LOADOBSERVATORIES);
	
	Element elemGrps = root.addElement("observatories");
	for(Observatory obs : observatories) {
		Element elemGrp = elemGrps.addElement("observatory");
		elemGrp.addElement("name").setText(obs.getName());
		elemGrp.addElement("id").setText(obs.getId()+"");
	}
	session.setCurrentListObs(observatories);
	return doc.asXML();
}



public  String loadTheme(String observatoryId, Session session) throws Exception {	
	
	IVanillaAPI api = getVanillaApi();
      
	//Observatory observatory= component.getObservatoryById(Integer.parseInt(observatoryId));
	Observatory observatory=null;
	List<Observatory> observatories = session.getCurrentListObs();
	for(Observatory obs : observatories){
		if (obs.getId()==Integer.parseInt(observatoryId))
		{
			observatory=obs;
			break;
		}
	}
	
	if(observatory==null)
		observatory=observatories.get(0);
	
	List<Theme> themes = observatory.getThemes();
	
	Document doc = DocumentHelper.createDocument();
	Element root = doc.addElement(KpiExcelConstants.REQUEST_LOADTHEME);
	
	Element elemGrps = root.addElement("themes");
	for(Theme theme : themes) {
		Element elemGrp = elemGrps.addElement("theme");
		elemGrp.addElement("name").setText(theme.getName());
		elemGrp.addElement("id").setText(theme.getId()+"");
	}
	
	return doc.asXML();
}


public  String saveParam(String groupId, String observatoryId, String themeId, Session session) throws Exception {	
	
	IVanillaAPI api = getVanillaApi();
	
	Group grp = api.getVanillaSecurityManager().getGroupById(Integer.parseInt(groupId));
	
//	Observatory observatory= component.getObservatoryById(Integer.parseInt(observatoryId));
	Observatory observatory=null;
	List<Observatory> observatories = session.getCurrentListObs();
	for(Observatory obs : observatories){
		if (obs.getId()==Integer.parseInt(observatoryId))
		{
			observatory=obs;
			break;
		}
	}	
	if(observatory==null)
		observatory=observatories.get(0);
	
	//Theme theme = component.getThemeById(Integer.parseInt(themeId));
	Theme theme=null;
	List<Theme> themes = observatory.getThemes();
	for(Theme th : themes){
		if (th.getId()==Integer.parseInt(themeId))
		{
			theme=th;
			break;
		}
	}
	if(theme==null)
		theme=themes.get(0);
	
	//Repository repo = api.getVanillaRepositoryManager().getRepositoryById(Integer.parseInt(repId));
	
	session.setGroup(grp);
	session.setObservatory(observatory);
	session.setTheme(theme);	
	return "success";
}


public String getAxis(Session session){
	Theme theme = session.getTheme();
	List<Axis> listAxis= theme.getAxis();
	
	Document doc = DocumentHelper.createDocument();
	Element root = doc.addElement(KpiExcelConstants.REQUEST_GETAXIS);
		
	for(Axis axis : listAxis){
		Element elemGrp = root.addElement("axis");
		elemGrp.addElement("name").setText(axis.getName());
		elemGrp.addElement("id").setText(axis.getId()+"");
		for(Level children : axis.getChildren()){
			Element elemChild = elemGrp.addElement("member");
			elemChild.addElement("name").setText(children.getName());
			elemChild.addElement("id").setText(children.getId()+"");
			elemChild.addElement("level_depth").setText(children.getLevelIndex()+"");
			elemChild.addElement("parentid").setText(children.getParentId()+"");
			elemChild.addElement("axisid").setText(axis.getId()+"");
			elemChild.addElement("table_name").setText(children.getTableName()+"");
		}
	}
	return doc.asXML();
}


public String loadAxis(String axisId, Session session) throws NumberFormatException, Exception{
	try{
		Theme theme = session.getTheme();
		AxisInfo axis = component.getLoaderAxe(Integer.parseInt(axisId));
		
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(KpiExcelConstants.REQUEST_GETAXIS);	
		
		Element elemGrp = root.addElement("axis");
		elemGrp.addElement("name").setText(axis.getAxis().getName());
		elemGrp.addElement("id").setText(axis.getAxis().getId()+"");
			
		for(LevelMember member : axis.getMembers()){
			addLevelMember(member, elemGrp);
			}
		
		return doc.asXML();
	}
	catch(Exception e){
		e.printStackTrace();
		return e.getMessage();
	}
	
}


private void addLevelMember(LevelMember member, Element elParent){
	Element elemChild = elParent.addElement("member");
	elemChild.addElement("name").setText(member.getLabel());
	elemChild.addElement("id").setText(member.getValue());
	if (member.getParent()!=null)
		elemChild.addElement("parentid").setText(member.getParent().getValue());
	elemChild.addElement("level_name").setText(member.getLevel().getName());
	elemChild.addElement("level_id").setText(member.getLevel().getId()+"");
	elemChild.addElement("level_depth").setText(member.getLevel().getLevelIndex()+"");
	elemChild.addElement("table_name").setText(member.getLevel().getTableName()+"");
	for(LevelMember child : member.getChildren()){
		addLevelMember(child, elemChild);
	}
	
}



public String updateValue (String axisId, String deleteList, String updateList, String insertList, Session session) throws NumberFormatException, Exception{
	try{
		Theme theme = session.getTheme();
		AxisInfo axis = component.getLoaderAxe(Integer.parseInt(axisId));
		
		Document document = DocumentHelper.parseText(deleteList);
		List<LevelMember> listmemberDelete= convertTolListMember(document, axis);
		
		document = DocumentHelper.parseText(updateList);
		List<LevelMember> listmemberUpdate= convertTolListMember(document, axis);
		
		document = DocumentHelper.parseText(insertList);
		List<LevelMember> listmemberInsert= convertTolListMember(document, axis);
		
		String errorsdelete;
		String errorsInsert;
		String errorsUpdate;
		String errors ="";
		
		errorsdelete= LoaderValueUpdater.deleteValueMember(listmemberDelete);	
		errorsInsert=LoaderValueUpdater.insertValueMember(listmemberInsert);
		errorsUpdate=LoaderValueUpdater.updateValueMember(listmemberUpdate);
		
		if(errorsdelete!=null)
			errors=errorsdelete;
		if(errorsInsert!=null)
			errors+= errorsInsert;
		if(errorsUpdate!=null)
			errors+=errorsUpdate;

		if(errors==null || errors.equals(""))
			return "success";
		else
			return errors;
	}
	catch(Exception e){
		e.printStackTrace();
		return e.getMessage();
	}
}



private List<LevelMember> convertTolListMember(Document doc, AxisInfo axis){
	List<LevelMember> listMember = new ArrayList<LevelMember>();
	
	Element root =doc.getRootElement();
	for(Object obj : root.elements()){
		Element element= (Element)obj;
		LevelMember member = new LevelMember();

		for (Object objChild : element.elements()){
			Element child = (Element) objChild;
			if (child.getName().equals("name"))
				member.setLabel(child.getText());
			if (child.getName().equals("id"))
				member.setValue(child.getText());
			
			if (child.getName().equals("level_id"))
				{
					List<Level> levels= axis.getAxis().getChildren();
					for (Level level : levels ){
						if(level.getId()== Integer.parseInt(child.getText())){
							member.setLevel(level);
							break;
						}				
					}
				}
			
			if (child.getName().equals("parentid")){
				LevelMember memberParent = new LevelMember();
				memberParent.setValue(child.getText());
				member.setParent(memberParent);
			}
			
			if (child.getName().equals("member"))
				convertMember(child, member, axis);
		}
		listMember.add(member);
	}
	return listMember;
}



private void convertMember(Element currentElement, LevelMember parentMember,  AxisInfo axis){
	
	LevelMember member = new LevelMember();
	for (Object objChild : currentElement.elements()){
		Element child = (Element) objChild;
		if (child.getName().equals("name"))
			member.setLabel(child.getText());
		if (child.getName().equals("id"))
			member.setValue(child.getText());
		
		if (child.getName().equals("level_id"))
			{
				List<Level> levels= axis.getAxis().getChildren();
				for (Level level : levels ){
					if(level.getId()== Integer.parseInt(child.getText())){
						member.setLevel(level);
						break;
					}				
				}
			}
		
		if (child.getName().equals("parentid")){
			LevelMember memberParent = new LevelMember();
			memberParent.setValue(child.getText());
			member.setParent(memberParent);
		}
		
		if (child.getName().equals("member"))
			convertMember(child, member, axis);
	}
	
	parentMember.addChild(member);	
}



public String getMetrics(Session session){
	try{
		Theme theme = session.getTheme();
		List<Metric> metrics= theme.getMetrics();
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(KpiExcelConstants.REQUEST_GETMETRICS);	
		
	//	Element elemGrp = root.addElement("metrics");
		
		for(Metric metric : metrics){
			Element elemMetric = root.addElement("metric");
			elemMetric.addElement("id").setText(metric.getId()+"");
			elemMetric.addElement("name").setText(metric.getName());
			elemMetric.addElement("table_name").setText(((FactTable)metric.getFactTable()).getPeriodicity());
		}
		
		return doc.asXML();
	}
	catch(Exception e){
		e.printStackTrace();
		return e.getMessage();
	}
}



public String loadMetricValue(String metricId, String date, Session session){
	try{
		Theme theme = session.getTheme();
		List<Metric> metrics= theme.getMetrics();
		
		int id = Integer.parseInt(metricId);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		Date d = sdf.parse(date);
		
		//Metric metric = component.getMetric(id);
		//FactTable factTable= (FactTable)metric.getFactTable();
		//List<FactTableAxis> listAxis = factTable.getFactTableAxis();
		
		List<Integer> listId = new ArrayList<Integer>();
		listId.add(id);
		
		List<LoaderDataContainer> values = component.getValuesForLoader(listId, d);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(KpiExcelConstants.REQUEST_LOADMETRICVALUE);	
		
		Element title = root.addElement("title");
		title.addElement("value").setText("Value");
		title.addElement("objectif").setText("Objectif");
		title.addElement("min").setText("Min");
		title.addElement("max").setText("Max");
		for(AxisInfo info : values.get(0).getAxisInfos()){
			title.addElement("axis").setText(info.getAxis().getId()+":"+info.getAxis().getName());
		}
		/*for(AxisInfo info : values.get(0).getAxisInfos()){
			Element axis = title.addElement("axis");
			axis.addElement("name").setText(info.getAxis().getName());
			axis.addElement("id").setText(info.getAxis().getId()+"");
		}
		*/
		int idTemp =0;
		for(LoaderMetricValue metricValue : values.get(0).getValues()){
			Element value= root.addElement("metric_value");
			value.addElement("id").setText(idTemp+"");
			value.addElement("value").setText(metricValue.getValue()+"");
			value.addElement("objectif").setText(metricValue.getObjective()+"");
			value.addElement("min").setText(metricValue.getMinimum()+"");
			value.addElement("max").setText(metricValue.getMaximum()+"");
			for(LevelMember member : metricValue.getMembers()){
				//Element elmMember = title.addElement("member");
				//elmMember.addElement("name").setText(member.getLabel());
				//elmMember.addElement("id").setText((member.getValue()+""));
				value.addElement("member").setText(member.getLevel().getParent().getId()+":"+member.getValue()+":"+member.getLabel());
			}
			idTemp++;
		}
		return doc.asXML();
	}
	catch(Exception e){
		e.printStackTrace();
		return e.getMessage();
	}
}



public String getListMember(String axisId, Session session){
	try{
		Theme theme = session.getTheme();
		List<Metric> metrics= theme.getMetrics();
		
		AxisInfo axis = component.getLoaderAxe(Integer.parseInt(axisId));
		List<Level> levels = axis.getAxis().getChildren();
		int lastLevel=0;
		for(Level level : levels){
			if (level.getLevelIndex()>lastLevel)
				lastLevel=level.getLevelIndex();
		}

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(KpiExcelConstants.REQUEST_GETLISTMEMBER);	
		
		Element elemAxis = root.addElement("axis");
		//elemAxis.addElement("name").setText(axis.getAxis().getName());
		//elemAxis.addElement("id").setText(axis.getAxis().getId()+"");
			
		for(LevelMember member : axis.getMembers()){
			addLastLevelMember(member, elemAxis, lastLevel);
			}

		return doc.asXML();
	}
	catch(Exception e){
		e.printStackTrace();
		return e.getMessage();
	}
}


private void addLastLevelMember(LevelMember member, Element axis, int level){
	if (member.getLevel().getLevelIndex()==level)
		axis.addElement("member").setText( member.getValue()+":"+member.getLabel());
	else
		for(LevelMember child : member.getChildren()){
			addLastLevelMember(child, axis, level);
		}
}



public String updateMetricValue(String metricId, String date, String stringMetricValues, Session session){
	try
	{
		Document xmlMetricValues = DocumentHelper.parseText(stringMetricValues);
		Theme theme = session.getTheme();
		//List<Metric> metrics= theme.getMetrics();
		
		int id = Integer.parseInt(metricId);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		Date d = sdf.parse(date);
		
		List<Integer> listId = new ArrayList<Integer>();
		listId.add(id);
		
		List<LoaderDataContainer> values = component.getValuesForLoader(listId, d);
		
		List<LoaderMetricValue> metricValues= convertToMetricValue(xmlMetricValues, values.get(0), values.get(0).getMetric(), d);
		
		LoaderDataContainer value = new LoaderDataContainer();
		value.setAxisInfos(values.get(0).getAxisInfos());
		value.setMetric(values.get(0).getMetric());
		value.setValues(metricValues);
		
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		date=sdf.format(d);
		
		List<Exception> listException = component.updateValuesFromLoader(value, date);
		
		if (listException.size()>0){
			String error="";
			for(Exception e : listException){
				error+=e.getMessage()+"\n";
			}
			return error;
		}
		
		return "success";
	}	
	catch(Exception e){
		e.printStackTrace();
		return e.getMessage();
	}
}



private List<LoaderMetricValue> convertToMetricValue(Document xmlMetricValues, LoaderDataContainer dataContainer, Metric metric, Date date){
	
	List<LoaderMetricValue> metricValues = new ArrayList<LoaderMetricValue>();
	Element root =xmlMetricValues.getRootElement();
	
	for(Object obj : root.elements()){
		Element element = (Element) obj;
		LoaderMetricValue metricValue= new LoaderMetricValue();
		metricValue.setMetric(metric);
		metricValue.setDate(date);
		for (Object objChild : element.elements()){
			Element child = (Element) objChild;
			if (child.getName().equals("value"))
				if (!child.getText().isEmpty())
					metricValue.setValue(Double.parseDouble(child.getText().replaceAll(",",".")));
				else 
					metricValue.setValue(0);
			if (child.getName().equals("objectif"))
				if (!child.getText().isEmpty())
					metricValue.setObjective(Double.parseDouble(child.getText().replaceAll(",",".")));
				else
					metricValue.setObjective(0);
			if (child.getName().equals("min"))
				if (!child.getText().isEmpty())
					metricValue.setMinimum(Double.parseDouble(child.getText().replaceAll(",",".")));
				else
					metricValue.setMinimum(0);
			if (child.getName().equals("max"))
				if (!child.getText().isEmpty())
					metricValue.setMaximum(Double.parseDouble(child.getText().replaceAll(",",".")));
				else
					metricValue.setMaximum(0);
			
			if (child.getName().equals("state")){
				String state = child.getText();
				if(state.equals("New"))
						metricValue.setNew(true);
				else 
					if(state.equals("Update"))
						metricValue.setUpdated(true);
					else 
						if(state.equals("Delete"))
							metricValue.setDeleted(true);
			}
			
			if (child.getName().equals("member")){
				metricValue.addMember(findLevelMember(dataContainer.getAxisInfos(), child.getText()));
			}
		}
		metricValues.add(metricValue);
	}
	return metricValues;
}



private LevelMember findLevelMember (List<AxisInfo> listaxis, String member){
	try{
		LevelMember memberSearched = null;
		
		String[] split = member.split(":");
	    int axisId= Integer.parseInt(split[0]);
	    String memberId = split[1];
	    
	    AxisInfo axis=null;
	    for(AxisInfo axisInfo : listaxis){
	    	if(axisInfo.getAxis().getId()==axisId){
	    		axis=axisInfo;
	    		break;
	    	}   		
	    }
	    List<Level> levels = axis.getAxis().getChildren();
		int lastLevel=0;
		for(Level level : levels){
			if (level.getLevelIndex()>lastLevel)
				lastLevel=level.getLevelIndex();
		}
		if (lastLevel>0){
			for(LevelMember levelMember : axis.getMembers()){
				memberSearched=findMember(levelMember, lastLevel, memberId);
				if(memberSearched!=null)
					break;
			}
		}else{
			for(LevelMember levelMember : axis.getMembers()){
				if(levelMember.getValue().equals(memberId)){
					memberSearched=levelMember;
					break;
				}
			}
		}
		return memberSearched;
	}
	catch(Exception e){
		e.printStackTrace();
		return null;
	}
}



private LevelMember findMember (LevelMember member, int lastLevel, String id){
	try{
		if(member.getLevel().getLevelIndex()==lastLevel-1){
			for(LevelMember child : member.getChildren()){
				if(child.getValue().equals(id)){
					return child;
				}
			}
		}else{
			for(LevelMember child : member.getChildren()){
				LevelMember memberSearched = findMember(child, lastLevel, id);
				if(memberSearched!=null)
					return memberSearched;
			}
		}		
	}
	catch(Exception e){
		e.printStackTrace();		
	}
	return null;
}


/*
//Import Export Excel
public static String addDocument (String directoryId , String fileName, InputStream stream, Session session){
	try {
		IRepositoryApi repositoryApi =getRepositoryApi(session);
		return ExcelFunctionsUtils.addDocument(directoryId, fileName, stream, repositoryApi);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}
	//return "failed";
}



public static String createDir (String directoryId , String name,  Session session){
	try {
		IRepositoryApi repositoryApi =getRepositoryApi(session);
		return ExcelFunctionsUtils.createDir(directoryId, name, repositoryApi);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}
//	return "failed";
}



public static String deleteDir (String directoryId , Session session){
	try {
		IRepositoryApi repositoryApi =getRepositoryApi(session);
		return ExcelFunctionsUtils.deleteDir(directoryId, repositoryApi);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}
}


public static String deleteItem (String directoryId , Session session){
	try {
		IRepositoryApi repositoryApi =getRepositoryApi(session);
		return ExcelFunctionsUtils.deleteItem(directoryId, repositoryApi);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}
}



public static InputStream downloadFile (String directoryId , Session session){
	try {	
		IRepositoryApi repositoryApi = getRepositoryApi(session);
		return ExcelFunctionsUtils.downloadFile(directoryId, repositoryApi);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
}


private static IRepositoryApi getRepositoryApi (Session session) throws Exception{
	
	IVanillaAPI api = getVanillaApi();
	
	IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
	IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaCtx, session.getGroup(), session.getRepository()));
	
	return repositoryApi;
}
*/


public static IVanillaAPI getVanillaApi() {
	if(vanillaApi == null) {
		vanillaApi = new RemoteVanillaPlatform(
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
	}
	return vanillaApi;
}

	
}
