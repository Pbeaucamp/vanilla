package bpm.gateway.ui.composites.labelproviders;

import java.text.SimpleDateFormat;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;

public class StepRuntimeLabelProvider 	extends DecoratingLabelProvider implements ITableLabelProvider{
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss"); //$NON-NLS-1$
	

	
	public StepRuntimeLabelProvider(ILabelProvider provider,
			ILabelDecorator decorator) {
//		super(provider, decorator);
		super(provider, decorator);
		
	}

	public Image getColumnImage(Object element, int columnIndex) {
		
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		RuntimeStep r = (RuntimeStep)element;
		switch (columnIndex){
		case 0:
			return r.getTransformation().getName();
		case 1:
			return r.getTransformationStateName();
		case 2:
			if (r.getStartTime() != null){
				return sdf.format(r.getStartTime());
			}
			 
			return "-----"; //$NON-NLS-1$
		case 3:
			if (r.getStopTime() != null){
				return sdf.format(r.getStopTime());
			}
			return "-----"; //$NON-NLS-1$
		case 4:
			return r.getStatsReadedRows() + ""; //$NON-NLS-1$
		case 5:
			return r.getStatsBufferedRows() + ""; //$NON-NLS-1$
		case 6:
			return r.getStatsProcessedRows() + ""; //$NON-NLS-1$
		case 7:
			if (r.getDuration() != null){
				double duration = r.getDuration();
				Double mill = duration % 1000;
				
				double secTmp = Math.floor(duration/1000);
				Double sec = secTmp % 60;
				
				double minTmp = Math.floor(secTmp/60);
				Double min = minTmp % 60;
				
				double hTmp = Math.floor(minTmp/60);
				Double h = hTmp % 24;
				
				return h.intValue() + " hours, " + min.intValue() + " mins, " + sec.intValue() + " seconds, " + mill.intValue() + " milliseconds"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//				try{
//					DateFormat df = new SimpleDateFormat("HH 'hours', mm 'mins,' ss 'seconds', SSS 'milliseconds'"); //$NON-NLS-1$
//					return df.format(new Date(r.getDuration()));
//				}catch(Exception e){
//					e.printStackTrace();
//					return "-----"; //$NON-NLS-1$
//				}
				
//				return tr.getDuration() + "";
			}
			else{
				return "-----"; //$NON-NLS-1$
			}
		}
		
		
		
		return null;
	}

	
	

	

//	@Override
//	public Color getForeground(Object element) {
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
//		return super.getForeground(element);
//	}

	@Override
	public Color getBackground(Object element) {
		ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();

		if (element instanceof RuntimeStep && !((RuntimeStep)element).getLogs().isEmpty()){
			
			if (((RuntimeStep)element).containsErrors()){
				return reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
			}
			else if (((RuntimeStep)element).containsWarnings()){
				return reg.get(ApplicationWorkbenchWindowAdvisor.WARN_COLOR_KEY);
			}
			else{
				return reg.get(ApplicationWorkbenchWindowAdvisor.DEBUG_COLOR_KEY);
			}
		}
		return super.getBackground(element);
	}

	public void addListener(ILabelProviderListener listener) {
		
		
	}

	public void dispose() {
		
		
	}

	public boolean isLabelProperty(Object element, String property) {
		
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		
		
	}
	
	
}
