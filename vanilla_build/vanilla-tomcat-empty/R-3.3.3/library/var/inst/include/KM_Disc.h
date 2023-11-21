
/*
 * FILE : KM_Disc.h
 * OBJECT : the K-Means Discretization algorithm (header file)
 * DATE : 19/6/2014
 * AUTHOR(S) :
 *		Christian   Ernst		- ernst@emse.fr
 * MOST SIGNIFICANT UPDATES (date/author/description)
 *
 */

#ifndef KM_DISC_HEADER
#define KM_DISC_HEADER

#include "struct.h"
#include <set>
#include <algorithm>

using namespace std;


namespace Clustering 
{

	typedef double Coord;            // a coordinate
	typedef double Distance;         // distance
	typedef unsigned int Dimensions; // how many dimensions
	typedef unsigned int PointId;    // the id of this point
	typedef unsigned int ClusterId;  // the id of this cluster

	typedef std::vector<Coord> Point;    // a point (a centroid)
	typedef std::vector<Point> Points;   // collection of points

	typedef std::set<PointId> SetPoints; // set of points
  
	// ClusterId -> (PointId, PointId, PointId, .... )
	typedef std::vector<SetPoints> ClustersToPoints;
	// PointId -> ClusterId
	typedef std::vector<ClusterId> PointsToClusters; 
	// coll of centroids
	typedef std::vector<Point> Centroids;


	// This class stores all the points available in the model
	//
	class PointsSpace
	{

	public :

		PointsSpace(PointId num_points, Dimensions num_dimensions) 
			: num_points__(num_points), num_dimensions__(num_dimensions)
		{
			init_points_rdy ();
		}

        PointsSpace(PointId num_points, Struct::CVDouble vec)
			: num_points__(num_points), num_dimensions__(1)
		{
			init_points_vec (vec);
		}

		inline const PointId getNumPoints() const {return num_points__;}
		inline const PointId getNumDimensions() const {return num_dimensions__;}
		inline const Point& getPoint(PointId pid) const { return points__[pid];}
 
	private :

	    // Init randomly collection of points
		void init_points_rdy ();

		// Init collection of points with a vector of (one-dimension) points
        void init_points_vec (Struct::CVDouble vec);
    
		// data
		PointId num_points__;
		Dimensions num_dimensions__;
		Points points__;
	};

  
	  // 
	  //  This class represents a cluster
	// 
	class Clusters 
	{

		private:
   
			ClusterId num_clusters__;    // number of clusters
			PointsSpace& ps__;           // the point space
			Dimensions num_dimensions__; // the dimensions of vectors
			PointId num_points__;        // total number of points
			ClustersToPoints clusters_to_points__;
			PointsToClusters points_to_clusters__;
			Centroids centroids__;

			// Initialize centroids
			void zero_centroids();     

			// Compute centroids
			void compute_centroids();     
    
	public:

		// Affichage des points-au-clusters 
		 vector<int>clustering_vector(void)
		{  
			vector<int>P;
			for(unsigned int i = 0; i < num_points__; i++)
				P.push_back(points_to_clusters__[i]+1);
				//cout<<points_to_clusters__[i]+1<<" ";
			    //cout<<endl;
			return P;
		}
    
			// Initial partition points among available clusters
			void initial_partition_points();

			Clusters(ClusterId num_clusters, PointsSpace &ps) 
				: 	num_clusters__(num_clusters), 
					ps__(ps), 
					num_dimensions__(ps.getNumDimensions()),
					num_points__(ps.getNumPoints()),
					points_to_clusters__(num_points__, 0)
			{
				ClusterId i = 0;
				Dimensions dim;
		
				for (; i < num_clusters; i++)
				{
					Point point;   // each centroid is a point
					for (dim = 0; dim < num_dimensions__; dim++) 
						point.push_back(0.0);
				
					SetPoints set_of_points;

					// init centroids
					centroids__.push_back(point);  

					// init clusterId -> set of points
					clusters_to_points__.push_back(set_of_points);
					// init point <- cluster, nothing todo
				}
			};
    
		// k-means
		void k_means (void);
  };

};	// end namespace


#endif
