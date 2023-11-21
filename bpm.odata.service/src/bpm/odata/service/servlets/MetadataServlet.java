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

import bpm.odata.service.MetadataEdmProvider;
import bpm.odata.service.OpenDataComponent;
import bpm.odata.service.processors.MetadataEntityCollectionProcessor;
import bpm.odata.service.processors.MetadataEntityProcessor;

public class MetadataServlet extends HttpServlet {

	public static final String OPEN_DATA_SERVLET = "/V4/ODataMetadataService.svc";
	private static final long serialVersionUID = 1L;
	
	private OpenDataComponent openDataComponent;

	public MetadataServlet(OpenDataComponent openDataComponent) {
		this.openDataComponent = openDataComponent;
	}

	@Override
	protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		try {
	        // create odata handler and configure it with EdmProvider and Processor
	        OData odata = OData.newInstance();
	        
	        ServiceMetadata edm = odata.createServiceMetadata(new MetadataEdmProvider(openDataComponent), new ArrayList<EdmxReference>());
	        
	        ODataHttpHandler handler = odata.createHandler(edm);
	        handler.register(new MetadataEntityCollectionProcessor(openDataComponent));
	        handler.register(new MetadataEntityProcessor(openDataComponent));

			// let the handler do the work
			handler.process(req, resp);
		} catch (RuntimeException e) {
			openDataComponent.getLogger().error("Server Error occurred in ExampleServlet", e);
			throw new ServletException(e);
		}
	}
	

//	public static final String OPEN_DATA_SERVLET = "/OpenDataService.svc";
//	private static final long serialVersionUID = 1L;
//	
//	private OpenDataComponent openDataComponent;
//
//	public OpenDataServlet(OpenDataComponent openDataComponent) {
//		this.openDataComponent = openDataComponent;
//	}
//
//	@Override
//	protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
//		try {
//			// create odata handler and configure it with CsdlEdmProvider and
//			// Processor
//			OData odata = OData.newInstance();
//			ServiceMetadata edm = odata.createServiceMetadata(new ODataEdmProvider(), new ArrayList<EdmxReference>());
//			ODataHttpHandler handler = odata.createHandler(edm);
//			handler.register(new ODataEntityCollectionProcessor());
//
//			// let the handler do the work
//			handler.process(req, resp);
//		} catch (RuntimeException e) {
//			openDataComponent.getLogger().error("Server Error occurred in ExampleServlet", e);
//			throw new ServletException(e);
//		}
//	}
}
