package bpm.gwt.commons.client.viewer;

import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.FileFormat;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

public class ExtDocViewer extends Viewer {
	private static final String CSS_LABEL_PROBLEM = "lblProblemLecture";
	private static final String CSS_BUTTON_PROBLEM = "btnProblemLecture";

	private PortailRepositoryItem item;
	private Group selectedGroup;
	private List<Group> availableGroups;
	
	
	private String reportName;
	private boolean hasBeenRun = false;
	
	public ExtDocViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups) {
		super(vanillaViewer);
		this.item = item;
		this.selectedGroup = selectedGroup;
		this.availableGroups = availableGroups;
		
		LaunchReportInformations itemInfo = new LaunchReportInformations();
		itemInfo.setItem(item);
		
		runItem(itemInfo);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if(itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);;
		}
		btnExport.setVisible(true);
		btnPrint.setVisible(true);
	}

	@Override
	public void runItem(LaunchReportInformations itemInformations) {
		defineToolbar(itemInformations);
		
		ReportingService.Connect.getInstance().getDocumentUrl(item.getId(), new AsyncCallback<DisplayItem>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			public void onSuccess(DisplayItem item) {
				reportName = item.getKey();
				hasBeenRun = true;
				showReport(reportName);
			}
		});
	}

	public void showReport(String reportName) {
		reportPanel.clear();
		
		String upperReportName = reportName.toUpperCase();
		if(upperReportName.contains(FileFormat.MPG) || upperReportName.contains(FileFormat.MPEG) 
				|| upperReportName.contains(FileFormat.AVI) || upperReportName.contains(FileFormat.MP4) 
				|| upperReportName.contains(FileFormat.WMW) || upperReportName.contains(FileFormat.MOV) 
				|| upperReportName.contains(FileFormat.MKV) || upperReportName.contains(FileFormat.MLV)){
			
			AbsolutePanel panelMusic = buildVideoPart(reportName);
			if(panelMusic != null){
				reportPanel.add(panelMusic);
			}
			else {
				reportFrame.setUrl(reportName);
				reportPanel.add(reportFrame);
			}
		}
		else if(upperReportName.contains(FileFormat.MP3) || upperReportName.contains(FileFormat.WAV) 
				|| upperReportName.contains(FileFormat.FLAC) || upperReportName.contains(FileFormat.WMA) 
				|| upperReportName.contains(FileFormat.M4A)){
	
			AbsolutePanel panelMusic = buildMusicPart(reportName);
			if(panelMusic != null){
				reportPanel.add(panelMusic);
			}
			else {
				reportFrame.setUrl(reportName);
				reportPanel.add(reportFrame);
			}
		}
		else {
			reportFrame.setUrl(reportName);
			reportPanel.add(reportFrame);
		}
	}
	
	private AbsolutePanel buildVideoPart(final String objectUrl){	
		Video video = Video.createIfSupported();
		if(video != null){
			video.setSrc(objectUrl);
			video.setControls(true);
			
			Label lblProblem = new Label(LabelsConstants.lblCnst.ProblemLecture());
			lblProblem.addStyleName(CSS_LABEL_PROBLEM);
			
			Button btnProblem = new Button(LabelsConstants.lblCnst.TryPlaying());
			btnProblem.addStyleName(CSS_BUTTON_PROBLEM);
			btnProblem.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					reportPanel.clear();
					
					reportFrame.setUrl(objectUrl);
					reportPanel.add(reportFrame);
				}
			});
			
			AbsolutePanel musicPanel = new AbsolutePanel();
			musicPanel.add(video);
			musicPanel.add(lblProblem);
			musicPanel.add(btnProblem);
			
			return musicPanel;
		}
		else {
			return null;
		}
	}
	
	private AbsolutePanel buildMusicPart(final String objectUrl){
		Audio audio = Audio.createIfSupported();
		if(audio != null) {
			audio.setSrc(objectUrl);
			audio.setControls(true);
			
			Label lblProblem = new Label(LabelsConstants.lblCnst.ProblemLecture());
			lblProblem.addStyleName(CSS_LABEL_PROBLEM);
			
			Button btnProblem = new Button(LabelsConstants.lblCnst.TryPlaying());
			btnProblem.addStyleName(CSS_BUTTON_PROBLEM);
			btnProblem.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					reportPanel.clear();
					
					reportFrame.setUrl(objectUrl);
					reportPanel.add(reportFrame);
				}
			});
			
			AbsolutePanel musicPanel = new AbsolutePanel();
			musicPanel.add(audio);
			musicPanel.add(lblProblem);
			musicPanel.add(btnProblem);
			
			return musicPanel;
		}
		else {
			return null;
		}
	}
	
	public static native void openWindow(String url) /*-{
		$wnd.open(url);
	}-*/;
	
	public static native void printWindow(String url) /*-{
		myWindowToPrint = $wnd.open(url);
		myWindowToPrint.print();
	}-*/;

	@Override
	public void onExportClick(ClickEvent event) {
		if(hasBeenRun){
			exportReport(reportName);
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.DocumentNotRun());
		}
	}
	
	public void exportReport(String reportName) {
		ToolsGWT.doRedirect(reportName);
	}

	@Override
	public void onPrintClick(ClickEvent event) {
		if(hasBeenRun){
			printReport(reportName);
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.DocumentNotRun());
		}
	}
	
	public void printReport(String reportName) {
		printWindow(reportName);
	}

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, item.getId(), TypeCollaboration.ITEM_NOTE, selectedGroup, availableGroups);
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}
}

