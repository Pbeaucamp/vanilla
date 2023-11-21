package bpm.connector.seveneleven.model;

import java.sql.Time;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimeAdapter extends XmlAdapter<String, Time> {
	
    private static SimpleDateFormat sdf = new SimpleDateFormat();

    @Override
    public Time unmarshal(String date) throws Exception {
        java.util.Date utilDate = sdf.parse(date);
        return new Time(utilDate.getTime());
    }

    @Override
    public String marshal(Time date) throws Exception {
        return sdf.format(date);
    }
 }