#include "../inst/include/Operateurs.h"

using namespace std;

ostream& operator << (ostream & os, const vector<int> &A)
{
  unsigned int j ;
  os << "[1:]";
  for( j = 0 ; j < A.size(); ++j)
    os << setprecision (6) << fixed <<" "<< A[j];
  os << endl;
  return os;
}

ostream& operator << (ostream & os, const CVDouble &A)
{
  unsigned int j ;
  os << "[1:]";
  for( j = 0 ; j < A.size(); ++j)
    os << setprecision (6) << fixed <<" "<< A[j];
  os << endl;
  return os;
}

ostream& operator << (ostream & os, const CMatDouble &A)
{
  int Nblign = A.size();
  int NbColumn = A[0].size();
  
  for( int j = 0 ; j < Nblign ; ++j)
    os<<"[,"<<j+1<<"]  ";
  os<<"\n";
  for( int i = 0 ; i < NbColumn  ; ++i)
  {
    os << "[" << i+1  << ",]";
    for( int j = 0 ; j < Nblign ; ++j)
      os << setprecision(4) << "   " << fixed << A[j][i];
    os<<"\n";
  }
  return os;
}
ostream& operator << (ostream & os, const vector<vector<int>> &A)
{
  int Nblign = A.size();
  int NbColumn = A[0].size();
  
  for( int j = 0 ; j < Nblign ; ++j)
    os<<"[,"<<j+1<<"]  ";
  os<<"\n";
  for( int i = 0 ; i < NbColumn  ; ++i)
  {
    os << "[" << i+1  << ",]";
    for( int j = 0 ; j < Nblign ; ++j)
      os << setprecision(4) << "   " << fixed << A[j][i];
    os<<"\n";
  }
  return os;
}
/***************************************************/
CVDouble operator * (const CVDouble & A, const CVDouble & B)
{
  unsigned int i, n = A.size();
  CVDouble M (n);
  
  for (i = 0 ; i < n ; ++i)
    M[i]  = (A[i] * B[i]) ;
  
  return M;
}
/*****************/
void MultCVDouble (const CVDouble & A, const CVDouble & B, CVDouble & Res)
{
  unsigned int i, n = A.size();
  Res.clear();
  Res.resize (n);
  
  for (i = 0 ; i < n ; ++i)
    Res[i]  = (A[i] * B[i]) ;
}
/***************************************************/
CVDouble operator * (const CMatDouble & A, const CVDouble & B)
{
  unsigned int i , j, n = A[0].size(), m = B.size();
  CVDouble M(n);
  
  for (i = 0 ; i < n ; ++i)
  {
    for (j = 0 ; j < m ; ++j)
    {
      M[i] += (A[j][i] * B[j]) ;
    }
  }
  return M;
}
/******************/
void MultCVDouble (const CMatDouble & A, const CVDouble & B, CVDouble & Res)
{
  unsigned int i , j, n = A[0].size (), m = B.size ();
  Res.clear();
  Res.resize(n);
  
  for (i = 0 ; i < n ; ++i)
  {
    for (j = 0 ; j < m ; ++j)
    {
      Res[i] += (A[j][i] * B[j]) ;
    }
  }
}
/***************************************************/
void MultCVDouble (const CMatDouble & A, const CMatDouble & B, CMatDouble & Res)
{
  unsigned int  n = B.size();
  //Res.clear ();
  Res.resize (n);
  for (unsigned int i = 0 ; i < n ; ++i)
  {
    MultCVDouble (A, B[i], Res[i]);
  }
}

CMatDouble operator * (const CMatDouble & A, const CMatDouble & B)
{
  unsigned int  n = B.size();
  CMatDouble M (n);
  for (unsigned int i = 0 ; i < n ; ++i)
  {
    M[i] = A * B[i];
  }
  return M;
}
