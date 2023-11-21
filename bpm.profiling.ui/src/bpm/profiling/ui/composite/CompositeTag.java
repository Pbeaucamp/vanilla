package bpm.profiling.ui.composite;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.profiling.database.bean.TagBean;

public class CompositeTag extends Composite {
	

	private Text creationDate;
	private Text modificationDate;
	private Text creator;
	private Text content;
	
	private boolean editionMode = false;
	private TagBean tagBean;
	
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public CompositeTag(Composite parent, int style, boolean edition) {
		super(parent, style);
		editionMode = edition;
		buildContent();
	}
	
	private void buildContent(){
		this.setLayout(new GridLayout(2, false));
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Creation Date");
		
		creationDate = new Text(this, SWT.BORDER);
		creationDate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creationDate.setEnabled(false);
		
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Last Modification Date");
		
		modificationDate = new Text(this, SWT.BORDER);
		modificationDate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		modificationDate.setEnabled(false);
		
		
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText("Creator");
		
		creator = new Text(this, SWT.BORDER);
		creator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creator.setEnabled(!editionMode);
		
		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l4.setText("Comment");
		
		content = new Text(this, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
	}
	

	public void setContent(TagBean tagBean){
		this.tagBean = tagBean;
		
		if (this.tagBean == null){
			return;
		}
		
		
		content.setText(tagBean.getContent());
		creationDate.setText(sdf.format(tagBean.getCreation()));
		modificationDate.setText(sdf.format(tagBean.getModification()));
		creator.setText(tagBean.getCreator());
	}
	
	public void performChanges(){
		if (tagBean == null){
			tagBean = new TagBean();
		}
		
		tagBean.setContent(content.getText());
		tagBean.setCreator(creator.getText());
		tagBean.setModification(Calendar.getInstance().getTime());
		
	}
	
	
	public TagBean getContent(){
		return tagBean;
	}

}
