package bpm.smart.web.client.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.workflow.commons.client.dialog.ParametersPanel;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScript.ScriptType;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.StatDataColumn;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.dialogs.CustomDialog;
import bpm.smart.web.client.dialogs.GraphDialog;
import bpm.smart.web.client.dialogs.ImportCodeDialog;
import bpm.smart.web.client.dialogs.MarkdownTemplateDialog;
import bpm.smart.web.client.dialogs.RepositoryConnectionDialog;
import bpm.smart.web.client.dialogs.ValideNameDialog;
import bpm.smart.web.client.dialogs.VersionDialog;
import bpm.smart.web.client.panels.resources.StatsPanel;
import bpm.smart.web.client.panels.resources.WorkspacePanel;
import bpm.smart.web.client.services.SmartAirService;
import bpm.smart.web.client.wizards.RecodeWizard;
import bpm.smart.web.client.wizards.RecodeWizard.Recode;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;
import bpm.vanilla.platform.core.repository.ILinkedParameter;
import bpm.vanilla.platform.core.repository.MarkdownType;

import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class ScriptPanel extends CompositeWaitPanel {
	protected static final int INTERCEPT_DELAY = 100;

	private static ScriptPanelUiBinder uiBinder = GWT.create(ScriptPanelUiBinder.class);

	interface ScriptPanelUiBinder extends UiBinder<Widget, ScriptPanel> {
	}

	interface MyStyle extends CssResource {

		String commandMenu();

		String subMenu();

		String btnGrid();

		String frameMarkdown();

		String frame();

		String hyperlinkPackage();

		String panelTextArea();

		String panelTextAreaEdit();

		String lblEdit();

		String lblInfoEmpty();
	}

	@UiField
	RichTextArea scriptArea;

	@UiField
	ListBox lstVersions, lstType;

	@UiField
	HTMLPanel console;

	@UiField
	HTMLPanel textToolbar, panelEdit, scriptToolbar, panelMarkdownOutputs, panelTextArea, scriptInfoToolbar;

	@UiField
	Image btnCheckIn, btnCheckOut, btnEdit, btnSaveAs, btnSave, btnUndo, btnRedo, btnRunAll, btnRunSelection, btnImportCode, btnExportCode, btnCopy, btnPaste, btnClearText, btnAddManager, btnRefreshDatasets, btnChangeStyle, btnMarkdownRepository, btnMarkdownTemplate, btnRCodeTools, btnHTML, btnPDF, btnWORD;

	@UiField
	TextBox txtHelp;

	@UiField
	Button btnHelp, btnName, btnComment;

	@UiField
	Label lblName, lblComment;

	@UiField
	MyStyle style;

	private AirProject currentProject;
	private RScript currentScript;
	private RScriptModel currentModel;
	private List<RScriptModel> currentVersions;

	private WorkspacePanel workspacePanel;
	private MenuBar commandMenu, styleMenu, rToolsMenu;

	private User user;
	private String rLibs;

	private boolean isCopyPasteEnabled;

	private Timer highlight;
	private boolean firstLoad = true;
	private boolean isInEdition = false;

	private String savedString;
	private CustomDialog dialparameters;

	private MultiWordSuggestOracle suggParam = new MultiWordSuggestOracle();
	private SuggestBox sbox = new SuggestBox(suggParam);

	public ScriptPanel(WorkspacePanel workspacePanel, String rLibs, boolean isCopyPasteEnabled, AirProject project, RScript script, RScriptModel model, List<RScriptModel> versions) {
		initWidget(uiBinder.createAndBindUi(this));
		this.workspacePanel = workspacePanel;
		this.user = workspacePanel.getUser();
		this.rLibs = rLibs;
		this.isCopyPasteEnabled = isCopyPasteEnabled;
		this.currentProject = project;
		this.currentScript = script;
		this.currentModel = model;
		this.currentVersions = versions;

		lstType.clear();
		for (ScriptType item : ScriptType.values()) {
			lstType.addItem(item.name());
		}
		lstType.setSelectedIndex(0);

		console.setVisible(false); // pas affichee

		scriptArea.addKeyUpHandler(new KeyUpHandler() { // sert a
														// l'autocompletion des
														// parametres

			@Override
			public void onKeyUp(KeyUpEvent event) {
				// traitement parametres
				String[] words = getText().substring(0, getCursorTextArea()).split("\\s|\n");
				String lastWord = words[words.length - 1];
				sbox.setText(lastWord);
				sbox.showSuggestionList();

				String html = getSuggestHTML();
				if (!html.equals("")) {
					scriptArea.getFormatter().insertHTML("<span id='tempPointer'/>");
					showCompletion(lastWord);
				}
			}
		});
		scriptArea.addKeyDownHandler(new KeyDownHandler() { // sert a inhiber
															// les actions
															// navigateur : TAB,
															// CTLR-R
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == 9) { // TAB
					event.stopPropagation();
					event.preventDefault();
					String[] words = getText().substring(0, getCursorTextArea()).split(" ");
					final String lastWord = words[words.length - 1];
					String html = getSuggestHTML();
					if (!html.equals("")) { // si suggesst panel popup is
											// visible
						// scriptArea.getElement().blur();
						sbox.addKeyUpHandler(new KeyUpHandler() {
							@Override
							public void onKeyUp(KeyUpEvent event) {
								if (event.getNativeKeyCode() == 13) {
									handleCompletionClick(lastWord);
									event.stopPropagation();
									event.preventDefault();
								}

							}
						});
						sbox.getElement().focus();// focusCompletion(lastWord);
					}
					else {
						scriptArea.getFormatter().insertHTML("    ");
					}
				}
				else if (event.getNativeKeyCode() == 82) { // CTLR-R
					if (event.isControlKeyDown()) {
						event.stopPropagation();
						event.preventDefault();
						if (getSelectedText().equals("")) {
							onRunAllClick(null);
						}
						else {
							onRunSelectionClick(null);
						}
					}

				}
			}
		});

		lblName.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (currentScript != null && currentScript.getId() != 0) {
					lblName.getElement().setAttribute("contentEditable", "true");
					lblName.addStyleName(style.lblEdit());
					btnName.setVisible(true);
				}
			}
		});
		lblName.addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == 10 || event.getNativeKeyCode() == 13) { // CR
																						// LF
					event.stopPropagation();
					event.preventDefault();
					currentScript.setName(lblName.getText());
					lblName.getElement().setAttribute("contentEditable", "false");
					lblName.removeStyleName(style.lblEdit());
					btnName.setVisible(false);
					updateRScript();
					ScriptPanel.this.workspacePanel.getCurrentVignette().setTitle(currentScript.getName());
				}
			}
		}, KeyDownEvent.getType());
		btnName.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentScript.setName(lblName.getText());
				lblName.getElement().setAttribute("contentEditable", "false");
				lblName.removeStyleName(style.lblEdit());
				btnName.setVisible(false);
				updateRScript();
				ScriptPanel.this.workspacePanel.getCurrentVignette().setTitle(currentScript.getName());
			}
		});

		lblComment.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (currentScript != null && currentScript.getId() != 0) {
					lblComment.getElement().setAttribute("contentEditable", "true");
					lblComment.addStyleName(style.lblEdit());
					if (lblComment.getText().equals(LabelsConstants.lblCnst.NoComment())) {
						lblComment.setText("");
						lblComment.removeStyleName(style.lblInfoEmpty());
					}
					btnComment.setVisible(true);
				}

			}
		});
		lblComment.addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == 10 || event.getNativeKeyCode() == 13) { // CR
																						// LF
					event.stopPropagation();
					event.preventDefault();
					currentScript.setComment(lblComment.getText().trim());
					lblComment.getElement().setAttribute("contentEditable", "false");
					lblComment.removeStyleName(style.lblEdit());
					btnComment.setVisible(false);
					if (lblComment.getText().equals(" ")) {
						lblComment.setText(LabelsConstants.lblCnst.NoComment());
						lblComment.addStyleName(style.lblInfoEmpty());
					}
					updateRScript();
				}
			}
		}, KeyDownEvent.getType());
		btnComment.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentScript.setComment(lblComment.getText().trim());
				lblComment.getElement().setAttribute("contentEditable", "false");
				lblComment.removeStyleName(style.lblEdit());
				btnComment.setVisible(false);
				if (lblComment.getText().equals(" ")) {
					lblComment.setText(LabelsConstants.lblCnst.NoComment());
					lblComment.addStyleName(style.lblInfoEmpty());
				}
				updateRScript();

			}
		});

		scriptArea.getElement().setId("scriptarea");
		panelEdit.getElement().setId("editPanel");
		scriptToolbar.getElement().setId("toolbarPanel");
		scriptInfoToolbar.getElement().setId("toolbarInfoPanel");

		btnName.setVisible(false);
		btnComment.setVisible(false);

		createCommandMenu();
		createStyleMenu();
		createRToolsMenu();

		sbox.getElement().getStyle().setZIndex(-100); // on cache cette
														// suggestbox !
		sbox.getElement().getStyle().setOpacity(0);
		sbox.getElement().getStyle().setHeight(0, Unit.PX);
		sbox.getElement().getStyle().setWidth(0, Unit.PX);
		panelEdit.add(sbox);

		txtHelp.getElement().setAttribute("placeholder", LabelsConstants.lblCnst.LibraryOrFunction());

	}

	////////////////////////////////////////////////////// XXX///////////////////////////////////////////////////////////
	//// LOAD de la fenetre ////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void onLoad() { // on initialise les elements necessitants d'etre
								// attaches
		initJs(this); // on init les fonctions js
		refreshAutoCompleteParams(); // recupere la liste des params workflow
										// air

		if (firstLoad) {
			if (currentModel != null && workspacePanel.getVignette(currentScript.getName()) == null) { // on
																										// cree
																										// une
																										// fois
																										// une
																										// vignette
				workspacePanel.createVignettewithHTML(scriptArea.getHTML(), (currentScript.getScriptType().equals("R")) ? Vignette.TypeVignette.R : Vignette.TypeVignette.MARKDOWN, (currentScript.getName().equals("")) ? "New" : currentScript.getName(), this.asWidget(), false, false);
				workspacePanel.setCurrentVignette(workspacePanel.getVignette((currentScript.getName().equals("")) ? "New" : currentScript.getName()));
			}

			refresh(currentProject, currentScript, currentModel, currentVersions); // etat
																					// des
																					// boutons,
																					// textarea
			savedString = getText(); // string servant a savoir si des modifs on
										// ete faites
			firstLoad = false; // pour ne pas executer a chaque affichage du
								// panel
			onResize();

		}

		if (workspacePanel.getCurrentVignette() != null && workspacePanel.getCurrentVignette().getType().equals(Vignette.TypeVignette.GENCODE)) {
			highlight();
		}

	};

	////////////////////////////////////////////////////// XXX///////////////////////////////////////////////////////////
	//// JAVASCRIPT ////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final native void initJs(ScriptPanel panel) /*-{
		var panel = panel;
		$wnd.removeRVar = function(name) { //JSNI 
			panel.@bpm.smart.web.client.panels.ScriptPanel::handleRemoveRVarClick(Ljava/lang/String;)(name);
		};

		$wnd.helpLibrary = function(name) {
			panel.@bpm.smart.web.client.panels.ScriptPanel::handleHelpLibraryClick(Ljava/lang/String;)(name);
		};

		$wnd.showStatsPanel = function(name) {
			panel.@bpm.smart.web.client.panels.ScriptPanel::handleShowStatsPanelClick(Ljava/lang/String;)(name);
		};

		$wnd.clickCompletion = function(word) {
			panel.@bpm.smart.web.client.panels.ScriptPanel::handleCompletionClick(Ljava/lang/String;)(word);
		};

		$wnd.replaceEditPanel = function() {
			var editzone = $doc.getElementById("editPanel");
			var toolbar = $doc.getElementById("toolbarPanel");
			var toolbarInfo = $doc.getElementById("toolbarInfoPanel");
			if (toolbar != null) {
				if (toolbar.clientHeight > 90) {
					editzone.style.top = "150px";
					toolbarInfo.top = "120px";
					//console.log("90");
				} else if (toolbar.clientHeight > 50) {
					editzone.style.top = "110px";
					toolbarInfo.top = "80px";
					//console.log("50");
				} else {
					editzone.style.top = "70px";
					toolbarInfo.top = "40px";
					//console.log("normal");
				}
			}
		};

		$wnd.hljs.configure({
			useBR : true
		}); // configuration de hljs (highlighting textare)
		$wnd
				.setTimeout(
						// insertion style dans la frame textarea
						'var node = document.createElement("link");'
								+ 'node.type = "text/css";'
								+ 'node.rel = "stylesheet";'
								+ 'node.href="highlights/color-brewer.css";'
								+ 'document.getElementById("scriptarea").contentDocument.head.appendChild(node);'
								+ 'document.getElementById("scriptarea").contentDocument.body.setAttribute("style", "padding-right: 50px;");'
								+ 'document.getElementById("scriptarea").contentDocument.body.setAttribute("spellcheck", "false")',
						100);

		$wnd.onresize = function() {
			$wnd.replaceEditPanel();
		};
		$wnd.$("#toolbarPanel").on("click", "*", function() {
			$wnd.replaceEditPanel();
		});
		//		$doc.getElementById("scriptarea").onresize = function(){ $wnd.replaceEditPanel();	};
		//		$wnd.replaceEditPanel();

	}-*/;

	private final native void highlight() /*-{

		//		$wnd.hljs.highlightBlock($doc.getElementById("scriptarea").contentDocument.body);
		$wnd
				.setTimeout(
						'hljs.highlightBlock(document.getElementById("scriptarea").contentDocument.body);',
						50);
	}-*/;

	public native int getCursorTextArea() /*-{
		var elem = $doc.getElementById("scriptarea");
		var node = elem.contentWindow.document.body;
		var range = elem.contentWindow.getSelection().getRangeAt(0);

		var treeWalker = $doc
				.createTreeWalker(
						node,
						NodeFilter.SHOW_ALL,
						function(node) {
							var nodeRange = $doc.createRange();
							nodeRange.selectNodeContents(node);
							if (node.tagName == 'BR' || node.nodeType == 3) {
								return nodeRange.compareBoundaryPoints(
										Range.END_TO_END, range) < 0 ? NodeFilter.FILTER_ACCEPT
										: NodeFilter.FILTER_SKIP;
							} else {
								return NodeFilter.FILTER_SKIP;
							}
						}, false);

		var charCount = 0;
		while (treeWalker.nextNode()) {
			if (treeWalker.currentNode.nodeType == 3) {
				charCount += treeWalker.currentNode.length;
			} else {
				charCount += 1;
			}
		}

		if (range.startContainer.nodeType == 3) {
			charCount += range.startOffset;
		}
		//$wnd.alert(charCount);
		return charCount;
	}-*/;

	public native void setCursor(int pos, int length) /*-{
		var elem = $doc.getElementById("scriptarea");
		var node = elem.contentWindow.document.body;
		var range = elem.contentWindow.getSelection().getRangeAt(0);

		var treeWalker = $doc.createTreeWalker(node, NodeFilter.SHOW_ALL,
				function(node) {
					var nodeRange = $doc.createRange();
					nodeRange.selectNodeContents(node);
					if (node.tagName == 'BR' || node.nodeType == 3) {
						return NodeFilter.FILTER_ACCEPT;
					}

				});
		var charCount = 0;
		while (treeWalker.nextNode()) {
			if (treeWalker.currentNode.nodeType == 3) {
				if (charCount + treeWalker.currentNode.length > pos)
					break;

				charCount += treeWalker.currentNode.length;
			} else {
				if (charCount + 1 > pos)
					break;

				charCount += 1;
			}

		}
		var offset = pos - charCount;
		var newRange = elem.contentWindow.document.createRange();
		newRange.setStart(treeWalker.currentNode, offset);
		newRange.setEnd(treeWalker.currentNode, offset + length);
		//$wnd.alert(charCount);
		var selection = elem.contentWindow.getSelection();

		if (selection.removeRange) { // Firefox, Opera, IE after version 9
			selection.removeRange(range);
		} else if (selection.removeAllRanges) { // Safari, Google Chrome
			selection.removeAllRanges();
		}

		selection.addRange(newRange);

	}-*/;

	public native String getSelectedText()/*-{
		var e = $doc.getElementById("scriptarea");
		if (e.contentWindow.document.selection) {
			return e.contentWindow.document.selection.createRange().text;
		} else {
			return e.contentWindow.document.getSelection().toString();
		}
	}-*/;

	public native String getTextJS()/*-{
		var e = $doc.getElementById("scriptarea");
		console.log(e);
		var select = e.contentWindow.document.getSelection();
		var range = e.contentWindow.document.createRange();
		range.setStart(e.contentWindow.document.body.firstChild, 0);
		range.setEnd(e.contentWindow.document.body.lastChild, 0);
		select.addRange(range);
		return select.toString();
	}-*/;

	public native void changeStyleArea(String style) /*-{
		$doc.getElementById("scriptarea").contentDocument.head.firstChild
				.setAttribute("href", "highlights/" + style + ".css");
	}-*/;

	public native void setTextAreaEnabled(String value) /*-{
		//		$wnd.console.log($doc.getElementById("scriptarea").contentDocument.designMode);
		if (value == "on") {
			$wnd
					.setTimeout(
							'document.getElementById("scriptarea").contentDocument.designMode = "on";',
							1000);
		} else {
			$wnd
					.setTimeout(
							'document.getElementById("scriptarea").contentDocument.designMode = "off";',
							1000);
		}

		//		$doc.getElementById("scriptarea").contentDocument.designMode = value;
		//		$wnd.console.log($doc.getElementById("scriptarea").contentDocument.designMode);
	}-*/;

	public native String getScriptHTML() /*-{
		return $doc.getElementById("scriptarea").contentDocument.head.innerHTML
				+ $doc.getElementById("scriptarea").contentDocument.body.innerHTML;

	}-*/;

	public native String getSuggestHTML() /*-{
		if ($doc.getElementsByClassName("gwt-SuggestBoxPopup").item(0) == null) {
			return "";
		} else {
			return $doc.getElementsByClassName("gwt-SuggestBoxPopup").item(0).innerHTML;
		}
	}-*/;

	public native void showCompletion(String preword) /*-{
		function getPos(el) {
			for (var lx = 0, ly = 0; el != null; lx += el.offsetLeft, ly += el.offsetTop, el = el.offsetParent)
				;
			return {
				x : lx,
				y : ly
			};
		}
		var x = getPos($doc.getElementById("scriptarea").contentWindow.document
				.getElementById("tempPointer")).x
				+ getPos($doc.getElementById("scriptarea")).x;
		var y = getPos($doc.getElementById("scriptarea").contentWindow.document
				.getElementById("tempPointer")).y
				+ getPos($doc.getElementById("scriptarea")).y;

		$doc.getElementById("scriptarea").contentWindow.document
				.getElementById("tempPointer").remove();

		$doc.getElementsByClassName("gwt-SuggestBoxPopup").item(0).style.position = 'absolute';
		$doc.getElementsByClassName("gwt-SuggestBoxPopup").item(0).style.top = y
				+ 'px';
		$doc.getElementsByClassName("gwt-SuggestBoxPopup").item(0).style.left = x
				+ 'px';
		$doc.getElementsByClassName("gwt-SuggestBoxPopup").item(0).style.zIndex = 10;

		$doc.getElementsByClassName("gwt-SuggestBoxPopup").item(0).onclick = function() {
			$wnd.clickCompletion(preword)
		};
	}-*/;

	public native void focusCompletion(String preword) /*-{
		$doc.getElementsByClassName("gwt-SuggestBox").item(0).focus();

		//$doc.getElementsByClassName("gwt-SuggestBoxPopup").item(0).onclick = function(){ $wnd.clickCompletion(preword)};
	}-*/;

	private final native void execCopyOnScriptArea(String text) /*-{
		//a faire ?
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

	private final native void execPasteOnScriptArea(String text) /*-{
		//a faire ?
		try {

			var keys = "CTRL + V";
			var successful = $doc.execCommand("paste");
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

	public native void onResize() /*-{
		$wnd.setTimeout('replaceEditPanel();', 50);
	}-*/;

	////////////////////////////////////////////////////// XXX///////////////////////////////////////////////////////////
	//// TOOLBAR HORIZONTALE ////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@UiHandler("btnEdit")
	public void onEditClick(ClickEvent event) {
		scriptsetEnabled(true);
		btnCheckIn.setVisible(false);
	}

	@UiHandler("btnCheckIn")
	public void onCheckInClick(ClickEvent event) {
		showWaitPart(true);
		if (currentProject.equals(null)) {
			return;
		}
		// if (txtName.getText().equals("") || scriptArea.getText().equals(""))
		// {
		// MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(),
		// LabelsConstants.lblCnst.MissingInformation());
		// return;
		// }
		if (currentScript.getName() == null || currentScript.getName().equals("")) {
			final ValideNameDialog dial = new ValideNameDialog();
			dial.center();

			if (dial.isConfirm()) {
				currentScript.setName(dial.getName());
			}
			else {
				return;
			}
		}

		RScript box = currentScript;
		// box.setName(txtName.getText());
		box.setFree(true);
		box.setHoldingUsername("");
		SmartAirService.Connect.getInstance().checkInScript(box, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				showWaitPart(false);
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.CheckInScriptSuccessfull());
				scriptsetEnabled(false);
				onNewVersionClick(null);
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToCheckInScript());
			}
		});
	}

	@UiHandler("btnCheckOut")
	public void onCheckOutClick(ClickEvent event) {
		showWaitPart(true);
		currentScript.setHoldingUsername(user.getLogin());
		SmartAirService.Connect.getInstance().checkOutScript(currentScript, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				showWaitPart(false);
				if (result.equals("")) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.CheckOutScriptSuccessfull());
					scriptsetEnabled(true);
					btnSave.setVisible(false);
				}
				else {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.ScriptAlreadyUsed() + result);
					scriptsetEnabled(false);
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToCheckOutScript());
			}
		});
	}

	@UiHandler("btnSaveAs")
	public void onNewSaveClick(ClickEvent event) {
		onNewSave(getText(), lstType.getItemText(lstType.getSelectedIndex()));
	}

	@UiHandler("btnSave")
	public void onSaveClick(ClickEvent event) {
		if (currentProject.equals(null)) {
			return;
		}
		// if (txtName.getText().equals("") || scriptArea.getText().equals(""))
		// {
		// MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(),
		// LabelsConstants.lblCnst.MissingInformation());
		// return;
		// }
		if (currentScript.getName() == null || currentScript.getName().equals("")) {
			final ValideNameDialog dial = new ValideNameDialog();
			dial.center();

			if (dial.isConfirm()) {
				currentScript.setName(dial.getName());
			}
			else {
				return;
			}
		}
		currentModel.setId(0);
		currentModel.setScript(getText());
		currentModel.setDateVersion(new Date());
		currentModel.setNumVersion(currentModel.getNumVersion() + 1);
		showWaitPart(true);

		SmartAirService.Connect.getInstance().addorEditScriptModel(currentModel, new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				currentModel.setId(result);
				SmartAirService.Connect.getInstance().addorEditScript(currentScript, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
						caught.printStackTrace();
						ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToSaveScript());
					}

					@Override
					public void onSuccess(Integer result) {
						showWaitPart(false);
						workspacePanel.getNavPanel().onRefreshClick(null);
						scriptsetEnabled(false);
						workspacePanel.getCurrentVignette().setScreenCapture(getScriptHTML());
						highlight();
						setSavedString(getText());
						refreshVersions();
					}
				});

			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToSaveScript());
			}
		});

	}

	@UiHandler("btnRunAll")
	public void onRunAllClick(ClickEvent event) {
		String script = getTextJS();
		String scriptAsText = getText();

		if (!getScriptParameters(script).isEmpty()) {
			dialparameters = new CustomDialog(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Parameters(), new ParametersPanel(getScriptParameters(scriptAsText), workspacePanel.getAirPanel().getResourceManager().getListOfValues()), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), new ClickHandler() { // okHandler
				@Override
				public void onClick(ClickEvent event) {
					dialparameters.hide();
					try {
						if (currentScript.getScriptType().equals(ScriptType.MARKDOWN.name())) {
							prepareMarkdown(changeTextParameters(script, ((ParametersPanel) dialparameters.getWidget()).getParameters()), ((ParametersPanel) dialparameters.getWidget()).getLOVParameters());
						}
						else {
							runScript(changeTextParameters(script, ((ParametersPanel) dialparameters.getWidget()).getParameters()), ((ParametersPanel) dialparameters.getWidget()).getLOVParameters());
						}

					} catch (Exception e) {
						MessageHelper.openMessageError(LabelsConstants.lblCnst.Error(), e);
						e.printStackTrace();
					}
				}
			}, new ClickHandler() { // cancelHandler
				@Override
				public void onClick(ClickEvent event) {
					dialparameters.hide();
				}
			});
			dialparameters.center();
		}
		else {
			if (currentScript.getScriptType().equals(ScriptType.MARKDOWN.name())) {
				prepareMarkdown(script, null);
			}
			else {
				runScript(script, null);
			}

		}

	}

	@UiHandler("btnRunSelection")
	public void onRunSelectionClick(ClickEvent event) {

		if (!getScriptParameters(getSelectedText()).isEmpty()) {
			dialparameters = new CustomDialog(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Parameters(), new ParametersPanel(getScriptParameters(getSelectedText()), workspacePanel.getAirPanel().getResourceManager().getListOfValues()), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), new ClickHandler() { // okHandler
				@Override
				public void onClick(ClickEvent event) {
					dialparameters.hide();
					try {
						if (currentScript.getScriptType().equals(ScriptType.MARKDOWN)) {
							prepareMarkdown(changeTextParameters(getSelectedText(), ((ParametersPanel) dialparameters.getWidget()).getParameters()), ((ParametersPanel) dialparameters.getWidget()).getLOVParameters());
						}
						else {
							runScript(changeTextParameters(getSelectedText(), ((ParametersPanel) dialparameters.getWidget()).getParameters()), ((ParametersPanel) dialparameters.getWidget()).getLOVParameters());
						}

					} catch (Exception e) {
						MessageHelper.openMessageError(LabelsConstants.lblCnst.Error(), e);
						e.printStackTrace();
					}
				}
			}, new ClickHandler() { // cancelHandler
				@Override
				public void onClick(ClickEvent event) {
					dialparameters.hide();
				}
			});
			dialparameters.center();
		}
		else {
			if (currentScript.getScriptType().equals(ScriptType.MARKDOWN)) {
				prepareMarkdown(getSelectedText(), null);
			}
			else {
				runScript(getSelectedText(), null);
			}

		}

	}

	@UiHandler("btnImportCode")
	public void onImportCodeClick(ClickEvent event) {
		ImportCodeDialog dial = new ImportCodeDialog(this);
		dial.center();
	}

	@UiHandler("btnExportCode")
	public void onExportCodeClick(ClickEvent event) {
		SmartAirService.Connect.getInstance().exportToFile(currentScript.getName(), getText(), currentScript.getScriptType(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToExecuteScript());

			}

			@Override
			public void onSuccess(String result) {
				String fullUrl = "";
				if (currentScript.getScriptType().equals(ScriptType.R.name())) {
					fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_R;
				}
				else if (currentScript.getScriptType().equals(ScriptType.MARKDOWN.name())) {
					fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_RMD;
				}
				ToolsGWT.doRedirect(fullUrl);

			}
		});
	}

	@UiHandler("btnRefreshDatasets")
	public void onReloadDatasetsClick(ClickEvent event) {
		showWaitPart(true);
		SmartAirService.Connect.getInstance().getDatasetsbyProject(currentProject, new AsyncCallback<List<Dataset>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
				showWaitPart(false);
			}

			@Override
			public void onSuccess(List<Dataset> result) {
				SmartAirService.Connect.getInstance().addDatasetsToR(result, new AsyncCallback<List<RScriptModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						ExceptionManager.getInstance().handleException(caught, caught.getMessage());
						showWaitPart(false);
					}

					@Override
					public void onSuccess(List<RScriptModel> result) {
						showWaitPart(false);
						for (RScriptModel mod : result) {
							console.add(new HTML(mod.getOutputLog()));
							// scrollBottom();
							workspacePanel.updateVignetteLog(new HTML(mod.getOutputLog()));
							workspacePanel.getAirPanel().getLogPanel().addLog(mod.getOutputLog());
						}
					}
				});
			}
		});
	}

	@UiHandler("lstVersions")
	public void onVersionChange(ChangeEvent event) {
		int id = Integer.parseInt(lstVersions.getValue(lstVersions.getSelectedIndex()));
		RScriptModel selected = null;
		for (RScriptModel mod : currentVersions) {
			if (mod.getId() == id) {
				selected = mod;
				break;
			}

		}
		VersionDialog dial = new VersionDialog(this, currentScript, selected);
		dial.center();
	}

	@UiHandler("lstType")
	public void onTypeChange(ChangeEvent event) {
		currentScript.setScriptType(lstType.getItemText(lstType.getSelectedIndex()));
		if (lstType.getItemText(lstType.getSelectedIndex()).equals(ScriptType.R.name())) {
			btnAddManager.setVisible(true);
			btnImportCode.setTitle(LabelsConstants.lblCnst.ImportRCode());
			btnExportCode.setTitle(LabelsConstants.lblCnst.ExportRCode());
			// panelVariables.setVisible(true);
			panelMarkdownOutputs.setVisible(false);
			btnMarkdownRepository.setVisible(false);
			btnRCodeTools.setVisible(true);
			// btnMarkdownTemplate.setVisible(false);
		}
		else {
			btnAddManager.setVisible(false);
			btnImportCode.setTitle(LabelsConstants.lblCnst.ImportRMDCode());
			btnExportCode.setTitle(LabelsConstants.lblCnst.ExportRMDCode());
			// panelVariables.setVisible(false);
			panelMarkdownOutputs.setVisible(true);
			if (currentModel.getId() != 0) {
				btnMarkdownRepository.setVisible(true);
			}
			// if(isInEdition){
			// btnMarkdownTemplate.setVisible(true);
			// }
			btnRCodeTools.setVisible(false);
		}
	}

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

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				// showWaitPart(false);
				// console.clear();
				console.add(new HTML(result.getOutputLog()));
				// scrollBottom();
				workspacePanel.updateVignetteLog(new HTML(result.getOutputLog()));
				workspacePanel.getAirPanel().getLogPanel().addLog(result.getOutputLog());

				if (result.getOutputFiles() != null) {
					int nbfiles = result.getOutputFiles().length;
					if (nbfiles > 0) {
						// String[] tempUrls = new String[nbfiles];
						for (int i = 0; i < nbfiles; i++) {
							// String url = result.getOutputFiles()[i];

							// panelVariables.clear();
							String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + "Help" + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_HTML;
							Frame frame = new Frame(fullUrl);
							frame.setStyleName(style.frame());
							// panelVariables.add(frame);
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

	@UiHandler("btnHTML")
	public void onOutputHTMLClick(ClickEvent e) {
		if (btnHTML.getElement().getStyle().getOpacity().equals("1")) {
			btnHTML.getElement().getStyle().setOpacity(0.2);
		}
		else {
			btnHTML.getElement().getStyle().setOpacity(1);
		}

	}

	@UiHandler("btnPDF")
	public void onOutputPDFClick(ClickEvent e) {
		if (btnPDF.getElement().getStyle().getOpacity().equals("1")) {
			btnPDF.getElement().getStyle().setOpacity(0.2);
		}
		else {
			btnPDF.getElement().getStyle().setOpacity(1);
		}

	}

	@UiHandler("btnWORD")
	public void onOutputWORDClick(ClickEvent e) {
		if (btnWORD.getElement().getStyle().getOpacity().equals("1")) {
			btnWORD.getElement().getStyle().setOpacity(0.2);
		}
		else {
			btnWORD.getElement().getStyle().setOpacity(1);
		}

	}

	////////////////////////////////////////////////////// XXX///////////////////////////////////////////////////////////
	//// TOOLBAR LATERALE ////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@UiHandler("btnUndo")
	public void onUndoClick(ClickEvent event) {
		scriptArea.getFormatter().undo();
		// if (!beforeStack.isEmpty()) {
		// String val = beforeStack.pop();
		// afterStack.push(val);
		// if (!beforeStack.isEmpty()) {
		// String prev = beforeStack.peek();
		// scriptArea.setText(prev);
		// }
		// else {
		// // scriptArea.setText("");
		// }
		// }
		// else {
		// // scriptArea.setText("");
		// }

	}

	@UiHandler("btnRedo")
	public void onRedoClick(ClickEvent event) {
		scriptArea.getFormatter().redo();

		// if (!afterStack.isEmpty()) {
		// String val = afterStack.pop();
		// beforeStack.push(val);
		// scriptArea.setText(val);
		// }
		// exec("redo");
	}

	// @UiHandler("btnCopy")
	// public void onCopyClick(ClickEvent event) {
	// if (scriptArea.getSelectedText().equals("")) {
	// scriptArea.setFocus(true);
	// scriptArea.selectAll();
	// }
	// execCopyOnScriptArea("copy");
	// }
	//
	// @UiHandler("btnPaste")
	// public void onPasteClick(ClickEvent event) {
	// scriptArea.setFocus(true);
	// scriptArea.setSelectionRange(0, 0);
	// scriptArea.setCursorPos(scriptArea.getText().length());
	// execPasteOnScriptArea("paste");
	//
	// }

	@UiHandler("btnClearText")
	public void onTextClearClick(ClickEvent event) {
		final InformationsDialog dialConfirm = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.ConfirmClearText(), true);
		dialConfirm.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dialConfirm.isConfirm()) {
					scriptArea.setText("");
					if (workspacePanel.getCurrentVignette() != null && workspacePanel.getCurrentVignette().getType().equals(Vignette.TypeVignette.GENCODE)) {
						workspacePanel.writeGeneratedCode("");
					}
				}
			}
		});
		dialConfirm.center();

	}

	@UiHandler("btnAddManager")
	public void onAddManagerClick(ClickEvent event) {
		commandMenu.getElement().getStyle().setTop(btnAddManager.getElement().getOffsetTop(), Unit.PX);
		commandMenu.setVisible(!commandMenu.isVisible());
	}

	@UiHandler("btnChangeStyle")
	public void onChangeStyleClick(ClickEvent event) {
		styleMenu.getElement().getStyle().setTop(btnChangeStyle.getElement().getOffsetTop(), Unit.PX);
		styleMenu.setVisible(!styleMenu.isVisible());
	}

	@UiHandler("btnRCodeTools")
	public void onRCodeToolsClick(ClickEvent event) {
		rToolsMenu.getElement().getStyle().setTop(btnRCodeTools.getElement().getOffsetTop(), Unit.PX);
		rToolsMenu.setVisible(!rToolsMenu.isVisible());
	}

	@UiHandler("btnMarkdownTemplate")
	public void onMarkdownTemplateClick(ClickEvent event) {
		MarkdownTemplateDialog dial = new MarkdownTemplateDialog(this, getText());
		dial.center();
	}
	/* Menus */

	private void createCommandMenu() {

		MenuItem ls = new MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.ToListVariables()), new Command() {

			@Override
			public void execute() {
				listRVariables();
			}
		});

		MenuItem installedPackages = new MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.ToListInstalledPackages()), new Command() {

			@Override
			public void execute() {
				listInstalledPackages();
			}
		});

		MenuItem executedCmd = new MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.ToListExecutedLines()), new Command() {

			@Override
			public void execute() {
				listExecutedCmd();
			}
		});

		// Make a new menu bar, adding a few cascading menus to it.
		commandMenu = new MenuBar(true);
		// commandMenu.addItem(clear);
		commandMenu.addItem(ls);
		commandMenu.addItem(installedPackages);
		commandMenu.addItem(executedCmd);

		commandMenu.addStyleName(style.commandMenu());

		// Add it to the root panel.
		textToolbar.add(commandMenu);
		commandMenu.setVisible(false);
		commandMenu.addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				commandMenu.setVisible(false);

			}
		}, MouseOutEvent.getType());
	}

	private void createStyleMenu() {

		MenuItem defaut = new MenuItem(SafeHtmlUtils.fromString("<default>"), new Command() {

			@Override
			public void execute() {
				changeStyleArea("default");
			}
		});

		MenuItem colorBrewer = new MenuItem(SafeHtmlUtils.fromString("color-brewer"), new Command() {

			@Override
			public void execute() {
				changeStyleArea("color-brewer");
			}
		});

		MenuItem railscasts = new MenuItem(SafeHtmlUtils.fromString("railscasts"), new Command() {

			@Override
			public void execute() {
				changeStyleArea("railscasts");
			}
		});

		MenuItem tomorrow = new MenuItem(SafeHtmlUtils.fromString("tomorrow"), new Command() {

			@Override
			public void execute() {
				changeStyleArea("tomorrow");
			}
		});

		MenuItem zenburn = new MenuItem(SafeHtmlUtils.fromString("zenburn"), new Command() {

			@Override
			public void execute() {
				changeStyleArea("zenburn");
			}
		});

		styleMenu = new MenuBar(true);
		styleMenu.addItem(defaut);
		styleMenu.addItem(colorBrewer);
		styleMenu.addItem(railscasts);
		styleMenu.addItem(tomorrow);
		styleMenu.addItem(zenburn);

		styleMenu.addStyleName(style.commandMenu());
		// Add it to the root panel.
		textToolbar.add(styleMenu);
		styleMenu.setVisible(false);
		styleMenu.addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				styleMenu.setVisible(false);

			}
		}, MouseOutEvent.getType());
	}

	private void createRToolsMenu() {

		MenuItem dtsDateToAge = new MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.ConvertDateToAge()), new Command() {

			@Override
			public void execute() {
				recode(Recode.DATETOAGE);
			}
		});

		MenuItem dtsAgeToRange = new MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.ConvertAgeToRange()), new Command() {

			@Override
			public void execute() {
				recode(Recode.AGETORANGE);
			}
		});

		MenuItem dtsManualClassification = new MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.CreateClassificationGroup()), new Command() {

			@Override
			public void execute() {
				recode(Recode.CLASS);
			}
		});

		MenuItem dtsCalculatedColumn = new MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.CreateCalculatedColumn()), new Command() {

			@Override
			public void execute() {
				recode(Recode.CALCULATED);
			}
		});

		MenuItem dtsFilterColumn = new MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.FilterColumn()), new Command() {

			@Override
			public void execute() {
				recode(Recode.FILTER);
			}
		});

		// MenuItem dtsMappingColumn = new
		// MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.GeoMappingColumn()),
		// new Command() {
		//
		// @Override
		// public void execute() {
		// recode(Recode.MAP);
		// }
		// });

		MenuItem dtsRecodeColumn = new MenuItem(SafeHtmlUtils.fromString(LabelsConstants.lblCnst.RecodeColumn()), new Command() {

			@Override
			public void execute() {
				recode(Recode.RECODE);
			}
		});

		rToolsMenu = new MenuBar(true);
		// final MenuBar datasetsMenu = new MenuBar(true);
		rToolsMenu.addItem(dtsDateToAge);
		rToolsMenu.addItem(dtsAgeToRange);
		rToolsMenu.addItem(dtsManualClassification);
		rToolsMenu.addItem(dtsCalculatedColumn);
		rToolsMenu.addItem(dtsFilterColumn);
		// rToolsMenu.addItem(dtsMappingColumn);
		rToolsMenu.addItem(dtsRecodeColumn);

		// MenuItem datasetsItem = new
		// MenuItem(LabelsConstants.lblCnst.Dataset(), new Command() {
		//
		// @Override
		// public void execute() {
		// datasetsMenu.setVisible(true);
		// }
		// });

		// rToolsMenu.addItem(datasetsItem);

		rToolsMenu.addStyleName(style.commandMenu());
		// datasetsMenu.addStyleName(style.subMenu());
		// Add it to the root panel.
		textToolbar.add(rToolsMenu);
		rToolsMenu.setVisible(false);
		rToolsMenu.addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				rToolsMenu.setVisible(false);

			}
		}, MouseOutEvent.getType());
		// datasetsMenu.addDomHandler(new MouseOutHandler() {
		//
		// @Override
		// public void onMouseOut(MouseOutEvent event) {
		// datasetsMenu.setVisible(false);
		//
		// }
		// }, MouseOutEvent.getType());
	}

	////////////////////////////////////////////////////// XXX///////////////////////////////////////////////////////////
	//// FUNCTIONS ////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void refresh(AirProject project, RScript script, RScriptModel model, List<RScriptModel> versions) {

		// beforeStack.clear();
		// afterStack.clear();

		DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
		if (project != null) {
			this.currentProject = project;
		}
		if (script != null) {
			this.currentScript = script;
			// txtName.setText(script.getName());
			if (script.getScriptType().equals(ScriptType.R.name())) {
				lstType.setSelectedIndex(0);
			}
			else {
				lstType.setSelectedIndex(1);
			}
			onTypeChange(null);
			lblName.setText(script.getName());
			lblComment.setText((script.getComment() != null) ? script.getComment() : LabelsConstants.lblCnst.NoComment());
			if (script.getComment() == null)
				lblComment.addStyleName(style.lblInfoEmpty());

		}
		if (model != null) {
			this.currentModel = model;

			if (model.getId() != 0 || (workspacePanel.getCurrentVignette() != null && workspacePanel.getCurrentVignette().getType().equals(Vignette.TypeVignette.GENCODE) && model.getScript() != null)) {
				scriptArea.setHTML(model.getScript().replace("\n", "<br>"));
				highlight();
			}
			else {
				scriptArea.setHTML("");
				highlight();
			}

			if (script.getScriptType().equals(ScriptType.MARKDOWN.name())) {
				// if(model.getOutputs() == null ||
				// !model.getOutputs().equals("")){
				btnHTML.getElement().getStyle().setOpacity(1);
				btnPDF.getElement().getStyle().setOpacity(0.2);
				btnWORD.getElement().getStyle().setOpacity(0.2);
				// } else {
				// btnHTML.getElement().getStyle().setOpacity(Arrays.asList(model.getOutputs()).contains(CommonConstants.FORMAT_HTML)?
				// 0.4 : 1);
				// btnPDF.getElement().getStyle().setOpacity(Arrays.asList(model.getOutputs()).contains(CommonConstants.FORMAT_PDF)?
				// 0.4 : 1);
				// btnWORD.getElement().getStyle().setOpacity(Arrays.asList(model.getOutputs()).contains(CommonConstants.FORMAT_DOCX)?
				// 0.4 : 1);
				// }
			}
		}
		if (versions != null) {
			this.currentVersions = versions;
			lstVersions.clear();
			lstVersions.addItem(LabelsConstants.lblCnst.SelectAVersion(), "0");
			for (RScriptModel mod : versions) {
				if (mod.getId() == model.getId()) {
					continue;
				}
				else {
					lstVersions.addItem(mod.getNumVersion() + "  -  " + format.format(mod.getDateVersion()), String.valueOf(mod.getId()));
				}
			}
		}

		if (script != null) {
			if (script.getHoldingUsername().equals(user.getLogin()) || script.getId() == 0) {
				scriptsetEnabled(true);
				// lstType.setEnabled(false);
			}
			else {
				scriptsetEnabled(false);
				// lstType.setEnabled(false);
			}
			if (script.getId() == 0) {
				btnSave.setVisible(false);
				btnCheckIn.setVisible(false);
				lstType.setEnabled(true);
			}
			else {
				// btnSave.setVisible(true);
				// btnCheckIn.setVisible(true);
			}
		}
		else {
			scriptsetEnabled(false);
			btnSave.setVisible(false);
			lstType.setEnabled(false);
		}
		if (project == null) {
			btnCheckOut.setVisible(false);
			btnEdit.setVisible(false);
			btnRunAll.setVisible(false);
			btnRunSelection.setVisible(false);
			btnExportCode.setVisible(false);
			btnMarkdownRepository.setVisible(false);
			panelMarkdownOutputs.setVisible(false);
			btnRefreshDatasets.setVisible(false);
			btnMarkdownTemplate.setVisible(false);
			btnRCodeTools.setVisible(false);
			// lstType.setVisible(false);
			scriptArea.setEnabled(false);
		}

		if (workspacePanel.getCurrentVignette() != null && workspacePanel.getCurrentVignette().getType().equals(Vignette.TypeVignette.GENCODE)) {
			scriptToolbar.clear();
			btnMarkdownTemplate.setVisible(false);
		}

		highlight();

	}

	public void scriptsetEnabled(boolean isenabled) {
		isInEdition = isenabled;
		scriptArea.setEnabled(isenabled);
		setTextAreaEnabled((isenabled) ? "on" : "off");
		panelTextArea.setStyleName((isenabled) ? style.panelTextAreaEdit() : style.panelTextArea());
		// txtName.setEnabled(isenabled);

		btnSave.setVisible(isenabled);
		btnSaveAs.setVisible(isenabled);

		btnEdit.setVisible(!isenabled);
		btnCheckIn.setVisible(isenabled);
		btnCheckOut.setVisible(!isenabled);

		btnUndo.setVisible(isenabled);
		btnRedo.setVisible(isenabled);
		btnImportCode.setVisible(isenabled);

		btnRunAll.setVisible(true);
		btnRunSelection.setVisible(true);
		btnExportCode.setVisible(true);
		// btnMarkdownRepository.setVisible(true);
		btnRefreshDatasets.setVisible(true);

		if (isCopyPasteEnabled) {
			btnCopy.setVisible(true);
			btnPaste.setVisible(true);
		}
		else {
			btnCopy.setVisible(false);
			btnPaste.setVisible(false);
		}

		lstType.setVisible(true);

		// if
		// (lstType.getItemText(lstType.getSelectedIndex()).equals(ScriptType.MARKDOWN.name()))
		// {
		// btnMarkdownTemplate.setVisible(isenabled);
		// } else {
		// btnMarkdownTemplate.setVisible(false);
		// }
		btnMarkdownTemplate.setVisible(true);
		btnClearText.setVisible(isenabled);
		btnRCodeTools.setVisible(isenabled);
	}

	public void onNewSave(final String scriptText, final String scriptType) {
		if (currentProject.equals(null)) {
			return;
		}

		// if (txtName.getText().equals("") || scriptArea.getText().equals(""))
		// {
		// MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(),
		// LabelsConstants.lblCnst.MissingInformation());
		// return;
		// }

		// if(currentScript.getName() == null ||
		// currentScript.getName().equals("")){
		final ValideNameDialog dial = new ValideNameDialog();
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					RScript newscript = new RScript();
					newscript.setName(dial.getName());
					newscript.setScriptType(scriptType);
					onNewSaveSuite(scriptText, newscript);
				}
				else {
					return;
				}
			}
		});

		// } else {
		// onNewSaveSuite();
		// }
	}

	public void onNewSaveSuite(final String scriptText, final RScript newscript) {

		for (RScript script : workspacePanel.getNavPanel().getScriptList()) {
			if (script.getIdProject() == currentProject.getId() && script.getName().equals(newscript.getName())) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.ExistantName());
				return;
			}
		}
		// onCheckInClick(null);

		// final RScript script = new RScript();
		newscript.setIdProject(currentProject.getId());
		showWaitPart(true);
		SmartAirService.Connect.getInstance().addorEditScript(newscript, new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				// showWaitPart(false);
				newscript.setId(result);
				final RScriptModel newmodel = new RScriptModel();
				newmodel.setScript(scriptText);
				newmodel.setIdScript(result);
				newmodel.setDateVersion(new Date());
				// box.setOutputs("no_return".split(" "));
				newmodel.setNumVersion(1);
				SmartAirService.Connect.getInstance().addorEditScriptModel(newmodel, new AsyncCallback<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						newmodel.setId(result);
						showWaitPart(false);
						// workspacePanel.getNavPanel().setCurrentModel(currentModel);
						// workspacePanel.getNavPanel().setCurrentScript(currentScript);
						// workspacePanel.getNavPanel().setCurrentProject(currentProject);

						ScriptPanel pan = new ScriptPanel(workspacePanel, rLibs, isCopyPasteEnabled, currentProject, newscript, newmodel, null);

						if (workspacePanel.getCurrentVignette().getTitle().equals("New")) { // on
																							// update
																							// la
																							// vignette
							workspacePanel.getCurrentVignette().setTitle(newscript.getName());
							workspacePanel.getCurrentVignette().setScreenCapture(scriptText);
							workspacePanel.getCurrentVignette().setWidget(pan);
							workspacePanel.showVignette(workspacePanel.getCurrentVignette());
						}
						else { // on cree une nouvelle vignette
							workspacePanel.createVignettewithHTML(scriptText, (newscript.getScriptType().equals("R")) ? Vignette.TypeVignette.R : Vignette.TypeVignette.MARKDOWN, newscript.getName(), pan, false, false);
							workspacePanel.showVignette(workspacePanel.getVignette(newscript.getName()));
						}
						// if(!currentScript.getScriptType().equals("R")){
						// //pour export markdown
						// btnMarkdownRepository.setVisible(true);
						// }
						workspacePanel.getNavPanel().onRefreshClick(null);
						scriptsetEnabled(false);
						// setSavedString(scriptText);
						// highlight();
					}

					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToSaveScript());
					}
				});

			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToSaveScript());
			}
		});
	}

	// @UiHandler("btnSaveAsNewVersion")
	public void onNewVersionClick(ClickEvent event) {
		if (currentProject.equals(null)) {
			return;
		}
		// if (txtName.getText().equals("") || scriptArea.getText().equals(""))
		// {
		// MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(),
		// LabelsConstants.lblCnst.MissingInformation());
		// return;
		// }
		if (currentScript.getName() == null || currentScript.getName().equals("")) {
			final ValideNameDialog dial = new ValideNameDialog();
			dial.center();

			if (dial.isConfirm()) {
				currentScript.setName(dial.getName());
			}
			else {
				return;
			}
		}

		RScriptModel box = new RScriptModel();
		box.setScript(getText());
		box.setIdScript(currentScript.getId());
		box.setDateVersion(new Date());
		box.setNumVersion(currentModel.getNumVersion() + 1);
		SmartAirService.Connect.getInstance().addorEditScriptModel(box, new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				showWaitPart(false);
				currentModel.setId(result);
				workspacePanel.getNavPanel().onRefreshClick(null);
				scriptsetEnabled(false);
				workspacePanel.getCurrentVignette().setScreenCapture(scriptArea.getHTML());
				highlight();
				setSavedString(getText());
				refreshVersions();
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToSaveScript());
			}
		});
	}

	private void runScript(String script, List<Parameter> lovParams) {
		RScriptModel box = new RScriptModel();
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());
		// box.setOutputs("no_return".split(" "));

		for (String str : box.getScript().split("\n")) {
			workspacePanel.consoleStack.push(str);
		}
		workspacePanel.consoleStackPointer = workspacePanel.consoleStack.size() + 1;
		// txtConsole.setText("");
		showWaitPart(true);
		SmartAirService.Connect.getInstance().executeScript(box, lovParams, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				showWaitPart(false);
				// console.clear();result = result;
				console.add(new HTML(result.getOutputLog()));
				// scrollBottom();

				workspacePanel.updateVignetteLog(new HTML(result.getOutputLog()));

				workspacePanel.getAirPanel().getLogPanel().addLog(result.getOutputLog());

				if (result.getOutputFiles() != null) {
					int nbfiles = result.getOutputFiles().length;
					if (nbfiles > 0) {
						// String[] tempUrls = new String[nbfiles];
						for (int i = 0; i < nbfiles; i++) {
							String url = result.getOutputFiles()[i];
							// if
							// (url.split(";")[0].equals("data:image/svg+xml"))
							// {
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
								// panelVariables.clear();
								String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + "Help" + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_PDF;
								Frame frame = new Frame(fullUrl);
								frame.setStyleName(style.frame());
								// panelVariables.add(frame);
								CustomDialog dial = new CustomDialog("Help", frame, "500px", "800px");
								dial.center();
							}
							else if (url.split(";")[0].equals("html")) {
								// GraphDialog dial = new
								// GraphDialog(result.getOutputFiles()[i],
								// LabelsConstants.lblCnst.Help(),
								// CommonConstants.FORMAT_TXT );
								// dial.center();
								// panelVariables.clear();
								String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + "Help" + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_HTML;
								Frame frame = new Frame(fullUrl);
								frame.setStyleName(style.frame());
								// panelVariables.add(frame);
								CustomDialog dial = new CustomDialog("Help", frame, "500px", "800px");
								dial.center();
							}
							else {
								// panelVariables.clear();
								String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + "Help" + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_TXT;
								Frame frame = new Frame(fullUrl);
								frame.setStyleName(style.frame());
								// panelVariables.add(frame);
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

	public void prepareMarkdown(final String script, final List<Parameter> lovParams) {
		if (currentScript.getName() == null || currentScript.getName().equals("")) {
			final ValideNameDialog dial = new ValideNameDialog();
			dial.center();
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {

				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if (dial.isConfirm()) {
						currentScript.setName(dial.getName());
						renderMarkdown(script, lovParams);
					}
					else {
						return;
					}
				}
			});
		}
		else {
			renderMarkdown(script, lovParams);
		}

	}

	public void renderMarkdown(String script, List<Parameter> lovParams) {

		final List<String> outputs = new ArrayList<String>();
		if (btnHTML.getElement().getStyle().getOpacity().equals("1")) {
			outputs.add(CommonConstants.FORMAT_HTML);
		}
		if (btnPDF.getElement().getStyle().getOpacity().equals("1")) {
			outputs.add(CommonConstants.FORMAT_PDF);
		}
		if (btnWORD.getElement().getStyle().getOpacity().equals("1")) {
			outputs.add(CommonConstants.FORMAT_DOCX);
		}
		showWaitPart(true);
		SmartAirService.Connect.getInstance().renderMarkdown(script, currentScript.getName(), outputs, lovParams, new AsyncCallback<RScriptModel>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToRenderMarkdown());
			}

			@Override
			public void onSuccess(RScriptModel result) {
				showWaitPart(false);
				console.add(new HTML(result.getOutputLog()));
				// scrollBottom();
				workspacePanel.updateVignetteLog(new HTML(result.getOutputLog()));

				workspacePanel.getAirPanel().getLogPanel().addLog(result.getOutputLog());

				// panelHTML.clear();
				if (result.getOutputVarstoString().size() > 0) {
					int i = 0;
					for (String output : result.getOutputVarstoString()) {

						if (result.getOutputs()[i].equals("temphtml")) {// if(outputs.contains(CommonConstants.FORMAT_HTML)){
							String htmlUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + currentScript.getName() + i + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_HTML;
							final Frame htmlframe = new Frame(htmlUrl);
							htmlframe.setStyleName(style.frameMarkdown());

							workspacePanel.createVignettewithUrl(htmlUrl, Vignette.TypeVignette.HTML, currentScript.getName(), htmlframe, true, false);
						}

						if (result.getOutputs()[i].equals("temppdf")) {// if(outputs.contains(CommonConstants.FORMAT_PDF)){
							String pdfUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + currentScript.getName() + i + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_PDF;
							final Frame pdfframe = new Frame(pdfUrl);
							pdfframe.setStyleName(style.frameMarkdown());

							workspacePanel.createVignettewithUrl(pdfUrl, Vignette.TypeVignette.PDF, currentScript.getName(), pdfframe, true, false);
						}

						if (result.getOutputs()[i].equals("tempdoc")) {// if(outputs.contains(CommonConstants.FORMAT_DOCX)){
							String wordUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + currentScript.getName() + i + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_DOCX;
							final Frame wordframe = new Frame(wordUrl);
							wordframe.setStyleName(style.frameMarkdown());

							workspacePanel.createVignettewithUrl(wordUrl, Vignette.TypeVignette.WORD, currentScript.getName(), wordframe, true, false);
						}

						i++;
					}

				}

			}
		});
	}

	public void addCode(String selectedCode) {
		scriptArea.setHTML(scriptArea.getHTML() + "<br>" + selectedCode.replace("\\n", "").replace("\n", "<br>"));
	}

	private void listRVariables() {
		RScriptModel box = new RScriptModel();
		String script = "f<-function(x){try(paste(x,class(get(x)), sep=';'))}\n";
		script += "environment(f) <- " + user.getLogin() + user.getId() + "\n";
		script += "manual_result <- sapply(ls(),f)\n";
		script += "rm(f)";
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());
		box.setOutputs("manual_result".split(" "));

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {

				List<String> list = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t")));

				workspacePanel.getAirPanel().getLogPanel().addLog(result.getOutputLog());
				console.add(new HTML(result.getOutputLog()));
				// scrollBottom();
				workspacePanel.updateVignetteLog(new HTML(result.getOutputLog()));
				TextCell cell = new TextCell();
				Column<String, String> nameColumn = new Column<String, String>(cell) {

					@Override
					public String getValue(String object) {

						if (object.contains(";")) {
							return object.split(";")[0].trim();
						}
						else {
							return object;
						}
					}
				};
				nameColumn.setSortable(true);

				Column<String, String> typeColumn = new Column<String, String>(cell) {

					@Override
					public String getValue(String object) {
						if (object.contains(";")) {
							return object.split(";")[1].trim();
						}
						else {
							return "";
						}
					}
				};
				typeColumn.setSortable(true);

				ImageCell imageCell = new ImageCell() {
					@Override
					public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
						if (value != null && value.contains(";") && value.split(";")[1].trim().equals("data.frame")) {
							sb.appendHtmlConstant("<img src = '" + bpm.smart.web.client.images.Images.INSTANCE.chart_activity().getSafeUri().asString() + "' height = '18px' width = '18px' class='" + style.btnGrid() + "' onclick='showStatsPanel(\"" + value.split(";")[0].trim() + "\")'/>");
						}
					}
				};

				final Column<String, String> showStatsFrame = new Column<String, String>(imageCell) {

					@Override
					public String getValue(String object) {

						return object;
					}

				};

				ImageCell imageCell2 = new ImageCell() {
					@Override
					public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
						if (value != null) {
							sb.appendHtmlConstant("<img src = '" + bpm.smart.web.client.images.Images.INSTANCE.ic_clear_black_24dp().getSafeUri().asString() + "' height = '18px' width = '18px' class='" + style.btnGrid() + "' onclick='removeRVar(\"" + value + "\")'/>");
						}
					}
				};

				final Column<String, String> removeRVar = new Column<String, String>(imageCell2) {

					@Override
					public String getValue(String object) {

						if (object.contains(";")) {
							return object.split(";")[0].trim();
						}
						else {
							return null;
						}
					}

				};

				// DataGrid.Resources resources = new CustomResources();
				DataGrid<String> dataGrid = new DataGrid<String>(99999);
				dataGrid.setWidth("100%");
				dataGrid.setHeight("100%");
				dataGrid.getElement().getStyle().setProperty("maxWidth", "500px");

				dataGrid.addColumn(nameColumn, LabelsConstants.lblCnst.Name());
				dataGrid.addColumn(typeColumn, LabelsConstants.lblCnst.Type());
				dataGrid.addColumn(showStatsFrame);
				dataGrid.addColumn(removeRVar);

				ListDataProvider<String> dataProvider = new ListDataProvider<String>();
				dataProvider.addDataDisplay(dataGrid);
				dataProvider.setList(list);

				ListHandler<String> sortNameHandler = new ListHandler<String>(new ArrayList<String>());
				sortNameHandler.setComparator(nameColumn, new Comparator<String>() {

					@Override
					public int compare(String m1, String m2) {
						return m1.compareToIgnoreCase(m2);
					}
				});
				ListHandler<String> sortTypeHandler = new ListHandler<String>(new ArrayList<String>());
				sortTypeHandler.setComparator(typeColumn, new Comparator<String>() {

					@Override
					public int compare(String m1, String m2) {
						return m1.split(";")[1].trim().compareToIgnoreCase(m2.split(";")[1].trim());
					}
				});
				sortNameHandler.setList(dataProvider.getList());
				sortTypeHandler.setList(dataProvider.getList());

				dataGrid.addColumnSortHandler(sortNameHandler);
				dataGrid.addColumnSortHandler(sortTypeHandler);

				// panelVariables.clear();
				// panelVariables.add(dataGrid);
				CustomDialog dial = new CustomDialog(LabelsConstants.lblCnst.ToListVariables(), dataGrid);
				dial.center();
			}

			@Override
			public void onFailure(Throwable caught) {

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToExecuteScript());
			}
		});
	}

	private void listInstalledPackages() {
		RScriptModel box = new RScriptModel();
		String script = "manual_result <- installed.packages(lib=\"" + rLibs + "\")[,\"Package\"]";
		box.setScript(script);
		box.setUserREnv(user.getLogin() + user.getId());
		box.setOutputs("manual_result".split(" "));

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				showWaitPart(false);
				List<String> list = new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t")));

				workspacePanel.getAirPanel().getLogPanel().addLog(result.getOutputLog());
				console.add(new HTML(result.getOutputLog()));
				// scrollBottom();
				workspacePanel.updateVignetteLog(new HTML(result.getOutputLog()));
				TextCell cell = new TextCell() {
					@Override
					public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
						if (value != null) {
							sb.appendHtmlConstant("<a class='" + style.hyperlinkPackage() + "' onclick='helpLibrary(\"" + value + "\")'>" + value + "</a>");
						}
					}
				};
				Column<String, String> nameColumn = new Column<String, String>(cell) {

					@Override
					public String getValue(String object) {
						return object;
					}
				};
				nameColumn.setSortable(true);

				// DataGrid.Resources resources = new CustomResources();
				DataGrid<String> dataGrid = new DataGrid<String>(99999);
				dataGrid.setWidth("100%");
				dataGrid.setHeight("100%");
				dataGrid.getElement().getStyle().setProperty("maxWidth", "500px");

				dataGrid.addColumn(nameColumn, LabelsConstants.lblCnst.Name());
				// dataGrid.addColumn(helpColumn,
				// LabelsConstants.lblCnst.Help());

				ListDataProvider<String> dataProvider = new ListDataProvider<String>();
				dataProvider.addDataDisplay(dataGrid);
				dataProvider.setList(list);

				ListHandler<String> sortHandler = new ListHandler<String>(new ArrayList<String>());
				sortHandler.setComparator(nameColumn, new Comparator<String>() {

					@Override
					public int compare(String m1, String m2) {
						return m1.compareToIgnoreCase(m2);
					}
				});
				sortHandler.setList(dataProvider.getList());

				dataGrid.addColumnSortHandler(sortHandler);

				// panelVariables.clear();
				// panelVariables.add(dataGrid);
				CustomDialog dial = new CustomDialog(LabelsConstants.lblCnst.ToListInstalledPackages(), dataGrid);
				dial.center();
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToExecuteScript());
			}
		});
	}

	public void handleRemoveRVarClick(String name) {
		RScriptModel box = new RScriptModel();
		String script = "rm(" + name + ")";
		box.setUserREnv(user.getLogin() + user.getId());
		box.setScript(script);

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				workspacePanel.getAirPanel().getLogPanel().addLog(result.getOutputLog());
				console.add(new HTML(result.getOutputLog()));
				// scrollBottom();
				workspacePanel.updateVignetteLog(new HTML(result.getOutputLog()));
				listRVariables();
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToExecuteScript());
			}
		});
	}

	public void handleShowStatsPanelClick(String name) {
		final String datasetName = name;
		showWaitPart(true);
		SmartAirService.Connect.getInstance().getDatasetColumns(datasetName, new AsyncCallback<List<DataColumn>>() {

			@Override
			public void onSuccess(List<DataColumn> result) {
				final Dataset tempDataset = new Dataset();
				tempDataset.setName(datasetName);
				tempDataset.setId(datasetName.hashCode());
				tempDataset.setRequest("");
				for (DataColumn col : result) {
					col.setId(col.getColumnLabel().hashCode());
					col.setIdDataset(tempDataset.getId());
				}
				tempDataset.setMetacolumns(result);

				SmartAirService.Connect.getInstance().calculateRStats(result, tempDataset, new AsyncCallback<List<StatDataColumn>>() {

					@Override
					public void onSuccess(List<StatDataColumn> result) {
						showWaitPart(false);
						StatsPanel panel = new StatsPanel(tempDataset, result, user, true, true, workspacePanel.getMainPanel(), workspacePanel);
						workspacePanel.getWorkspaceContent().add(panel);
						panel.popupizer();
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						showWaitPart(false);
						ExceptionManager.getInstance().handleException(caught, caught.getMessage());
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});
	}

	private void listExecutedCmd() {
		TextCell cell = new TextCell();
		Column<String, String> lineColumn = new Column<String, String>(cell) {

			@Override
			public String getValue(String object) {
				return object;
			}
		};
		lineColumn.setSortable(true);

		ImageCell imageCell = new ImageCell() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				if (value != null) {
					sb.appendHtmlConstant("<img src = '" + bpm.smart.web.client.images.Images.INSTANCE.ic_enter_32().getSafeUri().asString() + "' height = '18px' width = '18px' class='" + style.btnGrid() + "' onclick=\"addToStack('" + value + "')\"/>");
				}
			}
		};

		final Column<String, String> addStackCmdColumn = new Column<String, String>(imageCell) {

			@Override
			public String getValue(String object) {

				return object;
			}

		};

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<String> dataGrid = new DataGrid<String>(99999);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.getElement().getStyle().setProperty("maxWidth", "500px");

		dataGrid.addColumn(lineColumn, LabelsConstants.lblCnst.CommandLine());
		dataGrid.addColumn(addStackCmdColumn, "");

		ListDataProvider<String> dataProvider = new ListDataProvider<String>();
		dataProvider.addDataDisplay(dataGrid);
		dataProvider.setList(new ArrayList<String>(workspacePanel.consoleStack.subList(0, workspacePanel.consoleStack.size())));

		ListHandler<String> sortHandler = new ListHandler<String>(new ArrayList<String>());
		sortHandler.setComparator(lineColumn, new Comparator<String>() {

			@Override
			public int compare(String m1, String m2) {
				return m1.compareToIgnoreCase(m2);
			}
		});
		sortHandler.setList(dataProvider.getList());

		dataGrid.addColumnSortHandler(sortHandler);

		// panelVariables.clear();
		// panelVariables.add(dataGrid);
		CustomDialog dial = new CustomDialog(LabelsConstants.lblCnst.ToListExecutedLines(), dataGrid);
		dial.center();
	}

	public String getText() {
		RichTextArea temp = new RichTextArea();
		String result = "";
		for (String part : scriptArea.getHTML().split("<br>|<br/>")) {
			temp.setHTML(part);
			result += temp.getText().replace("\n", "") + "\n";
		}
		return result;
	}

	public void setText(String html) {
		scriptArea.setHTML(html);
	}

	@UiHandler("btnMarkdownRepository")
	public void onMarkdownRepository(ClickEvent event) {
		MarkdownType mtype = new MarkdownType(currentProject.getId(), currentModel.getId(), ScriptType.MARKDOWN.toString());
		List<Parameter> params = getScriptParameters(getText());
		Collections.sort(params, new Comparator<Parameter>() {
			@Override
			public int compare(Parameter arg0, Parameter arg1) {
				if (arg0.getIdParentParam() != 0 && arg1.getIdParentParam() != 0) {
					return 0;
				}
				else if (arg0.getIdParentParam() != 0) {
					return 1;
				}
				else if (arg1.getIdParentParam() != 0) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		for (Parameter param : params) {
			String pName = param.getName();
			String defaultValue = param.getDefaultValue();
			bpm.vanilla.platform.core.repository.Parameter p = new bpm.vanilla.platform.core.repository.Parameter();
			p.setName(pName);
			p.setInstanceName(pName);
			p.setDefaultValue(defaultValue);
			if (param.getIdParentParam() != 0) {
				for (Parameter param2 : params) {
					if (param2.getId() == param.getIdParentParam()) {
						for (bpm.vanilla.platform.core.repository.Parameter ppp : mtype.getParameters()) {
							if (ppp.getName().equals(param2.getName())) {
								p.setDataProviderName(ppp.getName());
							}
						}

					}
				}
			}

			try {
				mtype.addParameter(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		RepositoryConnectionDialog dial = new RepositoryConnectionDialog(IRepositoryApi.R_MARKDOWN_TYPE, mtype);
		dial.center();
	}

	public AirProject getCurrentProject() {
		return currentProject;
	}

	public void setCurrentProject(AirProject currentProject) {
		this.currentProject = currentProject;
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

	public void handleHelpLibraryClick(String name) {

		RScriptModel box = new RScriptModel();
		String script = "library(help = '" + name + "')";
		box.setUserREnv(user.getLogin() + user.getId());
		box.setScript(script);

		workspacePanel.consoleStack.push(script);

		workspacePanel.consoleStackPointer = workspacePanel.consoleStack.size() + 1;
		// txtConsole.setText("");

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {

				console.add(new HTML(result.getOutputLog()));
				// scrollBottom();
				workspacePanel.updateVignetteLog(new HTML(result.getOutputLog()));
				workspacePanel.getAirPanel().getLogPanel().addLog(result.getOutputLog());

				if (result.getOutputFiles() != null) {
					int nbfiles = result.getOutputFiles().length;
					if (nbfiles > 0) {
						for (int i = 0; i < nbfiles; i++) {
							String url = result.getOutputFiles()[i];

							GraphDialog dial = new GraphDialog(url, LabelsConstants.lblCnst.Help(), CommonConstants.FORMAT_TXT);
							dial.center();
						}
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToExecuteScript());
			}
		});

	}

	public String getSavedString() {
		return savedString;
	}

	public void setSavedString(String savedString) {
		this.savedString = savedString;
	}

	private List<Parameter> getScriptParameters(String script) {
		List<Parameter> params = new ArrayList<Parameter>();
		// String script = getText();
		for (Parameter param : workspacePanel.getAirPanel().getResourceManager().getParameters()) {
			if (script.contains(param.getParameterName())) {
				params.add(param);
			}
		}
		return params;
	}

	private String changeTextParameters(String script, List<Parameter> params) {
		// String script = getText();
		for (Parameter param : params) {
			if (param.getParameterType().equals(TypeParameter.RANGE)) {
				StringBuilder buf = new StringBuilder();
				boolean first = true;
				int rowCount = 0;
				int colCount = 1;
				for (String elem : param.getValueRange()) {
					if (first) {
						first = false;
					}
					else {
						buf.append(",");
					}

					try {
						Double.parseDouble(elem);
						buf.append(elem);
					} catch (Exception e) {
						buf.append("\"" + elem + "\"");
					}

					rowCount++;
				}

				buf.append("),nrow = " + rowCount + ",ncol = " + colCount + ", byrow = TRUE");

				script = script.replace(param.getParameterName(), "matrix(c(" + buf.toString() + ")");
			}
			else if (param.getParameterType().equals(TypeParameter.LOV)) {

			}
			else {
				script = script.replace(param.getParameterName(), param.getValue());
			}

		}
		return script;
	}

	public void refreshAutoCompleteParams() {
		suggParam.clear();
		if (workspacePanel.getAirPanel().getResourceManager().getParameters() != null) {
			for (Parameter param : workspacePanel.getAirPanel().getResourceManager().getParameters()) {
				suggParam.add(param.getParameterName());
			}
		}

	}

	public void handleCompletionClick(String preword) {
		String select = sbox.getText();
		if (!select.equals("")) {
			sbox.setText("");
			scriptArea.getFormatter().insertHTML(select.substring(preword.length()));
		}

	}

	public void refreshVersions() {
		showWaitPart(true);
		SmartAirService.Connect.getInstance().getModelsbyScript(currentScript.getId(), new GwtCallbackWrapper<List<RScriptModel>>(this, true) {
			@Override
			public void onSuccess(List<RScriptModel> result) {
				if (result != null) {
					DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM);
					currentVersions = result;
					lstVersions.clear();
					lstVersions.addItem(LabelsConstants.lblCnst.SelectAVersion(), "0");
					for (RScriptModel mod : result) {
						if (mod.getId() == currentModel.getId()) {
							continue;
						}
						else {
							lstVersions.addItem(mod.getNumVersion() + "  -  " + format.format(mod.getDateVersion()), String.valueOf(mod.getId()));
						}
					}
				}
			}
		}.getAsyncCallback());
	}

	public List<RScriptModel> getCurrentVersions() {
		return currentVersions;
	}

	private void recode(Recode recode) {
		final RecodeWizard wiz = new RecodeWizard(recode);
		wiz.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (wiz.isConfirm()) {
					workspacePanel.writeGeneratedCode(wiz.getGeneratedCode());
				}
			}
		});
		wiz.center();
	}

	public RichTextArea getScriptArea() {
		return scriptArea;
	}

	public void saveGenCode() { // lorsque le panel est generated code
		DateTimeFormat fmt = DateTimeFormat.getFormat("dd-MM-yyyy");
		Cookies.setCookie("gen" + user.getId(), getText(), fmt.parse("01-01-2030"));
	}

	public void updateRScript() {
		showWaitPart(true);
		SmartAirService.Connect.getInstance().addorEditScript(currentScript, new GwtCallbackWrapper<Integer>(this, true) {

			@Override
			public void onSuccess(Integer result) {
				// showWaitPart(false);
				currentScript.setId(result);
				ScriptPanel.this.workspacePanel.getNavPanel().onRefreshClick(null);
			}
		}.getAsyncCallback());
	}
}
