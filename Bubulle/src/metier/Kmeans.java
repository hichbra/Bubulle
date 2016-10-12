package metier;
// Kmeans.java 

import java.util.Random;
import java.util.ArrayList;

public class Kmeans {

	private double[][] data; // data to cluster
	private int numClusters; // number of clusters
	private double[][] clusterCenters; // cluster centers
	private int dataSize; // size of the data
	private int dataDim; // dimension of the data
	private ArrayList[] clusters; // calculated clusters
	private double[] clusterVars; // cluster variances

	private double epsilon;

	public Kmeans(double[][] data, int numClusters, double[][] clusterCenters) {
		dataSize = data.length;
		dataDim = data[0].length;

		this.data = data;

		this.numClusters = numClusters;

		this.clusterCenters = clusterCenters;

		clusters = new ArrayList[numClusters];
		for (int i = 0; i < numClusters; i++) {
			clusters[i] = new ArrayList();
		}
		clusterVars = new double[numClusters];

		epsilon = 0.01;
	}

	public Kmeans(double[][] data, int numClusters) {
		this(data, numClusters, true);
	}

	public Kmeans(double[][] data, int numClusters, boolean randomizeCenters) {
		dataSize = data.length;
		dataDim = data[0].length;

		this.data = data;

		this.numClusters = numClusters;

		this.clusterCenters = new double[numClusters][dataDim];

		clusters = new ArrayList[numClusters];
		for (int i = 0; i < numClusters; i++) {
			clusters[i] = new ArrayList();
		}
		clusterVars = new double[numClusters];

		epsilon = 0.01;

		if (randomizeCenters) {
			randomizeCenters(numClusters, data);
		}
	}

	private void randomizeCenters(int numClusters, double[][] data) {
		Random r = new Random();
		int[] check = new int[numClusters];
		for (int i = 0; i < numClusters; i++) {
			int rand = r.nextInt(dataSize);
			if (check[i] == 0) {
				this.clusterCenters[i] = data[rand].clone();
				check[i] = 1;
			} else {
				i--;
			}
		}
	}

	private void calculateClusterCenters() {
		for (int i = 0; i < numClusters; i++) {
			int clustSize = clusters[i].size();

			for (int k = 0; k < dataDim; k++) {

				double sum = 0d;
				for (int j = 0; j < clustSize; j++) {
					double[] elem = (double[]) clusters[i].get(j);
					sum += elem[k];
				}

				clusterCenters[i][k] = sum / clustSize;
			}
		}
	}

	private void calculateClusterVars() {
		for (int i = 0; i < numClusters; i++) {
			int clustSize = clusters[i].size();
			Double sum = 0d;

			for (int j = 0; j < clustSize; j++) {

				double[] elem = (double[]) clusters[i].get(j);

				for (int k = 0; k < dataDim; k++) {
					sum += Math.pow((Double) elem[k] - getClusterCenters()[i][k], 2);
				}
			}

			clusterVars[i] = sum / clustSize;
		}
	}

	public double getTotalVar() {
		double total = 0d;
		for (int i = 0; i < numClusters; i++) {
			total += clusterVars[i];
		}

		return total;
	}

	public double[] getClusterVars() {
		return clusterVars;
	}

	public ArrayList[] getClusters() {
		return clusters;
	}

	
	private void assignData() {
		for (int k = 0; k < numClusters; k++) {
			clusters[k].clear();
		}
		
		for (int i = 0; i < dataSize; i++) {
			int clust = 0;
			double dist = Double.MAX_VALUE;
			double newdist = 0;

			for (int j = 0; j < numClusters; j++)
			{
				// On limite les points par classes a 5 maximum
				// Test de l'angle des points
				/*if(clusters[j].size() < 2) 
				{
					newdist = distToCenter(data[i], j);
					
					if (newdist <= dist) {
						clust = j;
						dist = newdist;
					}
				}
				else */if (clusters[j].size() < 5 ) // Test de l'angle des points
				{
					/*double pAnglex = -1, pAngley = -1, pAnglez = -1, p1x = -1, p1y = -1, p1z = -1, p2x = -1, p2y = -1, p2z = -1 ;
					
					int cptligne = 0 ;
					for ( Object o : clusters[j])
			        {
						if (cptligne == 0)
						{
							double[] d = (double[])o ;
							p1x = d[0];
							p1y = d[1];
							p1z = d[2];
							//p1z = a[2];

						}
						else if (cptligne == 1 )
						{
							double[] d = (double[])o ;
							p2x = d[0];
							p2y = d[1];	
							p2z = d[2];

						}
						
						cptligne++;
			        }
					
					pAnglex = data[i][0];
					pAngley = data[i][1];
					pAnglez = data[i][2];
					*/

			        newdist = distToCenter(data[i], j);
					if (newdist <= dist /*&& (calculAngle(pAnglex, pAngley, p1x, p1y, p2x, p2y) <= 20) || calculAngle(pAnglex, pAnglez, p1x, p1z, p2x, p2z) <= 20 || calculAngle(pAngley, pAnglez, p1y, p1z, p2y, p2z) <= 20*/ )
					{
						clust = j;
						dist = newdist;
					}
				}
			}

			clusters[clust].add(data[i]);
		}

	}

	private double calculAngle(double pAnglex, double pAngley, double p1x, double p1y, double p2x, double p2y ) 
	{
		
		// Calcul des distances euclidiennes
		double pAp1 = Math.sqrt(Math.abs( Math.pow((pAnglex-p1x),2) + Math.pow((pAngley-p1y),2) ));
		double p1p2 = Math.sqrt(Math.abs( Math.pow((p1x-p2x),2) + Math.pow((p1y-p2y),2) ));
		double p2pA = Math.sqrt(Math.abs( Math.pow((p2x-pAnglex),2) + Math.pow((p2y-pAngley),2) ));
		
		double cospAngle = (Math.pow(pAp1,2) + Math.pow(p2pA,2) - Math.pow(p1p2,2)) / (2*pAp1*p2pA);
		double cosAngle = Math.acos(cospAngle);
		  
		double angle = cosAngle*(180/Math.PI);
	  
		return angle ;
	}
	
	private double distToCenter(double[] datum, int j) {
		double sum = 0d;
		for (int i = 0; i < dataDim; i++) {
			sum += Math.pow((datum[i] - getClusterCenters()[j][i]), 2);
		}

		return Math.sqrt(sum);
	}

	
	public void calculateClusters() {

		double var1 = Double.MAX_VALUE;
		double var2;
		double delta;

		int i = 0 ;
		do {
			calculateClusterCenters();
			assignData();
			calculateClusterVars();
			var2 = getTotalVar();
			if (Double.isNaN(var2)) // if this happens, there must be some empty
									// clusters
			{
				delta = Double.MAX_VALUE;
				randomizeCenters(numClusters, data);
				assignData();
				calculateClusterCenters();
				calculateClusterVars();
			} else {
				delta = Math.abs(var1 - var2);
				var1 = var2;
			}

			i++ ;
			System.out.println(i);
		} while (delta > epsilon && i < 10000);
	}
	
	public void setEpsilon(double epsilon) {
		if (epsilon > 0) {
			this.epsilon = epsilon;
		}
	}

	public int getNumClusters() {
		return numClusters;
	}
	/**
	 * @return the clusterCenters
	 */
	public double[][] getClusterCenters() {
		return clusterCenters;
	}
}