package bpm.vanilla.platform.core.runtime.dao.massreport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.MassReportState;
import bpm.vanilla.platform.core.beans.WorkflowRunInstance;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class MassReportMonitorDao extends HibernateDaoSupport {

	
	private MassReportReference getRef(IObjectIdentifier workflowItemId, String processInstanceId) throws Exception{
		StringBuffer buf = new StringBuffer();
		buf.append("from MassReportReference where ");
		buf.append("repositoryId=" + workflowItemId.getRepositoryId() + " and ");
		buf.append("workflowItemId=" + workflowItemId.getDirectoryItemId() + " and ");
		buf.append("processInstanceId='" + processInstanceId + "'");
			
		List<MassReportReference> refs = getHibernateTemplate().find(buf.toString());
		if (refs.isEmpty()){
			throw new Exception("No MassReportReference for {repId=" + workflowItemId.getRepositoryId() + ";workflowId=" + workflowItemId.getDirectoryItemId() + "}");
		}
		MassReportReference ref = refs.get(0);
		return ref;
	}
	
	public List<MassReportReference> listRefs(){
		return getHibernateTemplate().find("from MassReportReference");
	}
	

	public MassReportState getMassReportState(IObjectIdentifier workflowItemId, IObjectIdentifier reportIdentifier,
			String workflowInstanceUuid) throws Exception {
		

		MassReportReference ref = getRef(workflowItemId, workflowInstanceUuid);
		
		
		StringBuffer buf = new StringBuffer();
		buf.append("select count(*) ");
		buf.append("from MassReportState where ");
		buf.append("massReportItemId=" + ref.getId());

		List l = getHibernateTemplate().find(buf.toString());
		long total = (Long)l.get(0);
		
		buf.append(" and generated=1");
		l = getHibernateTemplate().find(buf.toString());
		long generated = (Long)l.get(0);
		
		MassReportState result = new MassReportState(
				workflowItemId.getDirectoryItemId(),
				reportIdentifier.getDirectoryItemId(),
				workflowItemId.getRepositoryId());
		result.setReportGenerationNumber(generated);
		result.setReportTaskNumber(total);
		
		
		return result;
	}


	public void setReportGenerated(IObjectIdentifier workflowItemId,
			IObjectIdentifier launchedReportId, 
			String workflowInstanceUuid, String activityInstanceUuid)
			throws Exception {
		

		MassReportReference ref = getRef(workflowItemId, workflowInstanceUuid);
		
		StringBuffer buf = new StringBuffer();
		buf.append("from MassReportState where ");
		buf.append("massReportItemId=" + ref.getId() + " and ");
//		buf.append("processInstanceId='" + workflowInstanceUuid + "' and ");
		buf.append("activityInstanceUuid='" + activityInstanceUuid + "' and ");
		buf.append("generated=0");

		List<bpm.vanilla.platform.core.runtime.dao.massreport.MassReportState> states = getHibernateTemplate().find(buf.toString());
		if (states.isEmpty()){
			throw new Exception("ProcessInstance " + workflowInstanceUuid + " did not ask a report Generation");
		}
		bpm.vanilla.platform.core.runtime.dao.massreport.MassReportState state = states.get(0);
		state.setGenerated(1);
		getHibernateTemplate().update(state);
		
		
	}


	public void setReportGenerationAsked(IObjectIdentifier workflowItemId,
			IObjectIdentifier launchedReportId, String workflowInstanceUuid, String activityInstanceUuid)
			throws Exception {

		MassReportReference ref = null;
		
		
		try{
			ref = getRef(workflowItemId, workflowInstanceUuid);
		}catch(Exception ex){
			//create the ref
			ref = new MassReportReference();
			ref.setProcessInstanceId(workflowInstanceUuid);
			ref.setRepositoryId(workflowItemId.getRepositoryId());
			ref.setWorkflowItemId(workflowItemId.getDirectoryItemId());
			
			ref.setId((Integer)getHibernateTemplate().save(ref));

		}

		bpm.vanilla.platform.core.runtime.dao.massreport.MassReportState state = new bpm.vanilla.platform.core.runtime.dao.massreport.MassReportState();
		state.setDate(new Date());
		state.setMassReportItemId(ref.getId());
		state.setReportItemId(launchedReportId.getDirectoryItemId());
		state.setActivityInstanceUuid(activityInstanceUuid);
		getHibernateTemplate().save(state);
		
	}

	public List<Integer> getReportItems(long massReportRefId) {
		StringBuffer buf = new StringBuffer();
		buf.append("select distinct(reportItemId) from MassReportState where ");
		buf.append("massReportItemId=" + massReportRefId );
		
		
		return getHibernateTemplate().find(buf.toString());
		
	}

	public void delete(List<WorkflowRunInstance> instances) {
		StringBuffer buf = new StringBuffer();
		buf.append("from MassReportReference where ");
					
		

		boolean first = true;
		for(WorkflowRunInstance i : instances){
			if (first){
				first = false;
			}
			else{
				buf.append(" or ");
			}
			buf.append("(repositoryId=" + i.getWorkflowId().getRepositoryId() + " and ");
			buf.append("workflowItemId=" + i.getWorkflowId().getDirectoryItemId() + " and ");
			buf.append("processInstanceId='" + i.getProcessInstanceUuid() + "')");

		}
		
		List<MassReportReference> refs = getHibernateTemplate().find(buf.toString());
		List l = new ArrayList();
		l.addAll(refs);
		StringBuffer b = new StringBuffer();
		b.append("from MassReportState where ");
		boolean f = true;
		for(MassReportReference o : refs){
			if (f){
				f = false;
				
			}
			else{
				b.append(" or ");
			}
			b.append("massReportItemId=" + o.getId());
			
			
		}
		l.addAll(getHibernateTemplate().find(b.toString()));
		getHibernateTemplate().deleteAll(l);
		
		
	}

}
