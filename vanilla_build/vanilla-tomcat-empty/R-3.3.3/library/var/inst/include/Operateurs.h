#ifndef OPERATEURS_H
#define OPERATEURS_H

#include "Struct.h"
#include <utility>

using namespace Struct ;


// Affichage d'un vecteur
std::ostream& operator << (std::ostream & os, const CVDouble & A);
std::ostream& operator << (std::ostream & os, const std::vector<int> & A);
// Affichege d'une matrice
std::ostream& operator << (std::ostream & os, const CMatDouble &A);
std::ostream& operator << (std::ostream & os, const std::vector<std::vector<int>> &A);

CVDouble operator * (const CVDouble & A, const CVDouble & B);

void MultCVDouble (const CVDouble & A, const CVDouble & B, CVDouble & Res);
void MultCVDouble (const CMatDouble & A, const CVDouble & B, CVDouble & Res);
void MultCVDouble (const CMatDouble & A, const CMatDouble & B, CMatDouble & Res);

// Produit Matrice vecteur
CVDouble operator * (const CMatDouble & A, const CVDouble & B);

// Produit Matricielle
CMatDouble operator * (const CMatDouble & A, const CMatDouble & B);


#endif // OPERATEURS_H
