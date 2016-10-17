package ihm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFrame;

import metier.Kmeans;
import metier.Spectral;

public class Controleur 
{
	private double[][] data ;
	private double[][] dataClasses ;

	public Controleur() throws IOException
	{
		File file = new File("donnees/norma_N5_tau4_dt2_delai820_000001.txt");
		File fileTraj = new File("trajectoiresBonnes/norma_N5_tau4_dt2_delai820_000001_tra.txt");

		this.data = lireDonnees(file);
		this.dataClasses = lireTrajectoire(data, fileTraj);
		
        /*
        for ( double[] d : dataClasses)
        {
        	for( double v: d)
        	{
        		System.out.print(v+" |||||| ");
        	}
        	System.out.println();
        }
        System.out.println("===========================");
        */
        
        /* ------------- METHODE DES K-MOYENNES --------------*/
        Kmeans kmeans = new Kmeans(data, countLines(file)/5, true);
        kmeans.setEpsilon(0.01);
        kmeans.calculateClusters();
     
        System.out.println("KMEANS = "+getAccuracy(kmeans.getClusters())*10+"%");
        /* ---------------------------------------------------*/
       
        
        /* ------------ CLASSIFICATION SPECTRAL ------------- */
        ArrayList<double[]>[] spectral = Spectral.getClusters(data, countLines(file)/5) ;
       
        System.out.println("SPECTRAL = "+getAccuracy(spectral)*10+"%");
        /* ---------------------------------------------------*/


        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new Graph(data, countLines(file)/5, kmeans, spectral));
        f.setSize(950,930);
        f.setLocation(50,50);

        f.setVisible(true);
	}
	
	private double getAccuracy(ArrayList<double[]>[] prediction)
	{
		double nbClassesTot = dataClasses.length/5 ;
		int nbClassesCorrect = (int) nbClassesTot ;
				
		ArrayList<Integer> mauvaisesClasses = new ArrayList<Integer>();
		
		for ( double[] dataclasse : dataClasses)
        {
			boolean classeBonne = false ;
		
			if ( ! mauvaisesClasses.contains((int)dataclasse[3]) )
			{
				for( double[] datapredict : prediction[(int)dataclasse[3]] )
				{
					//System.out.println(datapredict[0]+" "+datapredict[1]+" "+datapredict[2]);
					//System.out.println(dataclasse[0]+" "+dataclasse[1]+" "+dataclasse[2]);
					if( datapredict[0] == dataclasse[0] && datapredict[1] == dataclasse[1] && datapredict[2] == dataclasse[2] )
						classeBonne = true ;
					
				}
				
				if( !classeBonne )
				{
					mauvaisesClasses.add((int)dataclasse[3]);
					nbClassesCorrect-- ;
				}
			}
        }
		
		return ((double)nbClassesCorrect/(double)nbClassesTot) ;
		
	}

	private double[][] lireTrajectoire(double[][] data2, File fileTraj) throws IOException
	{        
        double[][] trajectoire = new double[countLines(fileTraj)][5];

		FileReader fr = new FileReader(fileTraj);
		BufferedReader br = new BufferedReader(fr);

        int numLine = 0 ;
        for (String line = br.readLine(); line != null; line = br.readLine())
        {
        	int numCol = 0 ;
           	for( String s : line.split(" "))
           	{
           		if( !s.isEmpty() && numCol < 5)
           		{
           			trajectoire[numLine][numCol] = Double.parseDouble(s);
           			
           			numCol++ ;
           		}
           	}
           	
        	numLine++ ;
        }

        br.close();
        fr.close();
		
        double[][] dataClasses = new double[countLines(fileTraj)*5][4]; 
        
        int classe = 0 ;
    	int i = 0 ;

        for( double[] lineTrajectoire : trajectoire)
        {
        	for ( double lineDonnes : lineTrajectoire )
        	{
        		double x = data2[(int)lineDonnes-1][0];
        		double y = data2[(int)lineDonnes-1][1];
        		double z = data2[(int)lineDonnes-1][2];
        		
        		dataClasses[i][0] = x ;
        		dataClasses[i][1] = y ;
        		dataClasses[i][2] = z ;
        		dataClasses[i][3] = classe ;
        		
        		i++ ;
        	}
        	classe++ ;
        }
        
        return dataClasses;
	}

	private double[][] lireDonnees(File file) throws IOException 
	{
		double[][] data = new double[countLines(file)][3];

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
		
        return data;
	}

	private int countLines(File file) throws IOException 
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
	
	public static void main(String[] args) throws IOException
	{
		new Controleur() ;
	}
	
}