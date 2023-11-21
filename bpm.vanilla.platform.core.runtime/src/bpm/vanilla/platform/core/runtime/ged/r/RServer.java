package bpm.vanilla.platform.core.runtime.ged.r;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REXPVector;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.runtime.ged.solr.SolrKeyword;
import bpm.vanilla.platform.core.runtime.ged.solr.SolrMatrix;

public class RServer {

	public HashMap<String, List<String>> solrClassification(SolrMatrix matrice) throws REXPMismatchException, REngineException {

		int i = 0, j = 0;
		HashMap<String, List<String>> newMat = new HashMap<String, List<String>>();
		HashMap<String, List<String>> clustersList = new HashMap<String, List<String>>();
		if (matrice.getNbRows() < 11) {
			return newMat;
		}
		HashMap<Integer, String> serverUrls = new HashMap<Integer, String>();

		int nbServerR = 0;
		while (true) {
			nbServerR++;
			String serverUrl = null;
			try {
				serverUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_SMART_R_SERVER_URL + nbServerR);
				if (serverUrl == null || serverUrl.isEmpty()) {
					break;
				}
				serverUrls.put(nbServerR, serverUrl);
			} catch (Exception e) {
				break;
			}
		}
		nbServerR--;
		
		String url = serverUrls.values().iterator().next();
		
		String host = "";
		int port = 0;
		
    	String[] parts = url.split(":");
    	if(parts.length == 2) {
    		host = parts[0];
    		port = Integer.parseInt(parts[1]);
    	}
    	else {
    		try {
				port = Integer.parseInt(parts[0]);
			} catch (NumberFormatException e) {
				host = parts[0];
			}
    	}
		
		RConnection c = new RConnection(host, port);
		try {

			System.out.println("Connecté");
			// String path =
			// ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.document.management.R.script");
			// REXP r = c.parseAndEval("try(source(\""+ path
			// +"\"),silent=TRUE)");
			REXP r = c.parseAndEval("try(source(\"script_final.R\"),silent=TRUE)");
			if (r.inherits("try-error"))
				System.err.println("Error: " + r.asString());
			c.assign("mat_solr", REXP.createDoubleMatrix(matrice.getBody()));
			c.assign("vect_doc_solr", matrice.getDocIds());
			c.assign("vect_term_solr", matrice.getWords());
			// c.assign("nbr_doc", matrice.getNbRows()+"");

			REXP mat_fin = c.parseAndEval("try(cluster(mat_solr, vect_term_solr, vect_doc_solr),silent=TRUE)");
			if (mat_fin.inherits("try-error"))
				System.err.println("Error: " + mat_fin.asString());

			c.assign("mat_fin", mat_fin);

			REXP lst_mots_cles = c.parseAndEval("try(classification(mat_fin),silent=TRUE)");
			if (lst_mots_cles.inherits("try-error"))
				System.err.println("Error: " + lst_mots_cles.asString());

			c.close();

			// ////// recup vecteur clusters ////////////
			double[][] body = mat_fin.asDoubleMatrix();
			List<String> vectC = new ArrayList<String>();
			int sizeRow = body[0].length;
			for (i = 0; i < body.length; i++) {
				vectC.add((int) (body[i][sizeRow - 1]) + "");
			}

			// ////// recup vecteur docId ////////////
			RList vect = mat_fin._attr().asList();
			String[] docs = ((REXPString) ((REXPGenericVector) vect.get(1)).asList().get(0)).asStrings();
			List<String> vectId = new ArrayList<String>();
			for (i = 0; i < docs.length; i++) {
				vectId.add(docs[i].replace("doc", ""));
			}

			// ///// mots clusters ////////////
			String[] clusters = lst_mots_cles.asStrings();
			for (i = 0; i < clusters.length; i++) {
				j = 0;
				if (clusters[i].contains("?")) {
					String name = clusters[i].replace("?", "");
					List<String> mots = new ArrayList<String>();
					j = i + 1;
					for (j = 1; j <= 30; j++) {
						if (j + i >= clusters.length) {
							break;
						}
						if (clusters[j + i].contains("?")) {
							break;
						}

						mots.add(clusters[j + i]);
					}
					clustersList.put(name, mots);
				}
			}

			// ////// lien doc keyword ///////////
			for (i = 0; i < vectId.size(); i++) {
				String docId = vectId.get(i);
				String docC = vectC.get(i) + "";

				if (clustersList.containsKey(docC)) {
					newMat.put(docId, clustersList.get(docC));
				} else {
					continue;
				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			c.close();
		}
		return newMat;
	}

	public Double[][] solrFunctions(List<List<Double>> varX, List<List<Double>> varY, String function, int nbcluster) throws REXPMismatchException, REngineException, Exception {
		if (function.equals("classKmean"))
			return solrClassificationKMean(varX, varY, nbcluster);
		else if (function.equals("dependenceCorrelation"))
			return solrDepCorrelation(varX);

		return null;
	}

	private Double[][] solrClassificationKMean(List<List<Double>> varX, List<List<Double>> varY, int nbcluster) throws REXPMismatchException, REngineException, Exception {
		int i = 0, j = 0;

		HashMap<Integer, String> serverUrls = new HashMap<Integer, String>();

		int nbServerR = 0;
		while (true) {
			nbServerR++;
			String serverUrl = null;
			try {
				serverUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_SMART_R_SERVER_URL + nbServerR);
				if (serverUrl == null || serverUrl.isEmpty()) {
					break;
				}
				serverUrls.put(nbServerR, serverUrl);
			} catch (Exception e) {
				break;
			}
		}
		nbServerR--;
		
		String url = serverUrls.values().iterator().next();
		
		String host = "";
		int port = 0;
		
    	String[] parts = url.split(":");
    	if(parts.length == 2) {
    		host = parts[0];
    		port = Integer.parseInt(parts[1]);
    	}
    	else {
    		try {
				port = Integer.parseInt(parts[0]);
			} catch (NumberFormatException e) {
				host = parts[0];
			}
    	}
		
		RConnection c = new RConnection(host, port);

		double[] listX = new double[varX.size()];
		int nbVal = 0;
		for (List<Double> values : varX) {
			listX[nbVal] = values.get(0);
			nbVal++;
		}

		double[] listY = new double[varX.size()];
		nbVal = 0;
		for (List<Double> values : varY) {
			listY[nbVal] = values.get(0);
			nbVal++;
		}

		int[] nbClustertab = new int[1];
		nbClustertab[0] = nbcluster;
		
		String path =ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VISUALR_DIRECTORY);
		
		try {

			REXP r = c.parseAndEval("try(source(\"tests_script_method_kmeans.R\"),silent=TRUE)");
			if (r.inherits("try-error"))
				System.err.println("Error: " + r.asString());
			c.assign("col_x", listX);
			c.assign("col_y", listY);
			c.assign("nb_clust", nbClustertab);

			REXP mat_fin = c.parseAndEval("try(cluster_kmeans(col_x, col_y, nb_clust),silent=TRUE)");

			if (mat_fin.inherits("try-error")) {
				throw new Exception("Error: " + mat_fin.asString());
			}
			if (mat_fin instanceof REXPString) {
				throw new Exception("Error: " + ((REXPString) mat_fin).asString());
			}

			c.close();

			// ////// recup vecteur clusters ////////////
			REXPDouble result = (REXPDouble) (mat_fin);

			int nbY = 0;
			int nbClus = 0;
			Double[][] tabResult = new Double[varX.size()][3];
			double[] tab = result.asDoubles();
			for (int nb = 0; nb < tab.length; nb++) {
				if (nb < varX.size())
					tabResult[nb][0] = tab[nb];
				else if (nb < (2 * varX.size())) {
					tabResult[nbY][1] = tab[nb];
					nbY++;
				} else if (nb < (3 * varX.size())) {
					tabResult[nbClus][2] = tab[nb];
					nbClus++;
				}
			}
			return tabResult;
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			c.close();
		}
		return null;
	}

	public Double[][] solrDepCorrelation(List<List<Double>> varX) throws REXPMismatchException, REngineException {
		HashMap<Integer, String> serverUrls = new HashMap<Integer, String>();

		int nbServerR = 0;
		while (true) {
			nbServerR++;
			String serverUrl = null;
			try {
				serverUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_SMART_R_SERVER_URL + nbServerR);
				if (serverUrl == null || serverUrl.isEmpty()) {
					break;
				}
				serverUrls.put(nbServerR, serverUrl);
			} catch (Exception e) {
				break;
			}
		}
		nbServerR--;
		
		String url = serverUrls.values().iterator().next();
		
		String host = "";
		int port = 0;
		
    	String[] parts = url.split(":");
    	if(parts.length == 2) {
    		host = parts[0];
    		port = Integer.parseInt(parts[1]);
    	}
    	else {
    		try {
				port = Integer.parseInt(parts[0]);
			} catch (NumberFormatException e) {
				host = parts[0];
			}
    	}
		
		RConnection c = new RConnection(host, port);

		double[][] listX = new double[varX.size()][varX.get(0).size()];
		int nbVal = 0;
		for (List<Double> values : varX) {
			for (int i = 0; i < values.size(); i++) {
				listX[nbVal][i] = values.get(i);
			}
			nbVal++;
		}
		
		String path =ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VISUALR_DIRECTORY);
		
		try {

			System.out.println("Connecté");

			REXP r = c.parseAndEval("try(source(\"tests_script_method_depand.R\"),silent=TRUE)");
			if (r.inherits("try-error"))
				System.err.println("Error: " + r.asString());
			c.assign("dataset", REXP.createDoubleMatrix(listX));

			REXP mat_fin = c.parseAndEval("try(depend_matcor(dataset),silent=TRUE)");

			if (mat_fin.inherits("try-error"))
				System.err.println("Error: " + mat_fin.asString());
			c.close();

			// ////// recup vecteur clusters ////////////
			REXPDouble result = (REXPDouble) (mat_fin);

			Double[][] tabResult = new Double[varX.get(0).size()][varX.get(0).size()];
			double[] tab = result.asDoubles();
			int nb = 0;

			for (int i = 0; i < varX.get(0).size(); i++) {
				for (int j = 0; j < varX.get(0).size(); j++) {
					if (j < i) {
						tabResult[j][i] = 0.0;
					} else {
						tabResult[j][i] = tab[nb];
					}
					nb++;
				}
			}
			return tabResult;

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			c.close();
		}
		return null;
	}
	
	public String solrDecisionTree(List<List<Double>> varX, List<String> varY, Double train, List<String> names) throws REXPMismatchException, REngineException {
		HashMap<Integer, String> serverUrls = new HashMap<Integer, String>();

		int nbServerR = 0;
		while (true) {
			nbServerR++;
			String serverUrl = null;
			try {
				serverUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_SMART_R_SERVER_URL + nbServerR);
				if (serverUrl == null || serverUrl.isEmpty()) {
					break;
				}
				serverUrls.put(nbServerR, serverUrl);
			} catch (Exception e) {
				break;
			}
		}
		nbServerR--;
		
		String url = serverUrls.values().iterator().next();
		
		String host = "";
		int port = 0;
		
    	String[] parts = url.split(":");
    	if(parts.length == 2) {
    		host = parts[0];
    		port = Integer.parseInt(parts[1]);
    	}
    	else {
    		try {
				port = Integer.parseInt(parts[0]);
			} catch (NumberFormatException e) {
				host = parts[0];
			}
    	}
		
		RConnection c = new RConnection(host, port);

		double[][] listX = new double[varX.size()][varX.get(0).size()];
		int nbVal = 0;
		for (List<Double> values : varX) {
			for (int i = 0; i < values.size(); i++) {
				listX[nbVal][i] = values.get(i);
			}
			nbVal++;
		}
		String[] varTarget = new String[varY.size()];
		for(int i =0; i<varY.size(); i++){
			varTarget[i]=varY.get(i);
		}
		String[] varName = new String[names.size()];
		for(int i =0; i<names.size(); i++){
			varName[i]=names.get(i);
		}
		double[] nTrain = new double[1];
		nTrain[0] =train; 
		
		//String path =ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VISUALR_DIRECTORY);
		String path ="";
		try {
			System.out.println("Connecté");

			REXP r = c.parseAndEval("try(source(\"tests_script_method_decision_tree.R\"),silent=TRUE)");
			if (r.inherits("try-error"))
				System.err.println("Error: " + r.asString());
			c.assign("dataset", REXP.createDoubleMatrix(listX));
			c.assign("var_target", varTarget);
			c.assign("var_names", varName);
			c.assign("n_train", nTrain);
			c.assign("xml_path", path);
			
			REXP mat_fin = c.parseAndEval("try(classif_tree(dataset, var_target, var_names, n_train, xml_path),silent=TRUE)");

			if (mat_fin.inherits("try-error"))
				System.err.println("Error: " + mat_fin.asString());
			c.close();

			// ////// recup vecteur clusters ////////////
			REXPString result = (REXPString) (mat_fin);
			
			String tabResult= result.asString();

			return tabResult;

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			c.close();
		}
		return null;
	}

}
