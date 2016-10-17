package metier;

import ihm.Controleur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import smile.clustering.SpectralClustering;

public class Spectral 
{

	public static ArrayList<double[]>[] getClusters(double[][] data, int nbClasses)
	{
		 // Les clusters définitifs contiennent 5 points chacunes

        double sigma = 1 ; boolean augmente = true ;
        ArrayList<double[]>[] clustersDefinitifs = new ArrayList[nbClasses];

  		
  		// Tant que toutes les classes ne contienne pas 5 éléments : on enregistre celle en contienne 5
  		// puis on les supprime du jeu de donnees pour recommencer la classification sans elle
  		int stagne = 0 ;
  		int memetaille = taille(clustersDefinitifs) ;
        while ( nbClasses > 1 )
  		{
  	        SpectralClustering s = new SpectralClustering(data, nbClasses, sigma);
  	        ArrayList<double[]>[] clusters = new ArrayList[nbClasses];

  			
  			// Regroupement des classes par labels 
  	  		for (int i = 0 ; i < nbClasses ; i++) 
  	  		{
  	  			ArrayList<double[]> classes = new ArrayList<double[]>();
  	  			
  	  			for ( int ligne = 0 ; ligne < data.length ; ligne++)
  	  			{
  	  				if ( s.getClusterLabel()[ligne] == i )
  	  					classes.add(data[ligne]);
  	  			}
  	  			
  	  			clusters[i] = classes ;
  	  		}
  			
  			// ENREGISTREMENT ET SUPPRESSION
  	        for( ArrayList<double[]> d : clusters)
  	        {	 	    	    
  	    	    
  	    	    // Enregistrement et suppression du jeu de donnes des classes contenant 5 éléments
  	    	    if ( d.size() == 5 )
  	    	    {  	    	    	
  	    	    	clustersDefinitifs[taille(clustersDefinitifs)] = d;
  	    	    	
  	    	    	// Suppression des donnees de la base
  	    	    	double x=-1, y=-1, z=-1 ;
  	    	    	for(double[] val : d)
  	    	    	{
  	    	    		x = val[0];
  	    	    		y = val[1];
  	    	    		z = val[2];
  	    	    		
  	  	    	    	//System.out.print(x+" "+y+" "+z);

  	    	    		int ligne = 0 ;
  	  	    	    	for ( double[] line : data)
  	  	    	    	{
  	  	    	    		if ( line[0] == x && line[1] == y && line[2] == z )
  	  	    	    			data = removeLine(data, ligne);
  	  	    	    		
  	  	    	    		ligne++;
  	  	    	    	}
  	    	    	}
  	    	    	
  	    	    	nbClasses--;

  	    	    }
  	        }
  	        
  			
  	        if ( taille(clustersDefinitifs) == memetaille )
  	        	stagne++ ;
  	        else
  	        	memetaille = taille(clustersDefinitifs) ;
  	        
  	        if ( stagne % 10 == 0 )
  	        {
  	        	if(sigma > 3 && stagne < 20000)
  	        		augmente = false ;
  	        	else if ( sigma < 0.15 )
  	        		augmente = true ;
  	        	else if ( sigma > 10 )
  	        		augmente = false ;
  	        	
  	        	if (augmente)
  	        		sigma += 0.01;
  	        	else
  	        		sigma -= 0.01;
  	        	
  	        	/*
  	        	System.out.println("SIGMA = "+sigma);
  	  			System.out.println("TAILLE = "+taille(clustersDefinitifs)+" Cluster="+nbClasses);
  	  			*/
  	        }
  	        
  		}
  		
        return clustersDefinitifs ;
	}



	private static int taille(ArrayList<double[]>[] clusters) 
	{
		int i = 0 ;
		
		for( ArrayList<double[]> d : clusters)
			if ( d != null )
				i++;
		
		return i;
	}
	
	private static double[][] removeLine(double[][] a, int v) 
	{
		List<double[]> l = new ArrayList<double[]>(Arrays.asList(a));

		l.remove(v);
		double[][] array2 = l.toArray(new double[][]{});
		
		return array2 ;
		
	}
}
