/*
 * Deux classes sont utilisées pour modéliser le problème :
 * CVDouble : vecteur numérique
 * Classe CMatDouble : vecteur de CVDouble
 */

#ifndef STRUCT_H
#define STRUCT_H

#include <cmath>
#include <vector>
#include <sstream>
#include <iomanip>
#include <algorithm>
#include "CException.h"

//using namespace std;

namespace Struct
{

class Date {
  
private:
  std::string month = " ", day = " ", year = " ",dat, value;
public:
  Date(std::string d, char sep){
    dat = d;
    if (dat.length () < 10)
    {
      value = "nan";
    }
    dat = dat.substr(0, 10);
    std::istringstream iss (dat);
    //std::getline (iss, year, sep);
    //std::getline (iss, month, sep);
    //std::getline (iss, day, sep);
    day+= "1";
    value = year + "-" + month + "-" + day ;
  }
    std::string getDate(){
      return (value);
    }
  };
  
class CVChar : public std::vector<char>
{
public:
    CVChar (unsigned Size) : vector<char> (Size) {}
    CVChar () : vector<char> () {}
};

class CMChar : public std::vector<CVChar>
{
public:
    CMChar (unsigned Size) : vector<CVChar> (Size) {}
    CMChar () : vector<CVChar> () {}
};
class CVDouble : public std::vector<double>
{
public:
    CVDouble            (unsigned Size) : vector<double> (Size) {}
    CVDouble            () : vector<double> () {}
    double Mean         () const    throw (nsUtil::CException);
    bool   Contains     (unsigned & x);
    double CMean        () const    throw (nsUtil::CException);
    double StdDev       () const    throw (nsUtil::CException);
    double Min          () const    throw (nsUtil::CException);
    double Max          () const    throw (nsUtil::CException);
    void Standardise    ()          throw (nsUtil::CException);
    void Normalise  ()              throw (nsUtil::CException);
    void Add            (double &m) throw (nsUtil::CException);
    bool NBR_NAN        () const;
};
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

unsigned int NbreDeCol (std::string ligne); // Nombre de colonnes dans une ligne

void lire_csv (const std::string & FileName , std::vector<std::vector<double>> & T) throw (nsUtil::CException); // Lecture d'un fichier CSV

void permute( CVDouble &X , CVDouble &Y);

CMatDouble Trans(const CMatDouble & M); // Matrice Transposée

bool Trig( CMatDouble & A , CMatDouble & B); // Triangularisation

double Det (const CMatDouble & M); // Determinant

void Resolve(const CMatDouble  & A , const CVDouble  & B, CVDouble & X); // Resolution du systeme A * X = B
    
void Inverse ( const CMatDouble & B, CMatDouble & M) throw (nsUtil::CException); // Matrice Inverse

void Save_Predictions   (CVDouble &, CVChar & , std::ofstream &);

} // namespace Struct

#endif // STRUCT_H
