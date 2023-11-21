/*
 File: CVAR_MODEL.h
 Type : Classe
 Author: youssef
 Date: 28/10/2015
 Objetc: Implementation of the model "Vector Auto-Regression Model (VAR)"
 */

#ifndef CVAR_MODEL_H
#define CVAR_MODEL_H

#include "Cmatrixoperations.h"

using namespace nsUtil;
using namespace std;
using namespace MatrixOperations;

class CVAR_MODEL
{
private:

    unsigned int p;           // Paramètre p du modèle VAR
    unsigned int h;           // Nombre de prévisions
    unsigned int Nb_Ln ;      // nombre de lignes
    unsigned int Nb_Cl ;      // nombre d'attributs
    CMatDouble Predict ;      // Données initiales avec prédictions
    CMatDouble X ;            // Données initiales
    CMChar Interpolated ;     // Vecteur indiquant les valeurs interpolées
    CVDouble Ind_Confiance;

public:
 
    CVAR_MODEL (const CMatDouble & ,
                unsigned int &, 
                unsigned int &) throw (nsUtil::CException);
  
    CMatDouble getPredicted () {return Predict;}
    CMChar getInterpolated () {return Interpolated;}
    CVDouble getIndConfiance () {return Ind_Confiance;}
    static void Diff (CVDouble & V);
    void Differencing (CMatDouble & M);
    void Ant_Differencing (CMatDouble & W , CVDouble & Y);
    void VarP (void);
};

#endif // VAR_MODEL_H
