package bpm.vanilla.wopi;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("wopi_vanilla/files")
public class VanillaWopiService {

	@GET
	@Path("{file_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkFileInfo(@PathParam("file_id") String fileId) {
		System.out.println("checkFileinfo");
		return Response.status(200).entity(SessionManager.getInstance().getVanillaManager().getFileInfo(fileId)).build();
	}
	
	@GET
	@Path("{file_id}/contents")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("file_id") String fileId) {
		System.out.println("getFile");
		return Response
				.status(200)
				.type(MediaType.APPLICATION_OCTET_STREAM)
				.entity(SessionManager.getInstance().getVanillaManager().getFile(fileId))
				.build();
	}
	
	@POST
	@Path("{file_id}/contents")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public void putFile(@PathParam("file_id") String fileId, InputStream is) {
		System.out.println("putFile");
		SessionManager.getInstance().getVanillaManager().putFile(fileId, is);
	}
	
	@POST
	@Path("{file_id}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public void putRelativeFile(@PathParam("file_id") String fileId, InputStream is) {
		System.out.println("putRelative");
	}
	
	@POST
	@Path("{file_id}")
	public void lock(@PathParam("file_id") String fileId) {
		System.out.println("lock");
	}
}
