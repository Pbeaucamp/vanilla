package bpm.faweb.server.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.faweb.server.security.FaWebSession;
import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.resources.IResource;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.impl.components.RemoteFdRuntime;

public class FdGeneratorServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(FdGeneratorServlet.class);

	private static final long serialVersionUID = -935482322756036102L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int keySession = Integer.parseInt(req.getParameter("keySession"));
		
		FaWebSession session = null;
		try {
			session = CommonSessionHelper.getCurrentSession(req, FaWebSession.class);
		} catch(ServiceException e2) {
			e2.printStackTrace();
		}
		FdProject fdProject = session.getFdProject(keySession);

		for (IResource res : fdProject.getResources()) {
			File file = res.getFile();
			if (!file.getName().equalsIgnoreCase("components.properties")) {
				FileChannel in = new FileInputStream(file).getChannel();
				FileChannel out = new FileOutputStream(new File(getServletContext().getRealPath(File.separator) + "/temp/" + res.getFile().getName())).getChannel();
				in.transferTo(0, in.size(), out);
			}
		}

		String modelXml = fdProject.getFdModelAsXML();

		String dicoXml = fdProject.getDictionaryAsXML();

		try {
			File file = new File(getServletContext().getRealPath(File.separator) + "temp/model.xml");
			FileWriter writer = new FileWriter(file);
			writer.write(modelXml);

			writer.close();

			File file2 = new File(getServletContext().getRealPath(File.separator) + "temp/dico.xml");
			FileWriter writer2 = new FileWriter(file2);
			writer2.write(dicoXml);

			writer2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (FdModel model : ((MultiPageFdProject) fdProject).getPagesModels()) {
			String xml = model.getElement().asXML();
			File file = new File(getServletContext().getRealPath(File.separator) + "temp/" + model.getName() + ".xml");
			FileWriter writer = new FileWriter(file);
			writer.write(xml);

			writer.close();
		}

		logger.info("Create the zip file with the dashboard ressources : ");

		int ramdomZipName = new Object().hashCode();
		File fRand = new File(getServletContext().getRealPath(File.separator) + "temp/test" + ramdomZipName + ".zip");
		while (fRand.exists()) {
			ramdomZipName = new Object().hashCode();
			fRand = new File(getServletContext().getRealPath(File.separator) + "temp/test" + ramdomZipName + ".zip");
		}

		// create the zip file
		try {
			BufferedInputStream origin = null;
			FileOutputStream dest = new FileOutputStream(getServletContext().getRealPath(File.separator) + "temp/test" + ramdomZipName + ".zip");

			logger.info("fileName : " + getServletContext().getRealPath(File.separator) + "temp/test" + ramdomZipName + ".zip");

			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			// out.setMethod(ZipOutputStream.DEFLATED);
			byte data[] = new byte[2048];
			// get a list of files from current directory
			File f = new File(getServletContext().getRealPath(File.separator) + "temp/");
			String files[] = f.list();

			for (int i = 0; i < files.length; i++) {
				logger.info("Adding: " + files[i]);

				if (files[i].contains(".svn") || files[i].contains(".zip")) {
					continue;
				}

				FileInputStream fi = new FileInputStream(getServletContext().getRealPath(File.separator) + "temp/" + files[i]);
				origin = new BufferedInputStream(fi, 2048);
				ZipEntry entry = new ZipEntry(files[i]);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, 2048)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();

				File toDel = new File(getServletContext().getRealPath(File.separator) + "temp/" + files[i]);
				try {
					logger.info("Delete : " + toDel.getName());
					toDel.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int repId = 0;
		try {
			repId = session.getCurrentRepository().getId();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		RemoteFdRuntime remoteFd = new RemoteFdRuntime(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), session.getUser().getLogin(), session.getUser().getPassword());

		IObjectIdentifier identifier = new ObjectIdentifier(repId, 0);

		IRuntimeConfig config = new RuntimeConfiguration(session.getCurrentGroup().getId(), identifier, new ArrayList<VanillaGroupParameter>());
		String jsp = null;
		try {
			jsp = remoteFd.deployDashboard(config, new FileInputStream(getServletContext().getRealPath(File.separator) + "temp/test" + ramdomZipName + ".zip"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		jsp = session.getVanillaRuntimeUrl() + jsp;

		resp.sendRedirect(resp.encodeRedirectURL(jsp));
	}

}
