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
import bpm.android.vanilla.core.beans.AndroidCube;
import bpm.android.vanilla.core.beans.AndroidCubeView;
import bpm.android.vanilla.core.beans.cube.AndroidDimension;
import bpm.android.vanilla.core.beans.cube.AndroidMeasureGroup;
import bpm.android.vanilla.core.beans.cube.HierarchyAndCol;
import bpm.android.vanilla.core.beans.cube.MeasureAndCol;
import bpm.android.vanilla.wrapper.ComponentAndroidWrapper;
import bpm.android.vanilla.wrapper.tools.SessionContent;

/**
 * Servlet implementation class InitCubeServlet
 */
public class InitCubeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ComponentAndroidWrapper component;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitCubeServlet(ComponentAndroidWrapper component) {
    	super();
    	this.component = component;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { }

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
			component.getLogger().error("Error when initing Cube - " + ex.getMessage(), ex);
			resp = ex.toString();
		}		

		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(resp);
	}
	
	private String prepareResponse(String req, SessionContent session){
		IAndroidRepositoryManager manager = session.getRepositoryManager();
		
		AndroidCube cube = session.getAndroidCube();

		String response = "";
		try {
			AndroidCube cubeInited = manager.initCube(cube);
			
			response = prepareXml(cubeInited, "");
		} catch (Throwable e) {
			e.printStackTrace();
			prepareXml(null, e.toString());
		}
		
		return response;
	}

	private String prepareXml(AndroidCube cube, String error){
		if(error.isEmpty()){
			String toResp;
			toResp  = "<rootElement>\n";
			toResp += "    <cube>\n";
			toResp += "	       <views>\n";
			if(cube.getViews() != null) {
				for(AndroidCubeView view : cube.getViews()){
					toResp += "	           <view>\n";
					toResp += "                <id>" + view.getId() + "</id>\n";
					toResp += "                <name>" + view.getName() + "</name>\n";
					toResp += "                <image>" + view.getImage() + "</image>\n";
					toResp += "	           </view>\n";				
				}
			}
			toResp += "	       </views>\n";
			toResp += "	       <dimensions>\n";
			for(AndroidDimension dim : cube.getDimensions()){
				toResp += "	           <dimension>\n";
				toResp += "                <uniquename>" + dim.getUniqueName() + "</uniquename>\n";
				toResp += "                <name>" + dim.getName() + "</name>\n";
				toResp += "	               <hierarchies>\n";
				for(HierarchyAndCol hier : dim.getHierarchyAndCol()){
					toResp += "                    <hierarchy>\n";
					toResp += "                        <uniquename>" + hier.getUniqueName() + "</uniquename>\n";
					toResp += "                        <name>" + hier.getName() + "</name>\n";
					toResp += "                        <isload>" + hier.isLoad() + "</isload>\n";
					toResp += "                        <iscol>" + hier.getIsCol() + "</iscol>\n";
					toResp += "                    </hierarchy>\n";
				}
				toResp += "	               </hierarchies>\n";
				toResp += "	           </dimension>\n";
			}
			toResp += "	       </dimensions>\n";
			
			toResp += "	       <measuregroups>\n";
			for(AndroidMeasureGroup meaGroup : cube.getMeasures()){
				toResp += "	           <measuregroup>\n";
				toResp += "                <uniquename>" + meaGroup.getUniqueName() + "</uniquename>\n";
				toResp += "                <name>" + meaGroup.getName() + "</name>\n";
				toResp += "	               <measures>\n";
				for(MeasureAndCol mea : meaGroup.getMeasuresAndCol()){
					toResp += "                    <measure>\n";
					toResp += "                        <uniquename>" + mea.getUniqueName() + "</uniquename>\n";
					toResp += "                        <name>" + mea.getName() + "</name>\n";
					toResp += "                        <isload>" + mea.isLoad() + "</isload>\n";
					toResp += "                        <iscol>" + mea.getIsCol() + "</iscol>\n";
					toResp += "                    </measure>\n";
				}
				toResp += "	               </measures>\n";
				toResp += "	           </measuregroup>\n";
			}
			toResp += "	       </measuregroups>\n";
			toResp += "    </cube>\n";
			toResp += "    <htmlpage>\n";
			toResp += cube.getHtml();
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

}
