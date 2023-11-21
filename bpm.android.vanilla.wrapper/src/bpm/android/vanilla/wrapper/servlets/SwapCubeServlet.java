package bpm.android.vanilla.wrapper.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;

/**
 * Servlet implementation class SwapCubeServlet
 */
public class SwapCubeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ComponentAndroidWrapper component;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SwapCubeServlet(ComponentAndroidWrapper component) {
        super();
        this.component = component;
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStream is = request.getInputStream();
		String req = IOUtils.toString(is, "UTF-8");
		is.close();
		
		String resp =  null;
		
		try{
			String sessionId = request.getParameter("sessionId");
			SessionContent session = component.getSessionHolder().getSession(sessionId);
			session.setUsedTime();
			
			resp = prepareResponse(req, session);
		}catch(Exception ex){
			component.getLogger().error("Error Swapping Cube axes - " + ex.getMessage(), ex);
			resp = ex.toString();
		}	

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}
	
	private String prepareResponse(String req, SessionContent session){
		IAndroidRepositoryManager manager = session.getRepositoryManager();

		String response = "";
		try {
			String html = manager.swapCube();
			
			response = prepareXml(html, "");
		} catch (Throwable e) {
			e.printStackTrace();
			prepareXml(null, e.toString());
		}
		
		return response;
	}

	private String prepareXml(String html, String error){
		if(error.equals("")){
			String toResp;
			toResp  = "<rootElement>\n";
			toResp += "    <htmlpage>\n";
			toResp += html;
			toResp += "    </htmlpage>\n";
			toResp += "</rootElement>";
			
			return toResp;
		}
		else{
			String toResp;
			toResp  = "<error>" + error + "</error>";
			
			return toResp;
		}
	}
	
//	private String prepareXml(HashMap<RepositoryItem, byte[]> views, List<String> cols, List<String> rows, List<Dimension> dimsAvailable, 
//			List<MeasureGroup> measAvailable, String html, String error){
//		if(error.equals("")){
//			String toResp;
//			toResp  = "<rootElement>\n";
//			toResp += "    <cube>\n";
//			toResp += "	       <views>\n";
//			for(RepositoryItem view : views.keySet()){
//				toResp += "	           <view>\n";
//				toResp += "                <id>" + view.getId() + "</id>\n";
//				toResp += "                <name>" + view.getItemName() + "</name>\n";
//				
//				String img = "";
//				for(byte b : views.get(view)) {
//					img+= b + ":";
//				}
//				img = img.substring(0, img.length() - 1);
//				
//				toResp += "                <image>" + img + "</image>\n";
//				toResp += "	           </view>\n";				
//			}
//			toResp += "	       </views>\n";
//			toResp += "	       <dimensions>\n";
//			for(Dimension dim : dimsAvailable){
//				toResp += "	           <dimension>\n";
//				toResp += "                <uniquename>" + dim.getUniqueName() + "</uniquename>\n";
//				toResp += "                <name>" + dim.getName() + "</name>\n";
//				toResp += "	               <hierarchies>\n";
//				for(Hierarchy hier : dim.getHierarchies()){
//					//Check if the hier is load by default and if it is in row or in column
//					boolean isLoad = false;
//					boolean isCol = false;
//					for(String col : cols){
//						if(col.contains(hier.getUniqueName())){
//							isLoad = true;
//							isCol = true;
//							break;
//						}
//					}
//					if(isLoad == false)
//					for(String row : rows){
//						if(row.contains(hier.getUniqueName())){
//							isLoad = true;
//							break;
//						}
//					}
//					toResp += "                    <hierarchy>\n";
//					toResp += "                        <uniquename>" + hier.getUniqueName() + "</uniquename>\n";
//					toResp += "                        <name>" + hier.getName() + "</name>\n";
//					toResp += "                        <isload>" + isLoad + "</isload>\n";
//					toResp += "                        <iscol>" + isCol + "</iscol>\n";
//					toResp += "                    </hierarchy>\n";
//				}
//				toResp += "	               </hierarchies>\n";
//				toResp += "	           </dimension>\n";
//			}
//			toResp += "	       </dimensions>\n";
//			
//			toResp += "	       <measuregroups>\n";
//			for(MeasureGroup meaGroup : measAvailable){
//				toResp += "	           <measuregroup>\n";
//				toResp += "                <uniquename>" + meaGroup.getUniqueName() + "</uniquename>\n";
//				toResp += "                <name>" + meaGroup.getName() + "</name>\n";
//				toResp += "	               <measures>\n";
//				for(Measure mea : meaGroup.getMeasures()){
//					boolean isLoad = false;
//					boolean isCol = false;
//					for(String col : cols){
//						if(col.contains(mea.getUniqueName())){
//							isLoad = true;
//							isCol = true;
//							break;
//						}
//					}
//					if(isLoad == false)
//					for(String row : rows){
//						if(row.contains(mea.getUniqueName())){
//							isLoad = true;
//							break;
//						}
//					}
//					toResp += "                    <measure>\n";
//					toResp += "                        <uniquename>" + mea.getUniqueName() + "</uniquename>\n";
//					toResp += "                        <name>" + mea.getName() + "</name>\n";
//					toResp += "                        <isload>" + isLoad + "</isload>\n";
//					toResp += "                        <iscol>" + isCol + "</iscol>\n";
//					toResp += "                    </measure>\n";
//				}
//				toResp += "	               </measures>\n";
//				toResp += "	           </measuregroup>\n";
//			}
//			toResp += "	       </measuregroups>\n";
//			toResp += "    </cube>\n";
//			toResp += "    <htmlpage>\n";
//			toResp += html;
//			toResp += "    </htmlpage>\n";
//			toResp += "</rootElement>";
//			
//			return toResp;
//		}
//		else{
//			String toResp;
//			toResp  = "<error>" + error + "</error>";
//			
//			return toResp;
//		}
//	}

}
