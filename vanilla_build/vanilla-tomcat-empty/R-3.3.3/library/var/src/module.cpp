 
#include <Rcpp.h>
#include "../inst/include/CNCSV.h"

RCPP_MODULE(MVAR ){
  
  using namespace Rcpp ;
  
  class_<CNCSV> ( "CNCSV" )
  .constructor <DataFrame, unsigned int, unsigned int> ()
  .method ("Result", &CNCSV::getOutput)
  ;
}

