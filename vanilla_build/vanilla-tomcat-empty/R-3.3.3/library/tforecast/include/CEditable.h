/*!
 * @file CEditable.h
 *
 * @date 11 mars 2012
 * @author    Author: alain
 */

#ifndef CEDITABLE_H_
#define CEDITABLE_H_

#include <ostream>
#include <fstream>

namespace nsUtil
{

    class CEditable;
    std::ostream & operator << (std::ostream & os,
                                const CEditable & Obj);
    class CEditable
    {
      protected :
        virtual std::ostream & _Edit (std::ostream & os) const = 0;

      public :
        virtual ~CEditable (void);
        friend std::ostream & operator << (std::ostream & os, const CEditable & Obj);

    }; // CEditable
}

#include "CEditable.hxx"

#endif /* CEDITABLE_H_ */
