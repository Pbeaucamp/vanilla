package bpm.gwt.aklabox.commons.server.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.gwt.aklabox.commons.server.methods.OrbeonServletAction;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.gwt.aklabox.commons.server.security.CommonSessionHelper;

public class OrbeonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setContentType("text/html; charset=UTF-8");
			req.setCharacterEncoding("UTF-8");

			String sending = req.getParameter("sending");
//			String pdf = req.getParameter("pdf");
			if (sending != null && !sending.isEmpty()) {
//				System.out.println("Sending : getting session");
//				
//				System.out.println(req.getParameter("app") + req.getParameter("form") + req.getParameter("document"));
				
				CommonSession session = getSession(req.getParameter("app"), req.getParameter("form"), req.getParameter("document"));
				
				OrbeonServletAction action = new OrbeonServletAction(session, req);
				action.handleFormValidation(getItem(req.getParameter("app"), req.getParameter("form"), req.getParameter("document")), sending);
				
				deleteSession(req.getParameter("app"), req.getParameter("form"), req.getParameter("document"));
			} 
//			else if (pdf != null && !pdf.isEmpty()) {
//				try {
//					CommonSession session = getSession(req.getParameter("app"), req.getParameter("form"), req.getParameter("document"));
//
//					OrbeonItem item = getItem(req.getParameter("app"), req.getParameter("form"), req.getParameter("document"));
//
//					Documents doc = session.getAklaboxService().getDocInfo(Integer.parseInt(item.getWorkflowdoc()));
//					
//					OrbeonWorkflowInformation info = session.getAklaflowService().getOrbeonInformation(doc);
//					
//					if(info.getInstance().getInstanceStatus().equals(IInstanceStatus.FINISH)) {
//						int parentId = doc.getParentId();
//						
//						File file = new File("form_" + new Date().getTime() + ".pdf");
//						OutputStream outputStream = new FileOutputStream(file);
//						IOUtils.copy(req.getInputStream(), outputStream);
//						outputStream.close();
//						
//						session.getAklaboxService().uploadfile(new ByteArrayInputStream(FileUtils.readFileToByteArray(file)), parentId + "", doc.getName() + ".pdf", file.getAbsolutePath(), session.getUser(), new Date());
//					}
//					
//					System.out.println("Deleting session");
//					deleteSession(req.getParameter("app"), req.getParameter("form"), req.getParameter("document"));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
////				sessions.clear();
//			}
			
			else {
				CommonSession session = getSession(req);
				String instanceId = req.getParameter("instance");
				instanceId = instanceId == null || instanceId.isEmpty() ? null : instanceId;

				OrbeonServletAction action = new OrbeonServletAction(session, req);
				
				String urlToCall = action.generateOrbeonUrl();
				
				addSession(req.getParameter("app"), req.getParameter("name"), instanceId, req.getParameter("workflowdoc"), session);

				resp.sendRedirect(urlToCall);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private CommonSession getSession(HttpServletRequest req) throws ServletException {
		CommonSession session;
		try {
			session = CommonSessionHelper.getCurrentSession(req, CommonSession.class);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new ServletException("Unable to get application's session: " + e1.getMessage());
		}

		return session;
	}

	// FIXME
	// Shit workaround because we lose the session when we call orbeon
	// It's terrible because there's nothing to check the user so it can cause
	// problems

	public class OrbeonItem {
		private String app;
		private String form;
		private String instance;
		private String workflowdoc;

		public OrbeonItem() {
			super();
		}

		public OrbeonItem(String app, String form, String instance) {
			super();
			this.app = app;
			this.form = form;
			this.instance = instance;
		}

		public OrbeonItem(String app, String form, String instance, String workflowdoc) {
			super();
			this.app = app;
			this.form = form;
			this.instance = instance;
			this.workflowdoc = workflowdoc;
		}

		public String getApp() {
			return app;
		}

		public void setApp(String app) {
			this.app = app;
		}

		public String getForm() {
			return form;
		}

		public void setForm(String form) {
			this.form = form;
		}

		public String getInstance() {
			return instance;
		}

		public void setInstance(String instance) {
			this.instance = instance;
		}

		public String getWorkflowdoc() {
			return workflowdoc;
		}

		public void setWorkflowdoc(String workflowdoc) {
			this.workflowdoc = workflowdoc;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((app == null) ? 0 : app.hashCode());
			result = prime * result + ((form == null) ? 0 : form.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			OrbeonItem other = (OrbeonItem) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (app == null) {
				if (other.app != null)
					return false;
			} else if (!app.equals(other.app))
				return false;
			if (form == null) {
				if (other.form != null)
					return false;
			} else if (!form.equals(other.form))
				return false;
			if (instance == null) {
				if (other.instance != null)
					return true;
			}
			return true;
		}

		private OrbeonServlet getOuterType() {
			return OrbeonServlet.this;
		}

	}

	private static Map<OrbeonItem, CommonSession> sessions = new HashMap<>();

	private void addSession(String app, String form, String instance, String workflowdoc, CommonSession session) {
		deleteSession(app, form, null);
		sessions.put(new OrbeonItem(app, form, instance, workflowdoc), session);
	}

	private CommonSession getSession(String app, String form, String instance) {
		return sessions.get(new OrbeonItem(app, form, instance));
	}

	private OrbeonItem getItem(String app, String form, String instance) {
		for (OrbeonItem i : sessions.keySet()) {
			if (i.equals(new OrbeonItem(app, form, instance))) {
				return i;
			}
		}
		return null;
	}

	private void deleteSession(String app, String form, String instance) {
		sessions.remove(new OrbeonItem(app, form, instance));
	}

}
