package bpm.vanilla.platform.core.wrapper.wopi;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;

@Path("/wopi")
@Component
public class VanillaWopiService {

	@GET
	public Response checkFileInfo() {
		
		return Response.status(200).entity("Ca marche ou bien ?").build();
	}
	
}
