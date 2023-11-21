package bpm.vanilla.repository.beans.impact;

import java.util.List;

import bpm.vanilla.platform.core.repository.DirectoryItemDependance;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DirectoryItemDependanceDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<DirectoryItemDependance> getForDirectoryItemId(int id) {
		return getHibernateTemplate().find("from DirectoryItemDependance where dependantDirItemId=" + id);
	}

	@SuppressWarnings("unchecked")
	public List<DirectoryItemDependance> getFor(int directoryItemId, int dependantdirectoryItemid) {
		return getHibernateTemplate().find("from DirectoryItemDependance where dependantDirItemId=" + dependantdirectoryItemid + "  and dirItemId=" + directoryItemId);
	}

	@SuppressWarnings("unchecked")
	public List<DirectoryItemDependance> getNeededForDirectoryItemId(int id) {
		return getHibernateTemplate().find("from DirectoryItemDependance where dirItemId=" + id);
	}

	@SuppressWarnings("unchecked")
	public List<DirectoryItemDependance> getAllReferenceTo(int id) {
		return getHibernateTemplate().find("from DirectoryItemDependance where dirItemId=" + id + " or dependantDirItemId =" + id);
	}

	public void add(DirectoryItemDependance i) {
		if (i.getDependantDirItemId() == i.getDirItemId()) {
			return;
		}
		if (getFor(i.getDirItemId(), i.getDependantDirItemId()).isEmpty()) {
			getHibernateTemplate().save(i);
		}
	}

	public void add(RepositoryItem it, RepositoryItem depIt) {
		DirectoryItemDependance dsi = new DirectoryItemDependance();
		dsi.setDirItemId(it.getId());
		dsi.setDependantDirItemId(depIt.getId());
		if (it.getId() == depIt.getId()) {
			return;
		}

		if (getFor(it.getId(), depIt.getId()).isEmpty()) {
			getHibernateTemplate().save(dsi);
		}

	}

	public void delete(DirectoryItemDependance i) {
		try {
			getHibernateTemplate().delete(i);
		} catch (Throwable ex) {

		}
	}

	public void deleteFor(RepositoryItem it) {
		for (DirectoryItemDependance i : getAllReferenceTo(it.getId())) {
			try {
				getHibernateTemplate().delete(i);

			} catch (Throwable ex) { }
		}
	}

}
