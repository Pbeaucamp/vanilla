/*!
 * @file CRegression.h
 *
 * @author Youssef Hmamouche
 *
 * @date 22/9/2015
 *
 * @brief Namespace regLib containing the following functionnalities : multiple regression function,
 * coefficient of
 * determination function, building the temporal matrix, reducing the matrix size by finding
 * the dependant columns.
 *
 */

#pragma once

#include "Operateurs.h"

namespace regLib
{
    double Reg              (const CMatDouble & X1, const CVDouble & X2);

    /*! @brief The determination coefficient beteween to vectors */
    double Reg              (const CVDouble & X1, const CVDouble & X2);
    
    
    /*! @brief Multiple regression function of the system Y = Mn*pBeta.
     *  The function finds the coefficents using Least Squares method,
     * and store them on the input vector pBeta.
     * @param Mn Matrix of endogenous variables
     * @param Y Target attribut
     * @param pBeta  the coefficents
     */
    void regression         (const CMatDouble & Mn, const CVDouble & Y,  CVDouble & pBeta  ) throw (nsUtil::CException);

    /*! @brief Reduce the number of columns in such way to have inversible matrix,
     * in other words, to have purely independant columns or : rang(Matrix) = ncol (Matrix)
     * @param M  The Matrix that we want to reduce
     * @param list Vector in which we store the indices of columns that we deleted
     */
    void Reduce             (CMatDouble & M, CVDouble & list);
    
    /*! @brief Temporal decomposition with p lags without the last date
    *  example : P_Part (V,M,p) -> M.push_back (V(t-1), V(t-2), ....., V(t-p)) */
    void P_Part             (CVDouble & V , CMatDouble & Present, CMatDouble & M, unsigned int & p) ;

    /*! @brief Temporal decomposition with p lags with the last date
     *  example : Pr_Part (V,M,p) -> M.push_back (V(t), V(t-1), ....., V(t-p+1)) */
    void Pr_Part            (CVDouble & V, CMatDouble & M, unsigned int & p) ;

}

