package org.fasd.cubewizard.dimension;

import org.fasd.olap.OLAPLevel;
import org.fasd.utils.trees.TreeLevel;
import org.fasd.utils.trees.TreeParent;

public class DateTools {

	private static final String DATE_YEAR = "year"; //$NON-NLS-1$
	private static final String DATE_SEMESTER_NAME = "semester name"; //$NON-NLS-1$
	private static final String DATE_SEMESTER_NUMBER = "semester number"; //$NON-NLS-1$
	private static final String DATE_QUARTER_NAME = "quarter name"; //$NON-NLS-1$
	private static final String DATE_QUARTER_NUMBER = "quarter number"; //$NON-NLS-1$
	private static final String DATE_MONTH_NUMBER = "month number"; //$NON-NLS-1$
	private static final String DATE_MONTH_NAME = "month name"; //$NON-NLS-1$
	private static final String DATE_WEEK_YEAR = "week of year"; //$NON-NLS-1$
	private static final String DATE_WEEK_MONTH = "week of month"; //$NON-NLS-1$
	private static final String DATE_DAY_YEAR = "day of year"; //$NON-NLS-1$
	private static final String DATE_DAY_MONTH = "day of month"; //$NON-NLS-1$
	private static final String DATE_DAY_WEEK = "day of week"; //$NON-NLS-1$
	private static final String DATE_DAY_NAME = "day name"; //$NON-NLS-1$
	private static final String DATE_HOUR = "hour"; //$NON-NLS-1$
	private static final String DATE_MINUTE = "minute"; //$NON-NLS-1$
	private static final String DATE_SECOND = "second"; //$NON-NLS-1$

	private static final String[] DATE_CATEGORIES = { "Year", "Semester", "Quarter", "Month", "Week", "Day", "Hour", "Minute", "Second" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$

	private static final String[][] DATE_SUBCATEGORIES = { { DATE_YEAR }, { DATE_SEMESTER_NAME, DATE_SEMESTER_NUMBER }, { DATE_QUARTER_NAME, DATE_QUARTER_NUMBER }, { DATE_MONTH_NUMBER, DATE_MONTH_NAME }, { DATE_WEEK_YEAR, DATE_WEEK_MONTH }, { DATE_DAY_YEAR, DATE_DAY_MONTH, DATE_DAY_WEEK, DATE_DAY_NAME }, { DATE_HOUR }, { DATE_MINUTE }, { DATE_SECOND } };

	public static TreeParent createInputLevels() {
		TreeParent root = new TreeParent("Levels"); //$NON-NLS-1$

		for (int i = 0; i < DATE_CATEGORIES.length; i++) {
			TreeParent treeCat = new TreeParent(DATE_CATEGORIES[i]);

			for (int j = 0; j < DATE_SUBCATEGORIES[i].length; j++) {
				OLAPLevel level = new OLAPLevel(DATE_SUBCATEGORIES[i][j]);
				level.setCaption(DATE_SUBCATEGORIES[i][j]);
				level.setDateColumnPart(DATE_SUBCATEGORIES[i][j]);
				TreeLevel tLevel = new TreeLevel(level);
				treeCat.addChild(tLevel);
			}

			root.addChild(treeCat);
		}

		return root;
	}

	public static String[] getOrders() {
		return new String[] { "NONE", DATE_YEAR, DATE_SEMESTER_NUMBER, DATE_QUARTER_NUMBER, DATE_MONTH_NUMBER, DATE_WEEK_YEAR, DATE_DAY_YEAR, DATE_HOUR, DATE_MINUTE, DATE_SECOND }; //$NON-NLS-1$
	}

}
