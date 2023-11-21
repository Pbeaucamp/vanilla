package bpm.odata.service.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;

import bpm.odata.service.KpiEdmProvider;
import bpm.odata.service.OpenDataComponent;
import bpm.odata.service.processors.KpiEntityCollectionProcessor;
import bpm.odata.service.processors.KpiEntityProcessor;

public class KpiServlet extends HttpServlet {

	public static final String OPEN_DATA_SERVLET = "/V4/ODataKpiService.svc";
	private static final long serialVersionUID = 1L;
	
	private OpenDataComponent openDataComponent;

	public KpiServlet(OpenDataComponent openDataComponent) {
		this.openDataComponent = openDataComponent;
	}

	@Override
	protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		try {
	        // create odata handler and configure it with EdmProvider and Processor
	        OData odata = OData.newInstance();
	        
	        ServiceMetadata edm = odata.createServiceMetadata(new KpiEdmProvider(openDataComponent), new ArrayList<EdmxReference>());
	        
	        ODataHttpHandler handler = odata.createHandler(edm);
	        handler.register(new KpiEntityCollectionProcessor(openDataComponent));
	        handler.register(new KpiEntityProcessor(openDataComponent));

			// let the handler do the work
			handler.process(req, resp);
		} catch (RuntimeException e) {
			openDataComponent.getLogger().error("Server Error occurred in ExampleServlet", e);
			throw new ServletException(e);
		}
	}
}
