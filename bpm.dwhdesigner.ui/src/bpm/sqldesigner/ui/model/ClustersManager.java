package bpm.sqldesigner.ui.model;

 public class ClustersManager {

     private static final ListClusters INSTANCE = new ListClusters();
 

     private ClustersManager() {}
 
     public static ListClusters getInstance() {
         return INSTANCE;
     }
 }
