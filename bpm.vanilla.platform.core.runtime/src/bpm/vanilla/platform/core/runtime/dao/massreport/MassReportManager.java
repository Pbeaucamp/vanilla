package bpm.vanilla.platform.core.runtime.dao.massreport;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.IMassReportMonitor;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.MassReportState;
import bpm.vanilla.platform.core.beans.WorkflowRunInstance;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.runtime.components.AbstractVanillaManager;

public class MassReportManager extends AbstractVanillaManager  implements IMassReportMonitor{
	private MassReportMonitorDao dao;

	public void activate(ComponentContext ctx){
		try {
			super.activate(ctx);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	protected void init() throws Exception {
		this.dao = getDao().getMassReportMonitorDao();
		if (this.dao == null){
			throw new Exception("MassReportMonitorDao is null");
		}
		
	}

	@Override
	public MassReportState getMassReportState(IObjectIdentifier workflowItemId,
			IObjectIdentifier reportIdentifier, String workflowInstanceUuid)
			throws Exception {
		return dao.getMassReportState(workflowItemId, reportIdentifier, workflowInstanceUuid);
	}

	@Override
	public void setReportGenerated(IObjectIdentifier workflowItemId,
			IObjectIdentifier launchedReportId, String workflowInstanceUuid,
			String activityInstanceUuid) throws Exception {
		dao.setReportGenerated(workflowItemId, launchedReportId, workflowInstanceUuid, activityInstanceUuid);
		
	}

	@Override
	public void setReportGenerationAsked(IObjectIdentifier workflowItemId,
			IObjectIdentifier launchedReportId, String workflowInstanceUuid,
			String activityInstanceUuid) throws Exception {
		dao.setReportGenerationAsked(workflowItemId, launchedReportId, workflowInstanceUuid, activityInstanceUuid);
		
	}
	@Override
	public List<WorkflowRunInstance> getWorklowsUsingMassReporting()
			throws Exception {
		List<WorkflowRunInstance> res = new ArrayList<WorkflowRunInstance>();
		
		List<MassReportReference> refs = dao.listRefs();
		
		for(MassReportReference r : refs){
			
			WorkflowRunInstance wkfI =  new WorkflowRunInstance();
			
			wkfI.setProcessInstanceUuid(r.getProcessInstanceId());
			wkfI.setWorkflowId(new ObjectIdentifier(r.getRepositoryId(), r.getWorkflowItemId()));
			res.add(wkfI);
			
			for(Integer reportId : dao.getReportItems(r.getId())){
				if (reportId != null && reportId.intValue() > 0)
				wkfI.addReportIdentifier(new ObjectIdentifier(
						r.getRepositoryId(),
						reportId));
			}
		}
		
		
		
		return res;
	}

	@Override
	public void delete(List<WorkflowRunInstance> instances) throws Exception {
		dao.delete(instances);
		
	}
	
}
