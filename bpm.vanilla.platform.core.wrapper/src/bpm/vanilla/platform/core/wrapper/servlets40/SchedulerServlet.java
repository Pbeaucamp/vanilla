package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.platform.core.ISchedulerManager;
import bpm.vanilla.platform.core.beans.scheduler.Job;
import bpm.vanilla.platform.core.components.impl.IVanillaComponentProvider;
import bpm.vanilla.platform.core.runtime.components.SchedulerManager;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class SchedulerServlet extends HttpServlet {

	private static final long serialVersionUID = 8106144832749363339L;

	private IVanillaComponentProvider component;
	private SchedulerManager mng;
	private XStream xstream = new XStream();

	public SchedulerServlet(IVanillaComponentProvider component) {
		this.setComponent(component);
		mng = (SchedulerManager) component.getSchedulerManager();
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			if (action.getActionType() == null) {
				try {
					throw new Exception("XmlAction has no actionType");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			XmlArgumentsHolder args = action.getArguments();
			ISchedulerManager.ActionType type = (ISchedulerManager.ActionType) action.getActionType();
			Object actionResult = null;

			switch (type) {
				case ADD_JOB:
					mng.addJob((Job)args.getArguments().get(0));
					break;
				case EDIT_JOB:
					mng.editJob((Job) args.getArguments().get(0));
					break;
				case DELETE_JOB:
					mng.delJob((Job) args.getArguments().get(0));
					break;
				case RUN_NOW_JOB:
					mng.runNowJob((Job) args.getArguments().get(0));
					break;
				case GET_JOB_HISTORIC:
					actionResult = mng.getJobHistoric((Job) args.getArguments().get(0));
					break;
				case GET_JOBS:
					actionResult = mng.getJobs((Integer) args.getArguments().get(0));
					break;
				default:
					resp.getWriter().write("<error>" + "not implemented into servlet" + "</error>");
			}

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
		} catch (Exception ex) {
			ex.printStackTrace();

			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}

	}

	public void setComponent(IVanillaComponentProvider component) {
		this.component = component;
	}

	public IVanillaComponentProvider getComponent() {
		return component;
	}

}
