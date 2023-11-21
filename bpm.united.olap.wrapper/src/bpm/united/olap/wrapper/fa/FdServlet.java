package bpm.united.olap.wrapper.fa;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemProperties;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.olap.xmla.XMLAMember;
import bpm.fa.api.repository.FaApiHelper;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.parse.DigesterCubeView;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.united.olap.wrapper.UnitedOlapWrapperComponent;
import bpm.united.olap.wrapper.fa.html.FaGrid;
import bpm.united.olap.wrapper.fa.html.GridCube;
import bpm.united.olap.wrapper.fa.html.HtmlElement;
import bpm.united.olap.wrapper.fa.html.HtmlItem;
import bpm.united.olap.wrapper.fa.html.HtmlValue;
import bpm.united.olap.wrapper.fa.html.ItemCube;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaSession;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FdServlet extends HttpServlet {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2920265574638775747L;

	private UnitedOlapWrapperComponent component;
	private FaApiHelper apiHelper;
	//http://localhost:8888/FdServlet?_viewId=181&_group=System&_user=system&_password=system&_repId=1&_encrypted=false&width=1000
	
	public FdServlet(UnitedOlapWrapperComponent faComponent) {
		this.component = faComponent;
		this.apiHelper = new FaApiHelper(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), new UnitedOlapLoader());
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		IVanillaContext vanillaContext = null;
		
		try{
			vanillaContext = createVanillaContext(req);
		}catch(Exception ex){
			resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unable to create VanillaContext from sessionId - " + ex.getMessage());
			return;
		}
		
		
//		String user = req.getParameter(Constantes.USER);
//		String password = req.getParameter(Constantes.PASSWORD);
		int groupId = Integer.parseInt(req.getParameter(Constantes.GROUP));
		int viewId = Integer.parseInt(req.getParameter(Constantes.VIEWID));
		
		int repositoryId = Integer.parseInt(req.getParameter(Constantes.REPID));
//		boolean isEncrypted = Boolean.parseBoolean(req.getParameter(Constantes.ENCRYPTED));
		
		this.component.getLogger().info("FaView {id=" + viewId + ";repId=" + repositoryId + ";group=" + groupId + ";user=" + vanillaContext.getLogin() + "}asked");
		HashMap<String, String> parameters = new HashMap<String, String>();
		Enumeration<String> paramNames = req.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			if(!Constantes.getViewInfos().contains(name)) {
				parameters.put(name, req.getParameter(name));
			}
		}
		
		
		RemoteVanillaPlatform vanillaApi = new RemoteVanillaPlatform(vanillaContext);
		
		bpm.vanilla.platform.core.beans.Repository repDef = null;
		try {
			this.component.getLogger().debug("looking for Repository {id=" + repositoryId + "}");
			repDef = vanillaApi.getVanillaRepositoryManager().getRepositoryById(repositoryId);
			if (repDef == null){
				throw new Exception("No repository with id " + repositoryId + " has been found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.component.getLogger().error("Unable to find repositoryId - " + e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to find repositoryId - " + e.getMessage());
		}
		
		Group grp = null;
		try {
			this.component.getLogger().debug("looking for Group {nameid=" + groupId + "}");
			grp = vanillaApi.getVanillaSecurityManager().getGroupById(groupId);
			if (grp == null){
				throw new Exception("No Group named " + groupId);
			}
		} catch (Exception e3) {
			e3.printStackTrace();
			this.component.getLogger().error("Unable to find VanillaGroup named " + groupId + " - " + e3.getMessage(), e3);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to find VanillaGroup named " + groupId + " - " + e3.getMessage());

		}
		
		IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, grp, repDef);
		
		IRepositoryApi sock = new RemoteRepositoryApi(ctx);
		
		RepositoryItem item = null;
		try {
			item = sock.getRepositoryService().getDirectoryItem(viewId);
			
			if (item == null){
				throw new Exception("The FaView with id = " + viewId + " does not exist or is not available for this user/group");
			}
			
			if (item.getType() != IRepositoryApi.FAV_TYPE){
				throw new Exception("The object is not an Fa View but a " + IRepositoryApi.TYPES_NAMES[item.getType()]);
			}
			
			
		} catch (Exception e3) {
			e3.printStackTrace();
			this.component.getLogger().error("Unable to find directoryItem " + viewId + " - " + e3.getMessage(), e3);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to find directoryItem " + viewId + " - " + e3.getMessage());

		}
		String itemXML = null;
		try {
			itemXML = sock.getRepositoryService().loadModel(item);
			
			
		} catch (Exception e2) {
			e2.printStackTrace();
			this.component.getLogger().error("Unable to load XML for FaView with directoryItem " + item.getId() + " - " + e2.getMessage(), e2);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load XML for FaView with directoryItem " + item.getId() + " - " + e2.getMessage());

		}
		String cubeName = null; 
		
		
		Document doc = null;
		Integer fasdItemId = null;
		try {
			int start = itemXML.indexOf("<cubename>") + 10;
			int end = itemXML.indexOf("</cubename>");
			cubeName = itemXML.substring(start, end);
			
			start = itemXML.indexOf("<fasdid>") + 8;
			end = itemXML.indexOf("</fasdid>");
			fasdItemId = Integer.parseInt( itemXML.substring(start, end));
			
			doc = DocumentHelper.parseText(itemXML);
		} catch (DocumentException e2) {
			e2.printStackTrace();
			this.component.getLogger().error("Unable to parse FaView XML with directoryItem " + item.getId() + " - " + e2.getMessage(), e2);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to parse XML FaView for with directoryItem " + item.getId() + " - " + e2.getMessage());

		}
		
				
		
		Element root = doc.getRootElement();
		
		String viewXml = root.element("view").asXML();
		
		DigesterCubeView dig = null;
		
		try{
			dig = new DigesterCubeView(viewXml);
		}catch(Exception ex){
			StringBuilder b = new StringBuilder();
			b.append("Unable to parse FAV model - ");
			b.append(ex.getMessage());
			Logger.getLogger(getClass()).error(b.toString(), ex);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, b.toString());
			return;
		}
		RepositoryCubeView view = dig.getCubeView();
		
		
		OLAPCube olapCube = null;
		try {
			IObjectIdentifier identifier = new ObjectIdentifier(repositoryId, fasdItemId);
			IRuntimeContext runCtx = new RuntimeContext(vanillaContext.getLogin(), vanillaContext.getPassword(), grp.getName(), grp.getId());
			olapCube = apiHelper.getCube(identifier, runCtx, cubeName);
		} catch (Exception e) {
			e.printStackTrace();
			this.component.getLogger().error("Unable to get OLAPCube named " + cubeName + " - " + e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to get OLAPCube named " + cubeName + " - " + e.getMessage());
			return;
		}
		
		for(Parameter param : view.getParameters()) {
			param.setValue(parameters.get(param.getName()));
		}
		
		olapCube.setView(view);
		OLAPResult res = null;
		try {
			res = olapCube.doQuery();
		} catch (Exception e) {
			e.printStackTrace();
			this.component.getLogger().error("Unable to perfom OLAPQuery for the view  - " + e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to perfom OLAPQuery for the view  - "  + e.getMessage());
			return;
		}
		
		
		try{
			String viewName = item.getName();
			
			String html = getHtml(getTable(res), ctx, req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID), viewName, fasdItemId, cubeName);
			
			ServletOutputStream outputStream = resp.getOutputStream();
			outputStream.print(html);
		}catch(Throwable t){
			this.component.getLogger().error("Unable to build HTML for the view - " + t.getMessage(), t);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to build HTML for the view - " + t.getMessage());
			return;
		}
		
	}

	
	
	
	private IVanillaContext createVanillaContext(HttpServletRequest req) throws Exception {
		String sessionId = req.getHeader(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		if (sessionId == null){
			sessionId = (String)req.getAttribute(VanillaConstants.HTTP_HEADER_VANILLA_SESSION_ID);
		}
		VanillaConfiguration conf = ConfigurationManager.getInstance().getVanillaConfiguration();
		IVanillaAPI api = new RemoteVanillaPlatform(
				conf.getVanillaServerUrl(), 
				conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), 
				conf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN));
		
		
		VanillaSession session = api.getVanillaSystemManager().getSession(sessionId);
		User user = session.getUser();
		
		IVanillaContext ctx = new BaseVanillaContext(conf.getVanillaServerUrl(), user.getLogin(), user.getPassword());
		return ctx;
	}

	public static String getHtml(GridCube cube, IRepositoryContext ctx, String sessionId, String viewName, Integer fasdItemId, String cubeName) {
		
		for(int i = 0 ; i < cube.getNbOfRow() ; i++) {
			if(((ItemCube)cube.getIJ(i, 0)).getType().equalsIgnoreCase("ItemElement")) {
				cube.setiFirst(i);
				break;
			}
		}
		for(int j = 0 ; j < cube.getNbOfCol() ; j++) {
			if(((ItemCube)cube.getIJ(cube.getIFirst(), j)).getType().equalsIgnoreCase("ItemValue")) {
				cube.setjFirst(j);
				break;
			}
		}
		
		int maxLenght = 0;
		
		FaGrid grid = new FaGrid();
		
		//Lists for total rows and cols
		List<Integer> totalRows = new ArrayList<Integer>();
		List<Integer> totalCols = new ArrayList<Integer>();
		
		//An hashmap who contains previous row itemElement (for Spanning)
		HashMap<Integer, Integer> precRowItem = new HashMap<Integer, Integer>();
		for(int i = 0 ; i < cube.getItems().size() ; i++) {
			//the previous column itemElement (for Spanning)
			int precColItem = -1;
			for(int j = 0 ; j < ((List)cube.getItems().get(i)).size() ; j++) {
				boolean isLastRow = false;
				boolean isLastCol = false;
				
				if(i == cube.getNbOfRow() - 1) {
					isLastRow = true;
				}
				if(j == cube.getNbOfCol() - 1) {
					isLastCol = true;
				}
				
				ItemCube curr = (ItemCube)  ((List) cube.getItems().get(i)).get(j);
				if(curr.getLabel().length() > maxLenght) {
					maxLenght = curr.getLabel().length();
				}
				
				//if ItemElement
				if(curr.getType().equalsIgnoreCase("ItemElement") && !curr.getLabel().equalsIgnoreCase("")) {
					HtmlItem lbl = null;
					
//					if(curr.getUname().contains("[Measures]")) {
//						FaWebMainComposite.allMeasuresAdded.put(curr.getUname(), curr.getLabel());
//					}

					String label = curr.getLabel();

					lbl = new HtmlItem(label, curr.getUname(), i, j, curr.getLabel());	
					
					
					//keep the precedent ItemElement for rowspan/colspan
					if(i < cube.getIFirst()) {
						precColItem = j;
						lbl.addStyleName("gridItemBorder");
					}
					else {
						precRowItem.put(j, i);
						lbl.addStyleName("gridItemBorder");
					}
					
					addEndStyle(isLastRow,isLastCol,lbl);
					
					grid.setWidget(i, j, lbl);
				}
				
				//if ItemValue
				else if(curr.getType().equalsIgnoreCase("ItemValue")) {
//					if(FaWebMainComposite.isOn) {
						HtmlValue lbl = null;
						if(curr.getLabel().equalsIgnoreCase(" ")) {
							lbl = new HtmlValue("&nbsp;", i, j);
						}
						else {
							
							lbl = new HtmlValue(curr.getLabel(), i, j);
						}
						lbl.addStyleName("gridItemBorder");
						addEndStyle(isLastRow,isLastCol,lbl);
						grid.setWidget(i, j, lbl);
//					}
				}
				
				//if ItemProperty
				else if(curr.getType().equalsIgnoreCase("ItemProperties")) {
					HtmlItem lbl = new HtmlItem(curr.getLabel());
					lbl.addStyleName("gridItemBorder");
					lbl.addStyleName("gridItemValue");
					addEndStyle(isLastRow,isLastCol,lbl);
					grid.setWidget(i, j, lbl);
				}
				
				//if ItemNull
				else {
					HtmlItem lbl = new HtmlItem("&nbsp;");
					addEndStyle(isLastRow,isLastCol,lbl);
					grid.setWidget(i, j, lbl);
					if(isSpanning(i, j, precColItem, precRowItem, cube, grid)) {
						if(i < cube.getIFirst()) {
							lbl.addStyleName("rightGridSpanItemBorder");
							lbl.addStyleName("draggableGridItem");

						}
						else {
							lbl.addStyleName("leftGridSpanItemBorder");
							lbl.addStyleName("draggableGridItem");

						}
					}
					
					else {
						//if its a total element
						if(isTotal(i,j,lbl,cube,grid)) {
							if(i < cube.getIFirst()) {
								totalCols.add(j);
							}
							else {
								totalRows.add(i);
							}
							
						}
						else {
							lbl.addStyleName("gridItemBorder");
							lbl.addStyleName("gridItemNull");
						}
					}
				}
				
			}
		}
		
		maxLenght = 14;
		
		grid.setProperty("height", cube.getItems().size() * 30 + "px");
		grid.setProperty("width", ((List)cube.getItems().get(0)).size() * 7 * maxLenght + "px");
		
		//add size and styles to cells
		for(int i = 0 ; i < cube.getItems().size() ; i++) {
			boolean isTotalRow = false;
			if(totalRows.contains(i)) {
				isTotalRow = true;
			}
			for(int j = 0 ; j < ((List)cube.getItems().get(i)).size() ; j++) {
				try {
					grid.getWidget(i, j).setProperty("width", 7 * maxLenght + "px");
					grid.getWidget(i, j).setProperty("min-width", 7 * maxLenght - 2 + "px");
					if(grid.getWidget(i, j) instanceof HtmlItem) {
						if(((HtmlItem)grid.getWidget(i, j)).isLast()) {
							grid.getWidget(i, j).setProperty("max-width", 7 * maxLenght - 4 + "px");
							grid.getWidget(i, j).setProperty("min-width", 7 * maxLenght - 4 + "px");
						}
						else {
							grid.getWidget(i, j).setProperty("max-width", 7 * maxLenght - 2 + "px");
						}
						grid.getWidget(i, j).setProperty("td-max-width", 7 * maxLenght + "px");
					}
					else if(grid.getWidget(i, j).getStyleName().contains("lastColItemBorder") && !grid.getWidget(i, j).getStyleName().contains("rightGridSpanItemBorder")) {
						grid.getWidget(i, j).setProperty("max-width", 7 * maxLenght - 4 + "px");
						grid.getWidget(i, j).setProperty("min-width", 7 * maxLenght - 4 + "px");
					}
					grid.getWidget(i, j).setProperty("height","30px");
					grid.getWidget(i, j).setProperty("td-width", 7 * maxLenght + "px");
					grid.getWidget(i, j).setProperty("td-min-width", 7 * maxLenght + "px");
//					grid.getCellFormatter().addStyleName(i, j, "cubeView");
//					if(isTotalRow && j >= FaWebMainComposite.infosReport.getJFirst()) {
//						grid.getCellFormatter().addStyleName(i, j, "gridItemValueBold");
//					}
//					if(totalCols.contains(j) && i >= infosReport.getIFirst()) {
//						grid.getCellFormatter().addStyleName(i, j, "gridItemValueBold");
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
		
		
		
		
		return grid.getGridHtml(ctx, sessionId, viewName, fasdItemId, cubeName);
	}

	private static boolean isTotal(int i, int j, HtmlElement lbl, GridCube infosReport, FaGrid grid) {
		boolean isTotal = false;
		if(i < infosReport.getIFirst() && j >= infosReport.getJFirst()) {
			isTotal = true;
			try {
				HtmlElement w = grid.getWidget(i - 1, j);
				if(w instanceof HtmlItem) {
					lbl.setHTML("&nbsp;&nbsp;");
					lbl.addStyleName("gridItemBorder");
					lbl.addStyleName("gridTotalItem");
					return true;
				}
				else {
					for(int row = i - 1 ; row > -1 ; row--) {
						if(((HtmlElement)grid.getWidget(row, j)).getHTML().equals("&nbsp;&nbsp;")) {
							lbl.addStyleName("leftGridSpanItemBorder");
							lbl.addStyleName("gridTotalItem");
							return true;
						}
					}
					lbl.addStyleName("gridTotalItem");
					for(int col = j - 1 ; col > -1 ; col--) {
						if(((HtmlElement)grid.getWidget(i, col)).getHTML().equals("&nbsp;&nbsp;")) {
							lbl.addStyleName("rightGridSpanItemBorder");
						}
					}
				}
				if(!lbl.getStyleName().contains("rightGridSpanItemBorder")) {
					lbl.addStyleName("rightGridSpanItemBorder");
				}
			} catch (Exception e) {

			}
		}
		else if(j < infosReport.getJFirst() && i >= infosReport.getIFirst()) {
			isTotal = true;
			try {
				HtmlElement w = grid.getWidget(i, j - 1);
				if(w instanceof HtmlItem) {
					lbl.setHTML("&nbsp;&nbsp;");
					lbl.addStyleName("gridItemBorder");
					lbl.addStyleName("gridTotalItem");
					return true;
				}
				else {
					for(int col = j - 1 ; col > -1 ; col--) {
						if(((HtmlElement)grid.getWidget(i, col)).getHTML().equals("&nbsp;&nbsp;")) {
							lbl.addStyleName("rightGridSpanItemBorder");
							lbl.addStyleName("gridTotalItem");
							return true;
						}
					}
					lbl.addStyleName("gridTotalItem");
					for(int row = i - 1 ; row > -1 ; row--) {
						if(((HtmlElement)grid.getWidget(row, j)).getHTML().equals("&nbsp;&nbsp;")) {
							lbl.addStyleName("leftGridSpanItemBorder");
						}
					}
				}
				if(!lbl.getStyleName().contains("leftGridSpanItemBorder")) {
					lbl.addStyleName("leftGridSpanItemBorder");
				}
			} catch (Exception e) {

			}
		}
		return isTotal;
	}

	private static boolean isSpanning(int i, int j, int precColItem, HashMap<Integer, Integer> precRowItem, GridCube infosReport, FaGrid grid) {
		boolean spanning = false;
		
		//If first element return false
		if(i < infosReport.getIFirst() && j < infosReport.getJFirst()) {
			return false;
		}
		
		//For a column element
		if(i < infosReport.getIFirst()) {
			try {
				if(j > precColItem && precColItem != -1) {
					spanning = true;
					if(((ItemCube)infosReport.getIJ(i, precColItem)).getUname().contains("[Measures]")) {
						return true;
					}					
				}
				//For a child item
				HtmlElement o = (grid.getWidget(i - 1, j));
				if(o instanceof HtmlItem) {
					spanning = false;
				}
				else {
					if(o.getStyleName().contains("gridItemNull")) {
						spanning = false;
					}
					else if(o.getStyleName().contains("gridTotalItem")) {
						spanning = false;
					}
				}
			} catch(Exception e) {
				spanning = true;
			}
		}
		else if(j < infosReport.getJFirst()) {
			try {
				//For a row element
				if(precRowItem.containsKey(j)) {
					if(i > precRowItem.get(j) ) {
						spanning = true;
						if(((ItemCube)infosReport.getIJ(precRowItem.get(j), j)).getUname().contains("[Measures]")) {
							return true;
						}	
					}
				}
				//For a child item
				HtmlElement o = (grid.getWidget(i, j - 1));
				if(o instanceof HtmlItem) {
					spanning = false;
				}
				else {
					if(o.getStyleName().contains("gridItemNull")) {
						spanning = false;
					}
					else if(o.getStyleName().contains("gridTotalItem")) {
						spanning = false;
					}
				}
			} catch(Exception e) {
				spanning = true;
			}
		}
		else {
			return false;
		}
		return spanning;
	}

	private static void addEndStyle(boolean isLastRow, boolean isLastCol, HtmlElement lbl) {
		if(isLastCol) {
			lbl.addStyleName("lastColItemBorder");
		}
		if(isLastRow) {
			lbl.addStyleName("lastRowItemBorder");
		}
		if(lbl instanceof HtmlItem) {
			HtmlItem item = (HtmlItem) lbl;
			if(isLastCol) {
				item.setLast(true);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public static GridCube getTable(OLAPResult res) {
		List tab = new ArrayList();
		tab.add(new ArrayList());
		tab = res.getRaw();

		
		List items = new ArrayList();
		HashMap itemsByUname = new HashMap();
		
		for (int i=0; i < tab.size(); i++) {//row
//			System.out.println("");
			ArrayList/*<Item>*/ Itable = (ArrayList) tab.get(i);
			items.add(i,new ArrayList());
			for (int j=0; j < Itable.size(); j++) { //col
				Object item = Itable.get(j);
				if(Itable.get(j) instanceof ItemNull){
					ItemCube itc = new ItemCube("", "ItemNull");
					((List) items.get(i)).add(j, itc) ;
					
				}
				else if (Itable.get(j) instanceof ItemElement) {
					ItemElement itemElement = ((ItemElement) Itable.get(j));
					
					if(itemElement.getDataMember() instanceof XMLAMember) {
						((XMLAMember)itemElement.getDataMember()).setUname(((XMLAMember)itemElement.getDataMember()).getUniqueName().replace("&", "&amp;"));
					}
					
					String uname = itemElement.getDataMember().getUniqueName();
					ItemCube itc = new ItemCube(itemElement.getLabel(), uname, "ItemElement");
					((List) items.get(i)).add(j, itc) ;	
					
					itemsByUname.put(uname, itemElement);
					
				}
				else if (item instanceof ItemValue) {
//					if (session.isFirstValue()) {
//						session.getInfosReport().setJFirst(j);
//						session.getInfosReport().setIFirst(i);
//						session.setFirstValue(false);
//					}
					ItemValue itv = (ItemValue) item;
					String v = itv.getValue();

					float test = 0.0f;
					try {
						if (v != null && !v.equalsIgnoreCase("")) {
							try {
								test = Float.parseFloat(v.replaceAll(",", ".").replace(" ", ""));
							} catch (Exception e) {
								test = NumberFormat.getInstance().parse(v.replaceAll(",", ".").replace(" ", "")).floatValue();
							}
						}
						else if (v != null && !itv.getLabel().equalsIgnoreCase("")) {
							test = Float.parseFloat(itv.getLabel());
						}
					} catch (Exception e) {
						test = Float.parseFloat(v.replaceAll(",", ""));
					}

					ItemCube itc;
					if (Math.abs(test - 0.0001f) > 0.0001f || itv.getLabel().contains("%")) {
//						System.out.println(itv.getLabel());
						itc = new ItemCube(itv.getValue(), "ItemValue");
//						System.out.println(itc.getLabel());
						try {
							float va = Float.parseFloat(v);
							itc.setValue(va);
						} catch (Exception e) {

						}

					}
					else {
						itc = new ItemCube(" ", "ItemValue");
						itc.setValue(0);
					}

					((List) items.get(i)).add(j, itc);
				}
				else if (Itable.get(j) instanceof ItemProperties) {
					ItemCube itc = null;
					if (((ItemProperties) Itable.get(j)).getLabel().equalsIgnoreCase("")) {
						itc = new ItemCube("", "ItemNull");
					}
					else{
						itc = new ItemCube(((ItemProperties) Itable.get(j)).getLabel(), "uname", "ItemProperties");
					}
					((List) items.get(i)).add(j, itc) ;
				}
				else {
					ItemCube itc = new ItemCube("", "ItemNull");
					((List) items.get(i)).add(j, itc) ;
				}
			}
		}
		
		GridCube gc = new GridCube();
		gc.setItems(items);	

		return gc;
	}
	
	
}

