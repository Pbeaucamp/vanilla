package bpm.fd.design.ui.editor.commands;

import java.awt.Rectangle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.DivCell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.IStructureElement;
import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class CreateCommand extends Command {
	private IComponentDefinition def;
	private Class<? extends IComponentDefinition> template;

	private EditPart host;
	private Rectangle layout;

	public final void setHost(EditPart host) {
		this.host = host;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	public final void setNewObject(Object newObject) {
		if(newObject instanceof IComponentDefinition) {
			this.def = (IComponentDefinition) newObject;
		}
		else if(newObject instanceof Class) {
			template = (Class) newObject;
		}
	}

	public void setLayout(Point location, Dimension size) {
		try {
			layout = new Rectangle(location.x, location.y, size.width, size.height);
		} catch(Exception ex) {
			layout = new Rectangle(location.x, location.y, 150, 100);
		}
	}

	public void execute() {
		if(def == null) {
			IWizard wiz = null;

			for(IWizardCategory c : NewWizardRegistry.getInstance().getRootCategory().getCategories()) {
				if(c.getId().equals("bpm.fd.design.ui.freedashComponentCategory")) { //$NON-NLS-1$
					for(IWizardDescriptor d : c.getWizards()) {
						try {
							IWorkbenchWizard _w = d.createWizard();

							if(_w instanceof IWizardComponent) {
								if(((IWizardComponent) _w).getComponentClass() == template) {
									wiz = _w;
									break;
								}
							}
						} catch(CoreException e) {
							e.printStackTrace();
						}
					}
				}

			}

			if(wiz == null) {
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.BuildComponentCommand_1, Messages.BuildComponentCommand_2);
				return;
			}
			WizardDialog d = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wiz);
			d.setMinimumPageSize(800, 600);
			if(d.open() == WizardDialog.OK) {
				def = ((IWizardComponent) wiz).getComponent();
			}
			else {
				return;
			}
		}

		FactoryStructure struct = ((FdModel) ((EditPart) host.getRoot().getChildren().get(0)).getModel()).getStructureFactory();
		Cell cell = struct.createCell("cell", 1, 1);
		cell.addBaseElementToContent(def);
		cell.setPosition(layout.x, layout.y);
		cell.setSize(layout.width, layout.height);

		((IStructureElement) host.getModel()).addToContent(cell);
		host.refresh();
	}

	@Override
	public boolean canExecute() {
		return (def != null || template != null) && host != null && (host.getModel() instanceof FdModel || host.getModel() instanceof StackableCell || host.getModel() instanceof DivCell);
	}
}
