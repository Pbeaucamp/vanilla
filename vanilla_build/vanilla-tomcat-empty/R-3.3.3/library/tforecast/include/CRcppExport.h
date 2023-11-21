/*!
 * @file CRcppExport.h
 *
 * @author youssef hmamouche
 *
 * @date 15/06/2015
 *
 * @brief Main classe of the module MVAR, allowing the interaction with Rcpp objects and
 * returning the prediction results.
 *
 */

#pragma once


#include <Rcpp.h>

#include "CPredictionModels.h"

class CRcppExport
{
private:

    /*! Vector of string that contains the attributs of the input time series */
    std::vector<std::string> Attributs;

    /*! Vector of string that contains the dates of the input time series */
    std::vector<std::string> Dates;

    Struct::CMatDouble M;

    Struct::CMatDouble Pred;
    
    Struct::CVDouble Indice_Confiance;
    
    Rcpp::DataFrame Output; // Output dataframe
    
    std::vector<std::vector<double> > tempMat;

public:

    // Constructor
    CRcppExport (Rcpp::DataFrame , unsigned int, unsigned int);
    
    /*! getOutput () returns original data of the target attribut plus the predictions and
     *  there evaluations */
    Rcpp::DataFrame getOutput () 
    {
      return Output;
    }
    
    //! getOutput () returns the temporal matrix of the input series
    Rcpp::DataFrame temporelMatrix ();
};

