package ihm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JPanel;

import metier.Kmeans;
 
public class Graph extends JPanel 
{	
	private final int ZOOM = 30;
	private final double ZOOMAxeZ = 9;
	private final int ZSTART = 850;
	
	private final int ECART = 30;
	private double[][] data ;
	private Kmeans kmeans ;
	private ArrayList<double[]>[] spectral ;
	private int w, h ; // Largeur et Hauteur de la fenetre
    private int nbClusters ;
    
	private Animation anim ;
	private boolean activeColor ;
	private boolean activeLien ;
	private boolean activeValeur ;

    public Graph(double[][] d, int nbClusters, Kmeans kmeans, ArrayList<double[]>[] spectral)
    {
    	this.data = d ;
    	this.kmeans = kmeans ;
    	this.spectral = spectral ;
    	this.nbClusters = nbClusters ;
    	
    	this.setLayout(new GridLayout(2, 2));
    	this.add(new GraphXY());
    	anim = new Animation();
    	this.add(anim);
    	this.add(new GraphXZ());
    	this.add(new GraphYZ());
    	
    	this.activeColor = false ;
    	this.activeLien = true ;
    	this.activeValeur = false ;
    }
    
    public void start()
    {
    	anim.start((Graphics2D)anim.getGraphics());
    }
    
    public void setCoordAnimation(String coord)
    {
    	anim.setCoordAnimation((Graphics2D)anim.getGraphics(), coord);
    }
    
    public void stop()
    {
    	anim.stop();
    }
    
    public boolean getEtatAnimation()
    {
    	return anim.getEtatAnimation();
    }
    
    public boolean getColor()
    {
    	return activeColor ;
    }
    
    public void setColor(boolean color)
    {
    	this.activeColor = color ;
    	repaint();
    }
    
    public boolean getLien()
    {
    	return activeLien ;
    }
    
    public void setLien(boolean lien)
    {
    	this.activeLien = lien ;
    	repaint();
    }
    
    public boolean getValeur()
    {
    	return activeValeur ;
    }
    
    public void setValeur(boolean valeur)
    {
    	this.activeValeur = valeur ;
    	repaint();
    }
    
    public void export(File file) throws IOException
    {
    	FileWriter fw = new FileWriter(file);
    	
    	ArrayList<double[]>[] jeuDeDonnees ;
    	if( kmeans != null )
    		jeuDeDonnees = kmeans.getClusters() ;
    	else
    		jeuDeDonnees = spectral ;
    	
    	
    	for( ArrayList<double[]> result : jeuDeDonnees)
		{
    	    if ( result != null)
    	    {
    	    	for(double[] i : result)
	        	{
    	    		double x = i[0];
	    			double y = i[1];
	    			double z = i[2];
	    			
	    			int ligne = 1 ;
	    			for( double[] d : data)
	    	    	{
	    	    		if ( x == d[0] && y == d[1] && z == d[2])
	    	    			fw.write(ligne+" ");
	    	    		
	    	    		ligne++ ;
	    	    	}
	        	}
    	    }
    	    fw.write("\n");
		}
    	
    	fw.close();
    }
    
	private void dessineRepere(Graphics2D g2, String coord) 
	{
		g2.drawLine(ECART, ECART, ECART, h-ECART);
        g2.drawLine(ECART, h-ECART, w-ECART, h-ECART);
        char[] c = coord.toCharArray();
    	g2.drawString(c[1]+"", ECART, ECART-15);
    	g2.drawString(c[0]+"", w-ECART+15, h-ECART);

    	if( c[1] == 'Z' )
    	{
    		for ( int i = h-ECART, description = (int) (ZSTART/ZOOMAxeZ) ; i > ECART ; i-=ZOOMAxeZ, description++)
            {
            	g2.drawLine(ECART,i,ECART+5,i);
            	g2.drawString(description+"", ECART-25,i);
            }
    	}
    	else
    	{
    		for ( int i = h-ECART, description = 0; i > ECART ; i-=ZOOM, description++)
            {
            	g2.drawLine(ECART,i,ECART+5,i);
            	g2.drawString(description+"", ECART-25,i);
            }	
    	}
        
        
        for ( int i = ECART, description = 0; i < w-ECART ; i+=ZOOM, description++ )
        {
        	g2.drawLine(i, h-ECART, i, h-(ECART+5));
        	g2.drawString(description+"", i, h-(ECART-25));
        }
	}
	
	
	private void dessineSpectral(Graphics2D g, String coord) 
	{
		int colorPas = 999999999/nbClusters ;
		int color = 0 ;
		
		for( ArrayList<double[]> d : spectral)
        {	
    	    ArrayList<Point> classes = new ArrayList<Point>();
    	    if ( d != null)
    	    {
    	    	for(double[] i : d)
	        	{
    	    		double x = i[0];
	    			double y = i[1];
	    			double z = i[2];

	        	    if (coord.equals("XZ"))
	        	    {
	        	    	y = z ;
	        	    	
	        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, (((h-ECART)-(int)(y*ZOOMAxeZ))+2)+ZSTART));
	        	        
	        	    	if(activeColor)
	        	    		g.setPaint(new Color(color));
	        	    	
	        	    	g.fillOval(ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART, 5, 5);
	        			
	        	    	if(activeValeur)
	        				g.drawString(""+z,ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART);

	        	    }
	        	    else if (coord.equals("YZ"))
	        	    {
	        	    	x = y ;
	        	    	y = z ;
	        	    	
	        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, (((h-ECART)-(int)(y*ZOOMAxeZ))+2)+ZSTART));
	        	    	
	        	    	if(activeColor)
	        	    		g.setPaint(new Color(color));
	        	    	
	        			g.fillOval(ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART, 5, 5);
	        			
	        			if(activeValeur)
	        				g.drawString(""+z,ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART);
	        	    }
	        	    else
	        	    {
	        	    	//p.addPoint((ECART+(int)(x*ZOOM))+2, ((h-ECART)-(int)(y*ZOOM))+2);
	        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, ((h-ECART)-(int)(y*ZOOM))+2));

	        	    	if(activeColor)
	        	    		g.setPaint(new Color(color));
	        	    	
	        			g.fillOval(ECART+(int)(x*ZOOM), (h-ECART)-(int)(y*ZOOM), 5, 5);
	        			
	        			if(activeValeur)
	        	    		g.drawString("("+x+","+y+","+z+")",(ECART+(int)(x*ZOOM))+2, ((h-ECART)-(int)(y*ZOOM))+2);

	        	    }
	        		//System.out.println(x+" ; "+y+" ; "+z);		
	        	}
	        	classes.sort(new Comparator<Point>() {
					@Override
					public int compare(Point p1, Point p2) {
						if ( p1.x < p2.x)
							return 1;
						else if ( p1.x > p2.x )
							return -1;
						else
							return 0;
					}
	        		
				});
	        	
	        	Point precedent = null ;
	        	for(Point point : classes)
	        	{
	        		if ( precedent != null )
	        			g.drawLine(precedent.x, precedent.y, point.x, point.y);
	        		precedent = point ;
	        	}
	        	
	        	//g.drawPolygon(p);

	        	color += colorPas;
    	    }   
        }	
	}
	
	
	private void dessineKmeans(Graphics2D g, String coord) 
	{ 
		int colorPas = 999999999/nbClusters ;
		int color = 0 ;
		
        for( ArrayList<double[]> d : kmeans.getClusters())
        {
    	    ArrayList<Point> classes = new ArrayList<Point>();
    	    
    	    for(double[] i : d)
        	{
        	    double x = i[0];
    			double y = i[1];
    			double z = i[2];

        	    if (coord.equals("XZ"))
        	    {
        	    	y = z ;
        	    	
        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, (((h-ECART)-(int)(y*ZOOMAxeZ))+2)+ZSTART));
        	    	if(activeColor)
        	    		g.setPaint(new Color(color));
        			
        	    	g.fillOval(ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART, 5, 5);
        			
        	    	if(activeValeur)
        	    		g.drawString("("+x+","+y+","+z+")",ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART);

        	    }
        	    else if (coord.equals("YZ"))
        	    {
        	    	x = y ;
        	    	y = z ;
        	    	
        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, (((h-ECART)-(int)(y*ZOOMAxeZ))+2)+ZSTART));
        	    	if(activeColor)
        	    		g.setPaint(new Color(color));
        	    	
        			g.fillOval(ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART, 5, 5);
        			
        			if(activeValeur)
        	    		g.drawString("("+x+","+y+","+z+")",ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART);
        	    }
        	    else
        	    {
        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, ((h-ECART)-(int)(y*ZOOM))+2));

        	    	if(activeColor)
        	    		g.setPaint(new Color(color));
        	    	
        			g.fillOval(ECART+(int)(x*ZOOM), (h-ECART)-(int)(y*ZOOM), 5, 5);
        			
        			if(activeValeur)
        	    		g.drawString("("+x+","+y+","+z+")",(ECART+(int)(x*ZOOM))+2, ((h-ECART)-(int)(y*ZOOM))+2);

        	    }
        	}
    	    
        	classes.sort(new Comparator<Point>() {
				@Override
				public int compare(Point p1, Point p2) {
					if ( p1.x < p2.x)
						return 1;
					else if ( p1.x > p2.x )
						return -1;
					else
						return 0;
				}
        		
			});
        	
        	if(activeLien)
        	{
        		Point precedent = null ;
            	for(Point point : classes)
            	{
            		if ( precedent != null )
            			g.drawLine(precedent.x, precedent.y, point.x, point.y);
            		precedent = point ;
            	}
        	}

        	//g.drawPolygon(p);

        	color += colorPas;
        }	
       
	}
	
	class VisuAnimation implements Runnable
	{
		private boolean playAnimation ;
		
		private Graphics2D g;
		private String coord;
		
		public VisuAnimation(Graphics2D g, String coord)
		{
			this.playAnimation = true ;
			this.g = g ;
			this.coord = coord ;
		}
		
		public boolean getEtatAnimation()
		{
			return this.playAnimation ;
		}
		
		public void stopAnimation()
		{
			playAnimation = false ;
		}

		public void run() 
		{
			int animation = 0 ;
			while(playAnimation)
			{				
				ArrayList<double[]>[] jeuDeDonnees ;
				if ( kmeans != null )
					jeuDeDonnees = kmeans.getClusters() ;
				else
					jeuDeDonnees = spectral ;
				
		        for( ArrayList<double[]> d : jeuDeDonnees)
		        {	
		    	    ArrayList<Point> classes = new ArrayList<Point>();
		    	    
		    	    int cptAnimation = 0 ;
		    	    if ( d != null )
		    	    {
		    	    	for(double[] i : d)
			        	{
			    	    	if (cptAnimation < animation)
			    	    	{
			    	    		double x = i[0];
				    			double y = i[1];
				    			double z = i[2];

				        	    if (coord.equals("XZ"))
				        	    {
				        	    	y = z ;
				        	    	
				        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, (((h-ECART)-(int)(y*ZOOMAxeZ))+2)+ZSTART));
				        	    
				        	    	g.fillOval(ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART, 5, 5);
				        	    }
				        	    else if (coord.equals("YZ"))
				        	    {
				        	    	x = y ;
				        	    	y = z ;
				        	    	
				        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, (((h-ECART)-(int)(y*ZOOMAxeZ))+2)+ZSTART));
				        	    	
				        			g.fillOval(ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART, 5, 5);		        	    }
				        	    else
				        	    {
				        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, ((h-ECART)-(int)(y*ZOOM))+2));
				        	    
				        			g.fillOval(ECART+(int)(x*ZOOM), (h-ECART)-(int)(y*ZOOM), 5, 5);
				        	    }
				        	
				        	    
				        	    classes.sort(new Comparator<Point>() {
									@Override
									public int compare(Point p1, Point p2) {
										if ( p1.x < p2.x)
											return 1;
										else if ( p1.x > p2.x )
											return -1;
										else
											return 0;
									}
					        		
								});
					        	
					        	//g.drawPolygon(p);
					        	cptAnimation++ ;
				        	}
			    	    }
		    	    }
		        }
		        
		        try 
		        {
					Thread.sleep(200);
				} 
		        catch (InterruptedException e) {e.printStackTrace();}

		        
		        animation++ ;
		        if( animation > 5 )
		        {
			        repaint();
		        	animation = 0 ;
		        }
			
			}
		}
		
	}
	
	class GraphXY extends JPanel 
	{
		protected void paintComponent(Graphics g) 
	    {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);
	        w = getWidth();
	        h = getHeight();
	        
	        dessineRepere(g2, "XY");
	        if ( kmeans != null )
	        	dessineKmeans(g2, "XY");
	        else
	        	dessineSpectral(g2, "XY");

	    }
	}
	
	class GraphXZ extends JPanel 
	{
		protected void paintComponent(Graphics g) 
	    {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);
	        w = getWidth();
	        h = getHeight();
	        
	        dessineRepere(g2, "XZ");
	        if ( kmeans != null )
	        	dessineKmeans(g2, "XZ");
	        else
	        	dessineSpectral(g2, "XZ");

	    }

	}
	
	class GraphYZ extends JPanel 
	{
		protected void paintComponent(Graphics g) 
	    {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);
	        w = getWidth();
	        h = getHeight();
	        
	        dessineRepere(g2, "YZ");
	        if ( kmeans != null )
	        	dessineKmeans(g2, "YZ");
	        else
	        	dessineSpectral(g2, "YZ");

	    }

	}
	
	class Animation extends JPanel 
	{
		private VisuAnimation t ;
		private String coord ;
		
		public Animation()
		{
			coord = "XY";
		}
		
		protected void paintComponent(Graphics g) 
	    {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);
	        w = getWidth();
	        h = getHeight();
	        
	        dessineRepere(g2, coord);

	        //dessineSpectral(g2, "YZ");
	    }
		
		public void stop() 
		{
			t.stopAnimation();
		}

		public void start(Graphics2D g)
		{
	        t = new VisuAnimation(g, "XY");
	        new Thread(t).start();
		}
		
		public void setCoordAnimation(Graphics2D g, String coord)
		{
			stop() ;
			removeAll();
			this.coord = coord ;
	        dessineRepere(g, coord);
			t = new VisuAnimation(g, coord);
			new Thread(t).start();
		}
		
		public boolean getEtatAnimation()
		{
			if ( t == null )
				return false ;
			else
				return t.getEtatAnimation();
		
		}
	}
}	