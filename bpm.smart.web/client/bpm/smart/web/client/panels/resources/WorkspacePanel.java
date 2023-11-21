package bpm.smart.web.client.panels.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScript.ScriptType;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.MainPanel;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.dialogs.CustomDialog;
import bpm.smart.web.client.panels.ConsolePanel;
import bpm.smart.web.client.panels.ScriptPanel;
import bpm.smart.web.client.panels.Vignette;
import bpm.smart.web.client.panels.Vignette.TypeVignette;
import bpm.smart.web.client.services.SmartAirService;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkspacePanel extends CompositeWaitPanel{

	protected static final int INTERCEPT_DELAY = 100;

	private static WorkspacePanelUiBinder uiBinder = GWT.create(WorkspacePanelUiBinder.class);

	interface WorkspacePanelUiBinder extends UiBinder<Widget, WorkspacePanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();
	}

	@UiField
	HTMLPanel workspaceContent, navRight, centerPanel;


	@UiField
	MyStyle style;

	private AirPanel airPanel;
	private MainPanel mainPanel;
	private NavigationPanel navPanel;
	private AirProject currentProject;
	private RScript currentScript;
	private RScriptModel currentModel;
	private List<RScriptModel> currentVersions;

	private Vignette currentVignette;
	// Stacks for undo/redo
//	private Stack<String> beforeStack = new Stack<String>();
//	private Stack<String> afterStack = new Stack<String>();

	private User user;
	private String rLibs;
	
	private boolean isCopyPasteEnabled;
	
	public Stack<String> consoleStack = new Stack<String>();
	public int consoleStackPointer = 0;
	
	private List<Vignette.TypeVignette> fullShow;
	
	private ConsolePanel consolePanel;
	private ScriptPanel genCodePanel;

	public WorkspacePanel(AirPanel airPanel, String rLibs, boolean isCopyPasteEnabled, MainPanel mainPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.airPanel = airPanel;
		this.navPanel = airPanel.getNavigationPanel();
		this.user = airPanel.getUser();
		this.rLibs = rLibs;
		this.isCopyPasteEnabled = isCopyPasteEnabled;
		this.mainPanel = mainPanel;
		
		this.addStyleName(style.mainPanel());
		
		fullShow = new ArrayList<Vignette.TypeVignette>();
		fullShow.add(Vignette.TypeVignette.HTML);
		fullShow.add(Vignette.TypeVignette.PDF);
		fullShow.add(Vignette.TypeVignette.WORD);
		fullShow.add(Vignette.TypeVignette.R);
		fullShow.add(Vignette.TypeVignette.MARKDOWN);
		fullShow.add(Vignette.TypeVignette.LOG);
		fullShow.add(Vignette.TypeVignette.GENCODE);

		centerPanel.add(new ScriptPanel(this, rLibs, isCopyPasteEnabled, null, null, null, null));
		
		consolePanel =  new ConsolePanel(this, isCopyPasteEnabled);
		createVignettewithHTML(consolePanel.getConsole().getElement().getInnerHTML(), Vignette.TypeVignette.LOG, "Logs", consolePanel.asWidget(), false, true);
		
		RScript genScript = new RScript();
		genScript.setIdProject(0);
		genScript.setName("Generated Code");
		genScript.setScriptType(ScriptType.R.name());
		RScriptModel genCode = new RScriptModel();
		genCode.setScript(Cookies.getCookie("gen"+user.getId()));
		genCodePanel =  new ScriptPanel(this, rLibs, isCopyPasteEnabled, new AirProject(), genScript, genCode, null);
		createVignettewithHTML(genCodePanel.getScriptArea().getHTML(), Vignette.TypeVignette.GENCODE, "Generated Code", genCodePanel.asWidget(), false, true);
	}
//	
//	@Override
//	protected void onLoad() {
//		((ConsolePanel)getVignette("Logs").getWidget()).onLoad();
//	};

	public void setCurrentProject(AirProject selectedProject) {
		this.currentProject = selectedProject;

	}

	public void refresh(AirProject project, RScript script, RScriptModel model, List<RScriptModel> versions) {
 		Vignette vignette = getVignette((script.getName().equals("")) ? "New" : script.getName());
		if(vignette != null){
			showVignette(vignette);
			if(vignette.getWidget() instanceof ScriptPanel){
				((ScriptPanel)vignette.getWidget()).setCurrentProject(project);
			}
		} else {
			centerPanel.clear();
			centerPanel.add(new ScriptPanel(this, rLibs, isCopyPasteEnabled, project, script, model, versions));
		}

		currentProject = project;
		currentScript = script;
		currentModel = model;
		currentVersions = versions;
	}
	
	public void createVignettewithUrl(String url, final Vignette.TypeVignette type, final String name, final Widget widget, final boolean isNew, final boolean isLocked){
		showWaitPart(true);
		SmartAirService.Connect.getInstance().getUrlContent(url, new GwtCallbackWrapper<String>(WorkspacePanel.this, true){
			@Override
			public void onSuccess(String result) {
				if(result.contains("<body")){
					result= result.substring(result.indexOf("<body"), result.indexOf("</body>") + "</body>".length());
				}
				Vignette vign = new Vignette(WorkspacePanel.this, type, name, result, widget, isLocked); 
				vign.setNew(isNew);
				navRight.add(vign);
			}
		}.getAsyncCallback());
	}
	
	public void createVignettewithHTML(String html, Vignette.TypeVignette type, String name, Widget widget, boolean isNew, boolean isLocked){
		Vignette vign = new Vignette(WorkspacePanel.this, type, name, html, widget, isLocked); 
		vign.setNew(isNew);
		navRight.add(vign);
		
	}

	public Vignette getCurrentVignette() {
		return currentVignette;
	}

	public void setCurrentVignette(Vignette currentVignette) {
		if(this.currentVignette != null){
			this.currentVignette.setSelected(false);
		}
		this.currentVignette = currentVignette;
		currentVignette.setSelected(true);
		
//		if(currentVignette.getWidget() instanceof ScriptPanel){
//			navPanel.setCurrentModel(currentModel);
//			navPanel.setCurrentScript(currentScript);
//			navPanel.setCurrentProject(currentProject);
//			navPanel.onRefreshClick(null);
//		}
	}
	
	public void showVignette(Vignette vignette){
		if(vignette != null){
			
			vignette.setNew(false);
			
			setCurrentVignette(vignette);
			
			if(fullShow.contains(vignette.getType())){
				centerPanel.clear();
				centerPanel.add(vignette.getWidget());
			} else {
				CustomDialog dial = new CustomDialog(vignette.getTitle(), vignette.getWidget());
				dial.center();
			}
			
			
		}
		
	}
	
	public Vignette getVignette(String name) { //recupere la premiere vignette du nom
		for(int i = 0; i<navRight.getWidgetCount(); i++){
			if(navRight.getWidget(i) instanceof Vignette){
				Vignette vign = (Vignette)navRight.getWidget(i);
				if(vign.getTitle().equals(name)){
					return vign;
				}
			}
		}
		return null;
	}
	
	public void updateVignetteLog(HTML html){
		//update logvignette
		Vignette logVignette = getVignette("Logs");
		if(logVignette != null){
			logVignette.setNew(true);
			HTMLPanel panel = ((ConsolePanel)logVignette.getWidget()).getConsole();
			panel.add(html);
			((ConsolePanel)logVignette.getWidget()).setConsole(panel);
			logVignette.setWidget(logVignette.getWidget());
			logVignette.setScreenCapture(panel.getElement().getInnerHTML());
		}
	}

	public NavigationPanel getNavPanel() {
		return navPanel;
	}

	public void setNavPanel(NavigationPanel navPanel) {
		this.navPanel = navPanel;
	}

	public AirPanel getAirPanel() {
		return airPanel;
	}

	public void setAirPanel(AirPanel airPanel) {
		this.airPanel = airPanel;
	}

	public HTMLPanel getNavRight() {
		return navRight;
	}

	public void setNavRight(HTMLPanel navRight) {
		this.navRight = navRight;
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public HTMLPanel getWorkspaceContent() {
		return workspaceContent;
	}

	public void setWorkspaceContent(HTMLPanel workspaceContent) {
		this.workspaceContent = workspaceContent;
	}

	public User getUser() {
		return user;
	}

	public void onCloseVignette(Vignette vignette) {
		int index = 1;
		for(int i = 0; i<navRight.getWidgetCount(); i++){ // on recupere l'index de la vignette
			if(navRight.getWidget(i) instanceof Vignette){
				Vignette vign = (Vignette)navRight.getWidget(i);
				if(vign.getTitle().equals(vignette.getTitle())){
					index = i;
					break;
				}
			}
		}
		
		
		if(vignette.getType().equals(TypeVignette.MARKDOWN) || vignette.getType().equals(TypeVignette.R)){
			final ScriptPanel w = (ScriptPanel)vignette.getWidget();
			if(!w.getSavedString().equals(w.getText())){ //scripts modifies
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Save(), LabelsConstants.lblCnst.Save(), 
						LabelsConstants.lblCnst.QuitWithoutSave(), LabelsConstants.lblCnst.UnsavedModificationsMessage(), true);
				dial.center();
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(dial.isConfirm()){
							w.onSaveClick(null);
						}
					}
				});
			}
		}
		
		showVignette((Vignette)navRight.getWidget(index - 1));
	}

	public RScript getCurrentScript() {
		return currentScript;
	}

	public void setCurrentScript(RScript currentScript) {
		this.currentScript = currentScript;
	}

	public RScriptModel getCurrentModel() {
		return currentModel;
	}

	public void setCurrentModel(RScriptModel currentModel) {
		this.currentModel = currentModel;
	}

	public List<RScriptModel> getCurrentVersions() {
		return currentVersions;
	}

	public void setCurrentVersions(List<RScriptModel> currentVersions) {
		this.currentVersions = currentVersions;
	}

	public AirProject getCurrentProject() {
		return currentProject;
	}

	public void onScriptDelete(RScript script) {
		Vignette vign = getVignette(script.getName());
		if(!vign.equals(null)){
			vign.removeFromParent();
		}
		showVignette((Vignette)navRight.getWidget(0));
	}

	public ConsolePanel getConsolePanel() {
		return consolePanel;
	}

	public void onResize() {
		if(currentVignette.getWidget() instanceof ScriptPanel){
			((ScriptPanel)currentVignette.getWidget()).onResize();
		}
	}
	
	public void writeGeneratedCode(String code) {
		genCodePanel.addCode(code);
		genCodePanel.saveGenCode();
		updateVignetteGenCode();
		
		if(currentVignette.getWidget() instanceof ScriptPanel && !currentVignette.getWidget().equals(genCodePanel)){
			((ScriptPanel)currentVignette.getWidget()).addCode(code);
			currentVignette.setNew(true);
			currentVignette.setScreenCapture(((ScriptPanel)currentVignette.getWidget()).getScriptArea().getHTML());
		}
	}
	
	public void updateVignetteGenCode(){
		Vignette genCodeVignette = getVignette("Generated Code");
		if(genCodeVignette != null){
			genCodeVignette.setNew(true);
			genCodeVignette.setScreenCapture(genCodePanel.getScriptArea().getHTML());
		}
	}

}
