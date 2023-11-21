package bpm.vanilla.wopi;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;

@Path("wopi_office/files")
public class OfficeWopiService   {

	@GET
	@Path("{file_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkFileInfo(@PathParam("file_id") String fileId) {
		System.out.println("checkFileinfo de wopi_office");
		return Response.status(200).entity(SessionManager.getInstance().getOfficeManager().getFileInfo(fileId)).build();
	}
	
	@GET
	@Path("{file_id}/contents")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile(@PathParam("file_id") String fileId) {
		System.out.println("getFile de Wopi service");
		return Response
				.status(200)
				.type("application/vnd.ms-excel")
				.entity(SessionManager.getInstance().getOfficeManager().getFile(fileId))
				.build();
	}
	
	
	@POST
	@Path("{file_id}/contents")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public void putFile(@PathParam("file_id") String fileId, InputStream is) {
		System.out.println("putFile");
		SessionManager.getInstance().getOfficeManager().putFile(fileId, is);
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
