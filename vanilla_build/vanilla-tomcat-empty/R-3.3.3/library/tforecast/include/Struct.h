/*!
 * @file Struct.h
 * @author Youssef Hmamouche
 * @date 06/2015
 * @brief Namespace Struct contains the general classes and functions used in the package
 */

#pragma once

#include <cmath>
#include <vector>
#include <sstream>
#include "CException.h"

//using namespace std;

namespace Struct
{
/*! @brief Classe used to modeilise 1 dimmensionnal data */
class CVDouble : public std::vector<double>
{
public:
    CVDouble            (unsigned Size) : vector<double> (Size) {}
    CVDouble            () : vector<double> () {}
    
    //! The mean of the vector
    double Mean         () const    throw (nsUtil::CException);
    
    /*! \brief bool function returning true if the vector contains the input value
     *  \param x the input value */
    bool   Contains     (unsigned & x);
    
    /*! The mean with the presence of NAN values */
    double CMean        () const    throw (nsUtil::CException);
    
    /*! The standart deviation */
    double StdDev       () const    throw (nsUtil::CException);
    
    /*! The minimum */
    double Min          () const    throw (nsUtil::CException);
    
    //! The maximum
    double Max          () const    throw (nsUtil::CException);
    
    //! Standardisation
    void Standardise    ()          throw (nsUtil::CException);
    
    //! Normalisation
    void Normalise  ()              throw (nsUtil::CException);
    
    //! Add value to each element of the vector
    void Add            (double &m) throw (nsUtil::CException);
    
    //! NBR_NAN function tests if the vector contains NAN values
    bool NBR_NAN        () const;
};
    
/*! @brief Classe used to modelise matrix */
class CMatDouble : public std::vector<CVDouble>
{
public:
    CMatDouble (unsigned Size /* = 0 */) : vector<CVDouble>(Size) {}
    CMatDouble () : vector<CVDouble>() {}
    void Init_Mat( const vector< vector<double> > & M);
    void Standardise  ();
    void Normalise  ();
    void Interpol()               throw (nsUtil::CException);
};
//! Permuter 2 vectors
void permute( CVDouble &X , CVDouble &Y);
    
//! @brief Transpose matrix
CMatDouble Trans(const CMatDouble & M);

//! @brief Triangularisation
bool Trig( CMatDouble & A , CMatDouble & B);

//! @brief Determinant calculation
double Det (const CMatDouble & M);

//! @brief Resolution of the system:  A.X = B
void Resolve(const CMatDouble  & A , const CVDouble  & B, CVDouble & X);
    
//! @brief Inverse matrix
void Inverse ( const CMatDouble & B, CMatDouble & M) throw (nsUtil::CException);

//! String2double conversion
double stod(const std::string &strInput);

double apow(const float &x, int y);
    
//! The first quartile
double Quartil_1 (const CVDouble &T);
 
//! The third quartile
double Quartil_3 (const CVDouble &T);
    
//! @brief Outlier detection using the boxPlot method
void boxPlotOutliersDetection (CMatDouble &M,
                                   unsigned int fstd);

//! @brief Outlier detection using the algebraic method
void algebraicOutlier (CMatDouble & M, unsigned fstd);

} // namespace Struct
