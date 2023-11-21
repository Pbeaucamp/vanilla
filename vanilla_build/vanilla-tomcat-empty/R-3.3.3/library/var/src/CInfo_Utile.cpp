#include "../inst/include/CInfo_Utile.h"

using namespace nsUtil;
using namespace std;
using namespace MatrixOperations;

/***********************************************************************************/

CInfo_Utile::CInfo_Utile (const CMatDouble & M, unsigned int &p, unsigned int &h) throw (CException)
  {
  
  if (M[0].size () <= p)
    throw CException ("the value of p must be less than the number of lignes for the Var(p) Model", KVectNullSize);

    // Réorganisation de la matrice d'entrée sous forme d'un vecteur de colonnes.
       //X.Init_Mat (M);
       X = M;
       Interpolated.resize (X.size ());

       for (unsigned int i = 0 ; i < X.size () ; i++)
       {
           for (unsigned int j = 0 ; j < X[0].size () ; j++)
               {
                 if ( isnan (X[i][j]) )
                   {
                       Interpolated[i].push_back ('I');
                   }
                 else Interpolated[i].push_back ('R');
               }
           for (unsigned int j = X[0].size() ; j < X[0].size () + h ; j++)
                    Interpolated[i].push_back ('P');
       }

       X.Interpol () ;
       Nb_Ln = X[0].size () ;
       Nb_Cl = X.size ();
       for (unsigned i = 0 ; i < Nb_Ln ; i++)
         Ind_Confiance.push_back(1);
       
       VarP (p, h, X, Predict, Ind_Confiance);
  }
/***********************************************************************************/

// Constructeur par défault
CInfo_Utile::CInfo_Utile () {}

/***********************************************************************************/

// Différentier un vecteur
void Diff (CVDouble & V)
{
       for (auto it = V.end () -1 ; it != V.begin () ; --it)
               *it -= *(it-1) ;
       V.erase (V.begin ());
}

/***********************************************************************************/

// Stationnariser les séries temporelles
void Differencing (CMatDouble & M)
{
    for_each (M.begin (), M.end (), Diff);
}

/***********************************************************************************/

void Ant_Differencing (CVDouble & W , CVDouble & Y)
{
    for (unsigned int i = 1 ; i < Y.size () ; i++)
        Y[i] = W[i - 1] +  Y[i - 1] ;
}

/***********************************************************************************/

/** Prediction using the VAR(p) model **/

void CInfo_Utile::VarP (unsigned int & p, unsigned int & h, CMatDouble & Input, CMatDouble & Predict, CVDouble & I_Confiance)
{
  unsigned int i, j, N, K;
  double Stdv = 1, Stdv_Value;
  CMatDouble B, A, Futurs, Present, Beta;
  CVDouble Cible, pBeta, one, Futur, MN, Predicted_Cible;
  Predict.clear ();
  Predict = Input;
  K = Nb_Ln;
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
    
    //cout << Present<< endl;
    //cout << B << endl;
    //cout << A << endl;
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
    
    
    //Futurs = A * Beta;
    MultCVDouble (A, Beta, Futurs);
    
    // Evaluation of the prediction using standart deviation
    // Predicted_Cible = B * Beta[0];
    MultCVDouble (B, Beta[0], Predicted_Cible);
    Predicted_Cible.Add(MN[0]);
    Stdv_Value =  pow (Predicted_Cible.StdDev ()  /  Present[0].StdDev () , 2);
    I_Confiance.push_back (Stdv * Stdv_Value);
    Stdv = Stdv_Value;
    
    for (i = 0 ; i < N ; i++)
    {
      Predict[i].push_back (Futurs[i].back () + MN[i]);
    }
    
  } /// End for
  /**************************************************/
}


