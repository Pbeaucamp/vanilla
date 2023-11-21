package bpm.united.olap.wrapper.servlet.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPQuery;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.unitedolap.UnitedOlapMember;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.parse.DigesterCubeView;
import bpm.office.core.ExcelFunctionsUtils;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstantsForFMDT;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class CubeHelper {
	
	private static IVanillaAPI vanillaApi;
	
	
	public static String generateResultXml(OLAPResult result, boolean drillthrough) {
		
		if(drillthrough) {
			return generateDrillThroughXml(result);
		}
		
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement("result");
		root.addElement("ifirst").setText(result.getXFixed()+"");
		root.addElement("jfirst").setText(result.getYFixed()+"");
		
		for(int i = 0 ; i < result.getRaw().size() ; i++) {
			
			Element row = root.addElement("row");
			
			for(int j = 0 ; j < result.getRaw().get(i).size() ; j++) {
				Element cell = row.addElement("cell");
				
				Item item = result.getRaw().get(i).get(j);
				
				if(item instanceof ItemNull) {
					cell.addElement("type").setText("null");
				}
				else if(item instanceof ItemElement) {
					cell.addElement("type").setText("element");
					cell.addElement("label").setText(((ItemElement)item).getLabel());
					cell.addElement("uname").setText(((ItemElement)item).getDataMember().getUniqueName());
				}
				else if(item instanceof ItemValue) {
					cell.addElement("type").setText("value");
					cell.addElement("label").setText(((ItemValue)item).getLabel());
					cell.addElement("uname").setText(((ItemValue)item).getDrillThroughSql());
				}
			}
		}
		
		return doc.asXML();
	}
	


	private static String generateDrillThroughXml(OLAPResult result) {
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement("drillthrough");
		
		for(int i = 0 ; i < result.getRaw().size() ; i++) {
			Element row = null;
			row = root.addElement("row");
			
			for(int j = 0 ; j < result.getRaw().get(i).size() ; j++) {
				Element cell = row.addElement("cell");
				
				Item item = result.getRaw().get(i).get(j);
				
				if(item instanceof ItemNull) {
					cell.addElement("type").setText("null");
				}
				else if(item instanceof ItemElement) {
					cell.addElement("type").setText("element");
					cell.addElement("label").setText(((ItemElement)item).getLabel());
					cell.addElement("uname").setText(((ItemElement)item).getDataMember().getUniqueName());
				}
				else if(item instanceof ItemValue) {
					cell.addElement("type").setText("value");
					cell.addElement("label").setText(((ItemValue)item).getLabel());
					cell.addElement("uname").setText(((ItemValue)item).getDrillThroughSql());
				}
			}
		}
		
		
		return doc.asXML();
	}
/*
	public static String drill(String rowItem, String colItem, OLAPCube cube) throws Exception {
		
		int row = Integer.parseInt(rowItem);
		int col = Integer.parseInt(colItem);
		
		List<ArrayList<Item>> tp = cube.getLastResult().getRaw();
		
		OLAPResult result = null;
		
		boolean drillthrough = false;
		ItemElement itemSearched =null;
		
		Item item = tp.get(row).get(col);
		if(item instanceof ItemElement) {
			ItemElement it = (ItemElement) item;
			if(!it.isDrilled()) {
				cube.drilldown(it);
			}
			else {
				cube.drillup(it);
			}
			result = cube.doQuery();
		}
		else if(item instanceof ItemValue) {
			ItemValue it = (ItemValue) item;
			result = cube.drillthrough(it, 1);
			drillthrough = true;
		}
		
		else {
			result = cube.getLastResult();
		}
		
		return generateResultXml(result, drillthrough);
	}
	*/
	
	
public static String drillPerUname(String uname, OLAPCube cube) throws Exception {
		try{
		
	//	List<ArrayList<Item>> tp = cube.getLastResult().getRaw();
		
		OLAPResult result = null;
		
		boolean drillthrough = false;
		//Item itemSearched =null;
		Item itemSearched =  getCubeItemPerName(uname, cube);
		
	/*	
		for (int row=0; row<tp.size();row++){
			for(int col=0;col<tp.get(row).size(); col++){
				Item item = tp.get(row).get(col);
				if(item instanceof ItemElement) {
					//String uniqueName =((ItemElement)item).getDataMember().getUniqueName();			
					if(((ItemElement)item).getDataMember().getUniqueName().equals(uname))
						itemSearched=item;
				}else if(item instanceof ItemValue) {
					if(((ItemValue)item).getDrillThroughSql().matches(uname))
						itemSearched=item;
				}
				
				if (itemSearched!=null)
					break;
			}
			if (itemSearched!=null)
				break;
		}
		*/
		if (itemSearched!=null){
			if(itemSearched instanceof ItemElement) {
				ItemElement it = (ItemElement) itemSearched;
				if(!it.isDrilled()) {
					cube.drilldown(it);
				}
				else {
					cube.drillup(it);
				}
				result = cube.doQuery();
			}
			else if(itemSearched instanceof ItemValue) {
				ItemValue it = (ItemValue) itemSearched;
				result = cube.drillthrough(it, 1);
				drillthrough = true;
			}
			
			else {
				result = cube.getLastResult();
			}
		}
			
		return generateResultXml(result, drillthrough);
		
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}


	
	public static String drillAllPerUname(String uname, OLAPCube cube) throws Exception {
		try{
		
		OLAPResult result = null;
	
		Item itemSearched = getCubeItemPerName(uname,  cube);	
		
		
		
		if (itemSearched!=null){
			if(itemSearched instanceof ItemElement) {
				ItemElement it = (ItemElement) itemSearched;
				OLAPMember member = cube.findOLAPMember(uname);
				
				Collection<OLAPMember> sybillingMembers ;
				if(member.getParent()!=null){
					sybillingMembers=(Collection<OLAPMember>)member.getParent().getMembers();					
					
					for (OLAPMember childMember : sybillingMembers) {
						ItemElement childItem = new ItemElement(childMember.getUniqueName(), it.isCol());
		
						if (!it.isDrilled()) {
							cube.drilldown(childItem);
						}
						else {
							cube.drillup(childItem);
						}
					}
					
					result = cube.doQuery();
				}	
				else
					return drillPerUname(uname, cube);
				}
			else {
					result = cube.getLastResult();
			}
		}
			
		return generateResultXml(result, false);
		
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}


	private static Item getCubeItemPerName(String uname, OLAPCube cube){
		try{		
			List<ArrayList<Item>> tp = cube.getLastResult().getRaw();
			
			Item itemSearched =null;
			
			for (int row=0; row<tp.size();row++){
				for(int col=0;col<tp.get(row).size(); col++){
					Item item = tp.get(row).get(col);
					if(item instanceof ItemElement) {
						//String uniqueName =((ItemElement)item).getDataMember().getUniqueName();			
						if(((ItemElement)item).getDataMember().getUniqueName().equals(uname))
							itemSearched=item;
					}else if(item instanceof ItemValue) {
						if(((ItemValue)item).getDrillThroughSql().matches(uname))
							itemSearched=item;
					}
					
					if (itemSearched!=null)
						break;
				}
				if (itemSearched!=null)
					break;
			}
			return itemSearched;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	

	public static String executeAction(String action, ExcelSession session, HttpServletRequest req) throws Exception {
		
		if(action.equals(ExcelConstantes.ACTION_SWAP)) {
			session.getCube().swapAxes();
		}
		
		else if(action.equals(ExcelConstantes.ACTION_UNDO)) {
			session.getCube().undo();
		}
		
		else if(action.equals(ExcelConstantes.ACTION_REDO)) {
			session.getCube().redo();
		}
		
		else if(action.equals(ExcelConstantes.ACTION_ADD)) {
			
			String uname = req.getParameter(ExcelConstantes.PARAMETER_ITEMUNAME);
			String axis = req.getParameter(ExcelConstantes.PARAMETER_AXIS);
			
			ItemElement elem = null;
			if(axis.equals("col")) {
				elem = new ItemElement(uname, true);
			}
			else {
				elem = new ItemElement(uname, false);
			}
			
			if(axis.equals("where")) {
				session.getCube().addWhere(elem);
			}
			else {
				session.getCube().add(elem);
			}
		}
		
		
		else if(action.equals(ExcelConstantes.ACTION_ADDMEASURE)) {
			
			String uname = req.getParameter(ExcelConstantes.PARAMETER_ITEMUNAME);		
			
			Item itemSearched = null;
			for(MeasureGroup mesGrp : session.getCube().getMeasures()) {
				for(Measure mes : mesGrp.getMeasures()) {
					itemSearched = getCubeItemPerName(mes.getUniqueName(), session.getCube());
					if (itemSearched!=null) 
						break;
				}
				if (itemSearched!=null) 
					break;
			}
			
			ItemElement elem = null;
			
			if (itemSearched!=null && itemSearched instanceof ItemElement)
			{
					elem = new ItemElement(uname, ((ItemElement)itemSearched).isCol());
			} else 
					elem = new ItemElement(uname, false);
							
			session.getCube().add(elem);
		}
		
		else if(action.equals(ExcelConstantes.ACTION_REMOVE)) {
			String uname = req.getParameter(ExcelConstantes.PARAMETER_ITEMUNAME);
			String axis = req.getParameter(ExcelConstantes.PARAMETER_AXIS);
			//ItemElement e = findItemElement(session.getCube(), uname);
			
			
			if(axis.equals("where")) {
				ItemElement e = findItemElement(session.getCube(), uname);
				session.getCube().removeWhere(e);
			}else
			{
				ItemElement e = findItemElement1(session.getCube(), uname);
				session.getCube().remove(e);
			}
				
			
		}
		
		else if(action.equals(ExcelConstantes.ACTION_ADDFILTERS)) {
			String xml = req.getParameter(ExcelConstantes.PARAMETER_XML);
			addFilters(xml, session);
		}
		
		return generateResultXml(session.getCube().doQuery(), false);
	}

	private static void addFilters(String xml, ExcelSession session) throws Exception {
		Document doc = DocumentHelper.parseText(xml);
		
		session.getCube().getMdx().getWhere().clear();
		
		Element root = doc.getRootElement();
		for(Element filterElem : (List<Element>) root.elements("filter"))  {
			String uname = filterElem.getText();
			session.getCube().getMdx().addWhere(uname);
		}
	}
	

	
	private static ItemElement findItemElement1(OLAPCube cube, String uname) {
		try{
			List<ArrayList<Item>> tp = cube.getLastResult().getRaw();
			
			ItemElement itemSearched =null;
			
			for (int row=0; row<tp.size();row++){
				for(int col=0;col<tp.get(row).size(); col++){
					Item item = tp.get(row).get(col);
					if(item instanceof ItemElement) {		
						if(((ItemElement)item).getDataMember().getUniqueName().equals(uname))
							itemSearched=(ItemElement)item;
					}
					
					if (itemSearched!=null)
						break;
				}
				if (itemSearched!=null)
					break;
			}
			
			return itemSearched;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	private static ItemElement findItemElement(OLAPCube cube, String uname) {
		
		for(Dimension dim : cube.getDimensions()) {
			for(Hierarchy hiera : dim.getHierarchies()) {
				if(uname.startsWith(hiera.getUniqueName())) {	
					return new ItemElement(cube.findOLAPMember(uname), false,false);
					//return new ItemElement(hiera.getDefaultMember().findMember(uname), false,false);
					
				}
			}
		}	
		return null;	
	}
	
	/*
	private static ItemElement findItemElement1(OLAPCube cube, String uname) {
		try{
			
			List<ArrayList<Item>> tp = cube.getLastResult().getRaw();
			
			ItemElement itemSearched =null;
			
			for (int row=0; row<tp.size();row++){
				for(int col=0;col<tp.get(row).size(); col++){
					Item item = tp.get(row).get(col);
					if(item instanceof ItemElement) {
						//String uniqueName =((ItemElement)item).getDataMember().getUniqueName();			
						if(((ItemElement)item).getDataMember().getUniqueName().equals(uname))
							itemSearched= (ItemElement)item;
					}
					if (itemSearched!=null)
						break;
				}
				if (itemSearched!=null)
					break;
			}	
			return itemSearched;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
*/

	public static String getCubeStructure(ExcelSession session) {
		
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement(ExcelConstantes.REQUEST_CUBESTRUCTURE);
		
		for(Dimension dim : session.getCube().getDimensions()) {
			
			Element dimElem = root.addElement("dimension");
			dimElem.addElement("name").setText(dim.getName());
			dimElem.addElement("uname").setText(dim.getUniqueName());
			
			for(Hierarchy hiera : dim.getHierarchies()) {
				
				Element hieraElem = dimElem.addElement("hierarchy");
				hieraElem.addElement("name").setText(hiera.getName());
				hieraElem.addElement("uname").setText(hiera.getUniqueName());
				
				for(Level level : hiera.getLevel()) {
					
					Element levelElem = hieraElem.addElement("level");
					levelElem.addElement("name").setText(level.getName());
					levelElem.addElement("uname").setText(level.getUniqueName());
					
				}
				
				Element allMbElem = hieraElem.addElement("member");
				allMbElem.addElement("name").setText("All " + dim.getName());
				allMbElem.addElement("uname").setText(hiera.getUniqueName() + ".[All " + dim.getName() + "]");
				
			}
			
		}
/*		
		for(MeasureGroup mesGrp : session.getCube().getMeasures()) {
			for(Measure mes : mesGrp.getMeasures()) {
				
				Element dimElem = root.addElement("measure");
				dimElem.addElement("name").setText(mes.getName());
				dimElem.addElement("uname").setText(mes.getUniqueName());
				
			}
		}
		*/
		return doc.asXML();
	}
	
	
	
	public static String getMeasure(ExcelSession session){
		
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement(ExcelConstantes.REQUEST_CUBEMEASURES);
		for(MeasureGroup mesGrp : session.getCube().getMeasures()) {
			for(Measure mes : mesGrp.getMeasures()) {
				
				Element dimElem = root.addElement("measure");
				dimElem.addElement("name").setText(mes.getName());
				dimElem.addElement("uname").setText(mes.getUniqueName());				
			}
		}
		
		return doc.asXML();
	}
	
	

	public static String getSubMembers(String uname, ExcelSession session) throws Exception {
		
		ItemElement item = findItemElement(session.getCube(), uname);
		OLAPMember mem = item.getDataMember();
		
		session.getCube().addChilds(mem,mem.getHiera());
		
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement(ExcelConstantes.REQUEST_GETSUBMEMBERS);
		
		for(Object m : mem.getMembers()) {
			Element memElem = root.addElement("member");
			memElem.addElement("name").setText(((OLAPMember)m).getName());
			memElem.addElement("uname").setText(((OLAPMember)m).getUniqueName());
		}
		
		return doc.asXML();
		
	}
	

	public static String refreshCube(String xml, ExcelSession session) throws Exception {
		
		OLAPQuery query = session.getCube().getMdx();
		query.getCols().clear();
		query.getRows().clear();
		
		Document doc = DocumentHelper.parseText(xml);
		
		for(Element colElem : (List<Element>)doc.getRootElement().elements("col")) {
			query.getCols().add(colElem.getText());
		}
		for(Element rowElem : (List<Element>)doc.getRootElement().elements("row")) {
			query.getRows().add(rowElem.getText());
		}
		for(Element mesElem : (List<Element>)doc.getRootElement().elements("mes")) {
			query.getRows().add(mesElem.getText());
		}
		
		return generateResultXml(session.getCube().doQuery(),false);
	}
	
	
public static String refreshCube1(ExcelSession session) throws Exception {				
		return generateResultXml(session.getCube().doQuery(),false);
	}

public static String clearCube(ExcelSession session) throws Exception {		
	session.getCube().restore();
	return generateResultXml(session.getCube().doQuery(),false);
}

public static String getView(ExcelSession session) throws Exception {		
	RepositoryCubeView view= session.getCube().getView();	
	return view.getXML();
}

public static String getListFilters(ExcelSession session) throws Exception {		
	Collection<String> filters= session.getCube().getFilters();	
	String listfilters ="";
	for(String filter : filters ){
		listfilters+= filter+"; " ;
	}	
	return listfilters;
}


public static String addDocument (String directoryId , String fileName, InputStream stream, ExcelSession session){
	try {
		IRepositoryApi repositoryApi =getRepositoryApi(session);
		IRepositoryContext ctx = getRepositoryContext(session);
		return ExcelFunctionsUtils.addDocument(directoryId, fileName, stream, repositoryApi, ctx);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}
	//return "failed";
}


public static String updateDocument (String directoryId , String fileName, InputStream stream, ExcelSession session){
	try {
		IRepositoryApi repositoryApi =getRepositoryApi(session);
		IRepositoryContext ctx = getRepositoryContext(session);
		return ExcelFunctionsUtils.UpdateDocument(directoryId, fileName, stream, repositoryApi, ctx);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}
	//return "failed";
}



public static String createDir (String directoryId , String name,  ExcelSession session){
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



public static String deleteDir (String directoryId , ExcelSession session){
	try {
		IRepositoryApi repositoryApi =getRepositoryApi(session);
		return ExcelFunctionsUtils.deleteDir(directoryId, repositoryApi);

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}
}


public static String deleteItem (String directoryId , ExcelSession session){
	try {
		IRepositoryApi repositoryApi =getRepositoryApi(session);
		IRepositoryContext ctx = getRepositoryContext(session);
		return ExcelFunctionsUtils.deleteItem(directoryId, repositoryApi, ctx);

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return e.getMessage();
	}
}



public static InputStream downloadFile (String directoryId , ExcelSession session){
	try {	
		IRepositoryApi repositoryApi = getRepositoryApi(session);
		return ExcelFunctionsUtils.downloadFile(directoryId, repositoryApi);

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
}


public static String generateXML(String type, ExcelSession session){	
	try {	
		IRepositoryApi repositoryApi = getRepositoryApi(session);
		return ExcelFunctionsUtils.generateXML(type, repositoryApi);
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}		
}


public static HashMap<String, Object> returnParameters (List<FileItem> items) throws IOException{	
	try{
		HashMap<String, Object> map = new HashMap<String, Object>();
        for (FileItem item : items) {
            if (item.isFormField()) {
               map.put(item.getFieldName(), item.getString("UTF-8")) ;
            } 
            else {
            	map.put(VanillaConstantsForFMDT.PARAMETER_FILE, item.getInputStream());
            }
        }
      return map;  
      
	}catch (Exception e) {
		e.printStackTrace();
		//logger.error("returnParameters function failed", e);
		return null;		
	}	
}


private static IRepositoryApi getRepositoryApi (ExcelSession session) throws Exception{
	
	IVanillaAPI api = getVanillaApi();
	
	IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
	IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(vanillaCtx, session.getGroup(), session.getRepository()));
	
	return repositoryApi;
}


public static IVanillaAPI getVanillaApi() {
	if(vanillaApi == null) {
		vanillaApi = new RemoteVanillaPlatform(
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
	}
	return vanillaApi;
}


private static IRepositoryContext getRepositoryContext (ExcelSession session){
	IVanillaAPI api = getVanillaApi();
	
	IVanillaContext vanillaCtx = new BaseVanillaContext(api.getVanillaUrl(), session.getUser(), session.getPassword());
	IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, session.getGroup(), session.getRepository());
	
	return ctx;
}



}
