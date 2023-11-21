#ifndef CNCSV_H
#define CNCSV_H

#include "CInfo_Utile.h"
#include <Rcpp.h>

class CNCSV
{
private:

    /* Vector of string that containt the noums of the attributs */
    std::vector<std::string> Attributs;

    /* Vector of string that containt the dates of the observations */
    std::vector<std::string> Dates;

    CMatDouble M;

    CInfo_Utile Obj;

    CMatDouble Pred;

    CMChar Real_Interpolated;
    
    CVDouble Indice_Confiance;
    
    Rcpp::DataFrame Output; // Output structure

public:

    // Fonctions de lecture des fichiers externes
    friend unsigned int NbreDeCol (std::string ligne);
    friend void lire_csv (const std::string & , std::vector< std::vector<double> > & ) throw (nsUtil::CException);

    // Constructeur
    CNCSV (Rcpp::DataFrame , unsigned int, unsigned int);
    
    Rcpp::DataFrame getOutput () 
    {
      return Output;
    }
    // Constructeur par default
    //CNCSV () {}
};

#endif // CNCSV_H
