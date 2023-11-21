package bpm.gwt.commons.client.utils;

public class MonthYear {

	private int month;
	private int year;

	public MonthYear(int month, int year) {
		this.month = month;
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public MonthYear getPrevious() {
		if (month > 1) {
			return new MonthYear(month - 1, year);
		}
		else {
			return new MonthYear(12, year - 1);
		}
	}

	public MonthYear getNext() {
		if (month < 12) {
			return new MonthYear(month + 1, year);
		}
		else {
			return new MonthYear(1, year + 1);
		}
	}

	@Override
	public String toString() {
		return Tools.getMonth(month) + " " + year;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MonthYear) {
			return month == ((MonthYear) obj).getMonth() && year == ((MonthYear) obj).getYear();
		}
		return false;
	}
}