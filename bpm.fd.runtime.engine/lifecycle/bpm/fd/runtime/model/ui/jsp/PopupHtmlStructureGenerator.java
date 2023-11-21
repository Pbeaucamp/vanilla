package bpm.fd.runtime.model.ui.jsp;

import java.awt.Rectangle;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.runtime.engine.VanillaProfil;
import bpm.fd.runtime.model.Component;
import bpm.fd.runtime.model.ComponentRuntime;
import bpm.fd.runtime.model.DashInstance;
import bpm.fd.runtime.model.ui.jsp.renderer.IHTMLRenderer;

public class PopupHtmlStructureGenerator extends HtmlStructureGenerator{
	private DashInstance instance;
	public PopupHtmlStructureGenerator(DashInstance instance, String runtimeApiVersion) {
		super(runtimeApiVersion);
		this.instance = instance;
	}
	@Override
	protected void generateComponent(int offset, IComponentDefinition def,
			String outputParameterName, VanillaProfil vanillaProfil)
			throws Exception {
		
		StringBuffer buf = new StringBuffer();
		buf.append(instance.renderComponent(def.getName(), null));

		
		html.append(buf.toString());
	}
	public String zoom(ComponentRuntime component, int width,int height) {
		StringBuffer buf = new StringBuffer();
		IHTMLRenderer renderer = JSPRenderer.get(component.getElement().getClass());
		
		if (renderer != null){
			Rectangle layout = null;
			if (component instanceof Component){
				layout = new Rectangle(((Component)component).getLayout());
				layout.width = width;
				layout.height = height;
				IQuery query = null;
				IResultSet resultSet = null;
				try{
					query = instance.getDashBoard().getQueryProvider().getQuery(instance.getGroup(), ((Component) component).getDataSet());
					
					if (query != null && ((Component)component).prepareQuery(query, instance.getState())){
						resultSet = query.executeQuery();
						resultSet = ((Component)component).adapt(instance.getState(), resultSet);
					}
					
					buf.append(renderer.getHTML(
							layout, 
							component.getElement(), 
							instance.getState(), 
							resultSet, 
							false));
				}catch(Exception ex){
					ex.printStackTrace();
					//Logger.getLogger(getClass()).warn("Failed to execute queyr for Component " + componentName, ex);
				}finally{
					if (query != null){
						try{
							query.close();
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
					if (resultSet != null){
						try{
							resultSet.close();
						}catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
				
				return buf.toString();
				
			}
			
		}
		return "";
	}
}
