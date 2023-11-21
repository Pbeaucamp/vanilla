/*!
 * @file CPredictionModels.h
 *
 * @author youssef hmamouche
 *
 * @date 28/10/2015
 *
 * @brief Class of the prediction model
 * @param M CMatdouble Matrix with the target attribut on the first colonne
 * @param p The lag parametre of the models VAR/VECM
 * @param h The number of the previsions wanted
 */

#pragma once

#include "CPrediction_algorithm.h"

class CVAR_MODEL:public CPrediction_algorithm
{
    
public:
    CVAR_MODEL (const Struct::CMatDouble &,
                unsigned int     &,
                unsigned int     &);
    std::vector<std::vector<double> > temporelMatrix;
    
    //! Vectors Differentiation
    static void Diff (Struct::CVDouble & V);
    
    //! Matrix Differentiation
    void Differencing (Struct::CMatDouble & M);
    
    // Reconstruire une matrice après differentiation
    void Ant_Differencing (Struct::CMatDouble & W , Struct::CVDouble & Y);
    
    //! The Vector Error Correction model
    void VECM ();
    
    // VARIMA
    void VARIMA ();
    
    //! Resolution of the VAR system
    void VAR (void);
    
    //! The Vector Auto-Regressive model
    void VARModel (void);
};

