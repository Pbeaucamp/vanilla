#include "../inst/include/Struct.h"
using namespace nsUtil;
using namespace std;

namespace Struct
{
    /***********************************************************************************/
    double CVDouble::Mean () const throw (CException)
    {
        if (this->size () == 0)
            throw CException ("Vector of size null", KVectNullSize);
        double Sum = 0;
        for (auto it = this->begin() ; it!= this->end() ; ++it)
            Sum += (*it);
        return Sum / this->size();
    };
    /***********************************************************************************/
    bool CVDouble::Contains (unsigned & x)
    {
        if (this->size () == 0) return false;
        bool test = false;
        for (auto Val = this->begin() ; Val != this->end() ; ++Val)
            if (*Val == x)
            {
                test = true;
                break;
            }
        return test;
    }
    /***********************************************************************************/
    double CVDouble::CMean () const throw (CException)
    {
        if (this->size () == 0)
            throw CException ("Vector of size null", KVectNullSize);
        
        double m = 0.0;
        int c = 0;
        for (auto it = this->begin() ; it!= this->end() ; ++it)
        {
            if ( ! isnan (*it) )
            {
                m += (*it);
                ++c;
            }
        }
        return m / c;
    }
    
    /***********************************************************************************/
    
    double CVDouble::StdDev () const throw (CException)
    {
        if (0 == this->size ())
            throw CException ("Vector of size null", KVectNullSize);
        double Sum = 0.0, Mean = this->Mean();
        for( auto it = this->begin() ; it != this->end() ; ++it)
        {
            Sum += pow (*it - Mean, 2);
        }
        return sqrt(Sum / (this->size ()));
    };
    
    /***********************************************************************************/
    
    double CVDouble::Min () const throw (CException)
    {
        if (0 == this->size ())
            throw CException ("Vector of size null", KVectNullSize);
        double min = *(this->begin());
        for (auto Val = this->begin() ; Val != this->end() ; ++Val)
            if ( *Val < min)
                min = *Val;
        return min;
    };
    
    /***********************************************************************************/
    
    double CVDouble::Max () const throw (CException)
    {
        if (0 == this->size ())
            throw CException ("Vector of size null", KVectNullSize);
        double max = *(this->begin());
        for (auto Val = this->begin() ; Val != this->end() ; ++Val)
            if (*Val > max)
                max = *Val;
        return max;
    };
    
    /***********************************************************************************/
    
    void   CVDouble::Standardise () throw (CException)
    {
        if (0 == this->size ())
            throw CException ("Vector of size null", KVectNullSize);
        double Mean = this->Mean ();
        if (Mean != 0 && this->StdDev() > 1E-6)
            for (auto it = this->begin () ; it != this->end () ; ++it)
                (*it) = (*it) - Mean;
        
    };
    
    /***********************************************************************************/
    void   CVDouble::Normalise () throw (CException)
    {
        if (0 == this->size ())
            throw CException ("Vector of size null", KVectNullSize);
        double Min = this->Min ();
        double Max = this->Max ();
        if (abs (Max) > abs (Min))
        for (auto it = this->begin () ; it != this->end () ; ++it)
            (*it) = (*it) / Max;
        if (abs (Max) < abs (Min))
            for (auto it = this->begin () ; it != this->end () ; ++it)
                (*it) = (*it) / Min;
        
    };
    
    /***********************************************************************************/
    
    void CVDouble::Add  (double & m)  throw (CException)
    {
        for (auto it = this->begin () ; it != this->end () ; ++it)
            (*it) += m;
    }
    
    /***********************************************************************************/
    
    /* Nombre de points virgules dans un objet string */
    unsigned int NbreDeCol (std::string ligne)
    {
        unsigned int max = 1;
        for (const auto & val : ligne)
            if (val == ';')
                max++ ;
        return (max);
    }
    
    /***********************************************************************************/
    
    /* Lecture du fichier csv et enregistrement les données dans un tableau 2 dimensions */
    void lire_csv (const string & FileName , vector<vector<double>> & T) throw (CException)
    {
        unsigned int Nbln = 0, Nbrcol;
        unsigned int i,j = 0, h = 0;
        vector<string> ligne;
        string lign,p,s;
        ifstream ifs(FileName);
        
        if ( !ifs )
            throw CException ("Impossible d'ouvrir le fichier", KCsvFileError);
            
            for (getline(ifs, lign); ifs; getline(ifs, lign))
            {
                if(Nbln > 0)
                    ligne.push_back (lign);
                    Nbln++;
            }
        Nbln -= 1;
        
        Nbrcol = NbreDeCol(ligne[0]);
        for( i = 0 ; i < Nbln ; i++)
        {
            if (NbreDeCol(ligne[i]) != Nbrcol)
                throw CException ("Error in csv file structure", KCsvFileError);
                s = "", p = "";
                h = 0;
                T.push_back (vector<double> (Nbrcol) );
                
            /*for ( auto & val : ligne[i])
             {
             if (val != ';' && j < ligne[i].size () - 1)
             {
             s += val;
             p = s;
             }
             if (val == ';' || j == ligne[i].size () - 1)
             {
             if (s != "")
             T[i][h] = stod (s);
             if (s == "")
             T[i][h] = (double) NAN ;
             s = "";
             h++;
             }
             j++;
             }
             }*/
                
                for (j = 0 ; j < ligne[i].size () - 1 ; ++j)
                {
                    if (ligne[i][j] != ';')
                    {
                        s += ligne[i][j];
                        p = s;
                    }
                    if (ligne[i][j] == ';')
                    {
                        if (s != "")
                            T[i][h] = stod (s);
                            if (s == "")
                                T[i][h] = (double) NAN ;
                                s = "";
                                h++;
                    }
                }
            if ( s == "")
                T[i][h] = (double) NAN ;
                else
                    T[i][h] = stod(p);
                    }
    };
    
    //void lire_csv (const string & FileName,  vector<vector<double>>& Mat)
    //{
    //    ifstream ifs(FileName);
    //    if (!ifs.is_open()) return;
    
    //    vector <double> VLine;
    //    string StrTmp, lign;
    //    double DTmp;
    //    //la ligne de headers
    //    getline(ifs, lign);
    //    unsigned NbComa (0);
    //    size_t Pos (0);
    //    for (Pos = lign.find ( ";", Pos) ; Pos != string::npos ; Pos = lign.find ( ";", Pos))
    //    {
    //        ++Pos;
    //        ++NbComa;
    //    }
    
    //    //cout << NbComa << endl;
    
    
    //    //for (getline(ifs, lign); ifs; getline(ifs, lign))
    //    //{
    //getline(ifs, lign);
    //        size_t Ind1, Ind2;
    //        Ind1 = lign.find ( ";");
    //        cout << Ind1 << endl;
    
    //        for (unsigned i (1); i < NbComa - 2 ; ++i)
    //        {
    //            Ind2 = lign.find ( ";", Ind1 + 1);
    //            Ind1 = lign.find ( ";", Ind2 + 1);
    
    //            cout << Ind2 << ' '<<  Ind1 << flush << endl;
    //             cout << lign.substr(Ind2, Ind1) << flush << endl;
    //        }
    
    /*istringstream iss (lign);
     cout << lign << endl;
     getline (iss, StrTmp, ';');
     for (getline (iss, StrTmp, ';');iss; getline (iss, StrTmp, ';'))
     {
     
     if (0 == StrTmp.size ()) DTmp = MAXDOUBLE;
     else {
     DTmp = stod (StrTmp);
     }
     VLine.push_back(DTmp);
     cout << setw (10) << DTmp << flush;
     }
     cout << endl;
     Mat.push_back(VLine);*/
    // }
    //}
    /* Construire la matrice transposée X organisée par colonnes
     *  à partir d'une matrice  organisée  par lignes */
    
    
    /***********************************************************************************/
    
    void CMatDouble::Init_Mat( const vector< vector <double> > & M)
    {
        unsigned int i,j;
        this->clear ();
        unsigned int Nblign = M.size ();
        unsigned int NbColumn = M[0].size () - 1;
        
        this->resize (NbColumn);
        for( j = 0 ; j < NbColumn ; ++j)
        {
            (*this) [j] = CVDouble (Nblign);
            for( i = 0 ; i < Nblign  ; ++i)
                (*this) [j][i] = M[i][j+1];
        }
    };
    
    /***********************************************************************************/
    
    // Verification si un vecteur contient une valeur nan
    bool CVDouble::NBR_NAN() const
    {
        bool test = 1;
        for (const auto & val : (*this) )
            if (isnan (val) )
            {
                test = 0;
                break;
            }
        return test;
    }
    
    /***********************************************************************************/
    
    // Interpolation linéaire
    void CMatDouble::Interpol () throw (CException)
    {
        if (this->size () == 0)
            throw CException ("Vector of size null", KVectNullSize);
        double moy ;
        CVDouble T ;
        for (auto it = this->begin () ; it!= this->end () ; ++it)
        {
            if(it->NBR_NAN() == 0)
            {
                moy = it->CMean ();
                T.clear ();
                for (auto val = it->begin () ; val!= it->end () ; ++val)
                {
                    if (! isnan(*val)) /// Si la première valeure est null, on l'affecte la mayenne du vecteur
                        T.push_back(*val);
                    if (isnan(*val) && T.size  () == 0)
                        *val = moy;
                    if (isnan(*val) && T.size  () != 0)
                        *val = T.Mean ();
                }
            }
        }
    }
    
    /***********************************************************************************/
    
    void CMatDouble::Standardise ()
    {
        for( auto it = this->begin() ; it!= this->end() ; ++it)
            it->Standardise ();
    }
    
    /***********************************************************************************/
    void CMatDouble::Normalise ()
    {
        for( auto it = this->begin() ; it!= this->end() ; ++it)
            it->Normalise ();
    }
    
    /***********************************************************************************/
    
    CMatDouble Trans (const CMatDouble & M)
    {
        unsigned int n = M.size(), m = M[0].size ();
        CMatDouble T (m);
        for (unsigned int i = 0 ; i < m ; ++i)
        {
            T[i] = CVDouble (n);
            for (unsigned int j = 0 ; j < n ; ++j)
                T[i][j] = M[j][i];
        }
        return T;
    }
    
    /***********************************************************************************/
    
    void permute( CVDouble &X , CVDouble &Y)
    {
        double a;
        for ( unsigned int k = 0 ; k < X.size() ; ++k)
        {
            a = X[k];
            X[k] = Y[k];
            Y[k] = a;
        }
        
    };
    
    /***********************************************************************************/
    
    bool Trig (CMatDouble & A ,  CMatDouble & B)
    {
        double pivot = 1, coef=0, max ;
        unsigned int i,j,k,l, indice_max;
        unsigned int n = A.size();
        vector<double> T, P;
        
        // Pivot de Gauss
        // Reorganisation de la matrice
        for ( k = 0 ; k < n-1 ; ++k)
        {
            T.resize(0);
            P.resize(0);
            indice_max = k, max = A[k][k];
            for( l = k+1 ; l < n ; l++)
            {
                if (abs(A[l][k]) > abs(max) )
                {
                    max = A[l][k];
                    indice_max = l;
                }
            }
            if(indice_max > k)
            {
                permute(B[k] , B[indice_max]);
                permute(A[k] , A[indice_max]);
            }
            // Affectation du pivot
            pivot = A[k][k];
            try
            {
                if( pivot == 0 )
                {
                    throw string("ERREUR : Matrice non reversible!");
                }
            }
            catch(string const& chaine)
            {
                cout<<chaine<<endl;
                return 0;
            }
            // Triangularisation de la matrice A
            for ( i = k+1 ; i < n ; ++i)
            {
                coef = A[i][k];
                for ( j = 0 ; j < n ; ++j)
                {
                    A[i][j] -= (coef * A[k][j] / pivot);
                    B[i][j] -= (B[k][j] * coef / pivot);
                }
            }
            
        } // Matrice Triangulaire
        try
        {
            if( A[n-1][n-1] == 0 )
            {
                throw string("ERREUR : Matrice non révérsible!");
            }
        }
        catch(string const& chaine)
        {
            cout<<chaine<<endl;
            return 0;
        }
        B = Trans(B);
        return 1;
    }
    
    /***********************************************************************************/
    
    double Det ( const CMatDouble & M)
    {
        CMatDouble A = M;
        double pivot = 1, coef = 0, max, det = 1 ;
        unsigned int i, j, k, l, indice_max;
        unsigned int n = A.size ();
        
        // Pivot de Gauss
        for (k = 0 ; k < n-1 ; ++k)
        {
            indice_max = k, max = A[k][k];
            for (l = k+1 ; l < n ; l++)
            {
                if (abs (A[l][k]) > abs (max) )
                {
                    max = A[l][k];
                    indice_max = l;
                }
            }
            if(indice_max > k)
            {
                permute(A[k] , A[indice_max]);
            }
            pivot = A[k][k];
            if (pivot == 0)
                return 0.0;
            det *= pivot;
            
            // Triangularisation de la matrice A
            for ( i = k+1 ; i < n ; ++i)
            {
                coef = A[i][k];
                for ( j = 0 ; j < n ; ++j)
                    A[i][j] -= (coef * A[k][j] / pivot);
            }
        } // Matrice Triangulaire
        det *= A[n-1][n-1];
        return det;
    }
    
    /***********************************************************************************/
    
    // Resolution du système linéaire A*X = B
    void Resolve(const CMatDouble  & A , const CVDouble  & B, CVDouble & X)
    {
        double somme = 0;
        int ind = 0, j;
        unsigned int n = B.size();
        X.clear ();
        X.resize (n);
        // Resolution du système linéaire
        X[n - 1] = B[n-1]/A[n - 1][n - 1];
        for (ind = n-2 ; ind >= 0 ; ind--)
        {
            somme = B[ind];
            for (j = ind + 1 ; j < (int) n ; ++j)
                somme -= A[ind][j] * X[j];
            X[ind]  =  somme / A[ind][ind];
        }
    }
    
    /***********************************************************************************/
    
    // Matrice Inverse
    void Inverse ( const CMatDouble & B, CMatDouble & M) throw (CException)
    {
        bool a;
        unsigned int i , j , n = B.size();
        CMatDouble I(n), A = B;
        
        M.clear ();
        M.resize (n);
        
        for (i = 0 ; i < n ; ++i)
        {
            M[i] = CVDouble (n);
            I[i] = CVDouble (n);
            for (j = 0 ; j < n ; ++j)
            {
                if ( i == j) I[i][j] = 1;
                    else I[i][j] = 0;
                        }
        }
        a = Trig(A,I);
        if( a == 1)
        {
            for (i = 0 ; i < n ; ++i)
            {
                Resolve(A , I[i], M[i]);
            }
        }
        else
        {
            throw CException ("Singular Matrix", KSingularMatrix);
        }
        I.clear ();
    }
    
    /*****************  Save results on extrenal file   ****************************************/
    
    void Save_Predictions (CVDouble & M ,CVChar & Interpol, CVDouble & Ind_Conf, ofstream & file)
    {
        unsigned int N = M.size  ();
        
        for (unsigned int i = 0 ; i < N ; i++)
        {
            file << M[i] << ";" << Interpol[i] << ";" << Ind_Conf[i] << fixed << endl;
        }
    }
    /***********************************************************************************/
} // namespace Struct
