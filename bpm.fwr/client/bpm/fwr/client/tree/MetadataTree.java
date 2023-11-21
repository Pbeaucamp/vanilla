package bpm.fwr.client.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.fwr.api.beans.dataset.GroupPrompt;
import bpm.fwr.client.Bpm_fwr;
import bpm.fwr.client.draggable.widgets.DraggableColumn;
import bpm.fwr.client.draggable.widgets.DraggableResourceHTML;
import bpm.fwr.client.draggable.widgets.DraggableTable;
import bpm.fwr.client.images.WysiwygImage;
import bpm.fwr.client.services.FwrServiceMetadata;
import bpm.fwr.shared.models.metadata.FwrBusinessModel;
import bpm.fwr.shared.models.metadata.FwrBusinessPackage;
import bpm.fwr.shared.models.metadata.FwrBusinessTable;
import bpm.fwr.shared.models.metadata.FwrMetadata;
import bpm.fwr.shared.models.metadata.FwrSavedQuery;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.shared.InfoUser;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class MetadataTree extends Tree {
	private static final String CSS_COL_DIMENSION = "colDimension";
	private static final String CSS_COL_MEASURE = "colMeasure";

	private IWait waitPanel;
	
	private boolean showRessource;

	private PickupDragController dragController, resourceDragController;

	private List<FwrMetadata> metadatas;
	private String selectedLanguage;
	private String filterName = "";
	
	private HashMap<FwrMetadata, TreeItem> metadataItems = new HashMap<FwrMetadata, TreeItem>();
	
//	private HashMap<Integer, String> languages = new HashMap<Integer, String>();

	public MetadataTree(IWait waitPanel, PickupDragController dragController, PickupDragController resourceDragController, boolean showRessource) {
		this.waitPanel = waitPanel;
		this.dragController = dragController;
		this.resourceDragController = resourceDragController;
		this.showRessource = showRessource;

		buildTree(true);

		this.addOpenHandler(openHandler);
	}

	public void buildTree(boolean isFirstTime) {
		this.clear();

//		HTML htmlRoot = new HTML(new Image(WysiwygImage.INSTANCE.table()) + " " + Bpm_fwr.LBLW.Metadata());
//		TreeItem root = new TreeItem(htmlRoot);
//		root.setTitle(Bpm_fwr.LBLW.Metadata());

		if (metadatas != null && !metadatas.isEmpty()) {
			for (FwrMetadata metadata : metadatas) {
				HTML htmlItem = new HTML(new Image(WysiwygImage.INSTANCE.metadata()) + " " + metadata.getName());
				TreeItem item = new TreeItem(htmlItem);
				item.setUserObject(metadata);
				item.setTitle(metadata.getName());

				this.addItem(item);
				item.setUserObject(metadata);

				HTML htmlTempItem = new HTML(new Image(WysiwygImage.INSTANCE.loading()) + " " + Bpm_fwr.LBLW.Wait());
				TreeItem tempItem = new TreeItem(htmlTempItem);
				tempItem.setTitle(Bpm_fwr.LBLW.Wait());
				item.addItem(tempItem);
			}
		}
		else {
//			if (isFirstTime) {
//				HTML htmlItem = new HTML(new Image(WysiwygImage.INSTANCE.loading()) + " " + Bpm_fwr.LBLW.Wait());
//				TreeItem item = new TreeItem(htmlItem);
//				item.setTitle(Bpm_fwr.LBLW.Wait());
//				root.addItem(item);
//			}
//			else {
				HTML htmlItem = new HTML(Bpm_fwr.LBLW.NoMetadata());
				TreeItem item = new TreeItem(htmlItem);
				item.setTitle(Bpm_fwr.LBLW.NoMetadata());
				
				HTML htmlRoot = new HTML(new Image(WysiwygImage.INSTANCE.table()) + " " + Bpm_fwr.LBLW.Metadata());
				TreeItem root = new TreeItem(htmlRoot);
				root.setTitle(Bpm_fwr.LBLW.Metadata());
				root.addItem(item);
//			}
		}
	}

	private void refreshMetadataTree(FwrMetadata metadata, TreeItem item, String defaultLanguage) {
		item.removeItems();

		List<FwrBusinessModel> models = metadata.getBusinessModels();
		for (FwrBusinessModel model : models) {
			boolean addModel = false;
			HTML packmod = new HTML(new Image(WysiwygImage.INSTANCE.table()) + " " + model.getTitles().get(defaultLanguage));
			TreeItem itemModel = new TreeItem(packmod);
			itemModel.setTitle(model.getTitles().get(defaultLanguage));
			itemModel.setUserObject(model);

			for (FwrBusinessPackage pack : model.getBusinessPackages()) {
				HTML packpan = new HTML(new Image(WysiwygImage.INSTANCE.table()) + " " + pack.getTitles().get(defaultLanguage));
				TreeItem itemPack = new TreeItem(packpan);
				itemPack.setTitle(pack.getTitles().get(defaultLanguage));
				itemPack.setUserObject(pack);
				

				for (FwrBusinessTable table : pack.getBusinessTables()) {
					HTML tab = new HTML(new Image(WysiwygImage.INSTANCE.object()) + " " + table.getTitles().get(defaultLanguage));
					TreeItem itemTable = new TreeItem(tab);
					itemTable.setUserObject(table);
					itemTable.setTitle(table.getTitles().get(defaultLanguage));
					

					if (table.getChilds() != null && table.getChilds().size() > 0) {
						fillChilds(table, itemTable, metadata, item, defaultLanguage);
					}

					for (Column column : table.getColumns()) {
						if(!column.getTitle(defaultLanguage).toLowerCase().contains(filterName.toLowerCase())) {
							continue;
						}
						addModel = true;
						// We set the metadata informations to the column that
						// we can create datasource and dataset later
						column.setBusinessPackageParent(pack.getName());
						column.setBusinessModelParent(model.getName());
						column.setMetadataParent(metadata.getName());
						column.setMetadataId(metadata.getId());

						DraggableColumn drgColumn = null;
						if (column.isDimension()) {
							drgColumn = new DraggableColumn(column, defaultLanguage, " D");
							drgColumn.addStyleName(CSS_COL_DIMENSION);
						}
						else if (column.isMeasure()) {
							drgColumn = new DraggableColumn(column, defaultLanguage, " M");
							drgColumn.addStyleName(CSS_COL_MEASURE);
						}
						else {
							drgColumn = new DraggableColumn(column, defaultLanguage, "");
						}
						TreeItem itemCol = new TreeItem(drgColumn);
						itemCol.setTitle(column.getTitle(defaultLanguage));
						itemCol.setUserObject(column);
						itemTable.addItem(itemCol);
						dragController.makeDraggable(drgColumn);
					}
					if(itemTable.getChildCount() > 0) {
						itemPack.addItem(itemTable);
					}
				}

				if (showRessource && (pack.getPrompts() != null && !pack.getPrompts().isEmpty()) || showRessource && (pack.getFilters() != null && !pack.getFilters().isEmpty())) {

					HTML rootResources = new HTML(new Image(WysiwygImage.INSTANCE.FWR_Resources()) + " " + Bpm_fwr.LBLW.Resources());
					TreeItem itemRootResources = new TreeItem(rootResources);
					itemRootResources.setTitle(Bpm_fwr.LBLW.Resources());
					
					

					if (pack.getPrompts() != null && !pack.getPrompts().isEmpty()) {

						HTML rootPrompt = new HTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + Bpm_fwr.LBLW.Prompts());
						TreeItem itemRootPrompt = new TreeItem(rootPrompt);
						itemRootPrompt.setTitle(Bpm_fwr.LBLW.Prompts());
						

						for (FwrPrompt prompt : pack.getPrompts()) {
							
							if(!prompt.getName().toLowerCase().contains(filterName.toLowerCase())) {
								continue;
							}

							DraggableResourceHTML promptName = new DraggableResourceHTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + prompt.getName(), prompt, null, null, 0);
							TreeItem itemPrompt = new TreeItem(promptName);
							itemPrompt.setUserObject(prompt);
							itemPrompt.setTitle(prompt.getName());
							itemRootPrompt.addItem(itemPrompt);

							resourceDragController.makeDraggable(promptName);
						}
						
						if(itemRootPrompt.getChildCount() > 0) {
							itemRootResources.addItem(itemRootPrompt);
						}
					}

					if (pack.getPrompts() != null && !pack.getPrompts().isEmpty()) {

						HTML grpRootPrompt = new HTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + "Group Prompts");
						TreeItem itemRootGrpPrompt = new TreeItem(grpRootPrompt);
						itemRootGrpPrompt.setTitle("Group Prompts");
						

						for (FwrPrompt prompt : pack.getPrompts()) {
							
							if(!prompt.getName().toLowerCase().contains(filterName.toLowerCase())) {
								continue;
							}
							
							if(prompt.isChildPrompt()) {
								
								List<FwrPrompt> cascadingPrompts = new ArrayList<FwrPrompt>();
								buildCascadingPrompts(pack, cascadingPrompts, prompt);
								
								if(!cascadingPrompts.isEmpty()) {
									
									GroupPrompt grpPrompt = new GroupPrompt();
									grpPrompt.setCascadinPrompts(cascadingPrompts);
									
									DraggableResourceHTML grpPromptName = new DraggableResourceHTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + grpPrompt.getName(), grpPrompt, null, null, 0);
									
									TreeItem itemGrpPrompt = new TreeItem(grpPromptName);
									itemGrpPrompt.setTitle(prompt.getName());
									itemGrpPrompt.setUserObject(prompt);
									itemRootGrpPrompt.addItem(itemGrpPrompt);
									
									resourceDragController.makeDraggable(grpPromptName);
								}
							}
						}
						
						if(itemRootGrpPrompt.getChildCount() > 0) {
							itemRootResources.addItem(itemRootGrpPrompt);
						}
					}

					if (pack.getFilters() != null && !pack.getFilters().isEmpty()) {

						HTML rootFilter = new HTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + Bpm_fwr.LBLW.Filters());
						TreeItem itemRootFilter = new TreeItem(rootFilter);
						itemRootFilter.setTitle(Bpm_fwr.LBLW.Filters());
						

						for (FWRFilter filter : pack.getFilters()) {
							if(!filter.getName().toLowerCase().contains(filterName.toLowerCase())) {
								continue;
							}
							DraggableResourceHTML filterName = new DraggableResourceHTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + filter.getName(), filter, null, null, 0);

							TreeItem itemFilter = new TreeItem(filterName);
							itemFilter.setTitle(filter.getName());
							itemFilter.setUserObject(filter);
							itemRootFilter.addItem(itemFilter);

							resourceDragController.makeDraggable(filterName);
						}
						
						if(itemRootFilter.getChildCount() > 0) {
							itemRootResources.addItem(itemRootFilter);
						}
					}
					
					if(itemRootResources.getChildCount() > 0) {
						itemPack.addItem(itemRootResources);
					}
				}
				
				if(pack.getSavedQueries() != null && !pack.getSavedQueries().isEmpty()) {
					HTML savedQueriesHtml = new HTML(new Image(WysiwygImage.INSTANCE.fmdt_bq_16()) + " " + "Saved Queries");
					TreeItem itemQueries = new TreeItem(savedQueriesHtml);
					
					
					for(FwrSavedQuery savedQuery : pack.getSavedQueries()) {

						HTML savedQueryHtml = new HTML(new Image(WysiwygImage.INSTANCE.fmdt_bq_16()) + " " + savedQuery.getName());
						TreeItem itemQuery = new TreeItem(savedQueryHtml);
						itemQuery.setUserObject(savedQuery);
						
						
						for (Column column : savedQuery.getColumns()) {
							
							if(!column.getTitle(selectedLanguage).toLowerCase().contains(filterName.toLowerCase())) {
								continue;
							}
							
							column.setBusinessPackageParent(pack.getName());
							column.setBusinessModelParent(model.getName());
							column.setMetadataParent(metadata.getName());
							column.setMetadataId(metadata.getId());

							DraggableColumn drgColumn = null;
							if (column.isDimension()) {
								drgColumn = new DraggableColumn(column, defaultLanguage, " D");
								drgColumn.addStyleName(CSS_COL_DIMENSION);
							}
							else if (column.isMeasure()) {
								drgColumn = new DraggableColumn(column, defaultLanguage, " M");
								drgColumn.addStyleName(CSS_COL_MEASURE);
							}
							else {
								drgColumn = new DraggableColumn(column, defaultLanguage, "");
							}
							TreeItem itemCol = new TreeItem(drgColumn);
							itemCol.setTitle(column.getTitle(defaultLanguage));
							itemCol.setUserObject(column);
							itemQuery.addItem(itemCol);
							dragController.makeDraggable(drgColumn);
						}
						
						if(showRessource) {
							HTML rootResources = new HTML(new Image(WysiwygImage.INSTANCE.FWR_Resources()) + " " + Bpm_fwr.LBLW.Resources());
							TreeItem itemRootResources = new TreeItem(rootResources);
							itemRootResources.setTitle(Bpm_fwr.LBLW.Resources());
							itemQuery.addItem(itemRootResources);
	
							if (savedQuery.getPrompts() != null && !savedQuery.getPrompts().isEmpty()) {
	
								HTML rootPrompt = new HTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + Bpm_fwr.LBLW.Prompts());
								TreeItem itemRootPrompt = new TreeItem(rootPrompt);
								itemRootPrompt.setTitle(Bpm_fwr.LBLW.Prompts());
	
								for (FwrPrompt prompt : savedQuery.getPrompts()) {
									
									if(!prompt.getName().toLowerCase().contains(filterName.toLowerCase())) {
										continue;
									}
	
									DraggableResourceHTML promptName = new DraggableResourceHTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + prompt.getName(), prompt, null, null, 0);
									TreeItem itemPrompt = new TreeItem(promptName);
									itemPrompt.setUserObject(prompt);
									itemPrompt.setTitle(prompt.getName());
									itemRootPrompt.addItem(itemPrompt);
	
									resourceDragController.makeDraggable(promptName);
								}
								
								if(itemRootPrompt.getChildCount() > 0) {
									itemRootResources.addItem(itemRootPrompt);
								}
							}
	
							if (savedQuery.getFilters() != null && !savedQuery.getFilters().isEmpty()) {
	
								HTML rootFilter = new HTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + Bpm_fwr.LBLW.Filters());
								TreeItem itemRootFilter = new TreeItem(rootFilter);
								itemRootFilter.setTitle(Bpm_fwr.LBLW.Filters());
								
	
								for (FWRFilter filter : savedQuery.getFilters()) {
									
									if(!filter.getName().toLowerCase().contains(filterName.toLowerCase())) {
										continue;
									}
									
									DraggableResourceHTML filterName = new DraggableResourceHTML(new Image(WysiwygImage.INSTANCE.filter()) + " " + filter.getName(), filter, null, null, 0);
	
									TreeItem itemFilter = new TreeItem(filterName);
									itemFilter.setTitle(filter.getName());
									itemFilter.setUserObject(filter);
									itemRootFilter.addItem(itemFilter);
	
									resourceDragController.makeDraggable(filterName);
								}
								
								if(itemRootFilter.getChildCount() > 0) {
									itemRootResources.addItem(itemRootFilter);
								}
							}
						}
						
						if(itemQuery.getChildCount() > 0) {
							itemQueries.addItem(itemQuery);
						}
					}
					if(itemQueries.getChildCount() > 0) {
						itemPack.addItem(itemQueries);
					}
				}
				if(itemPack.getChildCount() > 0) {
					itemModel.addItem(itemPack);
				}
			}

			if(showRessource && model.getRelations() != null && !model.getRelations().isEmpty()) {
				HTML strategiesHtml = new HTML(new Image(WysiwygImage.INSTANCE.database_link()) + " " + "Relation Strategies");
				TreeItem strategiesItem = new TreeItem(strategiesHtml);
				
			
				for(FwrRelationStrategy strat : model.getRelations()) {
					if(!strat.getLabel().toLowerCase().contains(filterName.toLowerCase())) {
						continue;
					}
					
					DraggableResourceHTML stratHtml = new DraggableResourceHTML(new Image(WysiwygImage.INSTANCE.database_link()) + " " + strat.getName(), strat, null, null, 0);

					TreeItem stratItem = new TreeItem(stratHtml);
					stratItem.setUserObject(strat);
					stratItem.setTitle(strat.getName());
					strategiesItem.addItem(stratItem);
					
					resourceDragController.makeDraggable(stratHtml);
				}
				
				if(strategiesItem.getChildCount() > 0) {
					itemModel.addItem(strategiesItem);
				}
			}
			
			if(itemModel.getChildCount() > 0) {
				item.addItem(itemModel);
			}
			
		}
	}
	
	private void buildCascadingPrompts(FwrBusinessPackage pack, List<FwrPrompt> cascadingPrompts, FwrPrompt childPrompt) {
		FwrPrompt parent = null;
		for(FwrPrompt prtParent : pack.getPrompts()) {
			if(prtParent.getName().equals(childPrompt.getParentPromptName())) {
				parent = prtParent;
				
				if(parent.isChildPrompt()) {
					buildCascadingPrompts(pack, cascadingPrompts, parent);
				}
				else {
					cascadingPrompts.add(parent);
				}
				break;
			}
		}
		
		cascadingPrompts.add(childPrompt);
	}

	private void fillChilds(FwrBusinessTable parenttable, TreeItem parentdrgTable, FwrMetadata metadata, TreeItem item, String defaultLanguage) {
		for (FwrBusinessTable table : parenttable.getChilds()) {
			DraggableTable drgTable = new DraggableTable(table, defaultLanguage);
			TreeItem itemTable = new TreeItem(drgTable);
			itemTable.setUserObject(table);
			itemTable.setTitle(table.getTitles().get(defaultLanguage));
			

			if (table.getChilds() != null && table.getChilds().size() > 0) {
				fillChilds(table, itemTable, metadata, item, defaultLanguage);
			}

			for (Column column : table.getColumns()) {
				if(!column.getTitle(selectedLanguage).toLowerCase().contains(filterName.toLowerCase())) {
					continue;
				}
				DraggableColumn drgColumn = new DraggableColumn(column, defaultLanguage, "");
				TreeItem itemCol = new TreeItem(drgColumn);
				itemCol.setUserObject(column);
				itemCol.setTitle(column.getTitle(defaultLanguage));
				itemTable.addItem(itemCol);
			}
			
			if(itemTable.getChildCount() > 0) {
				parentdrgTable.addItem(itemTable);
			}
		}
	}

	public void setMetadatas(List<FwrMetadata> metadatas) {
		this.metadatas = metadatas;
		buildTree(false);
	}
	
	public void filter(String filterName) {
		this.filterName = filterName;
		for(FwrMetadata met : metadatas) {
			if (met.isBrowsed()) {
				String defaultLanguage = "";
				String language = "";
				
				if (met != null && met.getLocales() != null) {
					boolean found = false;
					for (String key : met.getLocales().keySet()) {
						if(key.equals(selectedLanguage)) {
							found = true;
							defaultLanguage = key;
							break;
						}
						language = key;
					}
					
					if(!found) {
						defaultLanguage = language;
					}
				}
				refreshMetadataTree(met, metadataItems.get(met), defaultLanguage);
			}
		}
	}

	public void setLanguages(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	private OpenHandler<TreeItem> openHandler = new OpenHandler<TreeItem>() {

		@Override
		public void onOpen(final OpenEvent<TreeItem> event) {
			if (event.getTarget().getUserObject() != null && event.getTarget().getUserObject() instanceof FwrMetadata) {
				final FwrMetadata metadata = (FwrMetadata) event.getTarget().getUserObject();
				if (!metadata.isBrowsed()) {
					
					if(waitPanel != null) {
						waitPanel.showWaitPart(true);
					}

					InfoUser infoUser = Bpm_fwr.getInstance().getInfoUser();

					FwrServiceMetadata.Connect.getInstance().getMetadataContent(metadata.getId(), infoUser.getGroup().getName(), false, new AsyncCallback<List<FwrBusinessModel>>() {

						public void onSuccess(List<FwrBusinessModel> result) {
							if(waitPanel != null) {
								waitPanel.showWaitPart(false);
							}
							
							metadata.setBrowsed(true);
							metadata.setBusinessModels(result);

							if (result.size() > 0) {
								metadata.setLocales(result.get(0).getLocales());
							}

							String defaultLanguage = "";
							String language = "";
							
							if (metadata != null && metadata.getLocales() != null) {
								boolean found = false;
								for (String key : metadata.getLocales().keySet()) {
									if(key.equals(selectedLanguage)) {
										found = true;
										defaultLanguage = key;
										break;
									}
									language = key;
								}
								
								if(!found) {
									defaultLanguage = language;
								}
							}

							metadataItems.put(metadata, event.getTarget());
							
							refreshMetadataTree(metadata, event.getTarget(), defaultLanguage);
						}

						public void onFailure(Throwable caught) {
							if(waitPanel != null) {
								waitPanel.showWaitPart(false);
							}
							
							caught.printStackTrace();
						}
					});

				}
				else {
					String defaultLanguage = "";
					String language = "";
					
					if (metadata != null && metadata.getLocales() != null) {
						boolean found = false;
						for (String key : metadata.getLocales().keySet()) {
							if(key.equals(selectedLanguage)) {
								found = true;
								defaultLanguage = key;
								break;
							}
							language = key;
						}
						
						if(!found) {
							defaultLanguage = language;
						}
					}

					metadataItems.put(metadata, event.getTarget());
					
					refreshMetadataTree(metadata, event.getTarget(), defaultLanguage);
				}
			}
		}
	};
}
