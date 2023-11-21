package bpm.fd.design.ui.component.gauge.pages;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.components.definition.gauge.StaticGaugeDatas;
import bpm.fd.design.ui.component.Messages;

public class CompositeStaticGaugeDatas extends AbstractCompositeGaugeDatas{

	
	private Text  target;
	private Text  tolerancePerc;
	private Text  value;
	private Text  min;
	private Text minTolerated;
	private Text maxTolerated;
	private Text  max;
		
	private StaticGaugeDatas datas;
	
	private ModifyListener lst = new ModifyListener() {
		
		public void modifyText(ModifyEvent e) {
			notifyListeners(SWT.Modify, new Event());
			
		}
	};
	
	public CompositeStaticGaugeDatas(Composite parent, int style) {
		super(parent, style);
		buildContent();
	}

	
	private void buildContent(){
		setLayout(new GridLayout(2, false));
		
		Label l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeStaticGaugeDatas_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		min = new Text(this, SWT.BORDER);
		min.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		min.addModifyListener(lst);
		
		l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeStaticGaugeDatas_1);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		minTolerated = new Text(this, SWT.BORDER);
		minTolerated.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		minTolerated.addModifyListener(lst);
		
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeStaticGaugeDatas_2);
		
		
		target = new Text(this, SWT.BORDER);
		target.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		target.addModifyListener(lst);
		
		l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeStaticGaugeDatas_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		maxTolerated = new Text(this, SWT.BORDER);
		maxTolerated.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		maxTolerated.addModifyListener(lst);
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setText(Messages.CompositeStaticGaugeDatas_4);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		max = new Text(this, SWT.BORDER);
		max.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		max.addModifyListener(lst);
		
		
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeStaticGaugeDatas_5);
		
		
		tolerancePerc = new Text(this, SWT.BORDER);
		tolerancePerc.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		tolerancePerc.addModifyListener(lst);
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeStaticGaugeDatas_6);

		value = new Text(this, SWT.BORDER);
		value.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		value.addModifyListener(lst);

	}
	
	public IComponentDatas getDatas() throws Exception{
		if (datas == null){
			datas = new StaticGaugeDatas();
		}
		
		datas.setMax(Float.parseFloat(max.getText()));
		datas.setMin(Float.parseFloat(min.getText()));
		datas.setTarget(Float.parseFloat(target.getText()));
		datas.setValue(Float.parseFloat(value.getText()));
		datas.setMaxSeuil(Float.parseFloat(maxTolerated.getText()));
		datas.setMinSeuil(Float.parseFloat(minTolerated.getText()));
		datas.setTolerancePerc(Float.parseFloat(tolerancePerc.getText()));
		return datas;
	}
	
	public boolean isComplete(){
		boolean b = true;
		
		b = b && parseString(min.getText());
		b = b && parseString(max.getText());
		b = b && parseString(target.getText());
		b = b && parseString(tolerancePerc.getText());
		b = b && parseString(value.getText());
		b = b && parseString(maxTolerated.getText());
		b = b && parseString(minTolerated.getText());
		return b;
	}
	
	private boolean parseString(String s){
		try{
			Float.parseFloat(s);
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	
	@Override
	public void fill(IComponentDatas data) {
		 Assert.isTrue(data instanceof StaticGaugeDatas);
		 datas = (StaticGaugeDatas)data;
		 
		 max.setText(datas.getMax() != null ? datas.getMax() + "" : ""); //$NON-NLS-1$ //$NON-NLS-2$
		 min.setText(datas.getMin() != null ? datas.getMin() + "" : ""); //$NON-NLS-1$ //$NON-NLS-2$
		 target.setText(datas.getTarget() != null ?datas.getTarget() + "" : ""); //$NON-NLS-1$ //$NON-NLS-2$
		 value.setText(datas.getValue() != null ?datas.getValue() + "" : ""); //$NON-NLS-1$ //$NON-NLS-2$
		 maxTolerated.setText(datas.getMaxSeuil() != null ?datas.getMaxSeuil() + "" : ""); //$NON-NLS-1$ //$NON-NLS-2$
		 minTolerated.setText(datas.getMinSeuil() != null ?datas.getMinSeuil() + "" : ""); //$NON-NLS-1$ //$NON-NLS-2$
		 tolerancePerc.setText(datas.getTolerancePerc() != null ?datas.getTolerancePerc() + "" : ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
