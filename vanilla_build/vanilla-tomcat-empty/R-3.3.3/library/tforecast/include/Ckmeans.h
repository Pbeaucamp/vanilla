/*!
 * @file Ckmeans.h
 *
 * @author youssef hmamouche
 *
 * @date december, 2015
 *
 * @brief Main classe of the module Classify, allowing the interaction with Rcpp objects and
 * returning the clustering vector as Rcpp object.
 *
 */

#pragma once

#include <Rcpp.h>

#include "KM_Disc.h"


using namespace Clustering;
using namespace Rcpp;

class Classify
{

private:
  
  Struct::CMatDouble M;
  unsigned int nbreClasses_;
  unsigned int nbreIter_;
  vector<int> ClVector;
  
public:
  
  /*! @brief Classification model (constructor)
   * @param DF numerical dataFrame that we want to classify
   * @param nbreClasses The number of clusters
   * @param nbreIter The max number of iterations
   */
    
  Classify (Rcpp::DataFrame DF, unsigned int nbreClasses, unsigned int nbreIter):
          nbreClasses_ (nbreClasses),
          nbreIter_ (nbreIter)
       {
       std::vector <std::vector <double> > Mat = Rcpp::as < std::vector<vector<double> > > (DF);
       unsigned int i,j;
       unsigned int Nblign = Mat.size ();
       unsigned int NbColumn = Mat[0].size ();
       
       M. resize (Nblign);
       for( i = 0 ; i < Nblign  ; ++i)
       {
         M [i]. resize (NbColumn);
         for( j = 0 ; j < NbColumn ; ++j)
           M [i][j] = Mat [i][j];
       }

        M.Interpol ();
        PointsSpace P (M[0].size (), M);
        ClusterId num_clusters = (unsigned int) nbreClasses_;
        Clusters classifier (num_clusters , P);
        classifier.initial_partition_points();
        classifier.k_means (nbreIter_);
        ClVector = classifier.clustering_vector();
       }
    
  //! ClusteringVector () returns the integer vector containing clusters associated to each point
  Rcpp::IntegerVector ClusteringVector (void)
  {
    Rcpp::IntegerVector Vcet (M[0].size ());
    for (unsigned i = 0 ; i < M[0].size () ; ++i)
      Vcet(i) = ClVector[i];
    return Vcet;
  }
};






