package ihm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
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
	private int w, h ; // Largeur et Hauteur de la fenetre
    
    public Graph(double[][] d, Kmeans kmeans)
    {
    	this.data = d ;
    	this.kmeans = kmeans ;
    	
    	this.setLayout(new GridLayout(2, 2));
    	this.add(new GraphXY());
    	this.add(new JPanel());
    	this.add(new GraphXZ());
    	this.add(new GraphYZ());

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
	
/*
	private void dessineData(Graphics2D g) 
	{
		for ( double[] d : data)
		{
			double x = d[0];
			double y = d[1];
			double z = d[2];
			//System.out.println(x+" "+y);
			
			g.drawOval(ECART+(int)(x*ZOOM), (h-ECART)-(int)(y*ZOOM), 5, 5);
		}
	}*/
	
	private void dessineKmeans(Graphics2D g, String coord) 
	{ 
		int colorPas = 999999999/kmeans.getNumClusters() ;
		int color = 0 ;
		int r = 0 ;
		int v = 0 ;
		int b = 0 ;

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
        	        //g.setPaint(new Color(color));
        			
        	    	g.fillOval(ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART, 5, 5);
        			//g.drawString(""+z,ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART);

        	    }
        	    else if (coord.equals("YZ"))
        	    {
        	    	x = y ;
        	    	y = z ;
        	    	
        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, (((h-ECART)-(int)(y*ZOOMAxeZ))+2)+ZSTART));
        	        //g.setPaint(new Color(color));
        	    	
        			g.fillOval(ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART, 5, 5);
        			//g.drawString(""+z,ECART+(int)(x*ZOOM), (((h-ECART)-(int)(y*ZOOMAxeZ)))+ZSTART);
        	    }
        	    else
        	    {
        	    	//p.addPoint((ECART+(int)(x*ZOOM))+2, ((h-ECART)-(int)(y*ZOOM))+2);
        	    	classes.add(new Point((ECART+(int)(x*ZOOM))+2, ((h-ECART)-(int)(y*ZOOM))+2));

        	        //g.setPaint(new Color(color));
        			g.fillOval(ECART+(int)(x*ZOOM), (h-ECART)-(int)(y*ZOOM), 5, 5);
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
	        System.out.println(w+" "+h);

	        
	        dessineRepere(g2, "XY");
	        //dessineData(g2);
	        dessineKmeans(g2, "XY");
	       
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
	        //dessineData(g2);
	        dessineKmeans(g2, "XZ");
	       
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
	        //dessineData(g2);
	        dessineKmeans(g2, "YZ");
	       
	    }

	}
	
}	