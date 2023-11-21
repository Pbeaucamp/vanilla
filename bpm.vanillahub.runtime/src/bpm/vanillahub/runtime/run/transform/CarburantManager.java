package bpm.vanillahub.runtime.run.transform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import bpm.vanillahub.core.beans.managers.TransformManager;

public class CarburantManager implements TransformManager {

	public static final String CONNECTOR_NAME = "CarburantManager";

	public ByteArrayInputStream buildFile(ByteArrayInputStream parentStream) throws Exception {
		return buildCSV(parentStream);

//		new CibleHelper(runner, locale, cible, fileName, parentStream, parameters, variables);
//		return new ConnectorResult(Result.SUCCESS, null, 1);
	}

	private ByteArrayInputStream buildCSV(InputStream inputStream) throws JAXBException, IOException {
		JAXBContext jaxbContext = JAXBContext.newInstance(PointDeVentes.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        PointDeVentes pointDeVentes = (PointDeVentes) unmarshaller.unmarshal(inputStream);
        
        ByteArrayOutputStream is = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(is, Charset.forName("UTF-8"));
		
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withDelimiter(";".charAt(0));
		CSVPrinter p = new CSVPrinter(writer, csvFileFormat);

		List<List<Serializable>> values = new ArrayList<>();
		List<Serializable> head = new ArrayList<>();
		head.add("pdv_id");
		head.add("latitude");
		head.add("longitude");
		head.add("cp");
		head.add("pop");
		head.add("adresse");
		head.add("ville");
		head.add("prix_id");
		head.add("prix_nom");
		head.add("prix_maj");
		head.add("prix_valeur");
		values.add(head);

		for (PointDeVente item : pointDeVentes.getPointDeVentes()) {
			Double latitude = null;
			try {
				latitude = Double.parseDouble(item.getLatitude()) / 100000;
			} catch (Exception e) {
				e.printStackTrace();
			}
			Double longitude = null;
			try {
				longitude = Double.parseDouble(item.getLongitude()) / 100000;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<Serializable> pdvLine = new ArrayList<>();
			pdvLine.add(item.getId());
			pdvLine.add(latitude);
			pdvLine.add(longitude);
			pdvLine.add(new String(item.getCp().getBytes("UTF-8"), "UTF-8"));
			pdvLine.add(new String(item.getPop().getBytes("UTF-8"), "UTF-8"));
			pdvLine.add(new String(item.getAdresse().replace("\"", "").getBytes("UTF-8"), "UTF-8"));
			pdvLine.add(new String(item.getVille().getBytes("UTF-8"), "UTF-8"));
			
			if (item.getPrix() != null && !item.getPrix().isEmpty()) {
				for (Prix prix : item.getPrix()) {
					List<Serializable> line = new ArrayList<>();
					line.addAll(pdvLine);
					line.add(prix.getId());
					line.add(new String(prix.getNom().getBytes("UTF-8"), "UTF-8"));
					line.add(prix.getMaj());
					line.add(prix.getValeur() / 1000);
					
					values.add(line);
				}
			}
			else {
				List<Serializable> line = new ArrayList<>();
				line.addAll(pdvLine);
				line.add(null);
				line.add(null);
				line.add(null);
				line.add(null);
				
				values.add(line);
			}
		}

		p.printRecords(values);
		p.close();
		return new ByteArrayInputStream(is.toByteArray());
	}

}
