package bpm.sqldesigner.ui.utils;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.swt.graphics.FontMetrics;

import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.ui.figure.constants.Fonts;

public class LayoutUtils {
	
	public static int[] getTableDimension(Table table){
		int lMax = 40;
		int l = 0;
		int keyNumber = 0;
		FontMetrics fm = FigureUtilities.getFontMetrics(Fonts.normalSimple);	
		
		HashMap<String, Column> hmColumns = table.getColumns();
		Iterator<String> it = hmColumns.keySet().iterator();
		while (it.hasNext()) {
			Column c = hmColumns.get(it.next());
			l = (c.getName() + "---" + c.getType()).length(); //$NON-NLS-1$

			if (c.isPrimaryKey())
				keyNumber++;

			if (l > lMax)
				lMax = l;
		}
		int width = fm.getAverageCharWidth() * lMax - 100;

		int height = (fm.getHeight() + 2) * hmColumns.size() + 20 + 2
				* keyNumber;
		
		return new int[]{width,height};
	}

}
