package bpm.gateway.ui.composites.labelproviders;

import java.text.SimpleDateFormat;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.views.GatewayStepLog;

public class RemoteTransformationRuntimeLabelProvider extends DecoratingLabelProvider
	implements ITableLabelProvider{
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss"); //$NON-NLS-1$
	

	
	public RemoteTransformationRuntimeLabelProvider(ILabelProvider provider,
			ILabelDecorator decorator) {
		super(provider, decorator);
		
	}

	public Image getColumnImage(Object element, int columnIndex) {
		
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		
		
		if (element instanceof GatewayStepLog){
			return getForTransformationRuntime((GatewayStepLog)element, columnIndex);
		}
		
		else {
			return getForTransformationLog((String)element, columnIndex);
		}
		
		
		
	}

	
	private String getForTransformationLog(String log, int columnIndex) {
		if (columnIndex == 8){
			return log;
		}
		
		return null;
	}

	private String getForTransformationRuntime(GatewayStepLog tr, int columnIndex){
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss,SSS"); //$NON-NLS-1$
		switch (columnIndex){
		case 0:
			return tr.getStepName();
		case 1:
			return tr.getState();
		case 2:
			if (tr.getStart() != null){
//				return sdf.format(tr.getStartTime());
				return tr.getStart() ;
			}
			 
			return "-----"; //$NON-NLS-1$
		case 3:
			if (tr.getStop() != null){
//				return sdf.format(tr.getStopTime());
				return tr.getStop();
			}
			return "-----"; //$NON-NLS-1$
		case 4:
			return tr.getReaded() + ""; //$NON-NLS-1$
		case 5:
			return tr.getBuffered() + ""; //$NON-NLS-1$
		case 6:
			return tr.getProcessed() + ""; //$NON-NLS-1$
		case 7:
			if (tr.getDuration() != null){
//				try{
//					DateFormat df = new SimpleDateFormat("HH 'hours', mm 'mins,' ss 'seconds', SSS 'milliseconds'");
//					return df.format(new Date(tr.getDuration()));
//				}catch(Exception e){
//					e.printStackTrace();
//					return "-----";
//				}
				
				return tr.getDuration() + ""; //$NON-NLS-1$
			}
			else{
				return "-----"; //$NON-NLS-1$
			}
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
//		ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
//		
//		if (element instanceof TransformationLog){
//			switch (((TransformationLog)element).priority ){
//			case Level.WARN_INT:
//				return reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
//			case Level.ERROR_INT:
//				return reg.get(ApplicationWorkbenchWindowAdvisor.WARN_COLOR_KEY);
//			case Level.DEBUG_INT:
//				return reg.get(ApplicationWorkbenchWindowAdvisor.DEBUG_COLOR_KEY);
//			}
//			
//		}
		return super.getForeground(element);
	}

	@Override
	public Color getBackground(Object element) {
		ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();

		if (element instanceof GatewayStepLog && !((GatewayStepLog)element).getMessage().isEmpty()){
			
			boolean errors = ((GatewayStepLog)element).isContainingErrors();
			boolean warnings= ((GatewayStepLog)element).isContainingWarnings();
			
			
			
			
			if (errors){
				return reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
			}
			else if (warnings){
				return reg.get(ApplicationWorkbenchWindowAdvisor.WARN_COLOR_KEY);
			}
			else{
				return reg.get(ApplicationWorkbenchWindowAdvisor.DEBUG_COLOR_KEY);
			}
		}
		return super.getBackground(element);
	}
	
	
}
