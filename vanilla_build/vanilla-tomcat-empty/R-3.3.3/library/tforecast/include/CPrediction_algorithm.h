/*!
 * @file CPrediction_algorithm.h
 *
 * @author youssef hmamouche
 *
 * @date 16/11/2015
 *
 * @brief abstract classe defining the structure of the prediction models used
*/

#pragma once

#include "Struct.h"


class CPrediction_algorithm
{
protected:

    /*! Parameter of the method VAR */
    unsigned int p;
    
    /*! Number of prévisions */
    unsigned int h;
    
    /*! Number of lines  */
    unsigned int Nb_Ln ;
    
    /*! Number of attributs */
    unsigned int Nb_Cl ;
    
    /*! Initiales data plus the predictions */
    Struct::CMatDouble Predict ;
    
    /* Evalutation of the predictions */
    Struct::CVDouble Ind_Confiance;
    

public:

    CPrediction_algorithm (const Struct::CMatDouble &,
                            unsigned int &,
                            unsigned int &)     throw (nsUtil::CException);


    CPrediction_algorithm (void) {}

    ~CPrediction_algorithm (void) {}

    Struct::CMatDouble getPredicted (void) {return Predict;}

    Struct::CVDouble getIndConfiance (void) {return Ind_Confiance;}

};
