package bpm.gateway.runtime2.transformations.outputs;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.gateway.core.transformations.outputs.CielComptaOutput;
import bpm.gateway.core.transformations.outputs.IOutput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunCielComptaOutput extends RuntimeStep{

	private PrintWriter writer;
	
	private List<CompteLine> comptes = new ArrayList<>();
	private List<LigneLine> lignes = new ArrayList<>();
	
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
	SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
	
	public RunCielComptaOutput(CielComptaOutput cielComptaOutput, int bufferSize) {
		super(null, cielComptaOutput, bufferSize);
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // restore interrupted status
			}
			return;
		}
		
		Row row = readRow();
		
		if (row == null){
			return;
		}
		CompteLine cl1 = new CompteLine();
		CompteLine cl2 = new CompteLine();
		CompteLine cl3 = new CompteLine();
		LigneLine ll1 = new LigneLine();
		LigneLine ll2 = new LigneLine();
		LigneLine ll3 = new LigneLine();
		for(RuntimeStep rs : inputs) {
			
			Integer index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 0);
			if(index != null) {
				
				Date date = sdf.parse(row.get(index).toString());
				
				Date d = new Date();
				d.setMonth(11);
				d.setDate(31);
				d.setYear(date.getYear());
				cl1.date = sdf2.format(d);
				cl2.date = sdf2.format(d);
				cl3.date = sdf2.format(d);
				ll1.date = sdf2.format(date);
				ll2.date = sdf2.format(date);
				ll3.date = sdf2.format(date);
			}
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 1);
			if(index != null) {
				cl1.numero = String.valueOf(((Double)row.get(index)).longValue());
				ll1.compte = String.valueOf(((Double)row.get(index)).longValue());
			}
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 2);
			if(index != null) {
				cl1.libelle = row.get(index).toString();
			}
			
			
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 3);
			if(index != null) {
				cl2.numero = String.valueOf(((Double)row.get(index)).longValue());
				ll2.compte = String.valueOf(((Double)row.get(index)).longValue());
			}
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 4);
			if(index != null) {
				cl2.libelle = row.get(index).toString();
			}
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 5);
			if(index != null) {
				cl3.numero = String.valueOf(((Double)row.get(index)).longValue());
				ll3.compte = String.valueOf(((Double)row.get(index)).longValue());
			}
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 6);
			if(index != null) {
				cl3.libelle = row.get(index).toString();
			}
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 7);
			if(index != null) {
				ll1.journal = row.get(index).toString();
				ll2.journal = row.get(index).toString();
				ll3.journal = row.get(index).toString();
			}
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 8);
			if(index != null) {
				if(ll1.journal.equals("VT")) {
					try {
						ll2.credit = row.get(index).toString().replace(".", ",");
					} catch(Exception e) {
						ll2.credit = "0,0";
					}
				}
				else if(ll1.journal.equals("HA")) {
					try {
						ll2.debit = row.get(index).toString().replace(".", ",");
					} catch(Exception e) {
						ll2.debit = "0,0";
					}
				}
				else {
					try {
						ll1.debit = row.get(index).toString().replace(".", ",");
						ll2.credit = row.get(index).toString().replace(".", ",");
					} catch(Exception e) {
						ll1.debit = "0,0"; 
						ll2.credit = "0,0";
					}
				}
			}
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 9);
			if(index != null) {
				if(ll1.journal.equals("VT")) {
					try {
						ll1.debit = row.get(index).toString().replace(".", ",");
					} catch(Exception e) {
						ll1.debit = "0,0";
					}
				}
				else if(ll1.journal.equals("HA")) {
					try {
						ll1.credit = row.get(index).toString().replace(".", ",");
					} catch(Exception e) {
						ll1.credit = "0,0";
					}
				}
				else {
					try {
						ll1.debit = row.get(index).toString().replace(".", ",");
						ll2.credit = row.get(index).toString().replace(".", ",");
					} catch(Exception e) {
						ll1.debit = "0,0";
						ll2.credit = "0,0";
					}
				}
			}
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 10);
			if(index != null) {
				if(ll1.journal.equals("VT")) {
					try {
						ll3.credit = row.get(index).toString().replace(".", ",");
					} catch(Exception e) {
						ll3.credit = "0,0";
					}
				}
				if(ll1.journal.equals("HA")) {
					try {
						ll3.debit = row.get(index).toString().replace(".", ",");
					} catch(Exception e) {
						ll3.debit = "0,0";
					}
				}
			}
			
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 11);
			if(index != null) {
				try {
					ll1.libelle = String.valueOf(((Double)row.get(index)).longValue());
					ll2.libelle = String.valueOf(((Double)row.get(index)).longValue());
					ll3.libelle = String.valueOf(((Double)row.get(index)).longValue());
				} catch(Exception e) {
					ll1.libelle = row.get(index).toString();
					ll2.libelle = row.get(index).toString();
					ll3.libelle = row.get(index).toString();
				}
			}
			
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 12);
			if(index != null) {
				try {
					ll1.piece = String.valueOf(((Double)row.get(index)).longValue());
					ll2.piece = String.valueOf(((Double)row.get(index)).longValue());
					ll3.piece = String.valueOf(((Double)row.get(index)).longValue());
				} catch(Exception e) {
					ll1.piece = row.get(index).toString();
					ll2.piece = row.get(index).toString();
					ll3.piece = row.get(index).toString();
				}
			}
			
			index = ((IOutput)transformation).getMappingValueForThisNum(rs.getTransformation(), 13);
			if(index != null) {
				ll1.monnaie = row.get(index).toString();
				ll2.monnaie = row.get(index).toString();
				ll3.monnaie = row.get(index).toString();
			}
			
		}
		
		comptes.add(cl1);
		comptes.add(cl2);
		if(cl3.numero != null && !cl3.numero.isEmpty()) {
			comptes.add(cl3);
		}
		lignes.add(ll1);
		lignes.add(ll2);
		
		if(ll3.compte != null && !ll3.compte.isEmpty()) {
			lignes.add(ll3);
		}
	}
	
	@Override
	protected synchronized void setEnd() {
		writer.write("#Compte\r\n");
		writer.write("#date;numero;libelle\r\n");
		
		for(CompteLine l : comptes) {
			writer.write(l.toString() + "\r\n");
		}
		
		writer.write("#LigneComptable\r\n");
		writer.write("#date;journal;compte;debit;credit;libelle;piece;monnaie\r\n");
		
		for(LigneLine l : lignes) {
			writer.write(l.toString() + "\r\n");
		}
		
		super.setEnd();
	}
	
	private class CompteLine {
		public String date = "";
		public String numero = "";
		public String libelle = "";
		
		@Override
		public String toString() {
			return date + ";" + numero + ";" + libelle;
		}
	}
	
	private class LigneLine {
		public String date = "";
		public String journal = "";
		public String compte = "";
		public String debit = "0.0";
		public String credit = "0.0";
		public String libelle = "";
		public String piece = "";
		public String monnaie = "E";
		
		@Override
		public String toString() {
			return date + ";" + journal + ";" + compte + ";" + debit + ";" + credit + ";" + libelle + ";" + piece + ";" + monnaie;
		}
	}

	@Override
	public void releaseResources() {
		if (writer != null){
			writer.close();
			info(" close writer");
			writer = null;
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		CielComptaOutput csv = (CielComptaOutput)getTransformation();
		String fileName = null;
		try{
			fileName = csv.getDocument().getStringParser().getValue(getTransformation().getDocument(), csv.getDefinition());
		}catch(Exception e){
			error(" error when getting/parsing fileName", e);
			throw e;
		}
		
		File f = new File(fileName);
		if(f.exists()){
			f.delete();
			info(" delete file " + f.getAbsolutePath());
		}
		
		// flag to decide if the Headers Should be Writed or not
		f = new File(fileName);
		if (!f.exists()){
			try{
				f.createNewFile();
				info( " file " + f.getAbsolutePath() + " created");
			}catch(Exception e){
				error(" cannot create file " + f.getName(), e);
				throw e;
			}
		}
		
		try{
			writer = new PrintWriter(f);
			info( " Writer created");
		}catch (Exception e) {
			error(" cannot create writer", e);
		}
		
		isInited = true;
		
	}

}
