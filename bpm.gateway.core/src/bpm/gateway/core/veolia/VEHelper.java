package bpm.gateway.core.veolia;

import java.text.Collator;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.ibm.icu.text.SimpleDateFormat;

public class VEHelper {
	
	public static final String DEFAULT_LIST_VALUE = "NR";
	public static final String DEFAULT_VALUE = "NR";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DEFAULT_DATE = "1901-01-01";
	public static final int DEFAULT_INT = 999999999;
	public static final float DEFAULT_DECIMAL = 999999999.99f;
	
	public static final boolean ENUM_IGNORE_CASE = true;
	public static final boolean ENUM_IGNORE_ACCENT = true;

	public static Date toDate(XMLGregorianCalendar calendar) {
		if (calendar == null) {
			return null;
		}
		GregorianCalendar c = calendar.toGregorianCalendar();
		return c.getTime();
	}

	public static String toDateString(XMLGregorianCalendar calendar) {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime().toString();
	}

	public static int extractYear(XMLGregorianCalendar calendar) {
		if (calendar == null) {
			return 1901;
		}
		return calendar.getYear();
	}

	public static XMLGregorianCalendar getYear(String value) {
		int year = 1901;
		try {
			year = Integer.parseInt(value);
		} catch(Exception e) { }
		
		return getYear(year);
	}

	public static XMLGregorianCalendar getYear(Integer year) {
		if (year != null) {
			GregorianCalendar c = new GregorianCalendar();
			c.set(year, 0, 0);
			try {
				return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
				return null;
			}
		}
		else {
			return null;
		}
	}

	public static XMLGregorianCalendar fromDate(Date date) {
		if (date == null) {
//			SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
//			try {
//				date = format.parse(DEFAULT_DATE);
//			} catch (ParseException e) { }
			return null;
		}

		GregorianCalendar c = new GregorianCalendar();
		if (date.before(new Date(0))) {
			date.setHours(12);
			c.setTime(date);
			c.setTimeZone(TimeZone.getTimeZone("Etc/GMT+0"));
		}
		else {
			c.setTime(date);
		}
		
		try {
			XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			xmlCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			return xmlCalendar;
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static XMLGregorianCalendar fromDateString(String value) {
		if (value == null || value.isEmpty()) {
			value = DEFAULT_DATE;
		}
		
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		try {
			date = format.parse(value);
		} catch (ParseException e) {
			try {
				date = format.parse(DEFAULT_DATE);
			} catch (ParseException e1) { }
		}
		
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean checkEnum(String c, String v) {
		if (ENUM_IGNORE_ACCENT) {
			Collator col = Collator.getInstance();
			col.setStrength(Collator.PRIMARY);
			return col.equals(c, v) || (c.equals(DEFAULT_VALUE) && (v == null || v.trim().isEmpty()));
		}
		return ENUM_IGNORE_CASE && c.equalsIgnoreCase(v) || c.equals(v) || (c.equals(DEFAULT_VALUE) && (v == null || v.isEmpty()));
	}
}
