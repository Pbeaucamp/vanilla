package bpm.fmloader.client.dialog;

import java.util.List;

import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.dto.ApplicationDTO;
import bpm.fmloader.client.dto.AssoMetricAppDTO;
import bpm.fmloader.client.dto.CommentDTO;
import bpm.fmloader.client.dto.IndicatorValuesDTO;
import bpm.fmloader.client.dto.MetricDTO;
import bpm.fmloader.client.images.ImageResources;
import bpm.fmloader.client.infos.InfosUser;
import bpm.fmloader.client.wizard.Space;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A dialog who show an evolution chart with a table who contain the previous and the actual value 
 * @author Marc
 *
 */
public class ValueInformationsDialog extends DialogBox {

	private IndicatorValuesDTO valueCell;
	private IndicatorValuesDTO previousValue;
	private String filename;
	private HorizontalPanel lstPanel;
	private VerticalPanel chartPanel;
	private HorizontalPanel framePanel = new HorizontalPanel();
	private TextArea txtComment = new TextArea();
	private Image imgComment;
	
	public ValueInformationsDialog(IndicatorValuesDTO indicateurValueCell, IndicatorValuesDTO previousValueDTO, String filename) {
		super();
		this.setText(Constantes.LBL.ValueInformations());
		this.valueCell = indicateurValueCell;
		this.previousValue = previousValueDTO;
		this.filename = filename;
		
		
		VerticalPanel mainPanel = new VerticalPanel();
		
		HorizontalPanel datasPanel = new HorizontalPanel();
		fillDatasPanel(datasPanel);
		
		HorizontalPanel btnPanel = new HorizontalPanel();
		Image btnOk = new Image(ImageResources.INSTANCE.apply());
		btnOk.setTitle(Constantes.LBL.Apply());
		btnOk.setSize("32px", "32px");
		Image btnCancel = new Image(ImageResources.INSTANCE.cancel());
		btnCancel.setTitle(Constantes.LBL.Cancel());
		btnCancel.setSize("32px", "32px");
		
		btnPanel.add(btnCancel);
		btnPanel.add(btnOk);
		
		//show application and metric selected
		mainPanel.add(createApplicationMetricPanel());
		
		mainPanel.add(datasPanel);
		mainPanel.add(btnPanel);
		
		this.setWidget(mainPanel);
		
		this.addStyleName("infoDialog");
		
		this.setSize("800px", "370px");
		mainPanel.setSize("800px", "370px");
		datasPanel.setSize("800px", "320px");
		btnPanel.setSize("800px", "50px");
		mainPanel.addStyleName("backgroundBlue");
		
		btnPanel.setCellHorizontalAlignment(btnOk, HorizontalPanel.ALIGN_CENTER);
		btnPanel.setCellHorizontalAlignment(btnCancel, HorizontalPanel.ALIGN_CENTER);
		btnPanel.setCellVerticalAlignment(btnOk, HorizontalPanel.ALIGN_MIDDLE);
		btnPanel.setCellVerticalAlignment(btnCancel, HorizontalPanel.ALIGN_MIDDLE);
		
		btnOk.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(valueCell.getComment() != null) {
					valueCell.getComment().setComment(txtComment.getText());
					if(imgComment == null) {
						valueCell.getComment().setLocked(valueCell.getComment().getLocked());
					}
					else if(imgComment.getUrl().equalsIgnoreCase(GWT.getHostPageBaseURL() + "images/lock.png")) {
						valueCell.getComment().setLocked(0);
					}
					else {
						valueCell.getComment().setLocked(1);
					}
				}
				else {
					CommentDTO com = new CommentDTO();
					com.setValueId(valueCell.getId());
					com.setComment(txtComment.getText());
					
					if(imgComment == null) {
						com.setLocked(0);
					}
					else if(imgComment.getUrl().equalsIgnoreCase(GWT.getHostPageBaseURL() + "images/lock.png")) {
						com.setLocked(0);
					}
					else {
						com.setLocked(1);
					}
					
					valueCell.setComment(com);
				}
				
				ValueInformationsDialog.this.hide();
			}
		});
		
		btnCancel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ValueInformationsDialog.this.hide();
			}
		});
	}

	private HorizontalPanel createApplicationMetricPanel() {
		HorizontalPanel appMetricPanel = new HorizontalPanel();
		appMetricPanel.setWidth("100%");
		
		//find the application and the metric for the selected cell
		int assoId = 0;
		if(valueCell != null && !valueCell.isNull()) {
			assoId = valueCell.getAssoId();	
		}
		
		else if(previousValue != null){
			assoId = previousValue.getAssoId();
		}
		
		else {
			assoId = valueCell.getAssoId();	
		}
		
		
		List<ApplicationDTO> app = null;
		MetricDTO met = null;
		
//		for(AssoMetricAppDTO asso : InfosUser.getInstance().getSelectedAssos()) {
//			if(asso.getAssoId() == assoId) {
//				app = asso.getApplications();
//				met = asso.getMetric();
//				break;
//			}
//		}
		
		//create the panel
		StringBuilder buf = new StringBuilder();
		for(ApplicationDTO dto : app) {
			buf.append(dto.getName() +"_");
		}
		Label lblApp = new Label(Constantes.LBL.Applications() + " : " + buf.toString().substring(0, buf.length() - 1));
		lblApp.addStyleName("infosDialog-lblAppMet");
		
		Label lblMet = new Label(Constantes.LBL.metricCompteur() + " : " + met.getName());
		lblMet.addStyleName("infosDialog-lblAppMet");
		
		appMetricPanel.add(lblApp);
		appMetricPanel.add(lblMet);
		
		return appMetricPanel;
	}

	private void fillDatasPanel(HorizontalPanel datasPanel) {
		chartPanel = new VerticalPanel();
		Label lblChart = new Label(Constantes.LBL.ValueEvolution() + " : ");
		Label lblLst = new Label(Constantes.LBL.metricCompteur() + " : ");
		
		lblChart.setStylePrimaryName("infoDialog-lbltitle");
		lblLst.setStylePrimaryName("infosDialog-lstlbl");
		
		lstPanel = new HorizontalPanel();
		lstPanel.add(lblLst);
		
		getMetricsValues();
		
		chartPanel.add(lblChart);
		chartPanel.add(lstPanel);
		chartPanel.add(framePanel);
		
		VerticalPanel valuesPanel = new VerticalPanel();
		Label lblValues = new Label(Constantes.LBL.ValueComment() + " : ");
		lblValues.setStylePrimaryName("infoDialog-lbltitle");

		HorizontalPanel valuesTitlePanel = new HorizontalPanel();
		valuesTitlePanel.add(lblValues);
		valuesTitlePanel.add(new Space("10px", "1px"));
		
		txtComment.getElement().getStyle().setMarginTop(14, Unit.PX);
		txtComment.setSize("300px", "273px");
		if(valueCell.getComment() != null) {
			txtComment.setText(valueCell.getComment().getComment());
			if(valueCell.getComment().getLocked() == 1) {
				txtComment.setEnabled(false);
			}
		}
		txtComment.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				int sel = txtComment.getCursorPos();
				if(txtComment.getText().length() >= InfosUser.getInstance().getCommentLimit()) {
					txtComment.setText(txtComment.getText().substring(0, InfosUser.getInstance().getCommentLimit()));
					
				}
				txtComment.setCursorPos(sel);
			}
		});
		
//		if(InfosUser.getInstance().getUser().isCommentValidator()) {
//			if(valueCell.getComment() == null) {
//				imgComment = new Image(GWT.getHostPageBaseURL() + "images/lock.png");
//				imgComment.setTitle(Constantes.LBL.LockComment());
//			}
//			else if(valueCell.getComment().getLocked() == 1) {
//				imgComment = new Image(GWT.getHostPageBaseURL() + "images/unlock.png");
//				imgComment.setTitle(Constantes.LBL.UnlockComment());
//			}
//			else {
//				imgComment = new Image(GWT.getHostPageBaseURL() + "images/lock.png");
//				imgComment.setTitle(Constantes.LBL.LockComment());
//			}
//			imgComment.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					if(imgComment.getUrl().equalsIgnoreCase(GWT.getHostPageBaseURL() + "images/lock.png")) {
//						imgComment.setUrl(GWT.getHostPageBaseURL() + "images/unlock.png");
//						imgComment.setTitle(Constantes.LBL.UnlockComment());
////						valueCell.getOriginalValue().getComment().setLocked(1);
//						txtComment.setEnabled(false);
//					}
//					else {
//						imgComment.setUrl(GWT.getHostPageBaseURL() + "images/lock.png");
//						imgComment.setTitle(Constantes.LBL.LockComment());
////						valueCell.getOriginalValue().getComment().setLocked(0);
//						txtComment.setEnabled(true);
//					}
//				}
//			});
//			imgComment.addStyleName("pointer");
//			valuesTitlePanel.add(imgComment);
//		}
		
		//if the user can't write comments
		
		valuesPanel.add(valuesTitlePanel);
		valuesPanel.add(txtComment);
		
		chartPanel.setSize("500px", "320px");
		valuesPanel.setSize("300px", "320px");
		
		chartPanel.setCellHeight(lblChart, "30px");
		chartPanel.setCellHeight(lstPanel, "30px");
		valuesPanel.setCellHeight(lblValues, "60px");
		
		datasPanel.add(chartPanel);
		datasPanel.add(new Space("10px", "1px"));
		datasPanel.add(valuesPanel);
	}

	private void getMetricsValues() {
		Constantes.DATAS_SERVICES.getMetricsForValueInfos(new AsyncCallback<List<MetricDTO>>() {
			public void onSuccess(List<MetricDTO> result) {
				final ListBox lst = new ListBox(false);
				lst.addStyleName("infosDialog-lst");
				lst.setWidth("300px");
				lstPanel.add(lst);
				
				for(MetricDTO dto : result) {
					lst.addItem(dto.getName(), dto.getName());
				}
				
				lst.addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						filename = "evochart" + new Object().hashCode() + ".jsp";
						Constantes.DATAS_SERVICES.createEvoChart((valueCell.isNull() ? previousValue : valueCell), filename, GWT.getHostPageBaseURL(), lst.getValue(lst.getSelectedIndex()), InfosUser.getInstance().getSelectedDate(), new AsyncCallback<Void>() {
							public void onSuccess(Void result) {
								framePanel.clear();
								Frame frame = new Frame(GWT.getHostPageBaseURL() + filename);
								frame.setSize("500px", "270px");
								framePanel.add(frame);
								frame.setUrl(filename);
							}
							
							public void onFailure(Throwable caught) {
								
							}
						});
					}
				});
				
				lst.setSelectedIndex(0);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), lst);
			}
			
			public void onFailure(Throwable caught) {
				
			}
		});
	}

	public void createFrame() {
		framePanel.clear();
		if(previousValue != null) {
			Frame frame = new Frame(GWT.getHostPageBaseURL() + filename);
			frame.setSize("500px", "270px");
			framePanel.add(frame);
			frame.setUrl(filename);
		}
		
		else {
			SimplePanel sp = new SimplePanel();
			HTML html = new HTML(Constantes.LBL.NoPreviousValues());
			html.setSize("500px", "274px");
			html.addStyleName("noValuesForMetric");
			sp.add(html);
			framePanel.add(sp);
		}
	}

}
