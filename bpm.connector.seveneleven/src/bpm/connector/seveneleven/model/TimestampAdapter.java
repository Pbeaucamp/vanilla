package bpm.connector.seveneleven.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class TimestampAdapter extends XmlAdapter<String, Timestamp> {
	
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public Timestamp unmarshal(String date) throws Exception {
        java.util.Date utilDate = sdf.parse(date);
        return new java.sql.Timestamp(utilDate.getTime());
    }

    @Override
    public String marshal(Timestamp date) throws Exception {
        return sdf.format(date);
    }
 }