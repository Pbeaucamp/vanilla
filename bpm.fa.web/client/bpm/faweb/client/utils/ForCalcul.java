package bpm.faweb.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.listeners.SetCalcName;
import bpm.faweb.client.panels.center.grid.CubeView;
import bpm.faweb.shared.infoscube.Calcul;
import bpm.faweb.shared.infoscube.ItemCube;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public final class ForCalcul {
	public final static int SUM = 0;
	public final static int DIFF = 1;
	public final static int DIV = 2;
	public final static int MUL = 3;

	public final static String ON_ROW = "on_row";
	public final static String ON_COL = "on_col";

	public static void activatedMenu(MainPanel mainPanel, boolean activated) {
		mainPanel.setCalcul(activated);
		mainPanel.setCalculs(new ArrayList<Calcul>());
	}

	public static void associativeCalcul(MainPanel mainPanel, Calcul calcul, CubeView grid) {

		if (calcul.getOrientation().equalsIgnoreCase(ON_ROW)) {
			int max = grid.getRowCount();

			TextBox name = new TextBox();
			name.setWidth("100%");
			name.setText(calcul.getTitle());
			name.addValueChangeHandler(new SetCalcName(name, calcul));

			grid.setWidget(max, 0, name);

			String start = (String) calcul.getFields().get(0);
			int s = new Integer(start).intValue();
			for (int j = 0; j < grid.getCellCount(s); j++) {
				grid.getCellFormatter().removeStyleName(s, j, "pair");
			}

			s--;
			List<ItemCube> lgn0 = mainPanel.getInfosReport().getGrid().getLigne(s);

			float[] calc = new float[lgn0.size() + 1];

			for (int k = 1; k < lgn0.size(); k++) {
				ItemCube itk = lgn0.get(k);
				if (itk.getType().equalsIgnoreCase("ItemValue")) {
					float fk = 0.0f;
					try {
						fk = itk.getValue();
					} catch (Exception e) {

					}
					calc[k] = fk;
				}
				else {
					calc[k] = 0.0f;
				}
			}

			for (int i = 1; i < calcul.getFields().size(); i++) {
				String kString = (String) calcul.getFields().get(i);
				int n = new Integer(kString).intValue();
				for (int j = 0; j < grid.getCellCount(n); j++) {
					grid.getCellFormatter().removeStyleName(n, j, "pair");
				}
				n--;

				List<ItemCube> lgn = mainPanel.getInfosReport().getGrid().getLigne(n);
				for (int m = 1; m < lgn.size(); m++) {
					ItemCube it = lgn.get(m);
					if (it.getType().equalsIgnoreCase("ItemValue")) {
						float f = 0.0f;
						try {
							f = it.getValue();
						} catch (Exception e) {

						}
						if (calcul.getOperator() == ForCalcul.SUM)
							calc[m] += f;
						else if (calcul.getOperator() == ForCalcul.DIFF)
							calc[m] -= f;

					}
				}

			}

			for (int k = 0; k < lgn0.size(); k++) {
				ItemCube it = lgn0.get(k);
				if (it.getType().equalsIgnoreCase("ItemValue")) {

					String calcRes = "";
					Float a = calc[k];
					NumberFormat d = NumberFormat.getCurrencyFormat();
					String res = (d.format(a)).substring(3);
					if (res.charAt(0) == '$') {
						res = res.substring(1);
						calcRes = "-" + res;
						calcRes = calcRes.substring(0, calcRes.length() - 1);
					}
					else {
						calcRes = res;
					}
					Label lbl = new Label(" " + calcRes + " ");
					lbl.addStyleName("gridItemValue");
					if (k == lgn0.size() - 1) {
						lbl.addStyleName("calculLastBorder");
					}
					else {
						lbl.addStyleName("calculBorder");
					}
					grid.setWidget(max, k + 1, lbl);
					lbl.setHeight("30px");
					lbl.setWidth("100%");
					lbl.getElement().getStyle().setPropertyPx("minWidth", grid.getMaxLenght() * 7 - 4);
				}
				else {
					HTML ht = new HTML("&nbsp;");
					ht.addStyleName("gridItemNull");
					ht.addStyleName("calculBorder");
					ht.setHeight("30px");
					ht.setWidth("100%");
					ht.getElement().getStyle().setPropertyPx("minWidth", grid.getMaxLenght() * 7 - 4);
					grid.setWidget(max, k + 1, ht);
				}
			}

		}
		else if (calcul.getOrientation().equalsIgnoreCase(ON_COL)) {
			int max = grid.getCellCount(1);

			TextBox name = new TextBox();
			name.setWidth("100%");
			name.setText(calcul.getTitle());
			// name.addKeyboardListener(new SetCalcName(name, calcul));
			// name.addBlurHandler(new SetCalcName(name, calcul));
			name.addValueChangeHandler(new SetCalcName(name, calcul));

			grid.setWidget(0, max, name);

			String start = (String) calcul.getFields().get(0);
			int s = new Integer(start).intValue();
			for (int i = 0; i < grid.getRowCount(); i++) {
				grid.getCellFormatter().removeStyleName(i, s, "pair");
			}

			s--;

			List<ItemCube> col0 = mainPanel.getInfosReport().getGrid().getCol(s);

			float[] calc = new float[col0.size() + 1];

			for (int k = 1; k < col0.size(); k++) {
				ItemCube itk = col0.get(k);
				if (itk.getType().equalsIgnoreCase("ItemValue")) {
					float fk = 0.0f;
					try {
						fk = itk.getValue();
					} catch (Exception e) {

					}
					calc[k] = fk;
				}
				else {
					calc[k] = 0.0f;
				}
			}

			for (int i = 1; i < calcul.getFields().size(); i++) {
				String kString = (String) calcul.getFields().get(i);
				int n = new Integer(kString).intValue();
				for (int it = 0; it < grid.getRowCount(); it++) {
					grid.getCellFormatter().removeStyleName(it, n, "pair");
				}

				n--;
				List<ItemCube> col = mainPanel.getInfosReport().getGrid().getCol(n);

				for (int m = 1; m < col.size(); m++) {
					ItemCube it = col.get(m);
					if (it.getType().equalsIgnoreCase("ItemValue")) {
						float f = 0.0f;
						try {
							f = it.getValue();
						} catch (Exception e) {

						}
						if (calcul.getOperator() == ForCalcul.SUM)
							calc[m] += f;
						else if (calcul.getOperator() == ForCalcul.DIFF)
							calc[m] -= f;

					}
				}

			}

			for (int k = 0; k < col0.size(); k++) {
				ItemCube it = col0.get(k);
				if (it.getType().equalsIgnoreCase("ItemValue")) {
					String calcRes = "";
					Float a = calc[k];
					NumberFormat d = NumberFormat.getDecimalFormat();
					String res = (d.format(a));//.substring(3);
//					if (res.charAt(0) == '$') {
//						res = res.substring(1);
//						calcRes = "-" + res;
//						calcRes = calcRes.substring(0, calcRes.length() - 1);
//					}
//					else {
						calcRes = res;
//					}
					Label lbl = new Label(" " + calcRes + " ");
					lbl.addStyleName("gridItemValue");
					if (k == col0.size() - 1) {
						lbl.addStyleName("calculLastColBorder");
					}
					else {
						lbl.addStyleName("calculColBorder");
					}
					grid.setWidget(k + 1, max, lbl);
					lbl.setHeight("30px");
					lbl.setWidth("100%");
					lbl.getElement().getStyle().setPropertyPx("minWidth", grid.getMaxLenght() * 7 - 4);
				}
				else {
					HTML ht = new HTML("&nbsp;");
					ht.addStyleName("gridItemNull");
					ht.addStyleName("calculColBorder");
					ht.setHeight("30px");
					ht.setWidth("100%");
					ht.getElement().getStyle().setPropertyPx("minWidth", grid.getMaxLenght() * 7 - 4);
					grid.setWidget(k + 1, max, ht);
				}
			}

		}
		ajustGridSize(calcul.getOrientation(), grid);
	}

	private static void ajustGridSize(String orientation, CubeView grid) {
		if (orientation.equalsIgnoreCase(ON_ROW)) {
			// DOM.setStyleAttribute(grid.getElement(), "height",
			// grid.getOffsetHeight() + 30 + "px");
		}
		else {
			DOM.setStyleAttribute(grid.getElement(), "width", grid.getOffsetWidth() + (7 * grid.getMaxLenght()) + "px");
		}
	}

	public static void distributiveCalcul(MainPanel mainPanel, Calcul calcul, CubeView grid) {

		if (calcul.getOrientation().equalsIgnoreCase(ON_ROW)) {

			int max = grid.getRowCount();

			TextBox name = new TextBox();
			name.setWidth("70px");
			name.setText(calcul.getTitle());
			name.addValueChangeHandler(new SetCalcName(name, calcul));

			grid.setWidget(max, 0, name);

			String start = (String) calcul.getFields().get(0);
			int s = new Integer(start).intValue();
			for (int j = 0; j < grid.getCellCount(s); j++) {
				grid.getCellFormatter().removeStyleName(s, j, "pair");
			}

			s--;
			List<ItemCube> lgn0 = mainPanel.getInfosReport().getGrid().getLigne(s);

			float[] calc = new float[lgn0.size() + 1];

			for (int k = 1; k < lgn0.size(); k++) {
				ItemCube itk = lgn0.get(k);
				if (itk.getType().equalsIgnoreCase("ItemValue")) {
					float fk = 0.0f;
					try {
						fk = itk.getValue();
					} catch (Exception e) {

					}
					calc[k] = fk;
				}
				else {
					calc[k] = 0.0f;
				}
			}

			String kString = (String) calcul.getFields().get(0);

			int n = new Integer(kString).intValue();
			for (int j = 0; j < grid.getCellCount(n); j++) {
				grid.getCellFormatter().removeStyleName(n, j, "pair");
			}
			n--;
			List<ItemCube> lgn = mainPanel.getInfosReport().getGrid().getLigne(n);

			for (int m = 1; m < lgn.size(); m++) {
				ItemCube it = lgn.get(m);
				if (it.getType().equalsIgnoreCase("ItemValue")) {

					if (calcul.getOperator() == ForCalcul.DIV)
						calc[m] = (float) calc[m] / calcul.getConstant();
					else if (calcul.getOperator() == ForCalcul.MUL)
						calc[m] = (float) calc[m] * calcul.getConstant();

				}
			}

			for (int k = 0; k < lgn0.size(); k++) {
				ItemCube it = lgn0.get(k);
				if (it.getType().equalsIgnoreCase("ItemValue")) {
					String calcRes = "";
					Float a = calc[k];
					NumberFormat d = NumberFormat.getCurrencyFormat();
					String res = (d.format(a)).substring(3);
					if (res.charAt(0) == '$') {
						res = res.substring(1);
						calcRes = "-" + res;
						calcRes = calcRes.substring(0, calcRes.length() - 1);
					}
					else {
						calcRes = res;
					}
					Label lbl = new Label(" " + calcRes + " ");
					lbl.addStyleName("gridItemValue");
					if (k == lgn0.size() - 1) {
						lbl.addStyleName("calculLastBorder");
					}
					else {
						lbl.addStyleName("calculBorder");
					}
					grid.setWidget(max, k + 1, lbl);
					lbl.setHeight("30px");
					lbl.setWidth("100%");
					lbl.getElement().getStyle().setPropertyPx("minWidth", grid.getMaxLenght() * 7 - 4);
				}
				else {
					HTML ht = new HTML("&nbsp;");
					ht.addStyleName("gridItemNull");
					ht.addStyleName("calculBorder");
					ht.setHeight("30px");
					ht.setWidth("100%");
					ht.getElement().getStyle().setPropertyPx("minWidth", grid.getMaxLenght() * 7 - 4);
					grid.setWidget(max, k + 1, ht);
				}
			}

			// CalculGrid.selectedRow = new ArrayList();

		}
		else if (calcul.getOrientation().equalsIgnoreCase(ON_COL)) {

			int max = grid.getCellCount(1);

			TextBox name = new TextBox();
			name.setWidth("70px");
			name.setText(calcul.getTitle());
			name.addValueChangeHandler(new SetCalcName(name, calcul));

			grid.setWidget(0, max, name);

			String start = (String) calcul.getFields().get(0);
			int s = new Integer(start).intValue();
			for (int it = 0; it < grid.getRowCount(); it++) {
				grid.getCellFormatter().removeStyleName(it, s, "pair");
			}

			s--;
			List<ItemCube> col0 = mainPanel.getInfosReport().getGrid().getCol(s);

			float[] calc = new float[col0.size() + 1];

			for (int k = 1; k < col0.size(); k++) {
				ItemCube itk = col0.get(k);
				if (itk.getType().equalsIgnoreCase("ItemValue")) {
					float fk = 0.0f;
					try {
						fk = itk.getValue();
					} catch (Exception e) {

					}
					calc[k] = fk;
				}
				else {
					calc[k] = 0.0f;
				}
			}

			String kString = (String) calcul.getFields().get(0);
			int n = new Integer(kString).intValue();
			for (int it = 0; it < grid.getRowCount(); it++) {
				grid.getCellFormatter().removeStyleName(it, n, "pair");
			}

			n--;
			List<ItemCube> col = mainPanel.getInfosReport().getGrid().getCol(n);

			for (int m = 1; m < col.size(); m++) {
				ItemCube it = col.get(m);
				if (it.getType().equalsIgnoreCase("ItemValue")) {
					if (calcul.getOperator() == ForCalcul.DIV)
						calc[m] = (float) calc[m] / calcul.getConstant();
					else if (calcul.getOperator() == ForCalcul.MUL)
						calc[m] = (float) calc[m] * calcul.getConstant();
				}
			}

			for (int k = 0; k < col0.size(); k++) {
				ItemCube it = col0.get(k);
				if (it.getType().equalsIgnoreCase("ItemValue")) {
					String calcRes = "";
					Float a = calc[k];
					NumberFormat d = NumberFormat.getCurrencyFormat();
					String res = (d.format(a)).substring(3);
					if (res.charAt(0) == '$') {
						res = res.substring(1);
						calcRes = "-" + res;
						calcRes = calcRes.substring(0, calcRes.length() - 1);
					}
					else {
						calcRes = res;
					}
					Label lbl = new Label(" " + calcRes + " ");
					lbl.addStyleName("gridItemValue");
					if (k == col0.size() - 1) {
						lbl.addStyleName("calculLastColBorder");
					}
					else {
						lbl.addStyleName("calculColBorder");
					}
					grid.setWidget(k + 1, max, lbl);
					lbl.setHeight("30px");
					lbl.setWidth("100%");
					lbl.getElement().getStyle().setPropertyPx("minWidth", grid.getMaxLenght() * 7 - 4);
				}
				else {
					HTML ht = new HTML("&nbsp;");
					ht.addStyleName("gridItemNull");
					ht.addStyleName("calculColBorder");
					ht.setHeight("30px");
					ht.setWidth("100%");
					ht.getElement().getStyle().setPropertyPx("minWidth", grid.getMaxLenght() * 7 - 4);
					grid.setWidget(k + 1, max, ht);
				}
			}
		}
	}
}
