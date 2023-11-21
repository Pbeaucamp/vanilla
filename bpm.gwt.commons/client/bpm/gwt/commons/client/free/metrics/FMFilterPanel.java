package bpm.gwt.commons.client.free.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.FreeMetricService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class FMFilterPanel extends Composite {

	private static FMFilterPanelUiBinder uiBinder = GWT.create(FMFilterPanelUiBinder.class);

	interface FMFilterPanelUiBinder extends UiBinder<Widget, FMFilterPanel> {
	}

	@UiField
	ListBox lstGroups, lstObs, lstThemes;

	private HashMap<Group, List<Observatory>> items;

	private IFilterChangeHandler handler;

	private boolean callChangeHandler = false;

	public FMFilterPanel(IFilterChangeHandler handler) {
		initWidget(uiBinder.createAndBindUi(this));

		this.handler = handler;

		initLists(null, null);

		addHandlers();
	}

	public FMFilterPanel(IFilterChangeHandler handler, boolean callChangeHandler, Integer themeId, Integer groupId) {
		initWidget(uiBinder.createAndBindUi(this));

		this.handler = handler;
		this.callChangeHandler = callChangeHandler;

		initLists(themeId, groupId);

		addHandlers();
	}

	private void addHandlers() {

		lstGroups.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Group g = getSelectedGroup();

				lstObs.clear();
				lstThemes.clear();

				lstObs.addItem(LabelsConstants.lblCnst.allObs(), "-1");

				lstThemes.addItem(LabelsConstants.lblCnst.allThemes(), "-1");

				List<Theme> themes = new ArrayList<Theme>();

				for (Observatory ob : items.get(g)) {
					lstObs.addItem(ob.getName(), ob.getId() + "");
					themes.addAll(ob.getThemes());
				}

				Collections.sort(themes, new Comparator<Theme>() {
					@Override
					public int compare(Theme o1, Theme o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});

				for (Theme th : themes) {
					lstThemes.addItem(th.getName(), th.getId() + "");
				}

				handler.selectionChanged(getSelectedGroup(), getSelectedObservatory(), getSelectedTheme());
			}
		});

		lstObs.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				Group g = getSelectedGroup();
				Observatory obs = getSelectedObservatory();
				lstThemes.clear();

				lstThemes.addItem(LabelsConstants.lblCnst.allThemes(), "-1");

				List<Theme> themes = new ArrayList<Theme>();

				if (obs == null) {
					for (Observatory ob : items.get(g)) {
						themes.addAll(ob.getThemes());
					}
				}
				else {
					for (Observatory ob : items.get(g)) {
						if (ob.getId() == obs.getId()) {
							themes.addAll(ob.getThemes());
							break;
						}
					}
				}

				Collections.sort(themes, new Comparator<Theme>() {
					@Override
					public int compare(Theme o1, Theme o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});

				for (Theme th : themes) {
					lstThemes.addItem(th.getName(), th.getId() + "");
				}

				handler.selectionChanged(getSelectedGroup(), getSelectedObservatory(), getSelectedTheme());
			}
		});

		lstThemes.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				handler.selectionChanged(getSelectedGroup(), getSelectedObservatory(), getSelectedTheme());
			}
		});

	}

	public void initLists(final Integer themeId, final Integer groupId) {
		
		FreeMetricService.Connect.getInstance().getObservatories(new GwtCallbackWrapper<HashMap<Group, List<Observatory>>>(null, true) {
			@Override
			public void onSuccess(HashMap<Group, List<Observatory>> result) {

				lstGroups.clear();
				lstObs.clear();
				lstThemes.clear();

				items = result;

				boolean first = true;
				int indexGroup = -1;
				int i = 0;
				
				Group selectedGroup = null;
				for (Group g : result.keySet()) {
					if (first) {
						selectedGroup = g;
						first = false;
					}
					
					if (groupId != null && groupId.equals(g.getId())) {
						indexGroup = i;
						selectedGroup = g;
					}

					lstGroups.addItem(g.getName(), g.getId() + "");
					i++;
				}

				lstObs.addItem(LabelsConstants.lblCnst.allObs(), "-1");
				lstThemes.addItem(LabelsConstants.lblCnst.allThemes(), "-1");
				
				if (indexGroup != -1) {
					lstGroups.setSelectedIndex(indexGroup);
				}

				List<Theme> themes = new ArrayList<Theme>();
				if (selectedGroup != null) {
					for (Observatory ob : result.get(selectedGroup)) {
						lstObs.addItem(ob.getName(), ob.getId() + "");

						themes.addAll(ob.getThemes());
					}
				}

				Collections.sort(themes, new Comparator<Theme>() {
					@Override
					public int compare(Theme o1, Theme o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});

				int indexTheme = -1;
				for (int j = 0; j < themes.size(); j++) {
					if (themeId != null && themeId.equals(themes.get(j).getId())) {
						indexTheme = j;
					}
					lstThemes.addItem(themes.get(j).getName(), themes.get(j).getId() + "");
				}

				if (indexTheme != -1) {
					lstThemes.setSelectedIndex(indexTheme + 1);
				}

				if (callChangeHandler) {
					handler.selectionChanged(getSelectedGroup(), getSelectedObservatory(), getSelectedTheme());
				}
			}

		}.getAsyncCallback());

	}

	public Group getSelectedGroup() {
		int groupId = Integer.parseInt(lstGroups.getValue(lstGroups.getSelectedIndex()));

		for (Group g : items.keySet()) {
			if (g.getId() == groupId) {
				return g;
			}
		}

		return null;
	}

	public Observatory getSelectedObservatory() {
		int obsId;
		try {
			obsId = Integer.parseInt(lstObs.getValue(lstObs.getSelectedIndex()));
		} catch (Exception e) {
			return null;
		}

		if (obsId == -1) {
			return null;
		}

		for (Group g : items.keySet()) {
			for (Observatory obs : items.get(g)) {
				if (obs.getId() == obsId) {
					return obs;
				}
			}
		}

		return null;
	}

	public Theme getSelectedTheme() {
		int themeId;
		try {
			themeId = Integer.parseInt(lstThemes.getValue(lstThemes.getSelectedIndex()));
		} catch (Exception e) {
			return null;
		}

		if (themeId == -1) {
			return null;
		}

		for (Group g : items.keySet()) {
			for (Observatory obs : items.get(g)) {
				for (Theme th : obs.getThemes()) {
					if (th.getId() == themeId) {
						return th;
					}
				}
			}
		}

		return null;
	}

	public List<Observatory> getObservatories() {
		return items.get(getSelectedGroup());
	}
}
