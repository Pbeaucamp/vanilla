package bpm.fd.design.ui.component.olap.pages;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;

import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.report.pages.ReportPage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class OlapViewPage extends ReportPage{

	
	public OlapViewPage(){
		this.setTitle(Messages.OlapViewPage_0);
		this.setDescription(Messages.OlapViewPage_1);
	}
	@Override
	protected void createViewer(Composite main) {
		super.createViewer(main);
		viewer.setFilters(new ViewerFilter[]{new ViewerFilter(){

			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof RepositoryItem){
					return ((RepositoryItem)element).getType() == IRepositoryApi.FAV_TYPE;
				}
				return true;
			}
			
		}});
	}
}
