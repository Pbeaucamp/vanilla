package bpm.vanilla.repository.services;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.platform.core.repository.services.IRepositoryReportHistoricService;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;
import bpm.vanilla.repository.beans.historique.ReportHisto;
import bpm.vanilla.repository.beans.historique.SecurityReportHisto;

public class ServiceReportHistoric implements IRepositoryReportHistoricService {

	private RepositoryRuntimeComponent component;
	private int groupId;
	private int repositoryId;
	private User user;
	private String clientIp;

	public ServiceReportHistoric(RepositoryRuntimeComponent repositoryRuntimeComponent, int groupId, int repositoryId, User user, String clientIp) {
		this.component = repositoryRuntimeComponent;
		this.groupId = groupId;
		this.repositoryId = repositoryId;
		this.user = user;
		this.clientIp = clientIp;
	}

	@Override
	public void createHistoricAccess(int directoryItemId, int groupId) throws Exception {
		SecurityReportHisto o = new SecurityReportHisto();
		o.setDirectoryItemId(directoryItemId);
		o.setGroupId(groupId);

		component.getRepositoryDao(repositoryId).getSecurityReportHistoDao().save(o);
	}

	@Override
	public List<Integer> getAuthorizedGroupId(int directoryItemId) throws Exception {
		List<Integer> l = new ArrayList<Integer>();
		for (SecurityReportHisto s : component.getRepositoryDao(repositoryId).getSecurityReportHistoDao().getForItemId(directoryItemId)) {
			l.add(s.getGroupId());
		}
		return l;
	}

	@Override
	public List<Integer> getHistorizedDocumentIdFor(int directoryItemId, int groupId) throws Exception {
		List<Integer> i = new ArrayList<Integer>();
		if (directoryItemId == -1) {
			for (ReportHisto r : component.getRepositoryDao(repositoryId).getReportHistoDao().getAll()) {
				boolean present = false;
				for (Integer id : i) {
					if (id.intValue() == r.getGedDocId()) {
						present = true;
						break;
					}
				}
				if (!present) {
					i.add(r.getGedDocId());
				}

			}
		}
		else {
			for (ReportHisto r : component.getRepositoryDao(repositoryId).getReportHistoDao().getForItemId(directoryItemId)) {
				boolean present = false;
				for (Integer id : i) {
					if (id.intValue() == r.getGedDocId()) {
						present = true;
						break;
					}
				}
				if (!present) {
					i.add(r.getGedDocId());
				}

			}
		}

		return i;
	}

	@Override
	public List<Integer> getHistorizedDocumentIdForGroup(Integer groupId) throws Exception {
		List<Integer> i = new ArrayList<Integer>();
		for (ReportHisto r : component.getRepositoryDao(repositoryId).getReportHistoDao().getForGroupId(groupId, user)) {
			boolean present = false;
			for (Integer id : i) {
				if (id.intValue() == r.getGedDocId()) {
					present = true;
					break;
				}
			}
			if (!present) {
				i.add(r.getGedDocId());
			}

		}

		return i;
	}

	@Override
	public List<Integer> removeHistoricAccess(int directoryItemId, int groupId) throws Exception {
		List<SecurityReportHisto> secus = component.getRepositoryDao(repositoryId).getSecurityReportHistoDao().getForItemId(directoryItemId, groupId);
		List<Integer> results = new ArrayList<Integer>();
		for (SecurityReportHisto d : secus) {
			List<ReportHisto> reportHisto = component.getRepositoryDao(repositoryId).getReportHistoDao().getForItemId(directoryItemId);
			if(reportHisto != null && !reportHisto.isEmpty()) {
				results.add(reportHisto.get(0).getGedDocId());
			}
			component.getRepositoryDao(repositoryId).getSecurityReportHistoDao().delete(d);
		}
		return results;
	}

	@Override
	public void deleteHistoricEntry(int gedDocumentEntryId) throws Exception {
		component.getRepositoryDao(repositoryId).getReportHistoDao().delForDoc(gedDocumentEntryId);
	}

	@Override
	public List<Integer> getHistorizedDocumentIdFor(int directoryItemId) throws Exception {
		List<Integer> i = new ArrayList<Integer>();
		
		List<ReportHisto> reportHistos = new ArrayList<ReportHisto>();
		if(directoryItemId == -1) {
			reportHistos = component.getRepositoryDao(repositoryId).getReportHistoDao().getAll();
		}
		else {
			reportHistos = component.getRepositoryDao(repositoryId).getReportHistoDao().getForItemId(directoryItemId);
		}
		for (ReportHisto r : reportHistos) {
			boolean present = false;
			for (Integer id : i) {
				if (id.intValue() == r.getGedDocId()) {
					present = true;
					break;
				}
			}
			if (!present) {
				i.add(r.getGedDocId());
			}

		}
		return i;
	}

	@Override
	public void addOrUpdateReportBackground(ReportBackground report) throws Exception {
		if (report.getId() > 0) {
			component.getRepositoryDao(repositoryId).getReportHistoDao().update(report);
		}
		else {
			component.getRepositoryDao(repositoryId).getReportHistoDao().save(report);
		}
	}

	@Override
	public void deleteReportBackground(ReportBackground report) throws Exception {
		component.getRepositoryDao(repositoryId).getReportHistoDao().delete(report);
	}

	@Override
	public List<ReportBackground> getReportBackgrounds(int userId, int limit) throws Exception {
		return component.getRepositoryDao(repositoryId).getReportHistoDao().getReportBackgrounds(userId, limit);
	}

	@Override
	public ReportBackground getReportBackground(int reportBackgroundId) throws Exception {
		return component.getRepositoryDao(repositoryId).getReportHistoDao().getReportBackground(reportBackgroundId);
	}

}
