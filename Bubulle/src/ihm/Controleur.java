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

public class Controleur 
{
	public static void main(String[] args) throws IOException
	{
		File file = new File("/home/etudiant/bh110413/Bureau/Coordonnees_mm/norma_N5_tau4_dt2_delai820_000001.txt");
		//File file = new File("/home/etudiant/bh110413/Bureau/a.txt");

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
        
        for ( double[] d : data)
        {
        	for( double v: d)
        	{
        		System.out.print(v+" |||||| ");
        	}
        	System.out.println();
        }
        System.out.println("===========================");
        
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
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new Graph(data, kmeans));
        f.setSize(950,930);
        f.setLocation(50,50);

        f.setVisible(true);
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
}