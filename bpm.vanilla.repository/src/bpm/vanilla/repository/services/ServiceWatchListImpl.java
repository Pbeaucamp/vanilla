package bpm.vanilla.repository.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.services.IWatchListService;
import bpm.vanilla.repository.beans.RepositoryDaoComponent;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;
import bpm.vanilla.repository.beans.historique.Historic;
import bpm.vanilla.repository.beans.watchlist.WatchList;

public class ServiceWatchListImpl implements IWatchListService {

	private RepositoryRuntimeComponent component;
	private RepositoryDaoComponent repositoryDaoComponent;
	private int groupId;
	private int repositoryId;
	private User user;
	private String clientIp;

	public ServiceWatchListImpl(RepositoryRuntimeComponent repositoryRuntimeComponent, int groupId, int repositoryId, User user, String clientIp) throws Exception {
		this.component = repositoryRuntimeComponent;
		this.repositoryDaoComponent = repositoryRuntimeComponent.getRepositoryDao(repositoryId);
		this.groupId = groupId;
		this.repositoryId = repositoryId;
		this.user = user;
		this.clientIp = clientIp;
	}

	@Override
	public void addToWatchList(RepositoryItem item) throws Exception {
		for (WatchList it : repositoryDaoComponent.getWatchListDao().getForUserId(user.getId())) {
			if (it.getDirectoryItemId() == item.getId()) {
				return;
			}
		}

		// TODO : check if the directoryItem is available for the given group
		WatchList o = new WatchList();
		o.setCreationDate(Calendar.getInstance().getTime());
		o.setCreatorId(user.getId());
		o.setDirectoryItemId(item.getId());

		repositoryDaoComponent.getWatchListDao().save(o);
	}

	@Override
	public List<RepositoryItem> getLastConsulted() throws Exception {
		List<RepositoryItem> l = new ArrayList<RepositoryItem>();

		ServiceBrowseImpl browser = new ServiceBrowseImpl(component, groupId, repositoryId, user, clientIp);

		List<Historic> historics = repositoryDaoComponent.getHistoricDao().getForUser(user.getId());
		for (Historic h : historics) {
			boolean skip = false;
			for (RepositoryItem it : l) {
				if (it.getId() == h.getDirectoryItemId().intValue()) {
					skip = true;
					break;
				}
			}
			if (skip) {
				continue;
			}
			RepositoryItem it = browser.getDirectoryItem(h.getDirectoryItemId());
			if (it != null && it.isDisplay()) {
				l.add(it);
				if (l.size() >= 10) {
					return l;
				}
			}

		}
		return l;
	}

	@Override
	public List<RepositoryItem> getWatchList() throws Exception {
		List<RepositoryItem> l = new ArrayList<RepositoryItem>();

		ServiceBrowseImpl browser = new ServiceBrowseImpl(component, groupId, repositoryId, user, clientIp);

		for (WatchList w : repositoryDaoComponent.getWatchListDao().getForUserId(user.getId())) {
			RepositoryItem it = browser.getDirectoryItem(w.getDirectoryItemId());
			if (it != null) {
				l.add(it);
			}

		}
		return l;
	}

	@Override
	public void removeFromWatchList(RepositoryItem item) throws Exception {
		for (WatchList it : repositoryDaoComponent.getWatchListDao().getForUserId(user.getId())) {
			if (it.getDirectoryItemId() == item.getId()) {
				repositoryDaoComponent.getWatchListDao().delete(it);
			}
		}
	}
}
