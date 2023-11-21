
/**!
 *
 * @file    CstCodErr.h
 *
 * @authors M. Laporte, D. Mathieu
 *
 * @date    21/04/2010
 *
 * @version V2.0
 *
 * @brief   Codes d'erreurs
 *
 **/
#ifndef CSTCODERR_H
#define CSTCODERR_H

namespace nsUtil
{
    enum {KNoExc          = 0,
          KNoError        = 0,

          KExcOpInterdite = 101,      // operation interdite

          KFileNotFound 	= 102, 		//fichier non trouvé
          KBoundOff			= 103,

          KVectNullSize       = 200,
          KVectSizeOne        = 201,
          KStdDevEqualToZero  = 202,
          KKurtosisImp        = 203,
          KSingularMatrix     = 204,

          KCsvFileError       = 220,

          KErrArg         = 253,      // erreur des arguments (nombre ou types)
          KExcStd         = 254,
          KExcInconnue    = 255
         };

} // namespace nsUtil


#endif // CSTCODERR_H
