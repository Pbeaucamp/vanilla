package bpm.architect.web.server.servlets;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

public class SchemaUploadServlet extends UploadAction {

	private static final long serialVersionUID = 1L;

	@Override
	public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles) throws UploadActionException {
		String response = "";
		for (FileItem item : sessionFiles) {
			if (false == item.isFormField()) {
				String filename = item.getName();
				
				String schemaPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "schemas/" + filename;
				try (FileOutputStream fos = new FileOutputStream(schemaPath);
						OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
					IOUtils.copy(item.getInputStream(), osw, StandardCharsets.UTF_8);
				} catch (Exception e) {
					throw new UploadActionException(e);
				}
			}
		}

		// / Remove files from session because we have a copy of them
		removeSessionFileItems(request);

		// / Send your customized message to the client.
		return response;
	}
}