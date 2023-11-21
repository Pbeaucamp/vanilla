/*
 * CEditable.hxx
 *
 *  Created on: 11 mars 2012
 *      Author: alain
 */

#ifndef CEDITABLE_HXX_
#define CEDITABLE_HXX_

//#include "CEditable.h"


inline std::ostream & nsUtil::operator << (std::ostream & os, const CEditable & Obj)
{
    return Obj._Edit (os);

} // operator <<()

inline nsUtil::CEditable::~CEditable () {}

#endif /* CEDITABLE_HXX_ */
