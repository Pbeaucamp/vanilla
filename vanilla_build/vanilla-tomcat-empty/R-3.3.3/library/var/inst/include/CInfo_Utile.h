#ifndef CINFO_UTILE_H
#define CINFO_UTILE_H

#include "Cmatrixoperations.h"

class CInfo_Utile
{
private:

    CMatDouble Predict ;  // Données initiales + prédiction
    CMatDouble X ;   // Données initiales
    CMChar Interpolated ;
    CVDouble Ind_Confiance;
    unsigned int Nb_Ln ;  // nombre de lignes
    unsigned int Nb_Cl ; // nombre d'attributs

public:
    /* ON construit un objet CInfo_Utile à partir d'une
     * matrice d'entrée sans attrubuts et sans dates */
    CInfo_Utile (const CMatDouble & M, unsigned int &p, unsigned int &h)throw (nsUtil::CException);

    /* Constructeur par défault */
    CInfo_Utile () ;

    CMatDouble getPredicted () {return Predict;}
    CMChar getInterpolated () {return Interpolated;}
    CVDouble getIndConfiance () {return Ind_Confiance;}

    void VarP   (unsigned int  & p, unsigned int & h, CMatDouble & , CMatDouble &, CVDouble & );
};

#endif // CINFO_UTILE_H
