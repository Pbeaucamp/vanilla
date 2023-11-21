#include "../inst/include/CVAR_MODEL.h"
#include "../inst/include/F_Distribution.h"


CVAR_MODEL::CVAR_MODEL(const CMatDouble & X, unsigned int & ep, unsigned int & eh) throw (CException)
{
  p = ep;
  h = eh;

 // Réorganisation de la matrice d'entrée sous forme d'un vecteur de colonnes.
   Nb_Ln = X[0].size () ;
   Nb_Cl = X.size ();
   
 // Vérifier la valeur de p
   if (Nb_Ln <= p)
     throw CException ("the value of p must be less than the number of lignes for the Var(p) Model", KVectNullSize);
  

 // Memory reservation
    Interpolated.resize (Nb_Cl);
    Ind_Confiance.reserve (Nb_Ln + h);

   for (unsigned i = 0 ; i < Nb_Ln ; i++)
      Ind_Confiance.push_back (1);

  // Linear Interpolation for nul values
     for (unsigned int i = 0 ; i < Nb_Cl; i++)
     {
        Interpolated[i].reserve (Nb_Ln + h);
         for (unsigned int j = 0 ; j < Nb_Ln ; j++)
             {
               if ( isnan (X[i][j]) )
                 {
                     Interpolated[i].push_back ('I');
                 }
               else Interpolated[i].push_back ('R');
             }
         for (unsigned int j = 0 ; j < h ; j++)
                  Interpolated[i].push_back ('P');
     }

  // ///////////////////////////
 
     Predict = X;
     CVDouble Initial_Points (Nb_Cl);
     for (unsigned i = 0 ; i < Nb_Cl ; i++)
       Initial_Points[i] = X[i][0];
     Differencing (Predict);
     //cout << Predict << endl;
     VarP ();
     Ant_Differencing (Predict , Initial_Points);
     //cout << Predict << endl;
}


/****************************************************************/

// Différentier un vecteur
void CVAR_MODEL::Diff (CVDouble & V)
{
  for (auto it = V.end () -1 ; it != V.begin () ; --it)
    *it -= *(it-1) ;
  V.erase (V.begin ());
  //V.insert (V.begin(),1,1);
}

/*****************************************************************/

// Stationnariser les séries temporelle
void CVAR_MODEL::Differencing (CMatDouble & M)
{
  for_each (M.begin () , M.end () , this->Diff);
  
}

/********************************************************************/

void CVAR_MODEL::Ant_Differencing (CMatDouble & W , CVDouble & Y)
{
  for (unsigned int i = 0 ; i < Nb_Cl ; i++)
  {
    W[i].insert (W[i].begin (), 1, Y[i]);
    for (auto it = W[i].begin () + 1 ; it != W[i].end () ; ++it)
      *it += *(it-1) ;
  }
}
/***********************************************************************************/
    //                                                    //
  //             Prediction using the VAR(p) model      //
//                                                    //

void CVAR_MODEL::VarP (void)
{
  unsigned int i, j, N, K;
  double p_value = 0, Stdv_Value, CME, CMT;
  double F_test;
  CMatDouble B, A, Futurs, Present, Beta;
  CVDouble Cible, pBeta, one, Futur, MN, Predicted_Cible;
  
  N = Nb_Cl; // Le nombre d'attribut reste fixe
  // Building N * p attribut acoording to the p previous values of each attribut
  
  for (j = 0 ; j < h ; j++)
  {
    Futur.clear ();
    Predicted_Cible.clear ();
    Futurs.clear ();
    A.clear ();
    B.clear ();
    MN.clear ();
    Beta.clear ();
    Present.clear ();
    one.clear ();
    
    if (p > (int) ( (Nb_Ln + j - 1) / Nb_Cl ))
      p = (int) ( (Nb_Ln + j - 1) / Nb_Cl );
    
    for (i = 1 ; i <= Predict[0].size () - p ; i++)
    {
      one.push_back (1);
    }
    
    A.push_back(one);
    B.push_back(one);
    
    /*************************************************************/
    
    for (i = 0  ; i < N ; i++)
      P_Part (Predict[i], Present, B, p);
    
    for (i = 0  ; i < N ; i++)
      Pr_Part (Predict[i], A, p);
    
    /************* Calculation of the model coefficients  ************/
    
    for (unsigned int l = 0 ; l < N ; l++ )
    {
      Cible = Present [l];
      double mean = Cible.Mean();
      MN.push_back(mean);
      Cible.Standardise ();
      
      // Resolution of the regression problem using OLS : Ordinary Least Squares
      
      regression (B , Cible, pBeta);
      Beta.push_back (pBeta);
      Cible.clear ();
    }
    
    // Futurs = A * Beta;
    MultCVDouble (A, Beta, Futurs);
    
    // Evaluation of the prediction using standart deviation
    // Predicted_Cible = B * Beta[0];
    
    MultCVDouble (B, Beta[0], Predicted_Cible);
    Predicted_Cible.Add(MN[0]);
    //cout << Beta[0] << endl;
    //cout << "Present: " << Present << endl;
    
    //cout <<  Predicted_Cible.Mean() << "Predicted_Cible: " << Present[0].Mean() << endl;
    
    // K the number of independant variables
    K = 0;
    for (double val : Beta[0]) {
      if (val != 0)  K += 1 ; }
    
    // The F-test
    CME = 0, CMT = 0;
    for ( auto sum : Predicted_Cible)
      CME += pow(sum - MN[0] , 2);
    //CME = CME / K;
    
    for ( auto sum : Present[0])
      CMT += pow(sum - MN[0] , 2);
    //CMT = CMT / (Nb_Ln + h - p - K - 1);
    
    Stdv_Value =  CME / CMT;  // adjusted R^2 coefficient
    
    F_test = (Stdv_Value / K)  /  ( (1 - Stdv_Value) / (Nb_Ln + h - p - K - 1) ) ;
    p_value = 1 - (1 - p_value) * (1 - getPvalue (F_test , K , Nb_Ln + h - p - K - 1));
    Ind_Confiance.push_back (p_value);
    //cout << p_value << endl;
    
    for (i = 0 ; i < N ; i++)
    {
      Predict[i].push_back (Futurs[i].back () + MN[i]);
    }
    //cout << "futurs: " << Futurs << endl;
    
  } /// End for
  
}