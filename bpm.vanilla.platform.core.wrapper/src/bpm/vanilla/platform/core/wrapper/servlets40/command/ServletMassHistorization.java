package bpm.vanilla.platform.core.wrapper.servlets40.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.commands.IVanillaCommandManager;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.utils.IOWriter;

import com.thoughtworks.xstream.XStream;

public class ServletMassHistorization extends HttpServlet{
	private IVanillaCommandManager commandMgr;
	
	private XStream xstream;
	public ServletMassHistorization(IVanillaCommandManager commandMgr){
		this.commandMgr = commandMgr;
	}
	@Override
	public void init() throws ServletException {
		super.init();
		xstream = new XStream();

	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			resp.setCharacterEncoding("UTF-8"); 
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IOWriter.write(req.getInputStream(), bos, true, true);
			
			String destinationFolder = new String(bos.toByteArray(), "UTF-8");
			commandMgr.historizeFolderContent(destinationFolder);

		}catch(Exception ex){
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
			
		}
	}
}
