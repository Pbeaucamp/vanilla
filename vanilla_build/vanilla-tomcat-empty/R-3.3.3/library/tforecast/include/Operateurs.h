/*!
 * @file Operateurs.h
 * @author Youssef Hmamouche
 * @date 06/2015
 * @brief Fonctions utiles
 */

#pragma once

#include <iomanip> //setprecision

#include "Struct.h"


using namespace Struct ;


//! Show vector
/*template <typename Vect>
std::ostream& operator << (std::ostream & os, const Vect & A) {
    unsigned int j ;
    os << "[1:]";
    for( j = 0 ; j < A.size(); ++j)
        os << std::setprecision (6) << std::fixed <<" "<< A[j];
    os << std::endl;
    return os;
}*/

//! Show Matrix
template <typename Mat>
void Show (const Mat & A){
    int Nblign = A.size();
    int NbColumn = A[0].size();
    
    std::cout << "     ";
    for( int j = 0 ; j < Nblign ; ++j)
        std::cout << "   " << "[," << j+1 << "]  ";
    std::cout <<"\n";
    for( int i = 0 ; i < NbColumn  ; ++i)
    {
        std::cout << "[" << i+1  << ",]";
        for( int j = 0 ; j < Nblign ; ++j)
            std::cout << std::setprecision(4) << "   " << std::fixed << A[j][i];
        std::cout <<"\n";
    }
}

/*!  \brief Vectors multipication
 *   \param A The fist vector
 *   \param B The second vector
 *   \param Res The result
 *   \return the vector res = A.B
 */
void MultCVDouble (const CVDouble & A, const CVDouble & B, CVDouble & Res);

/*!  \brief Matrix-Vectors multipication
 *   \param A The Matrix
 *   \param B The vector
 *   \param Res The result
 *   \return The vector res = A.B
 */
void MultCVDouble (const CMatDouble & A, const CVDouble & B, CVDouble & Res);

/*!  \brief Matrix multipication
 *   \param A The fist matrix
 *   \param B The second matrix
 *   \param Res The result
 *   \return the matrix Res A.B
 */
void MultCVDouble (const CMatDouble & A, const CMatDouble & B, CMatDouble & Res);
