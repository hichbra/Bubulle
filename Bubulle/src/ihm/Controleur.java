package ihm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import metier.Kmeans;
import metier.Spectral;

public class Controleur 
{
	private double[][] data ;
	private int nbClasse ;
	private double[][] dataClasses ;
	 
	private JFrame frame ;
	private Graph graph ;
	 
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fichier = new JMenu("Fichier");
  	private JMenu affichage = new JMenu("Affichage");
  	private JMenu animation = new JMenu("Animation");
  	private JMenu algo = new JMenu("Algorithme");
  	//private JMenu spectral = new JMenu("Classification Spectral");


  	private JMenuItem fermer = new JMenuItem("Fermer");
  	private JMenuItem importer = new JMenuItem("Importer");
  	
  	private JMenuItem couleur = new JMenuItem("Couleur");
  	private JMenuItem lien = new JMenuItem("Lien");
  	private JMenuItem valeur = new JMenuItem("Valeur");

  	private JMenuItem xy = new JMenuItem("Axes XY");
  	private JMenuItem xz = new JMenuItem("Axes XZ");
  	private JMenuItem yz = new JMenuItem("Axes YZ");
  	private JMenuItem play = new JMenuItem("Play");
  	private JMenuItem stop = new JMenuItem("Stop");
  	
  	private JMenuItem kmean = new JMenuItem("K-Means");
  	private JMenuItem spectral = new JMenuItem("Spectral");
  	
  	
  	public Controleur() throws IOException
	{
  		frame = new JFrame();
  		graph = null ;
  		nbClasse = -1 ;
  		/*
		File file = new File("donnees/norma_N5_tau4_dt2_delai820_000001.txt");
		File fileTraj = new File("trajectoiresBonnes/norma_N5_tau4_dt2_delai820_000001_tra.txt");

		this.data = lireDonnees(file);
		this.dataClasses = lireTrajectoire(data, fileTraj);
		*/
        
        /* ------------ CLASSIFICATION SPECTRAL ------------- */
      //  ArrayList<double[]>[] spectral = Spectral.getClusters(data, countLines(file)/5) ;
       
       // System.out.println("SPECTRAL = "+getAccuracy(spectral)*10+"%");
        /* ---------------------------------------------------*/
  		
        importer.addActionListener(new Action());
        fermer.addActionListener(new Action());

        couleur.addActionListener(new Action());
        lien.addActionListener(new Action());
        valeur.addActionListener(new Action());
        
        xy.addActionListener(new Action());
        xz.addActionListener(new Action());
        yz.addActionListener(new Action());
        play.addActionListener(new Action());
        stop.addActionListener(new Action());

        kmean.addActionListener(new Action());
        spectral.addActionListener(new Action());
        
        fichier.add(importer);
        fichier.addSeparator();
        fichier.add(fermer);
        //------
        affichage.add(couleur);
        affichage.add(lien);
        affichage.add(valeur);
        
        animation.add(play);
        animation.add(stop);
        animation.addSeparator();
        animation.add(xy);
        animation.add(xz);
        animation.add(yz);
        
        affichage.add(animation);
        //------
        algo.add(kmean);
        algo.add(spectral);
        
        majBouton();
        
        menuBar.add(fichier);
        menuBar.add(affichage);
        menuBar.add(algo);
        
        frame.setJMenuBar(menuBar);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950,930);
        frame.setLocation(50,50);

        frame.setVisible(true);
	}
	
  	class Action implements ActionListener
  	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if ( e.getSource() == importer )
			{
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(frame);
				
				if( returnVal == 0 )
				{
					try
					{	
						File file = fc.getSelectedFile();
						data = lireDonnees(file);
						nbClasse = countLines(file)/5 ;
				        /* ------------- METHODE DES K-MOYENNES --------------*/
				        Kmeans kmeans = new Kmeans(data, nbClasse, true);
				        kmeans.setEpsilon(0.01);
				        int iterationFaite = kmeans.calculateClusters(10000, 90);
				     
				        //System.out.println("KMEANS = "+getAccuracy(kmeans.getClusters())*10+"%");
				        /* ---------------------------------------------------*/
				        if ( graph != null )
				        	graph.stop();
				        
				        graph = new Graph(data, nbClasse, kmeans, null);
				        
				        frame.setContentPane(graph);
				        
				        majBouton();
				        
				        frame.revalidate();
				        frame.repaint();
				        
						JOptionPane.showMessageDialog(null, iterationFaite+" iteration realisé", "Information", JOptionPane.INFORMATION_MESSAGE);
					 } 
					 catch (Exception ex)
					 {
						 ex.printStackTrace();
						 JOptionPane.showMessageDialog(null, "Traitement du fichier impossible, veuillez vérifier le format du fichier", "Erreur", JOptionPane.ERROR_MESSAGE);
					 }
				}
			}
			else if ( e.getSource() == fermer )
			{
				System.exit(0); 
			}
			else if ( e.getSource() == couleur )
			{
				graph.setColor(!graph.getColor()) ;
			}
			else if ( e.getSource() == lien )
			{
				graph.setLien(!graph.getLien()) ;
			}
			else if ( e.getSource() == valeur )
			{
				graph.setValeur(!graph.getValeur()) ;
			}
			else if ( e.getSource() == xy )
			{
				graph.setCoordAnimation("XY") ;
				majBouton();
			}
			else if ( e.getSource() == xz )
			{
				graph.setCoordAnimation("XZ") ;
				majBouton();
			}
			else if ( e.getSource() == yz )
			{
				graph.setCoordAnimation("YZ") ;
				majBouton();
			}
			else if ( e.getSource() == play )
			{
		        graph.start();
		        majBouton();
			}
			else if ( e.getSource() == stop )
			{
		        graph.stop();
		        majBouton();
			}
			else if ( e.getSource() == kmean )
			{
				String i = JOptionPane.showInputDialog(null, "Veuillez indiquer le nombre d'iteration ?", "K-Means", JOptionPane.QUESTION_MESSAGE);
				String a = JOptionPane.showInputDialog(null, "Veuillez indiquer la contrainte d'angle (en degré)", "K-Means", JOptionPane.QUESTION_MESSAGE);

				try
				{
					int iteration = Integer.parseInt(i);
					int angle = Integer.parseInt(a);

					/* ------------- METHODE DES K-MOYENNES --------------*/
			        Kmeans kmeans = new Kmeans(data, nbClasse, true);
			        kmeans.setEpsilon(0.01);
			        int iterationFaite = kmeans.calculateClusters(iteration, angle);
			        /* ---------------------------------------------------*/
			        if ( graph != null && graph.getEtatAnimation())
			        	graph.stop();
			        
			        graph = new Graph(data, nbClasse, kmeans, null);
			        
			        frame.setContentPane(graph);
			        
			        majBouton();
			        
			        frame.revalidate();
			        frame.repaint();
			        
					JOptionPane.showMessageDialog(null, "K-Means: "+iterationFaite+" iteration realisé", "Information", JOptionPane.INFORMATION_MESSAGE);

				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Erreur lors de l'execution de l'algorithme. Veuillez vérifier les paramètres", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
			else if ( e.getSource() == spectral )
			{
				int confirm = JOptionPane.showConfirmDialog(null, "Afin de filtrer les classes à 5 éléments chacune, nous allons devoir faire plusieurs recherches en ajustant le sigma du noyau Gaussien.\nCe processus peut être long, voulez-vous continuer ?", "Classification Spectral", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);;

				if(confirm != JOptionPane.NO_OPTION && confirm != JOptionPane.CANCEL_OPTION && confirm != JOptionPane.CLOSED_OPTION)
				{
					ArrayList<double[]>[] spectral = Spectral.getClusters(data, nbClasse) ;
					  
					if ( spectral != null )
					{
						if ( graph != null && graph.getEtatAnimation())
							graph.stop();
				        
				        graph = new Graph(data, nbClasse, null, spectral);
				        
				        frame.setContentPane(graph);
				        
				        majBouton();
				        
				        frame.revalidate();
				        frame.repaint();
				        
						JOptionPane.showMessageDialog(null, "Spectral: Classification terminée", "Information", JOptionPane.INFORMATION_MESSAGE);

					}
					else
						JOptionPane.showMessageDialog(null, "La classification a atteint les 500000 itérations sans avoir convergé. Veuillez réessayer", "Information", JOptionPane.INFORMATION_MESSAGE);

				   
				}
			}
		}
  	}
  	
  	public void majBouton()
  	{
  		if ( data == null )
  		{
  			affichage.setEnabled(false);
  			algo.setEnabled(false);
  		}
  		else
  		{
  			affichage.setEnabled(true);
  			algo.setEnabled(true);
  		}
  		
  		if (graph == null)
  		{
  			play.setEnabled(false);
  			stop.setEnabled(false);
  		}
  		else if ( graph.getEtatAnimation() )
  		{
  			play.setEnabled(false);
  			stop.setEnabled(true);
  		}
  		else
  		{
  			play.setEnabled(true);
  			stop.setEnabled(false);
  		}
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