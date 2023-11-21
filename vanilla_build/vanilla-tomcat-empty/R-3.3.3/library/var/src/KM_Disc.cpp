
/*
 * FILE : KM_Disc.cpp
 * OBJECT : the K-Means Discretization algorithm
 * DATE : 19/6/2014
 * AUTHOR(S) :
 *		Christian   Ernst		- ernst@emse.fr
 * MOST SIGNIFICANT UPDATES (date/author/description)
 *
 */


#include "../inst/include/KM_Disc.h"

namespace Clustering
{

	// distance between two points
    Distance distance(const Point& x, const Point& y)
	{
		Distance total = 0.0;
		Distance diff;
    
		Point::const_iterator cpx = x.begin(); 
		Point::const_iterator cpy = y.begin();
		Point::const_iterator cpx_end = x.end();
		for (; cpx != cpx_end; ++cpx, ++cpy)
		{
			diff = *cpx - *cpy;
			total += (diff * diff); 
		}
		return total;  // no need to take sqrt, which is monotonic
	}

	// Init randomly collection of points
	void PointsSpace::init_points_rdy() 
	{ 
        for (PointId i = 0; i < num_points__; i++)
		{
			Point p;
			for (Dimensions d = 0; d < num_dimensions__; d++)
			{ 
				p.push_back( rand() % 100 ); 
			}
			points__.push_back(p);

			// std::cout << "pid[" << i << "]= (" << p << ")" << endl;
		}
	}

	// Init collection of points with a vector of (one-dimension) points
    void PointsSpace::init_points_vec (Struct::CVDouble vec)
	{
        for (PointId i = 0; i < num_points__; i++)
		{  
			  Point p;
			  p.push_back(vec[i]); 
			  points__.push_back(p);
			}
			// std::cout << "pid[" << i << "]= (" << p << ")" << endl;
	}


	// Zero centroids
	void Clusters::zero_centroids() 
	{
		for_each (centroids__.begin(), centroids__.end(), [](Centroids::value_type& centroid)
		{
			for_each (centroid.begin(), centroid.end(), [](Point::value_type& d)
			{
				d = 0.0;
			});
		});
	}

	// Compute Centroids
	void Clusters::compute_centroids () 
	{
		ClusterId cid = 0;

		// for_each centroid
		for_each (centroids__.begin(), centroids__.end(), [this, &cid](Centroids::value_type& centroid)
		{
			PointId num_points_in_cluster = 0;
			Dimensions i;

			// modification effectuee le 01/08/2014
			for (i = 0; i < num_dimensions__; i++)
					centroid[i] = 0;
			//*********
			// For each PointId in this set
			for (auto it = clusters_to_points__[cid].begin(); it != clusters_to_points__[cid].end(); it++)
			{
				Point p = ps__.getPoint(*it);
				for (i = 0; i < num_dimensions__; i++)
					centroid[i] += p[i];	
				num_points_in_cluster++;
			}

			// if no point in the clusters, this goes to inf (correct!)
			for (i = 0; i < num_dimensions__; i++)
				centroid[i] /= num_points_in_cluster;	
			cid++;
		});
	}

	// Initial partition points among available clusters
	void Clusters::initial_partition_points ()
	{
		ClusterId cid;
    
		for (PointId pid = 0; pid < ps__.getNumPoints(); pid++)
		{
            cid = pid % num_clusters__;
			points_to_clusters__[pid] = cid;
			clusters_to_points__[cid].insert(pid);
		}
	}


	// k-means
	void Clusters::k_means (void)
	{
		bool move;
		bool some_point_is_moving = true;
		unsigned int num_iterations = 0;
		PointId pid;
		ClusterId cid, to_cluster;
		Distance d, min;
		//std::cout << clusters_to_points__;

		// Until not converge
		while (some_point_is_moving && num_iterations < 1000)
		{
			//cout << endl << "*** Num Iterations " << num_iterations  << endl << endl;

			some_point_is_moving = false;
			compute_centroids();
			//cout<<num_iterations<<endl;
			//cout << "Centroids" << endl << centroids__;      

			// for_each point
			for (pid = 0; pid < num_points__; pid++)
			{    
				// distance from current cluster
				min = Clustering::distance(centroids__[points_to_clusters__[pid]], ps__.getPoint(pid));

 				// cout << "pid[" << pid << "] in cluster=" << points_to_clusters__[pid] << " with distance=" << min << endl;

				// for_each centroid
				cid = 0; 
				move = false;
				for_each (centroids__.begin(), centroids__.end(), 
					[this, &d, pid, &min, &move, &to_cluster, &cid, &some_point_is_moving](Centroids::value_type c)
				{   
					d = Clustering::distance(c, ps__.getPoint(pid));
					if (d < min)
					{  
						min = d;
						move = true;
						to_cluster = cid;
						some_point_is_moving = true;
						
						// cout << "\tcluster=" << cid << " closer, dist=" << d << endl;
					}
					cid++;
					
				});		// end for_each
	
				// move towards a closer centroid 
				if (move)
				{   
					// remove from current cluster
						clusters_to_points__[points_to_clusters__[pid]].erase(pid);
					// insert
					points_to_clusters__[pid] = to_cluster;
					clusters_to_points__[to_cluster].insert(pid);
					compute_centroids(); // modification
					// std::cout << "\t\tmove to cluster=" << to_cluster << std::endl;
				}
			}      

			num_iterations++;
		} // end while (some_point_is_moving)

		//std::cout << std::endl << "Final clusters" << std::endl;
		//std::cout << clusters_to_points__;
	}

  
};	// end namespace

