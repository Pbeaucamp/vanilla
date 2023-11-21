package bpm.vanilla.repository.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Revision;
import bpm.vanilla.platform.core.repository.services.IModelVersionningService;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;
import bpm.vanilla.repository.beans.log.ServerLoger;
import bpm.vanilla.repository.beans.model.ItemModel;
import bpm.vanilla.repository.beans.versionning.Lock;

public class ServiceCheckingImpl implements IModelVersionningService {

	private RepositoryRuntimeComponent component;
	private int groupId;
	private int repositoryId;
	private User user;
	private String clientIp;

	public ServiceCheckingImpl(RepositoryRuntimeComponent repositoryRuntimeComponent, int groupId, int repositoryId, User user, String clientIp) {
		this.component = repositoryRuntimeComponent;
		this.groupId = groupId;
		this.repositoryId = repositoryId;
		this.user = user;
		this.clientIp = clientIp;
	}

	@Override
	public void checkIn(RepositoryItem item, String comment, InputStream modelStream) throws Exception {
		/*
		 * remove the Lock
		 */
		component.getRepositoryDao(repositoryId).getLockDao().removeLock(item.getLockId());
		item.setLockId(null);
		component.getRepositoryDao(repositoryId).getItemDao().update(item);

		/*
		 * update the model in database
		 */
		component.getServiceBrowse(repositoryId, groupId, user, clientIp).updateModel(item, IOUtils.toString(modelStream, "UTF-8"));
	}

	@Override
	public InputStream checkOut(RepositoryItem item) throws Exception {
		if (item.getLockId() != null) {
			throw new Exception("The BiObject is already locked! You cannot perform any action on it until its lock is released.");
		}

		ItemModel rev = component.getRepositoryDao(repositoryId).getItemModelDao().getLastVersion(item.getId());

		if (rev == null) {
			throw new Exception("This object haven't been shared yet.");
		}

		InputStream result = IOUtils.toInputStream(rev.getXml(), "UTF-8");

		/*
		 * lock the DirectoryItem
		 */
		Lock lock = component.getRepositoryDao(repositoryId).getLockDao().addLock(item.getId(), user.getId());
		item.setLockId(lock.getId());

		/*
		 * update the model in database
		 */
		component.getRepositoryDao(repositoryId).getItemDao().update(item);

		return result;
	}

	@Override
	public InputStream getRevision(RepositoryItem item, int revisionNumber) throws Exception {
		String xml = component.getRepositoryDao(repositoryId).getItemModelDao().getVersion(item.getId(), revisionNumber).getXml();
		return IOUtils.toInputStream(xml, "UTF-8");
	}

	@Override
	public List<Revision> getRevisions(RepositoryItem item) throws Exception {

		List<ItemModel> l = component.getRepositoryDao(repositoryId).getItemModelDao().getAllVersion(item.getId());
		List<Revision> revisions = new ArrayList<Revision>();
		for (ItemModel v : l) {
			revisions.add(convertAsRevision(v));
		}
		return revisions;
	}

	private Revision convertAsRevision(ItemModel v) {
		Revision rev = new Revision();
		rev.setDate(v.getCreationDate());
		rev.setId(v.getId());
		rev.setItem(v.getItemId());
		rev.setRevisionNumber(v.getVersion());
		rev.setUserId(v.getUserId());
		return rev;
	}

	@Override
	public void revertToRevision(RepositoryItem item, int revisionNumber, String comment) throws Exception {
		long start = Calendar.getInstance().getTime().getTime();

		ItemModel version = component.getRepositoryDao(repositoryId).getItemModelDao().getVersion(item.getId(), revisionNumber);

		if (version == null) {
			throw new Exception("Unable to find the revision " + revisionNumber + " for the DirectoryItem with id " + item.getId());
		}

		component.getRepositoryDao(repositoryId).getItemModelDao().save(version);

		long end = Calendar.getInstance().getTime().getTime();
		ServerLoger.log(clientIp, item.getType(), ServerLoger.REVERT_TO_REVISION, item.getId(), user, groupId, end - start, repositoryId);
	}

	@Override
	public void share(RepositoryItem item) throws Exception {
		

	}

	@Override
	public boolean unlock(RepositoryItem item) throws Exception {
		long start = Calendar.getInstance().getTimeInMillis();

		component.getRepositoryDao(repositoryId).getLockDao().removeLock(item.getId());
		item.setLockId(null);
		component.getRepositoryDao(repositoryId).getItemDao().update(item);
		long end = Calendar.getInstance().getTime().getTime();
		ServerLoger.log(clientIp, item.getType(), ServerLoger.UNLOCK, item.getId(), user, null, end - start, repositoryId);

		return true;
	}

	@Override
	public void updateRevision(Revision revision) throws Exception {
//		component.getRepositoryDao(repositoryId).updateRevision(revision);
	}
}
