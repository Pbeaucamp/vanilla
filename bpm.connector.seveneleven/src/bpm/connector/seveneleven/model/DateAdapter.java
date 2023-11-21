package bpm.connector.seveneleven.model;

import java.text.SimpleDateFormat;
import java.sql.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {
	
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public Date unmarshal(String date) throws Exception {
        java.util.Date utilDate = sdf.parse(date);
        return new Date(utilDate.getTime());
    }

    @Override
    public String marshal(Date date) throws Exception {
        return sdf.format(date);
    }
 }