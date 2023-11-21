package bpm.es.clustering.ui.composites;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * hacked version to provide update if the selection is a {@link RefreshSelection}
 * @author manu
 *
 */
public class SectionPartWithListener extends SectionPart implements IPartSelectionListener {

	private ModuleMasterDetailsBlock master;
	
	/**
	 * not used?
	 * @param parent
	 * @param toolkit
	 * @param style
	 * @param master
	 */
	public SectionPartWithListener(Composite parent, FormToolkit toolkit,
			int style, ModuleMasterDetailsBlock master) {
		super(parent, toolkit, style);
		this.master = master;
	}

	public SectionPartWithListener(Section section, ModuleMasterDetailsBlock master) {
		super(section);
		this.master = master;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		//System.out.println("aa");
		
		if (selection instanceof StructuredSelection && 
				((StructuredSelection)selection).getFirstElement() instanceof RefreshSelection) {
			master.refreshData();
		}
	}
}
