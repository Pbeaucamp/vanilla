/*
 File: Cmatrixoperations.h
Type : namespace
Author: youssef
Date: 22/9/2015
Objetc: multiple regression, reorganization of a matrix, reducing a matrix by elimination dependant columns
 
*/

#include "../inst/include/Cmatrixoperations.h"

using namespace nsUtil;
using namespace std;

namespace MatrixOperations
{


/* Evaluation de la  Regression bidimensionelle : On calcule le coéfficient  de  determination
 * R^2 entre deux colonnes  X1 et  X2  afin  de detecter si il y a une colinéarité entre eux */
    double Reg (const CVDouble & X1, const CVDouble & X2)
    {
        CMatDouble X , B, A;
        CVDouble beta, teta;
        CVDouble predicted;
        CVDouble Y = X2;
        double R = 0.0;
        X.push_back(X1);
        //if (abs (Det (X)) > 0.0)
        // return 0;
        //for (unsigned int i = 0 ; i < X[0].size() ; i++)
        //X[1].push_back (1);
        B = Trans (X) * X;
        MultCVDouble (Trans (X), X, B);
        Y.Standardise ();
        //X.Standardise();
        if (abs (Det (X)) > 0.0000)
        {
            Inverse (B, A);
            //teta = Trans (X) * Y;
            MultCVDouble (Trans (X), Y, teta);
            //beta = A * teta;
            MultCVDouble (A, teta, beta);
            //predicted = X * beta;
            MultCVDouble (X, beta, predicted);
            predicted.Standardise();
            //Y.Standardise ();
            if (Y.StdDev () == 0 && predicted.StdDev() == 0) return 0;
            if (Y.StdDev () == 0 && predicted.StdDev() != 0) return 1;
            R = pow (predicted.StdDev ()  /  Y.StdDev () , 2);
            return 1 - R;
        }
        else return 1;
    }

/***********************************************************************************/

    double Reg (const CMatDouble & X1, const CVDouble & X2)
    {
        CMatDouble X = X1 , B, A;
        CVDouble beta, teta;
        CVDouble predicted;
        CVDouble Y = X2;
        
        //B = Trans (X) * X;
        MultCVDouble (Trans (X), X, B);
        Y.Standardise ();
        
        if (abs (Det (B)) > 0.0001)
        {
            Inverse (B , A);
            //teta = Trans (X) * Y;
            MultCVDouble (Trans (X), Y, teta);
            //beta = A * teta;
            MultCVDouble (A, teta, beta);
            //predicted = X * beta;
            MultCVDouble (X, beta, predicted);
            
            A.clear();
            B.clear();
            teta.clear();
            beta.clear();
            
            if (Y.StdDev () == 0 && predicted.StdDev() == 0) return 1;
            if (Y.StdDev () == 0 && predicted.StdDev() != 0) return 0;
            
            return pow (predicted.StdDev ()  /  Y.StdDev () , 2);
        }
        
        else return 1;
    }
/***********************************************************************************/

/* Régression par la méthode des moindres carrés : résolution matricielle */

    void regression (const CMatDouble & Mn, const CVDouble & Y,  CVDouble & pBeta ) throw (CException)
    {
        using namespace  Clustering;
        
        unsigned int j, i, k, test;
        CMatDouble A, B, M = Mn;
        CVDouble  Beta, Teta;
        CVDouble black_list ;
        double alpha = 0.9;
        pBeta.clear ();
        pBeta.resize (M.size ());
        
        // Reduce M
        Reduce (M, B, alpha, black_list);
        //B = Trans (M) * M;
        MultCVDouble (Trans (M), M, B);
        if (abs(Det(B)) < 1E-16)
            throw CException ("Singular Regression Matrix", KSingularMatrix);
            Inverse (B , A);
            //Beta = Trans (M) * Y ;
            MultCVDouble (Trans (M), Y, Teta);
            //Beta = A * Beta;
            MultCVDouble (A, Teta, Beta);
            
            Teta.clear ();
            A.clear();
            M.clear();
            B.clear();
            
            // Beta globale
            
            if (black_list.size () != 0)
            {
                for (i = 0 ; i < black_list.size () ; ++i)
                {
                    pBeta[black_list[i]] = Beta[i];
                }
            }
            else
                pBeta = Beta;
                Beta.clear();

}
/***********************************************************************************/

/* Elimination des redondances par regression deux à deux avec
 * un coéfficient  de  determination   superieur   à  0.9  */

/*void Reduce (CMatDouble & M, double alpha, CVDouble & list)
{
    unsigned int j, i, x, w, k, N = 0 ;
    CMatDouble  P;
    list.clear ();
    const double KEps = 1E-6;
    
    list.reserve (M.size ());
    for (i = 0 ; i < M.size () ; ++i)
        list.push_back (i);
    
    for (i = 0 ; i < M.size () ; ++i)
        if (abs (M[i].Mean ()) < KEps && M[i].StdDev () < KEps)
        {
            M.erase (M.begin () + i);
            list.erase(list.begin () + i);
        }
    
    
    for (x = 1; x < M.size () ; ++x)
    {
        P.reserve (x);
        //for (w = 0 ; w < x ; ++w)
        //    P[w].reserve (M[0].size ());
        
        for (w = 0 ; w < x ; ++w)
            P.push_back (M[w]);
        
        for (j = x ; j < M.size () ; ++j)
        {
            if (Reg (P , M[j]) > alpha)
            {
                M.erase (M.begin () + j);
                list.erase(list.begin () + j);
            }
            
        } /// end for j
        P.clear ();
    } /// end for x
}*/
/***********************************************************************************/
void Reduce (CMatDouble & M, CMatDouble & B, double & alpha, CVDouble & list)
{
  unsigned int j, i, x, w, k, N = 0 ;
  CMatDouble  P, Dt;
  list.clear ();
  const double KEps = 1E-12;
  
  //M.Standardise();
  list.reserve (1);
  
  P.push_back (M[0]);
  list.push_back (0);
  for (x = 1 ; x < M.size () ; ++x)
  {
    if ( M[x].StdDev () > KEps)
    {
      P.push_back (M[x]);
      MultCVDouble (Trans (P) , P, Dt);
      Dt.Normalise ();
      if ( abs (Det (Dt))  < KEps)
      {
        P.pop_back();
      }
      else
        list.push_back (x);
    } /// end if
  } /// end for
  M.clear();
  M = P;
  //B = Dt;
}
/***********************************************************************************/
/// Building the multidimensional matrix of the Var(p) Model

void P_Part (CVDouble & V , CMatDouble & Present, CMatDouble & M, unsigned int & p)
{
  unsigned int  N = V.size ();
  unsigned int i;
  unsigned int j;
  CVDouble P ;
  
  for (i = p ; i < N ; i++)
  {
    P.push_back (V[i]) ;
  }
  Present.push_back(P);
  P.clear ();
  
  for (j = 1 ; j <= p ; j++)
  {
    for (i = p ; i < N ; i++)
    {
      P.push_back (V[i - j]) ;
    }
    M.push_back (P);
    P.clear ();
  }
  //M = Trans(M);
}

/*********************************************************************************/

/// Building the multidimensional matrix of the Var(p) Model

void Pr_Part (CVDouble & V, CMatDouble & M, unsigned int & p)
{
  unsigned int  N = V.size ();
  unsigned int i;
  unsigned int j;
  CVDouble P ;
  
  for (j = 1 ; j <= p ; j++)
  {
    for (i = p + 1 ; i <= N ; i++)
    {
      P.push_back (V[i - j]) ;
      
    }
    M.push_back (P);
    P.clear ();
  }
}

/***********************************************************************************/

} // namespace matrixoperations
