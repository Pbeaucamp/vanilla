package bpm.vanilla.repository.services;

import java.util.Collection;
import java.util.List;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.RepositoryLog;
import bpm.vanilla.platform.core.repository.services.IRepositoryLogService;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;

public class ServiceLogImpl implements IRepositoryLogService {

	private RepositoryRuntimeComponent component;
	private int groupId;
	private int repositoryId;
	private User user;
	private String clientIp;

	public ServiceLogImpl(RepositoryRuntimeComponent repositoryRuntimeComponent, int groupId, int repositoryId, User user, String clientIp) {
		this.component = repositoryRuntimeComponent;
		this.groupId = groupId;
		this.repositoryId = repositoryId;
		this.user = user;
		this.clientIp = clientIp;
	}

	@Override
	public int addLog(RepositoryLog l) throws Exception {

		RepositoryLog log = new RepositoryLog();
		log.setId(l.getId());
		log.setAppName(l.getAppName());
		log.setClientIp(l.getClientIp());
		log.setDelay(l.getDelay());
		log.setFunction(l.getFunction());
		log.setGroupId(l.getGroupId());
		log.setObjectId(l.getObjectId() == null ? 0 : l.getObjectId());
		log.setOperation(l.getOperation());
		log.setOutputType(l.getOutputType());
		log.setQuery(l.getQuery());
		log.setTime(l.getTime());
		log.setUserDefined1(l.getUserDefined1());
		log.setUserDefined2(l.getUserDefined2());
		log.setUserDefined3(l.getUserDefined3());
		log.setUserId(user.getId());

		return component.getRepositoryDao(repositoryId).getLogDao().save(log);

	}

	@Override
	public void delReportModel(RepositoryLog d) throws Exception {
		component.getRepositoryDao(repositoryId).getLogDao().delete(d);
	}

	@Override
	public RepositoryLog getById(int logId) throws Exception {
		return component.getRepositoryDao(repositoryId).getLogDao().findByModelId(logId);
	}

	@Override
	public Collection<RepositoryLog> getLogs() throws Exception {
		return component.getRepositoryDao(repositoryId).getLogDao().findAll();
	}

	@Override
	public List<RepositoryLog> getLogsFor(RepositoryItem i) throws Exception {
		List<RepositoryLog> logs = component.getRepositoryDao(repositoryId).getLogDao().findByItemId(i.getId());
		if(logs.size() > 500) {
			return logs.subList(logs.size() - 501, logs.size() - 1);
		}
		return logs;
	}

}
