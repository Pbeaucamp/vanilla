package bpm.smart.web.client.panels;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.dialogs.CustomDialog;
import bpm.smart.web.client.dialogs.GraphDialog;
import bpm.smart.web.client.panels.resources.WorkspacePanel;
import bpm.smart.web.client.services.SmartAirService;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ConsolePanel extends CompositeWaitPanel{
	protected static final int INTERCEPT_DELAY = 100;

	private static ConsolePanelUiBinder uiBinder = GWT.create(ConsolePanelUiBinder.class);

	interface ConsolePanelUiBinder extends UiBinder<Widget, ConsolePanel> {
	}

	interface MyStyle extends CssResource {

		String frame();
	}
	
	@UiField
	TextBox txtHelp;

	@UiField
	TextBox txtConsole;
	
	@UiField
	HTMLPanel console;
	
	@UiField
	Image btnClear, btnCopyConsole;

	@UiField
	Button btnHelp;
	
	@UiField
	MyStyle style;
	
	private WorkspacePanel workspacePanel;
	private User user;
	private boolean isCopyPasteEnabled;
	
	public ConsolePanel(WorkspacePanel workspacePanel, boolean isCopyPasteEnabled) {
		initWidget(uiBinder.createAndBindUi(this));
		this.workspacePanel = workspacePanel;
		this.user = workspacePanel.getUser();
		this.isCopyPasteEnabled = isCopyPasteEnabled;
		
		if (isCopyPasteEnabled) {
			btnCopyConsole.setVisible(true);
		}
		else {
			btnCopyConsole.setVisible(false);
		}

		txtConsole.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
//				if (currentScript.getScriptType().equals(ScriptType.MARKDOWN)) {
//					return;
//				}
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					runScript(txtConsole.getText());
				}
				else if (event.getNativeKeyCode() == KeyCodes.KEY_DOWN) {
					onKeyDownConsole();
				}
				else if (event.getNativeKeyCode() == KeyCodes.KEY_UP) {
					onKeyUpConsole();
				}
			}
		});

		this.txtHelp.getElement().setAttribute("placeholder", LabelsConstants.lblCnst.LibraryOrFunction());
		
		console.getElement().setId("console");
		
		initJs(this);
	}
	
	@Override
	public void onLoad() {
		scrollBottom();
	};
	
//	@Override
//	public void onAttach() {
//		initJs(this);
//	};
	
	private final native void initJs(ConsolePanel consolePanel) /*-{
		var consolePanel = consolePanel;
		

		$wnd.addToStack = function(name) {
			consolePanel.@bpm.smart.web.client.panels.ConsolePanel::handleAddToStackClick(Ljava/lang/String;)(name);
		};

		
	}-*/;

	@UiHandler("btnClear")
	public void onConsoleClearClick(ClickEvent event) {
		console.clear();
	}
//
//	@UiHandler("btnCopyConsole")
//	public void onConsoleCopyClick(ClickEvent event) {
//		scriptArea.setSelectionRange(0, 0);
//
//		execOnConsole("copy");
//	}
	

	@UiHandler("btnHelp")
	public void onHelpClick(ClickEvent event) {
		if (txtHelp.getText().equals("")) {
			txtHelp.setFocus(true);
			return;
		}

		RScriptModel box = new RScriptModel();
		String script = "help(" + txtHelp.getText() + ")";
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());

		workspacePanel.consoleStack.push(script);

		workspacePanel.consoleStackPointer = workspacePanel.consoleStack.size() + 1;
		txtConsole.setText("");

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				// showWaitPart(false);
				// console.clear();
				console.add(new HTML(result.getOutputLog()));
				scrollBottom();
//				workspacePanel.updateVignetteLog(new HTML(result.getOutputLog()));
				workspacePanel.getAirPanel().getLogPanel().addLog(result.getOutputLog());

				if (result.getOutputFiles() != null) {
					int nbfiles = result.getOutputFiles().length;
					if (nbfiles > 0) {
						// String[] tempUrls = new String[nbfiles];
						for (int i = 0; i < nbfiles; i++) {
							//String url = result.getOutputFiles()[i];

//							panelVariables.clear();
							String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + "Help" + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_HTML;
							Frame frame = new Frame(fullUrl);
							frame.setStyleName(style.frame());
//							panelVariables.add(frame);
							CustomDialog dial = new CustomDialog("Help", frame, "500px", "800px");
							dial.center();
						}
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToExecuteScript());
			}
		});

	}


	public final native void scrollBottom() /*-{
		var element = $doc.getElementById("console");
		element.scrollTop = element.scrollHeight;
	}-*/;
	private final native void execOnConsole(String text) /*-{
		if (document.selection) {
			var range = $doc.body.createTextRange();
			range.moveToElementText($doc.getElementById("console"));
			range.select();
		} else if ($wnd.getSelection) {
			var range = $doc.createRange();
			range.selectNode($doc.getElementById("console"));
			$wnd.getSelection().addRange(range);
		}
	
		try {
	
			var keys = "CTRL + C";
			var successful = $doc.execCommand("copy");
			var msg = successful ? 'successful' : 'unsuccessful';
	
			if (successful) {
				console.log('The text command was ' + msg);
			} else {
				console.log('The text command was ' + msg);
				alert('Oops, unable to do that on this browser. Please use the keys '
						+ keys + ' instead');
			}
	
		} catch (err) {
			console.log('Oops, unable to do that on this browser');
			alert('Oops, unable to do that on this browser. Please use the keys '
					+ keys + ' instead');
		}
	}-*/;
	
	private void onKeyUpConsole() {
		if (workspacePanel.consoleStackPointer <= 1) {
			return;
		}
		workspacePanel.consoleStackPointer--;
		txtConsole.setText(workspacePanel.consoleStack.get(workspacePanel.consoleStackPointer - 1));
	}
	
	private void onKeyDownConsole() {
		if (workspacePanel.consoleStackPointer > workspacePanel.consoleStack.size() || workspacePanel.consoleStack.size() == 0) {
			return;
		}
		else if (workspacePanel.consoleStackPointer == workspacePanel.consoleStack.size()) {
			workspacePanel.consoleStackPointer++;
			txtConsole.setText("");
		}
		else {
			workspacePanel.consoleStackPointer++;
			txtConsole.setText(workspacePanel.consoleStack.get(workspacePanel.consoleStackPointer - 1));
		}
	}public void handleAddToStackClick(String name) {
		workspacePanel.consoleStack.push(name);
		workspacePanel.consoleStackPointer = workspacePanel.consoleStack.size() + 1;
		onKeyUpConsole();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				workspacePanel.showVignette(workspacePanel.getVignette("Logs"));
				txtConsole.setFocus(true);
			}
		});
	}

	
	
	private void runScript(String script) {
		RScriptModel box = new RScriptModel();
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());
		// box.setOutputs("no_return".split(" "));

		for (String str : box.getScript().split("\n")) {
			workspacePanel.consoleStack.push(str);
		}
		workspacePanel.consoleStackPointer = workspacePanel.consoleStack.size() + 1;
		txtConsole.setText("");
		showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				showWaitPart(false);
				// console.clear();result = result;
				console.add(new HTML(result.getOutputLog()));
				scrollBottom();
				
//				workspacePanel.updateVignetteLog(new HTML(result.getOutputLog()));
				
				workspacePanel.getAirPanel().getLogPanel().addLog(result.getOutputLog());

				if (result.getOutputFiles() != null) {
					int nbfiles = result.getOutputFiles().length;
					if (nbfiles > 0) {
						// String[] tempUrls = new String[nbfiles];
						for (int i = 0; i < nbfiles; i++) {
							String url = result.getOutputFiles()[i];
//							if (url.split(";")[0].equals("data:image/svg+xml")) {
							if (url.startsWith("<svg")) {
								GraphDialog dial = new GraphDialog(result.getOutputFiles()[i], LabelsConstants.lblCnst.Image(), CommonConstants.FORMAT_SVG);
								dial.center();
							}
							else if (url.split(";")[0].equals("pdf")) {
								// GraphDialog dial = new
								// GraphDialog(result.getOutputFiles()[i],
								// LabelsConstants.lblCnst.Help(),
								// CommonConstants.FORMAT_TXT );
								// dial.center();
		//						panelVariables.clear();
								String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + "Help" + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_PDF;
								Frame frame = new Frame(fullUrl);
								frame.setStyleName(style.frame());
		//						panelVariables.add(frame);
								CustomDialog dial = new CustomDialog("Help", frame, "500px", "800px");
								dial.center();
							}
							else if (url.split(";")[0].equals("html")) {
								// GraphDialog dial = new
								// GraphDialog(result.getOutputFiles()[i],
								// LabelsConstants.lblCnst.Help(),
								// CommonConstants.FORMAT_TXT );
								// dial.center();
//								panelVariables.clear();
								String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + "Help" + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_HTML;
								Frame frame = new Frame(fullUrl);
								frame.setStyleName(style.frame());
//								panelVariables.add(frame);
								CustomDialog dial = new CustomDialog("Help", frame, "500px", "800px");
								dial.center();
							}
							else {
		//						panelVariables.clear();
								String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + "Help" + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_TXT;
								Frame frame = new Frame(fullUrl);
								frame.setStyleName(style.frame());
		//						panelVariables.add(frame);
								CustomDialog dial = new CustomDialog("Help", frame, "500px", "800px");
								dial.center();
							}

						}
						// result.setOutputFiles(tempUrls);
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToExecuteScript());
			}
		});
	}


	public HTMLPanel getConsole() {
		return console;
	}


	public void setConsole(HTMLPanel console) {
		this.console = console;
	}
	
	
}
