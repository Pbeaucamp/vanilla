#ifndef CMATRIXOPERATIONS_H
#define CMATRIXOPERATIONS_H

#include "Operateurs.h"
#include "KM_Disc.h"

namespace MatrixOperations
{
    double Reg              (const CMatDouble & X1, const CVDouble & X2);

    double Reg              (const CVDouble & X1, const CVDouble & X2);

    void regression         (const CMatDouble & , const CVDouble & ,  CVDouble &  ) throw (nsUtil::CException);

    void Reduce             (CMatDouble & , CMatDouble & , double & , CVDouble & );

    void P_Part             (CVDouble & V , CMatDouble & Present, CMatDouble & M, unsigned int & p) ;

    void Pr_Part            (CVDouble & V, CMatDouble & M, unsigned int & p) ;

}

#endif // CMATRIXOPERATIONS_H
