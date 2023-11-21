package bpm.gateway.ui.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries.SeriesType;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

public class RuntimeErrorViewPart extends ViewPart{

	public  static final String ID = "bpm.gateway.ui.views.RuntimeErrorViewPart"; //$NON-NLS-1$

	private Composite main, result;
	private Text viewer;
	private Tree root;
    private double[] ySeries ;
    private String[] xSeries ;
    private String start,end;
    private String toShow=""; //$NON-NLS-1$
    private int index = 0;
    private ArrayList<Double> listTime;
	private ArrayList<String> listDate;
    private ILineSeries lineSeries;
    private Chart chart;
    private Text search;
    
	@Override
	public void createPartControl(final Composite parent) {

		this.setTitleImage(Activator.getDefault().getImageRegistry().get(IconsNames.LOGS_ERROR_16));

		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		search = new Text(main, SWT.BORDER);
		search.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true , false));
		search.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.character == SWT.CR){
					refresh();
					
				}
				
			}
		});
		
		Button research = new Button(main, SWT.PUSH);
		research.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false , false));
		research.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.browse_datas_16));
		research.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				refresh();
			}
		});
		
		createErrorEnvironment(main, ""); //$NON-NLS-1$
		
		result = new Composite(parent, SWT.BORDER);
		result.setLayout(new GridLayout());
		result.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createErrorResultEnvironment(result);

		createChart(parent);
	}

	private void createChart(Composite comp2) {
		
		chart = new Chart(comp2, SWT.NONE);
		chart.getTitle().setText(Messages.Transformation_RunTime);
	    chart.getAxisSet().getXAxis(0).getTitle().setText(Messages.Nombre_of_Run);
	    chart.getAxisSet().getYAxis(0).getTitle().setText(Messages.Time_in_seconds);
	       lineSeries = (ILineSeries) chart.getSeriesSet()
           .createSeries(SeriesType.LINE, Messages.Evolution_of_Run);
 
	}

	private void createErrorResultEnvironment(Composite result) {
		
		viewer = new Text(result, SWT.MULTI|SWT.V_SCROLL| SWT.H_SCROLL);
		viewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setEditable(false);
		Device device = Display.getCurrent ();
		Color white = new Color (device, 255, 255, 255);
		viewer.setBackground(white);

	}

	private void createErrorEnvironment(Composite main, final String search) {		
		
		root = new Tree(main, SWT.BORDER);
		root.setLayout(new GridLayout());
		root.setLayoutData(new GridData(GridData.FILL_BOTH));
		root.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				
				String string = ""; //$NON-NLS-1$
				index= 0;
			    TreeItem[] selection = root.getSelection();
			        
			    TreeItem item = null;
			    
				for (int i = 0; i < selection.length; i++){
					item =selection[i];
				}
				      
				string += item.getText() + ""; //$NON-NLS-1$
				Object data = item.getData();
				        
				FileReader fr = null;
			        
				if(data != null){
					toShow ="";     		 //$NON-NLS-1$
					try {
						fr = new FileReader(data+"/"+string); //$NON-NLS-1$
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
								
					BufferedReader br = new BufferedReader(fr); 
					String s = null; 
								
					listTime = new ArrayList<Double>();
					listDate = new ArrayList<String>();
					try {
						showLogs(s, br, listDate, listTime);
					} catch (Exception e) {
						e.printStackTrace();
					}

				 }else{
					 toShow =""; //$NON-NLS-1$
					 
					 string = "Transformations_"+string; //$NON-NLS-1$
					 File transfoFiles = new File("logs/"+string); //$NON-NLS-1$
					 
					 showTransfoLogs(transfoFiles, string, fr);

				 }
				ySeries = new double[listTime.size()];
				
				xSeries= new String[listTime.size()];
				
				index = 0;
				for(Double d : listTime){
					
					ySeries[index] = d;
					xSeries[index] = listDate.get(index);
					index ++;
				}
				
				
				lineSeries.setYSeries(ySeries);
				chart.getAxisSet().getXAxis(0).setCategorySeries(xSeries);
				
//				chart.getAxisSet().getXAxis(0).enableCategory(true);
//				lineSeries.setVisibleInLegend(true);
				chart.getAxisSet().adjustRange();
				viewer.setText(toShow);
				chart.redraw(); 
			}
		});
	
	}
	
	public void refresh(){
		root.removeAll();
		
		File errorFiles = new File("logs"); //$NON-NLS-1$
		String[] listErrorFiles = (String[])errorFiles.list();
		for(int i=0;i<listErrorFiles.length;i++){
			
			if(search.getText()=="" || search.getText() == null){ //$NON-NLS-1$
				if (listErrorFiles[i].contains("Transformations")){ //$NON-NLS-1$
					
					errorFiles = new File("logs/"+listErrorFiles[i]); //$NON-NLS-1$
					
					TreeItem inerRoot = new TreeItem(root, SWT.NONE);
					inerRoot.setText(listErrorFiles[i].replace("Transformations_", "")); //$NON-NLS-1$ //$NON-NLS-2$
					String[] listLogs = errorFiles.list();
						
					for(int a=0;a<listLogs.length;a++){
						TreeItem file = new TreeItem(inerRoot, SWT.NONE);
						file.setData("logs/"+listErrorFiles[i]); //$NON-NLS-1$

						file.setText(listLogs[a]);
					}				
				}
			}else {
				if (listErrorFiles[i].contains("Transformations")){ //$NON-NLS-1$
					
					errorFiles = new File("logs/"+listErrorFiles[i]); //$NON-NLS-1$
					
				
					
					String[] listLogs = errorFiles.list();
					boolean exist = false;
					TreeItem inerRoot = null;
					for(int a=0;a<listLogs.length;a++){
						
						if(listLogs[a].toUpperCase().startsWith(search.getText().toUpperCase()) && !exist){
							inerRoot = new TreeItem(root, SWT.NONE);
							inerRoot.setText(listErrorFiles[i].replace("Transformations_", "")); //$NON-NLS-1$ //$NON-NLS-2$
							exist = true;
						}
						if(exist){
							TreeItem file = new TreeItem(inerRoot, SWT.NONE);
							file.setData("logs/"+listErrorFiles[i]); //$NON-NLS-1$

							file.setText(listLogs[a]);
						}

					}				
				}
			}

		}
	}
	
	public void showLogs(String s, BufferedReader br, ArrayList<String> listDate, ArrayList<Double> listTime) throws Exception{
		while((s = br.readLine()) != null) { 
			
			toShow = toShow+s+System.getProperty("line.separator"); //$NON-NLS-1$
			if(s.contains("INFO - Start")){ //$NON-NLS-1$
				
				start = ((s.replaceAll("[a-zA-Z]", "")).replace(" ", "")).substring(2, 16); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			
			}else if(s.contains("INFO - Gateway Transformation Execution Ended")){ //$NON-NLS-1$
				
				end = ((s.replaceAll("[a-zA-Z]", "")).replace(" ", "")).substring(2, 16); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				DateFormat dateFormat = new SimpleDateFormat("ddhh:mm:ssyyyy" ); //$NON-NLS-1$
				
				 try {
					Date sd = dateFormat.parse(start);
					Date sd1 = dateFormat.parse(end);
					double time = sd1.getTime() - sd.getTime();
					
					listTime.add(time/1000);
					listDate.add(sd1.toString());
					index ++;
				} catch (ParseException e) {
					e.printStackTrace();
				}
				 
				
			}
		}
	}
	
	public void showTransfoLogs(File transfoFiles, String string, FileReader fr){
		
		listTime = new ArrayList<Double>();
		listDate = new ArrayList<String>();
		List<String> ttransfo = Arrays.asList(transfoFiles.list());
		Collections.sort(ttransfo);
		ttransfo = sortByDate(ttransfo);
		for(String fileToRead : ttransfo){
		 try {
			fr = new FileReader("logs/"+string+"/"+fileToRead); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(fr);
		String s;
		try {
			while((s = br.readLine()) != null) { 
				toShow = toShow+s+System.getProperty("line.separator"); //$NON-NLS-1$
				
				if(s.contains("INFO - Start")){ //$NON-NLS-1$
					
					start = ((s.replaceAll("[a-zA-Z]", "")).replace(" ", "")).substring(2, 16); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				
				}else if(s.contains("INFO - Gateway Transformation Execution Ended")){ //$NON-NLS-1$
					
					end = ((s.replaceAll("[a-zA-Z]", "")).replace(" ", "")).substring(2, 16); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					DateFormat dateFormat = new SimpleDateFormat("ddhh:mm:ssyyyy" ); //$NON-NLS-1$
					
					 try {
						Date sd = dateFormat.parse(start);
						Date sd1 = dateFormat.parse(end);
						double time = sd1.getTime() - sd.getTime();
						
						listTime.add(time/1000);
						listDate.add(sd1.toString());
						index ++;
					} catch (ParseException e) {
						e.printStackTrace();
					}
					 
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	}
	
	private List<String> sortByDate(List<String> ttransfo) {
		
		Boolean gotFirst = false;
		List<String> returnList = new ArrayList<String>();
		String first = null;
		
		for (String string : ttransfo){
			
			if(!gotFirst){
				
				first = string;
				gotFirst = true;
				
			}else{
				
				returnList.add(string);
				
			}
		}
		returnList.add(first);
		
		return returnList;
	}
	

	@Override
	public void setFocus() { }
}
