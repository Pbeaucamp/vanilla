package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IOutputProvider;
import bpm.workflow.runtime.model.Transition;
import bpm.workflow.runtime.model.TransitionException;
import bpm.workflow.runtime.model.WorkflowException;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.StartActivity;
import bpm.workflow.runtime.model.activities.StopActivity;
import bpm.workflow.runtime.model.activities.reporting.BurstActivity;
import bpm.workflow.runtime.model.activities.reporting.ReportActivity;
import bpm.workflow.runtime.model.activities.vanilla.GedActivity;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.LoopEditPart;
import bpm.workflow.ui.gef.part.MacroProcessEditPart;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the name of the nodes (activities)
 * 
 * @author CAMUS, CHARBONNIER, MARTIN
 * 
 */
public class GeneralSection extends AbstractPropertySection {
	private Text nameTxt;

	private Node node;
	private boolean isLoop = false;
	private boolean isMacroProcess = false;

	public GeneralSection() {

	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel labelLabel = getWidgetFactory().createCLabel(composite, Messages.GeneralSection_0);
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, true));

		nameTxt = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		nameTxt.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true));

	}

	@Override
	public void refresh() {
		nameTxt.removeModifyListener(listener);
		try {
			nameTxt.setText(node.getName() + ""); //$NON-NLS-1$
		} catch(Exception e) {
			e.printStackTrace();
		}
		nameTxt.addModifyListener(listener);

	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();

		if(input instanceof LoopEditPart) {
			Assert.isTrue(input instanceof LoopEditPart);
			isLoop = true;
			isMacroProcess = false;

		}
		else if(input instanceof MacroProcessEditPart) {
			Assert.isTrue(input instanceof MacroProcessEditPart);
			isLoop = false;
			isMacroProcess = true;
		}
		else {
			Assert.isTrue(input instanceof NodePart);
			isLoop = false;
			isMacroProcess = false;
		}

		this.node = (Node) ((NodeEditPart) input).getModel();
	}

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent evt) {
			if(!isLoop && !isMacroProcess) {
				if(node.getWorkflowObject() instanceof IActivity && !(node.getWorkflowObject() instanceof StartActivity || node.getWorkflowObject() instanceof StopActivity)) {
					IActivity ac = null;
					try {
						ac = Activator.getDefault().getCurrentInput().getWorkflowModel().getActivity(node.getWorkflowObject().getId());

					} catch(Exception e) {

					}
					if(ac != null) {
						String oldvalue = ""; //$NON-NLS-1$
						String oldoutPutVar = ""; //$NON-NLS-1$

						try {
							oldvalue = node.getName();

							if(oldvalue == null) {
								oldvalue = node.getWorkflowObject().getName();
							}
							if(ac instanceof IOutputProvider) {
								oldoutPutVar = ((IOutputProvider) ac).getOutputVariable();
							}

							IActivity oldact = (IActivity) node.getWorkflowObject();
							List<Transition> assource = new ArrayList<Transition>();
							List<Transition> astarget = new ArrayList<Transition>();

							for(Transition t : ((WorkflowModel) Activator.getDefault().getCurrentModel()).getTransitions()) {
								if(t.getSource().equals(oldact)) {
									assource.add(t);
								}
								else if(t.getTarget().equals(oldact)) {
									astarget.add(t);
								}
							}

							Activator.getDefault().getCurrentInput().getWorkflowModel().removeActivity(node.getWorkflowObject().getId());

							if(!nameTxt.getText().equalsIgnoreCase("") && !nameTxt.getText().equalsIgnoreCase("null")) { //$NON-NLS-1$ //$NON-NLS-2$
								ac.setName(nameTxt.getText());
								node.setName(nameTxt.getText());
							}
							else {
								ac.setName(node.getWorkflowObject().getName());
								node.setName(node.getWorkflowObject().getName());
							}

							Activator.getDefault().getCurrentInput().getWorkflowModel().addActivity(ac);

							for(Transition nt : assource) {
								nt.setSource(ac);
								try {
									Activator.getDefault().getCurrentInput().getWorkflowModel().addTransition(nt);
								} catch(TransitionException e) {
									e.printStackTrace();
								}
							}
							for(Transition nt : astarget) {
								nt.setTarget(ac);
								try {
									Activator.getDefault().getCurrentInput().getWorkflowModel().addTransition(nt);
								} catch(TransitionException e) {
									e.printStackTrace();
								}
							}

							if(ac instanceof IConditionnable) {
								List<Transition> allTransitions = ((WorkflowModel) Activator.getDefault().getCurrentModel()).getTransitions();
								for(Transition t : allTransitions) {
									String suffix = ((IConditionnable) ac).getSuccessVariableSuffix();
									if(t.getCondition() != null && t.getCondition().getVariable().equals(oldvalue + suffix)) {
										t.getCondition().setVariable(ac.getName() + suffix);
									}
								}
							}

							if(!oldoutPutVar.equalsIgnoreCase("")) { //$NON-NLS-1$
								for(IActivity a : Activator.getDefault().getCurrentInput().getWorkflowModel().getActivities().values()) {
									if(a instanceof IAcceptInput) {
										List<String> inputs = ((IAcceptInput) a).getInputs();
										for(String i : inputs) {
											if(i.indexOf(oldvalue) > -1) {
												((IAcceptInput) a).removeInput(oldoutPutVar);
												((IAcceptInput) a).addInput(((IOutputProvider) ac).getOutputVariable());

												if(ac instanceof BurstActivity && a instanceof GedActivity) {
													((GedActivity) a).addItemIdForFile(((BurstActivity) ac).getBiObject().getId(), ((IOutputProvider) ac).getOutputVariable());
												}
												else if(ac instanceof ReportActivity && a instanceof GedActivity) {
													((GedActivity) a).addItemIdForFile(((ReportActivity) ac).getBiObject().getId(), ((IOutputProvider) ac).getOutputVariable());
												}
												break;
											}
										}

									}
								}
							}

						} catch(WorkflowException e) {
							MessageDialog.openError(getPart().getSite().getShell(), Messages.GeneralSection_8, e.getMessage());
							node.setName(oldvalue);

							try {
								Activator.getDefault().getCurrentInput().getWorkflowModel().addActivity(ac);
							} catch(WorkflowException e2) {
								e2.printStackTrace();
							}
						} catch(Exception e) {

							e.printStackTrace();
						}

					}

				}

			}
			else if(isLoop) {

				IActivity ac = null;
				try {
					ac = Activator.getDefault().getCurrentInput().getWorkflowModel().getActivity(((LoopModel) node).getWorkflowObject().getId());
				} catch(Exception e) {

				}
				if(ac != null) {
					String oldvalue = ""; //$NON-NLS-1$
					try {
						oldvalue = ((LoopModel) node).getName();
						Activator.getDefault().getCurrentInput().getWorkflowModel().removeActivity(((LoopModel) node).getWorkflowObject().getId());
						ac.setName(nameTxt.getText());
						((LoopModel) node).setName(nameTxt.getText());
						Activator.getDefault().getCurrentInput().getWorkflowModel().addActivity(ac);

					} catch(WorkflowException e) {
						MessageDialog.openError(getPart().getSite().getShell(), Messages.GeneralSection_11, e.getMessage());
						((LoopModel) node).setName(oldvalue);
						try {
							Activator.getDefault().getCurrentInput().getWorkflowModel().addActivity(ac);
						} catch(WorkflowException e2) {
							e2.printStackTrace();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}

				}

			}
			else if(isMacroProcess) {
				IActivity ac = null;
				try {
					ac = Activator.getDefault().getCurrentInput().getWorkflowModel().getActivity(((MacroProcessModel) node).getWorkflowObject().getId());
				} catch(Exception e) {

				}
				if(ac != null) {
					String oldvalue = ""; //$NON-NLS-1$
					try {

						oldvalue = ((MacroProcessModel) node).getName();
						Activator.getDefault().getCurrentInput().getWorkflowModel().removeActivity(((MacroProcessModel) node).getWorkflowObject().getId());
						ac.setName(nameTxt.getText());
						((MacroProcessModel) node).setName(nameTxt.getText());
						Activator.getDefault().getCurrentInput().getWorkflowModel().addActivity(ac);

					} catch(WorkflowException e) {
						MessageDialog.openError(getPart().getSite().getShell(), Messages.GeneralSection_14, e.getMessage());
						((MacroProcessModel) node).setName(oldvalue);
						try {
							Activator.getDefault().getCurrentInput().getWorkflowModel().addActivity(ac);
						} catch(WorkflowException e2) {
							e2.printStackTrace();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}

				}

			}

		}

	};
}
