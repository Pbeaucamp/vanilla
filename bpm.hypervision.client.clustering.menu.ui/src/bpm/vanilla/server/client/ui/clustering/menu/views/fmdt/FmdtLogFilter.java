package bpm.vanilla.server.client.ui.clustering.menu.views.fmdt;

import java.util.Date;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import bpm.vanilla.platform.core.beans.FMDTQueryBean;

public class FmdtLogFilter extends ViewerFilter {
	private Date from;
	private Date to;

	private Integer repositoryId;
	private Integer directoryItemId;

	public void setFrom(Date from) {
		this.from = from;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public void setRepositoryId(Integer repositoryId) {
		this.repositoryId = repositoryId;
	}

	public void setDirectoryItemId(Integer directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		FMDTQueryBean bean = (FMDTQueryBean) element;

		boolean val = true;

		if (repositoryId != null) {
			if (directoryItemId != null && bean.getRepositoryId() == repositoryId.intValue()) {
				val = bean.getDirectoryItemId() == directoryItemId.intValue();
			}
			else {
				val = bean.getRepositoryId() == repositoryId.intValue();
			}
		}

		if (val) {

			if (from != null) {
				if (from.before(bean.getDate())) {
					if (to != null) {
						if (to.after(bean.getDate())) {
							val = true;
						}
						else {
							val = false;
						}
					}
				}
				else {
					val = false;
				}
			}

		}

		return val;
	}

}
