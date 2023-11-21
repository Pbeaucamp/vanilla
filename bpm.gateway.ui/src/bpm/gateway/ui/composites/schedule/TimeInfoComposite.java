package bpm.gateway.ui.composites.schedule;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.utils.CronTimer;




public class TimeInfoComposite extends Composite {

	// To be internationalized. 
	// Screen labels. 
	public static final String Cycle   = Messages.TimeInfoComposite_0;
	public static final String Once    = Messages.TimeInfoComposite_1;
	public static final String Hourly  = Messages.TimeInfoComposite_2;
	public static final String Daily   = Messages.TimeInfoComposite_3;
	public static final String Weekly  = Messages.TimeInfoComposite_4;
	public static final String Monthly = Messages.TimeInfoComposite_5;
	public static final String Message = Messages.TimeInfoComposite_6;

	public static final String Every   = Messages.TimeInfoComposite_7;
	public static final String Hour    = Messages.TimeInfoComposite_8;
	public static final String Day     = Messages.TimeInfoComposite_9;
	public static final String Week    = Messages.TimeInfoComposite_10;
	public static final String Month   = Messages.TimeInfoComposite_11;
	public static final String On_day  = Messages.TimeInfoComposite_12;

	public static final String Start_time = Messages.TimeInfoComposite_13;
	public static final String Start_on   = Messages.TimeInfoComposite_14;

	// note: always starts on sunday, 
	// if you want to change the first day of the week, do it on the screen   
	public static final String[] Day_of_week = { Messages.TimeInfoComposite_15, Messages.TimeInfoComposite_16, Messages.TimeInfoComposite_17, Messages.TimeInfoComposite_18, Messages.TimeInfoComposite_19, Messages.TimeInfoComposite_20, Messages.TimeInfoComposite_21 };
	public static final String[] Offset      = {Messages.TimeInfoComposite_22, Messages.TimeInfoComposite_23};
	

	// Errors messages
	public static final String Error_reading_hourly_cycle =	Messages.TimeInfoComposite_24;
	public static final String Error_cron_syntax = Messages.TimeInfoComposite_25;
	// End internationalization.
	
	// Screen buttons. 
	private Group    cycleDetail; 
	private Button   btnOnce;
	private Button   btnHourly;
	private Button   btnDaily;
	private Button   btnWeekly;
	private Button   btnMonthly;
	private Button[] btnDayOfWeek = new Button[7];
	private Text     txtHour;
	private Text     txtDay;
	//private Text     txtWeek;
	//private Text     txtMonth;
	private Text     txtDayOfMonth;
	private Button   absoluteDayOfMonth;
	private Button   relativeDayOfMonth;
	private Combo    cmbRelativeOffset;
	private Combo    cmbRelativeDay;
	private DateTime startDate;
	private DateTime startTime;

	
	// variables
//	private Job job;
	private Text ttime; // What is used for ?
	private CronTimer timer = new CronTimer();

	
	private String startTimeStr;
	
	public  TimeInfoComposite(Composite parent, int style) {
		super(parent, style);
//		createPageContent(this);
		setLayout(new GridLayout());
		
		Composite page = new Composite(this, SWT.NONE);
		page.setLayout(new GridLayout(1, false));
		
		Composite main = new Composite(this, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createFrequencyGroup(main);
		createDetailGroup(main);
		createTimeGroup(page);
		updateTimer();
	}
//	public void setJob(Job job){
//		this.job = job;
//	}
	
	


//	public Composite createPageContent(Composite parent) {
//		Composite page = new Composite(parent, SWT.NONE);
//		page.setLayout(new GridLayout(1, false));
//		
//		Composite main = new Composite(page, SWT.NONE);
//		main.setLayout(new GridLayout(2, false));
//		main.setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		createFrequencyGroup(main);
//		createDetailGroup(main);
//		createTimeGroup(page);
//		createMessageGroup(page);
//		return page;
//	}
	
	
	public void createFrequencyGroup(Composite c) {
		Group gr = new Group(c, SWT.NONE);
		gr.setLayout(new GridLayout(1, false));
		gr.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		gr.setText(Cycle);
		
		btnOnce = new Button(gr, SWT.RADIO);
		btnOnce.setSelection(true);
		btnOnce.setText(Once);
		btnOnce.setLayoutData(new GridData());
		btnOnce.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				cycleDetail.setText(Once);
				setupDetailOnce();
				updateTimer();
			}
		});

		btnHourly = new Button(gr, SWT.RADIO);
		btnHourly.setText(Hourly);
		btnHourly.setLayoutData(new GridData());
		btnHourly.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				cycleDetail.setText(Hourly);
				setupDetailHourly();
				updateTimer();
			}
		});
		
		btnDaily = new Button(gr, SWT.RADIO);
		btnDaily.setText(Daily);
		btnDaily.setLayoutData(new GridData());
		btnDaily.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				cycleDetail.setText(Daily);
				setupDetailDaily();
				updateTimer();
			}
		});
		
		btnWeekly = new Button(gr, SWT.RADIO);
		btnWeekly.setText(Weekly);
		btnWeekly.setLayoutData(new GridData());
		btnWeekly.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				cycleDetail.setText(Weekly);
				setupDetailWeekly();
				updateTimer();
			}
		});
		
		btnMonthly = new Button(gr, SWT.RADIO);
		btnMonthly.setText(Monthly);
		btnMonthly.setLayoutData(new GridData());
		btnMonthly.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				cycleDetail.setText(Monthly);
				setupDetailMonthly();
				updateTimer();
			}
		});
	}
	
	public void createDetailGroup(Composite c) {
		cycleDetail = new Group(c, SWT.NONE);
		cycleDetail.setLayout(new GridLayout(1, false));
		cycleDetail.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		cycleDetail.setText(""); //$NON-NLS-1$
		
	}
	
	public void createTimeGroup(Composite c) {
		Group grpTiming = new Group(c, SWT.NONE);
		grpTiming.setLayout(new GridLayout(4, false));
		grpTiming.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		grpTiming.setText(Start_time);
		
		Label lblStart = new Label(grpTiming, SWT.NONE);
		lblStart.setLayoutData(new GridData());
		lblStart.setText(Start_on);
	
		startDate = new DateTime(grpTiming, SWT.DATE);
		startDate.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				updateTimer();
			}
		});

		startTime = new DateTime(grpTiming, SWT.TIME);
		startTime.addSelectionListener (new SelectionAdapter () {
			public void widgetSelected (SelectionEvent e) {
				updateTimer();
			}
		});
		
	}

	
	
	
	public void cleanDetail() {
		Control[] sons = cycleDetail.getChildren();
		for (int i=0; i < sons.length; i++) {
			sons[i].dispose();
		}
	}
	
	public void setupDetailOnce() {
		cycleDetail.setVisible(false);
		updateTimer();
	}

	public void setupDetailHourly() {
		cleanDetail();
		cycleDetail.setVisible(true);
		cycleDetail.setLayout(new GridLayout(4, false));
		
		Label lblHeader = new Label(cycleDetail, SWT.NONE);
		lblHeader.setLayoutData(new GridData());
		lblHeader.setText(Every);
		
		txtHour = new Text(cycleDetail, SWT.BORDER);
		txtHour.setLayoutData(new GridData());
		txtHour.setText(" 1   "); //$NON-NLS-1$
		txtHour.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
		        updateTimer();
			}
		});
	
		Label lblUnits = new Label(cycleDetail, SWT.NONE);
		lblUnits.setLayoutData(new GridData());
		lblUnits.setText(Hour);
		cycleDetail.pack();
	}
	
	public void setupDetailDaily() {
		cleanDetail();
		cycleDetail.setVisible(true);
		cycleDetail.setLayout(new GridLayout(3, false));
		
		Label lblHeader = new Label(cycleDetail, SWT.NONE);
		lblHeader.setLayoutData(new GridData());
		lblHeader.setText(Every);
		
		txtDay = new Text(cycleDetail, SWT.BORDER);
		txtDay.setLayoutData(new GridData());
		txtDay.setText("01"); //$NON-NLS-1$
		addVerifierForNumeric(txtDay);
		txtDay.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
		        updateTimer();
			}
		});
		
		Label lblUnits = new Label(cycleDetail, SWT.NONE);
		lblUnits.setLayoutData(new GridData());
		lblUnits.setText(Day);
		
		cycleDetail.pack();
	}
	
	public void setupDetailWeekly() {
		cleanDetail();
		cycleDetail.setVisible(true);
		cycleDetail.setLayout(new GridLayout(1, false));
		
//		Composite header = new Composite(cycleDetail, SWT.NONE); 
//		header.setLayout(new GridLayout(3, false));
//		
//		Label lblHeader = new Label(header, SWT.NONE);
//		lblHeader.setLayoutData(new GridData());
//		lblHeader.setText(Every);
//		
//		txtWeek = new Text(header, SWT.BORDER);
//		txtWeek.setLayoutData(new GridData());
//		txtWeek.setText("01");
//		addVerifierForNumeric(txtWeek);
//		txtWeek.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//		        updateTimer();
//			}
//		});
//		
//		Label lblUnits = new Label(header, SWT.NONE);
//		lblUnits.setLayoutData(new GridData());
//		lblUnits.setText(Week);
		
		for(int i=0; i<btnDayOfWeek.length; i++){
			btnDayOfWeek[i] = new Button(cycleDetail, SWT.CHECK);
			btnDayOfWeek[i].setLayoutData(new GridData());
			btnDayOfWeek[i].setText(Day_of_week[i]);
			btnDayOfWeek[i].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
			        updateTimer();
				}
			});
		}
		btnDayOfWeek[0].setSelection(true);

		cycleDetail.pack();
	}
	
	public void setupDetailMonthly() {
		cleanDetail();
		cycleDetail.setVisible(true);
		cycleDetail.setLayout(new GridLayout(1, false));
		
//		Composite header = new Composite(cycleDetail, SWT.NONE); 
//		header.setLayout(new GridLayout(3, false));
//		
//		Label lblHeader = new Label(header, SWT.NONE);
//		lblHeader.setLayoutData(new GridData());
//		lblHeader.setText(Every);
//		
//		txtMonth = new Text(header, SWT.BORDER);
//		txtMonth.setLayoutData(new GridData());
//		txtMonth.setText("01");
//		addVerifierForNumeric(txtMonth);
//		txtMonth.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//		        updateTimer();
//			}
//		});
//		
//		Label lblUnits = new Label(header, SWT.NONE);
//		lblUnits.setLayoutData(new GridData());
//		lblUnits.setText(Month);
		
		Composite absoluteDay = new Composite(cycleDetail, SWT.NONE); 
		absoluteDay.setLayout(new GridLayout(3, false));
		
		absoluteDayOfMonth = new Button(absoluteDay, SWT.RADIO);
		absoluteDayOfMonth.setLayoutData(new GridData());
		absoluteDayOfMonth.setSelection(true);
		absoluteDayOfMonth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				relativeDayOfMonth.setSelection(false);
				txtDayOfMonth.setEnabled(true);
				cmbRelativeOffset.setEnabled(false);
				cmbRelativeDay.setEnabled(false);
				cmbRelativeDay.setEnabled(false);
				updateTimer();
			}
		});
		
		Label lblDayOfMonth = new Label(absoluteDay, SWT.NONE);
		lblDayOfMonth.setLayoutData(new GridData());
		lblDayOfMonth.setText(On_day);
		
		txtDayOfMonth = new Text(absoluteDay, SWT.BORDER);
		txtDayOfMonth.setLayoutData(new GridData());
		txtDayOfMonth.setText("01"); //$NON-NLS-1$
		addVerifierForNumeric(txtDayOfMonth);
		txtDayOfMonth.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
		        updateTimer();
			}
		});
		

		Composite relativeDay = new Composite(cycleDetail, SWT.NONE); 
		relativeDay.setLayout(new GridLayout(3, false));

		relativeDayOfMonth = new Button(relativeDay, SWT.RADIO);
		relativeDayOfMonth.setLayoutData(new GridData());
		relativeDayOfMonth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				absoluteDayOfMonth.setSelection(false);
				txtDayOfMonth.setEnabled(false);
				cmbRelativeOffset.setEnabled(true);
				cmbRelativeDay.setEnabled(true);
				cmbRelativeOffset.select(0);
				cmbRelativeDay.select(0);
				updateTimer();
				}
		});
		
		cmbRelativeOffset = new Combo(relativeDay, SWT.NONE);
		cmbRelativeOffset.setLayoutData(new GridData());
		cmbRelativeOffset.setItems(Offset);
		cmbRelativeOffset.setEnabled(false);
		cmbRelativeOffset.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
		        updateTimer();
			}
		});
		
		cmbRelativeDay = new Combo(relativeDay, SWT.NONE);
		cmbRelativeDay.setLayoutData(new GridData());
		cmbRelativeDay.setItems(Day_of_week);
		cmbRelativeDay.setEnabled(false);
		cmbRelativeDay.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
		        updateTimer();
			}
		});
		cycleDetail.pack();
	}
	
	private void addVerifierForNumeric(Text txt) {
		txt.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				Character c = e.character;
				e.doit = false;
		        if (Character.isDigit(c)) e.doit = true;
		        if (c == '\b') e.doit = true;
			}
		});
	}

    protected void updateTimer() {
		//System.out.println("Update timer");

    	if(btnOnce.getSelection()){
			int hh = startTime.getHours();
			int mn = startTime.getMinutes();
			int ss = startTime.getSeconds();
			int dd = startDate.getDay();
			int mm = startDate.getMonth()+1;
			int yy = startDate.getYear();
        	try{
        		timer.setOnce(mm, dd, hh, mn, ss);
        	}catch(Exception e){
        		e.printStackTrace();
        	}
    	}
		if(btnHourly.getSelection()) {
			int hh = startTime.getHours();
			int mn = startTime.getMinutes();
			int ss = startTime.getSeconds();
        	try{
				int interval = Integer.parseInt(txtHour.getText().trim()); 
        		timer.setHourly(interval, hh, mn, ss);
        	}catch(Exception e){
    			e.printStackTrace();
        	}
		} 
		if(btnDaily.getSelection()) {
			int hh = startTime.getHours();
			int mn = startTime.getMinutes();
			int ss = startTime.getSeconds();
			int dd = startDate.getDay();
			try {
				int interval = Integer.parseInt(txtDay.getText().trim()); 
				timer.setDaily(interval, dd, hh, mn, ss );
			}catch(Exception e){
    			e.printStackTrace();
        	}
		}
		if(btnWeekly.getSelection()) {
			int hh = startTime.getHours();
			int mn = startTime.getMinutes();
			int ss = startTime.getSeconds();
			int dd = startDate.getDay();
			try {
				List<Integer> days = new ArrayList<Integer>();
				for(int i=0; i<btnDayOfWeek.length; i++)
					if(btnDayOfWeek[i].getSelection())
						days.add(i);
				timer.setWeekly(days, hh, mn, ss);
			}catch(Exception e){
    			e.printStackTrace();
        	}
		}
		if(btnMonthly.getSelection()) {
			int hh = startTime.getHours();
			int mn = startTime.getMinutes();
			int ss = startTime.getSeconds();
			int dd = startDate.getDay();
			try {
				if(absoluteDayOfMonth.getSelection()) {
					dd = Integer.parseInt(txtDayOfMonth.getText());
					timer.setMonthlyByDay(dd, hh, mn, ss);
				}
				if(relativeDayOfMonth.getSelection()) {
					System.out.println(cmbRelativeOffset.getSelectionIndex());
					if(cmbRelativeOffset.getSelectionIndex()==0){
						timer.setMonthlyByFirstDay(cmbRelativeDay.getSelectionIndex(), hh, mn, ss);
					}
					if(cmbRelativeOffset.getSelectionIndex()==1){
						timer.setMonthlyByLastDay(cmbRelativeDay.getSelectionIndex(), hh, mn, ss);
					}
				}
			}catch(Exception e){
    			e.printStackTrace();
			}
		}
		
		try {
			// Date format = "yyyy.MM.dd 'at' HH:mm:ss"
			// TODO : replace by date format found on server
			NumberFormat f = new DecimalFormat("00");  //$NON-NLS-1$
			startTimeStr  = ""; //$NON-NLS-1$
			startTimeStr += startDate.getYear() +"."; //$NON-NLS-1$
			startTimeStr += f.format(startDate.getMonth()) +"."; //$NON-NLS-1$
			startTimeStr += f.format(startDate.getDay());
			startTimeStr += " at "; //$NON-NLS-1$
			startTimeStr += f.format(startTime.getHours()) + ":"; //$NON-NLS-1$
			startTimeStr += f.format(startTime.getMinutes()) + ":"; //$NON-NLS-1$
			startTimeStr += f.format(startTime.getSeconds());
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.TimeInfoComposite_37, e.getMessage());
		}
    }
    
    public String getCronString(){
    	return timer.getCronString();
    }
    
    public String getStartTime(){
    	return startTimeStr;
    }
}

