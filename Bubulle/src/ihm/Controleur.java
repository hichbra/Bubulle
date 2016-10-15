package ihm;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import smile.clustering.SpectralClustering;
import metier.Kmeans;

public class Controleur 
{
	public static void main(String[] args) throws IOException
	{
		File file = new File("donnees/norma_N5_tau4_dt2_delai820_000001.txt");

		double[][] data = new double[Controleur.countLines(file)][3];

	    FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        int numLine = 0 ;
        for (String line = br.readLine(); line != null; line = br.readLine())
        {
        	int numCol = 0 ;
           	for( String s : line.split(" "))
           	{
           		if( !s.isEmpty() && numCol < 3)
           		{
           			data[numLine][numCol] = Double.parseDouble(s);
           			
           			numCol++ ;
           		}
           	}
           	
        	numLine++ ;
        }

        br.close();
        fr.close();
        
        /*
        for ( double[] d : data)
        {
        	for( double v: d)
        	{
        		System.out.print(v+" |||||| ");
        	}
        	System.out.println();
        }
        System.out.println("===========================");
        */
        
        /*
        Kmeans kmeans = new Kmeans(data, 3, true);
        kmeans.calculateClusters();
        */
        /*
        double varMin = 999;
        Kmeans kmeansVarMin = null ;
        double varMax = 0;
        Kmeans kmeansVarMax = null ;
        
        for (int i = 0 ; i <= 1 ; i++)
        {
            Kmeans kmeans = new Kmeans(data, Controleur.countLines(file)/5, true);
            kmeans.calculateClusters();
            if (kmeans.getTotalVar() < varMin)
            {
            	varMin = kmeans.getTotalVar() ;
            	kmeansVarMin = kmeans ;
            }
            
            if (kmeans.getTotalVar() > varMax)
            {
            	varMax = kmeans.getTotalVar() ;
            	kmeansVarMax = kmeans ;
            }
            
            System.out.println(i);

        }
        
        
        JFrame f = new JFrame("kmeansVarMin "+varMin);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new Graph(data, kmeansVarMin));
        f.setSize(950,930);
        f.setLocation(50,50);

        f.setVisible(true);
        
        JFrame f2 = new JFrame("kmeansVarMax "+varMax);
        f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f2.add(new Graph(data, kmeansVarMax));
        f2.setSize(950,930);
        f2.setLocation(50,50);

        f2.setVisible(true);*/    
        
        
        /*JFrame f2 = new JFrame();
        f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f2.add(new Graph(data, kmeans));
        f2.setSize(950,930);
        f2.setLocation(50,50);

        f2.setVisible(true);*/
        
        
        Kmeans kmeans = new Kmeans(data, Controleur.countLines(file)/5, true);
        kmeans.setEpsilon(0.01);
        kmeans.calculateClusters();
     
        
       
        // Les clusters définitifs contiennent 5 points chacunes
        int nbClasses = Controleur.countLines(file)/5 ;
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
  	        	if(sigma > 3)
  	        		augmente = false ;
  	        	else if ( sigma < 0.15 )
  	        		augmente = true ;
  	        	
  	        	if (augmente)
  	        		sigma += 0.01;
  	        	else
  	        		sigma -= 0.01;
  	        	
  	        	//System.out.println("SIGMA = "+sigma);
  	  			System.out.println("TAILLE = "+taille(clustersDefinitifs)+" Cluster="+nbClasses+" ancienne taille = "+memetaille);

  	        }
  	        
  		}
  		

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new Graph(data, Controleur.countLines(file)/5, kmeans, clustersDefinitifs));
        f.setSize(950,930);
        f.setLocation(50,50);

        f.setVisible(true);
	}
	
	
	
	
	private static int taille(ArrayList<double[]>[] clusters) 
	{
		int i = 0 ;
		
		for( ArrayList<double[]> d : clusters)
			if ( d != null )
				i++;
		
		
		return i;
	}

	private static int taille(double[][] data) 
	{
		int i = 0 ;
		
		for( double[] d : data)
		{
			boolean vide = true;
			for ( double v : d )
			{
				vide = true ;
				if ( v != 0 )
					vide = false ;
			}
			
			if( !vide )
				i++;
		}
		return i;
	}


	public static int countLines(File file) throws IOException 
	{
	    InputStream is = new BufferedInputStream(new FileInputStream(file));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	/*
	public static double[][] removeLine(double[][] a, int v) 
	{
	    int r = a.length;
	    int c = a[0].length;

	    double[][] b = new double[r][c];

	    int red = 0;
	    boolean s = false;
	    for (int i = 0; i < r; i++) {
	        for (int j = 0; j < c; j++) {
	            b[i - red][j] = a[i][j];
	            if (a[i][j] == v) {
	                red += 1;
	                if(i==r-1){
	                    s = true;
	                }
	                break;
	            }
	        }
	    }
	    //check last row
	    if(s){
	    for(int i = r-red;i <r-red +1; i++ )
	        for (int j = 0; j<c; j++){
	            b[i][j] = 0;
	        }
	    }
	    return b;
	}*/
	
	public static double[][] removeLine(double[][] a, int v) 
	{
		List<double[]> l = new ArrayList<double[]>(Arrays.asList(a));

		l.remove(v);
		double[][] array2 = l.toArray(new double[][]{});
		
		return array2 ;
		
	}

}