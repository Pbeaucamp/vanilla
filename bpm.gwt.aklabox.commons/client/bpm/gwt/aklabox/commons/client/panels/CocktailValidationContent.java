package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.Cocktail;
import bpm.document.management.core.model.PastellData;
import bpm.document.management.core.model.PastellData.ObjectType;
import bpm.gwt.aklabox.commons.client.customs.LabelDateBox;
import bpm.gwt.aklabox.commons.client.customs.LabelTextArea;
import bpm.gwt.aklabox.commons.client.customs.LabelTextBox;
import bpm.gwt.aklabox.commons.client.customs.ListBoxWithButton;
import bpm.gwt.aklabox.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.aklabox.commons.client.panels.CocktailNavigationItem.Type;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.TvaInterface;
import bpm.gwt.aklabox.commons.client.utils.TvaItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class CocktailValidationContent extends CompositeWaitPanel implements TvaInterface{

	private static CocktailValidationContentUiBinder uiBinder = GWT.create(CocktailValidationContentUiBinder.class);

	interface CocktailValidationContentUiBinder extends UiBinder<Widget, CocktailValidationContent> {
	}

	interface MyStyle extends CssResource {
		String validated();
		String boxItem();
	}

	@UiField
	HTMLPanel panelDataCocktail, panelTvas, tvaList;
	@UiField
	Image btnAddTva;
	@UiField
	MyStyle style;

	// private DateTimeFormat dateFormat =
	// DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss");
	private Cocktail cocktail;
	private CocktailNavigationItem parent;
	private boolean isConfirm;
	
	private List<LabelTextBox> txts = new ArrayList<>();
	private List<LabelDateBox> dates = new ArrayList<>();
	private List<ListBoxWithButton<String>> lsts = new ArrayList<>();
	
	private LabelTextBox txtTva, txtHt, txtTtc;
	private LabelTextArea txtCom;
	
	private int cpt = 0;

	public CocktailValidationContent(CocktailNavigationItem parent, Chorus chorus) {
		initWidget(uiBinder.createAndBindUi(this));

		this.cocktail = new Cocktail();
		if(chorus == null){
			chorus = new Chorus();
		}
		chorus.setCocktail(cocktail);
		this.parent = parent;
		cocktail.loadChorus(chorus);
		//parent.getAeo().setCocktailMetadata(cocktail);
		
		
		parent.setChorusMetadata(chorus);
		
		if(parent.getType() == Type.AKLAD && parent.getAkLadExportObject().getPagesMeta().get(0).getSelectedType().isChorusType()){
			String idfour = parent.getAkLadExportObject().getPagesMeta().get(0).getSelectedType().getIdFournisseur();
			if(idfour != null && !idfour.isEmpty()){
				try {
					parent.getAkLadExportObject().getChorusMetadata().getCocktail().addData(Cocktail.P_CK_FOU_ORDRE, idfour);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		

		panelDataCocktail.clear();
		if (cocktail != null) {
			Map<String, PastellData> metadata = cocktail.getMetadata();
			for (final String key : metadata.keySet()) {
				if(/*key.equals(Cocktail.P_CK_TOTAL_HT) || key.equals(Cocktail.P_CK_TOTAL_TTC) || key.equals(Cocktail.P_CK_TOTAL_TVA) 
						||*/ key.equals(Cocktail.P_CK_NUM_EJ) || key.equals(Cocktail.P_CK_ID_DEP_FACTURE)  || key.equals(Cocktail.P_CK_NUM_FACTURE_FOURNISSEUR)
								|| key.equals(Cocktail.P_CK_FOU_ORDRE)){
					final LabelTextBox txt = new LabelTextBox();
					txt.setLabel(key);
					txt.setText(metadata.get(key).getValueAsString());
					txt.addStyleName(style.boxItem());
					txt.addKeyUpHandler(new KeyUpHandler() {
						
						@Override
						public void onKeyUp(KeyUpEvent event) {
							updateProgress(key, txt.getText());
						}
					});
					if(key.equals(Cocktail.P_CK_FOU_ORDRE)){
						txt.setEnabled(false);
					}
					txts.add(txt);
					panelDataCocktail.add(txt);
				} else if(key.equals(Cocktail.P_CK_DATE_EMISSION) || key.equals(Cocktail.P_CK_DATE_RECEPTION)){
					final LabelDateBox txt = new LabelDateBox();
					txt.setLabel(key);
					try {
						txt.setValue(metadata.get(key).getValueAsDate());
					} catch (Exception e) {
						e.printStackTrace();
					}
					txt.addStyleName(style.boxItem());
					txt.addValueChangeHandler(new ValueChangeHandler<Date>() {
						
						@Override
						public void onValueChange(ValueChangeEvent<Date> event) {
							updateProgress(key, txt.getValue());
						}
					});
					dates.add(txt);
					panelDataCocktail.add(txt);
				} else if(key.equals(Cocktail.P_CK_CHAINE_DETAILS)){
					panelTvas.removeFromParent();
					if(!metadata.get(key).getValueAsString().isEmpty()){
						String[] tvas = metadata.get(key).getValueAsString().split(":");
						for(String tva : tvas){
							tvaList.add(new TvaItem(this, tva));
						}
					}
					
					panelDataCocktail.add(panelTvas);
					panelTvas.setVisible(true);
					
					List<String> totaux = new ArrayList<>();
					totaux.add(Cocktail.P_CK_TOTAL_HT);
					totaux.add(Cocktail.P_CK_TOTAL_TVA);
					totaux.add(Cocktail.P_CK_TOTAL_TTC);
					for(String total : totaux){
						final LabelTextBox txt = new LabelTextBox();
						txt.setLabel(total);
						txt.setText(metadata.get(total).getValueAsString());
						txt.addStyleName(style.boxItem());
						/*txt.addKeyUpHandler(new KeyUpHandler() {
							
							@Override
							public void onKeyUp(KeyUpEvent event) {
								updateProgress(total, txt.getText());
							}
						});*/
						txt.setEnabled(false);
						
						txts.add(txt);
						panelDataCocktail.add(txt);
						if(total.equals(Cocktail.P_CK_TOTAL_HT)){
							txtHt = txt;
						} else if(total.equals(Cocktail.P_CK_TOTAL_TVA)){
							txtTva = txt;
						} else if(total.equals(Cocktail.P_CK_TOTAL_TTC)){
							txtTtc = txt;
						}
					}
				} else if(key.equals(Cocktail.P_CK_COMMENTAIRE)){
					final LabelTextArea txt = new LabelTextArea();
					txt.setPlaceHolder(Cocktail.P_CK_COMMENTAIRE);
					txt.addStyleName(style.boxItem());
					txt.addKeyUpHandler(new KeyUpHandler() {
						
						@Override
						public void onKeyUp(KeyUpEvent event) {
							updateProgress(key, txt.getText());
						}
					});
					panelDataCocktail.add(txt);
					
					txtCom = txt;
					
				} else {
					if(key.equals(Cocktail.P_CK_TOTAL_HT) || key.equals(Cocktail.P_CK_TOTAL_TTC) || key.equals(Cocktail.P_CK_TOTAL_TVA)) continue;
					
					final ListBoxWithButton<String> lst = new ListBoxWithButton<>(key, new ArrayList<String>(), "", null);
					lst.setList(cocktail.getPossibleValues(key), true);
					for(String val : lst.getList()){
						if(val.toLowerCase().equals(metadata.get(key).getValueAsString().toLowerCase())){
							lst.setSelectedIndex(lst.getList().indexOf(val)+1);
						}
					}
					lst.addStyleName(style.boxItem());
					lst.addChangeHandler(new ChangeHandler() {
						
						@Override
						public void onChange(ChangeEvent event) {
							updateProgress(key, lst.getValue(lst.getSelectedIndex()+1));
						}
					});
					
					lsts.add(lst);
					panelDataCocktail.add(lst);
				}
				
			}

		}

		updateProgress(null, null);
		//createButtonBar(LabelConstants.lblCnst.Save(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

//	private ClickHandler okHandler = new ClickHandler() {
//
//		@Override
//		public void onClick(ClickEvent event) {
//			for (Widget w : panelDataCocktail) {
//				if (w instanceof LabelTextBox) {
//					LabelTextBox txt = (LabelTextBox) w;
//					chorus.addData(txt.getLabel(), new PastellData(ObjectType.STRING, txt.getText()));
//				}
//			}
//			isConfirm = true;
//			hide();
//		}
//	};
	
	public void updateProgress(String key, Object value){
		if(key == null) {
			key = "";
		}
		final int count = lsts.size() + txts.size() + dates.size();
		cpt = 0;
		
		for(final LabelTextBox txt : txts){
			if(txt.getText().isEmpty()){
				txt.removeStyleName(style.validated());
			} else {
				if(txt.getLabel().equals(Cocktail.P_CK_TOTAL_HT) || txt.getLabel().equals(Cocktail.P_CK_TOTAL_TTC) || txt.getLabel().equals(Cocktail.P_CK_TOTAL_TVA)){
					try {
						Double d = Double.parseDouble((String) txt.getText());
						if((txt.getLabel().equals(Cocktail.P_CK_TOTAL_HT) && d.equals(getSumHt()))
								|| (txt.getLabel().equals(Cocktail.P_CK_TOTAL_TVA) && d.equals(getSumTva()))
										|| (txt.getLabel().equals(Cocktail.P_CK_TOTAL_TTC) && d.equals(getSumTtc()))){
							txt.addStyleName(style.validated());
							cpt++;
						} else {
							txt.removeStyleName(style.validated());
							//MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Warning(), txt.getLabel() + " " + LabelsConstants.lblCnst.isNotEqualToSum());
						}
						
					} catch(Exception e) {
						txt.removeStyleName(style.validated());
					}
				} else if(txt.getLabel().equals(Cocktail.P_CK_NUM_EJ) && !txt.getText().isEmpty()){
					if(key.equals(Cocktail.P_CK_NUM_EJ)){
						AklaCommonService.Connect.getService().searchCocktailEngagement(txt.getText(), new GwtCallbackWrapper<String>(null, false, false, false) {
							
							@Override
							public void onSuccess(String result) {
								if(result != null && result.split(";").length > 1){
									cocktail.addData(Cocktail.P_CK_ID_DEP_EJ, result.split(";")[0]);
									if(cocktail.getFouOrdre() == null || cocktail.getFouOrdre().isEmpty()){
										cocktail.addData(Cocktail.P_CK_FOU_ORDRE, result.split(";")[1]);
										//txt.setText(result.split(";")[1]);
									} 
//									else if(!cocktail.getFouOrdre().equals(result.split(";")[1])){
//										Map<String, String> items = new HashMap<>();
//										items.put(cocktail.getFouOrdre(), cocktail.getFouOrdre());
//										items.put(result.split(";")[1], result.split(";")[1]);
//										final ChoiceDialog cd = new ChoiceDialog(LabelsConstants.lblCnst.FouOrdreConflict(), items);
//										
//										DefaultDialog d = new DefaultDialog(LabelsConstants.lblCnst.Confirmation(), cd, 670, 0, 10);
//										d.addCloseHandler(new CloseHandler<PopupPanel>() {
//											
//											@Override
//											public void onClose(CloseEvent<PopupPanel> event) {
//												cocktail.addData(Cocktail.P_CK_FOU_ORDRE, cd.getOptionValue(false));
//												/*for(LabelTextBox txt2 : txts){
//													if(txt2.getLabel().equals(Cocktail.P_CK_FOU_ORDRE)){
//														txt2.setText(cd.getOptionValue(false));
//													}
//												}*/
//												
//											}
//										});
//										d.show();
//									}
									
									txt.addStyleName(style.validated());
									parent.updateProgress(parent.getProgress() + ((double) 100/ count));
								} else {
									cocktail.addData(Cocktail.P_CK_ID_DEP_EJ, "");
									if(cocktail.getFouOrdre() == null){
										cocktail.addData(Cocktail.P_CK_FOU_ORDRE, "");
										//txt.setText("");
									}
									
									txt.removeStyleName(style.validated());
								}
							}
							@Override
							public void onFailure(Throwable t) {
								
									cocktail.addData(Cocktail.P_CK_ID_DEP_EJ, "");
									cocktail.addData(Cocktail.P_CK_FOU_ORDRE, "");
									txt.removeStyleName(style.validated());
//									/*local test*/
//									txt.addStyleName(style.validated());
//									parent.updateProgress(parent.getProgress() + ((double) 100/ count));
							}
						}.getAsyncCallback());
					} else {
						if(txt.getStyleName().contains(style.validated())){
							cpt++;
						}
					}
					
				}	else {
				
					txt.addStyleName(style.validated());
					cpt++;
				}
				
			}
		}
		for(LabelDateBox txt : dates){
			if(txt.getValue() == null){
				txt.removeStyleName(style.validated());
			} else {
				txt.addStyleName(style.validated());
				cpt++;
			}
		}
		for(ListBoxWithButton<String> txt : lsts){
			if(txt.getSelectedIndex()+1 <= 0){
				txt.removeStyleName(style.validated());
			} else {
				txt.addStyleName(style.validated());
				cpt++;
			}
		}
		
		double pct = ((double)((double) cpt)/((double) count))*100;
		parent.updateProgress(pct);
		
		if(key != null){ //maj cocktail object
			if(key.equals(Cocktail.P_CK_DATE_EMISSION)){
				try {
					cocktail.addData(Cocktail.P_CK_DATE_EMISSION, new PastellData(ObjectType.DATE, (Date) value));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(key.equals(Cocktail.P_CK_DATE_RECEPTION)){
				try {
					cocktail.addData(Cocktail.P_CK_DATE_RECEPTION, new PastellData(ObjectType.DATE, (Date) value));
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(key.equals(Cocktail.P_CK_FOU_ORDRE)){
				cocktail.addData(Cocktail.P_CK_FOU_ORDRE, (String) value);
			} else if(key.equals(Cocktail.P_CK_ID_DEP_EJ)){
				cocktail.addData(Cocktail.P_CK_ID_DEP_EJ, (String) value);
			} else if(key.equals(Cocktail.P_CK_NUM_EJ)){
				cocktail.addData(Cocktail.P_CK_NUM_EJ, ((String) value).toUpperCase());
			} else if(key.equals(Cocktail.P_CK_ID_DEP_FACTURE)){
				cocktail.addData(Cocktail.P_CK_ID_DEP_FACTURE, (String) value);
			} else if(key.equals(Cocktail.P_CK_TOTAL_TTC) || key.equals(Cocktail.P_CK_TOTAL_HT) || key.equals(Cocktail.P_CK_TOTAL_TVA)){
				try {
					Double d1 = Double.parseDouble((String) value);
					cocktail.addData(key, new PastellData(ObjectType.DOUBLE, d1));
				} catch(Exception e) {
					cocktail.addData(key, (String) value);
				}
			} else if(key.equals(Cocktail.P_CK_TYPE_FACTURE)){
				cocktail.addData(Cocktail.P_CK_TYPE_FACTURE, (String) value);
			} else if(key.equals(Cocktail.P_CK_NUM_FACTURE_FOURNISSEUR)){
				cocktail.addData(Cocktail.P_CK_NUM_FACTURE_FOURNISSEUR, (String) value);
			} else if(key.equals(Cocktail.P_CK_CHAINE_DETAILS)){
				cocktail.addData(Cocktail.P_CK_CHAINE_DETAILS, getCahineDetails());
				for(final LabelTextBox txt : txts){
					if(txt.getLabel().equals(Cocktail.P_CK_TOTAL_HT) || txt.getLabel().equals(Cocktail.P_CK_TOTAL_TTC) || txt.getLabel().equals(Cocktail.P_CK_TOTAL_TVA)){
						try {
							Double d1 = Double.parseDouble((String) txt.getText());
							cocktail.addData(txt.getLabel(), new PastellData(ObjectType.DOUBLE, d1));
						} catch(Exception e) {
							cocktail.addData(txt.getLabel(), (String) value);
						}	
					}
				}
				
			} else if(key.equals(Cocktail.P_CK_COMMENTAIRE)){
				cocktail.addData(Cocktail.P_CK_COMMENTAIRE, (String) value);
			}
			
		}
	}

	public boolean isConfirm() {
		return isConfirm;
	}

	public Cocktail getCocktail() {
		return cocktail;
	}

	public CocktailNavigationItem getParentNavItem() {
		return parent;
	}
	
	@UiHandler("btnAddTva")
	public void onAddTva(ClickEvent e){
		tvaList.add(new TvaItem(this));
	}

	@Override
	public void onTvaChange() {
		try {
			txtHt.setText(getSumHt()+"");
			txtTtc.setText(getSumTtc()+"");
			txtTva.setText(getSumTva()+"");
			
			updateProgress(Cocktail.P_CK_CHAINE_DETAILS, "");
			//updateProgress(Cocktail.P_CK_TOTAL_TVA, txtTva.getText());
			//updateProgress(Cocktail.P_CK_TOTAL_TTC, txtTtc.getText());
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private Double getSumHt(){
		Double tot = 0.0;
		for(Widget w : tvaList){
			if(w instanceof TvaItem){
				tot += ((TvaItem)w).getHt();
			}
		}
		return tot;
	}
	
	private Double getSumTtc(){
		Double tot = 0.0;
		for(Widget w : tvaList){
			if(w instanceof TvaItem){
				tot += ((TvaItem)w).getTtc();
			}
		}
		return tot;
	}
	
	private Double getSumTva(){
		Double tot = 0.0;
		for(Widget w : tvaList){
			if(w instanceof TvaItem){
				tot += ((TvaItem)w).getTva();
			}
		}
		return tot;
	}

	private String getCahineDetails(){
		String res = "";
		for(Widget w : tvaList){
			if(w instanceof TvaItem){
				res += ((TvaItem)w).getRowTva()+":";
			}
		}
		res = res.substring(0,  res.length() - 1);
		return res;
	}
}
