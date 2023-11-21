package org.fasd.utils.trees;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fasd.olap.OLAPHierarchy;
import org.freeolap.FreemetricsPlugin;

import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.runtime.IRuntimeContext;

public class TreeUOlapContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private String schemaId;
	private IRuntimeContext ctx;

	public TreeUOlapContentProvider(String schemaId) {
		this.schemaId = schemaId;
	}

	public TreeUOlapContentProvider(String schemaId, IRuntimeContext ctx) {
		this.schemaId = schemaId;
		this.ctx = ctx;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeUOlapMember) {
			try {
				List<Member> members = FreemetricsPlugin.getDefault().getModelService().getChilds(((TreeUOlapMember) parent).getUname(), schemaId, ctx);
				for (Member mem : members) {
					TreeUOlapMember member = new TreeUOlapMember(mem.getName(), mem.getUname());
					((TreeUOlapMember) parent).addChild(member);
				}
				return ((TreeUOlapMember) parent).getChildren();
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(FreemetricsPlugin.getDefault().getWorkbench().getDisplay().getActiveShell(), "Error while retriving children", e.getMessage()); //$NON-NLS-1$
			}
		} else if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeUOlapMember) {
			TreeUOlapMember mem = (TreeUOlapMember) parent;

			int i = 0;
			TreeParent treeItem = mem;
			OLAPHierarchy hiera = null;
			while (treeItem != null) {
				i++;
				if (treeItem instanceof TreeHierarchy) {
					hiera = ((TreeHierarchy) treeItem).getOLAPHierarchy();
				}
				treeItem = treeItem.getParent();
			}

			if (i - 4 == hiera.getLevels().size()) {
				return false;
			}

		} else if (parent instanceof TreeParent) {
			return ((TreeParent) parent).hasChildren();
		}

		return true;
	}

	public void setContext(IRuntimeContext ctx) {
		this.ctx = ctx;
	}

}
