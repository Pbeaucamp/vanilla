package bpm.fd.design.ui.editor.policies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editor.editparts.CellWrapper;

public class ComponentSelectionEditPolicy extends SelectionEditPolicy{

	private static final Color providerColor = new Color(Display.getDefault(), 80,47,247);
	private static final Color providedColor = new Color(Display.getDefault(), 82,245,17);
	
//	private HashMap<EditPart, IFigure >feedBackProviderFigures = new HashMap<EditPart, IFigure>();
//	private HashMap<EditPart, IFigure >feedBackProvidedFigures = new HashMap<EditPart, IFigure>();
	
	private List<IFigure> feedBackFigures = new ArrayList<IFigure>();
	@Override
	protected void hideSelection() {
		/*
		for(EditPart k : feedBackProviderFigures.keySet()){
			getFeedbackLayer().remove(feedBackProviderFigures.get(k));
		}
		feedBackProviderFigures.clear();
		
		for(EditPart k : feedBackProvidedFigures.keySet()){
			getFeedbackLayer().remove(feedBackProvidedFigures.get(k));
		}
		feedBackProviderFigures.clear();*/
		for(IFigure f : feedBackFigures){
			removeFeedback(f);
		}
		feedBackFigures.clear();
	}

	@Override
	protected void showSelection() {
		if (getHost().getModel() instanceof CellWrapper){
			Cell cell = ((CellWrapper)getHost().getModel()).getCell();
			/*
			 * feedBack for provider
			 */
			for(ComponentConfig cf : cell.getConfigs()){
				for(ComponentParameter p : cf.getTargetComponent().getParameters()){
					String sourceName = cf.getComponentNameFor(p);
					if (sourceName != null){
						IComponentDefinition def = Activator.getDefault().getProject().getDictionary().getComponent(sourceName);
							if (def != null){
								EditPart componentEditPart = getComponentEditPart(getHost().getRoot(), def);
								if (componentEditPart == null || ((AbstractGraphicalEditPart)componentEditPart).getFigure() == null){
									continue;
								}
								//if (feedBackProviderFigures.get(componentEditPart) == null){
									Figure feedBackFigure = new Figure(){
										@Override
										public void paint(Graphics graphics) {
											graphics.setAlpha(50);
											super.paint(graphics);
										}
									};
									feedBackFigure.setBackgroundColor(providerColor);
									feedBackFigure.setOpaque(true);
									feedBackFigure.setBounds(((AbstractGraphicalEditPart)componentEditPart).getFigure().getBounds());
									feedBackFigures.add(feedBackFigure);
									//feedBackProviderFigures.put(componentEditPart, feedBackFigure);
								//}
							}
					}
				}
			}
			/*
			 * feedBack for provided component's parameters
			 */
			HashMap<IComponentDefinition, ComponentConfig> map = Activator.getDefault().getProject().getComponents();
			
			
			for(IComponentDefinition def : map.keySet()){
				ComponentConfig cf = map.get(def);
				
				for(ComponentParameter p : cf.getParameters()){
					if (!cell.getContent().isEmpty() && cell.getContent().get(0).getName().equals(cf.getComponentNameFor(p))){
						
						
						EditPart controledEditPart = getComponentEditPart(getHost().getRoot(), def);
						
						if (controledEditPart == null || ((AbstractGraphicalEditPart)controledEditPart).getFigure() == null){
							continue;
						}
						Figure feedBackFigure = new Figure(){
							@Override
							public void paint(Graphics graphics) {
								graphics.setAlpha(50);
								super.paint(graphics);
							}
						};
						feedBackFigure.setBackgroundColor(providedColor);
						feedBackFigure.setOpaque(true);
						//feedBackProvidedFigures.put(controledEditPart, feedBackFigure);
						feedBackFigure.setBounds(((AbstractGraphicalEditPart)controledEditPart).getFigure().getBounds());
						feedBackFigures.add(feedBackFigure);
					}
				}
				
			}
			
		}
		for(IFigure f : feedBackFigures){
			addFeedback(f);
		}
		
		/*
		for(EditPart p : feedBackProvidedFigures.keySet()){
			if (p instanceof AbstractGraphicalEditPart){
				IFigure curF = ((AbstractGraphicalEditPart)p).getFigure();
				feedBackProvidedFigures.get(p).setBounds(curF.getBounds());
				
				getFeedbackLayer().add(feedBackProvidedFigures.get(p));
			}
			
		}
		
		for(EditPart p : feedBackProviderFigures.keySet()){
			if (p instanceof AbstractGraphicalEditPart){
				IFigure curF = ((AbstractGraphicalEditPart)p).getFigure();
				feedBackProviderFigures.get(p).setBounds(curF.getBounds());
				
				getFeedbackLayer().add(feedBackProviderFigures.get(p));
			}
			
		}*/
	}

	private EditPart getComponentEditPart(EditPart part, IComponentDefinition def) {
		
		if (part.getModel() instanceof CellWrapper){
			if (((CellWrapper)part.getModel()).getCell().getContent().contains(def)){
				return part;
			}
		}
		
		for(EditPart c : (List<EditPart>)part.getChildren()){
			EditPart res = getComponentEditPart(c, def);
			if (res != null){
				return res;
			}
		}
		
		return null;
	}

}
