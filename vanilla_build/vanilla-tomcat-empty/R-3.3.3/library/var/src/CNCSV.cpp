/*
 Created by youssef
 15/06/2015

Classe CNCSV :
Lecture des données en entrée
et les retourner avec les prédictions
*/
#include "../inst/include/CNCSV.h"
#include "../inst/include/CVAR_MODEL.h"
using namespace std;

CNCSV::CNCSV (Rcpp::DataFrame DF, unsigned int  p, unsigned int  h)
{ 
  vector<vector<string>> Mat = Rcpp::as < vector<vector<string>> > (DF);
  Dates = Mat[0];
  unsigned Nc = Mat.size ();
  M.resize (Nc - 1);
  
  unsigned i = 1;
  while(i < Nc)
  {
    for (auto Value = Mat[i].begin () ; Value != Mat[i].end () ; ++Value)
    {
      if ((*Value) == "" or (*Value) == "NA")
        M[i - 1].push_back ((double) NAN);
      else
        M[i - 1].push_back (stod (*Value));
    }
    i++;
  }

  /// Prediction  ///
  M.Interpol () ;
  CVAR_MODEL Obj (M, p, h);
    
  /*Obj = CInfo_Utile (M, p, h);*/
  Pred = Obj.getPredicted ();
  Real_Interpolated = Obj.getInterpolated ();
  Indice_Confiance = Obj.getIndConfiance();
  //cout << Real_Interpolated << endl;
  for (unsigned I = 0 ; I < h ; ++I)
  {
    //Date date = Date(Dates[I - 1], '/');
    Dates.push_back (" - -" + to_string(I + 1) + " 00:00");
    //Real_Interpolated.push_back ('P');
  }
  
  Output = Rcpp::DataFrame::create ( Rcpp::Named("Dates") = Dates,
                                     Rcpp::Named("Values") = Pred[0],
                                     Rcpp::Named("Type") = Real_Interpolated[0],
                                     Rcpp::Named("Indice de confiance") = Indice_Confiance
                                     );
}
