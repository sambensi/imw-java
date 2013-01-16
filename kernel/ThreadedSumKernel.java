package upmc.imw.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;


import upmc.imw.classifier.TrainingSample;

/**
 * Major kernel computed as a weighted sum of minor kernels : 
 * K = w_i * k_i<br />
 * Computation of the kernel matrix is done by running a thread on sub matrices.
 * The number of threads is choosen as function of the number of available cpus.
 * @author dpicard
 *
 * @param <T>
 */
public class ThreadedSumKernel<T> extends Kernel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7780445301175174296L;
	
	
	private Hashtable<Kernel<T>, Double> kernels;
	private transient HashMap<Kernel<T>, double[][]> matrixMap;
	protected int numThread = 0;
	
	public ThreadedSumKernel()
	{
		kernels = new Hashtable<Kernel<T>, Double>();
	}

	/**
	 * Sets the weights to h. Beware! It does not make a copy of h!
	 * @param h
	 */
	public ThreadedSumKernel(Hashtable<Kernel<T>, Double> h)
	{
		kernels = new Hashtable<Kernel<T>, Double>();
		kernels.putAll(h);
	}
	
	/**
	 * adds a kernel to the sum with weight 1.0
	 * @param k
	 */
	public void addKernel(Kernel<T> k)
	{
		kernels.put(k, 1.0);
	}
	
	/**
	 * adds a kernel to the sum with weight d
	 * @param k
	 * @param d
	 */
	public void addKernel(Kernel<T> k , double d)
	{
		kernels.put(k, d);
	}
	
	/**
	 * removes kernel k from the sum
	 * @param k
	 */
	public void removeKernel(Kernel<T> k)
	{
		kernels.remove(k);
	}
	
	/**
	 * gets the weights of kernel k
	 * @param k
	 * @return the weight associated with k
	 */
	public double getWeight(Kernel<T> k)
	{
		Double d = kernels.get(k);
		if(d == null)
			return 0.;
		return d.doubleValue();
	}
	
	/**
	 * Sets the weight of kernel k
	 * @param k
	 * @param d
	 */
	public void setWeight(Kernel<T> k, Double d)
	{
		kernels.put(k, d);
	}
	
	@Override
	public double valueOf(T t1, T t2) {
		double sum = 0.;
		for(Kernel<T> k : kernels.keySet())
			sum += kernels.get(k)*k.valueOf(t1, t2);
		
		return sum;
	}

	@Override
	public double valueOf(T t1) {
		double sum = 0.;
		for(Kernel<T> k : kernels.keySet())
			sum += kernels.get(k)*k.valueOf(t1);
		
		return sum;
	}
	
	/**
	 * get the list of kernels and associated weights.
	 * @return hashtable containing kernels as keys and weights as values.
	 */
	public Hashtable<Kernel<T>, Double> getWeights()
	{
		return kernels;
	}
	
	
	public double[][] getKernelMatrix(ArrayList<TrainingSample<T>> e)
	{
		double matrix[][] = new double[e.size()][e.size()];
		numThread = 0;
		
		//computing each matrix and storing them
		matrixMap = new HashMap<Kernel<T>, double[][]>();
		for(Kernel<T> k : kernels.keySet())
		{
			double[][] m = k.getKernelMatrix(e);
			matrixMap.put(k, m);
		}

		int nbc = ((int)Math.sqrt(2*Runtime.getRuntime().availableProcessors()))+1;
		int icrem = e.size()/nbc ;
		
		
		ArrayList<MatrixThread> threads = new ArrayList<MatrixThread>();
		for(int i = 0 ; i < e.size() ; i+=icrem)
		for(int j = 0 ; j < e.size() ; j+=icrem)
		{
			MatrixThread t = new MatrixThread(matrix, e, i, i+icrem, j, j+icrem);
			threads.add(t);
			t.start();
		}

		
		boolean cont = true;
		while(cont)
		{
			cont = false;
			for(MatrixThread t : threads)
				if(!t.hasFinished())
					cont = true;
			Thread.yield();
		}
		
		return matrix;
	}
	

	private class MatrixThread extends Thread
	{
		double[][] m;
		ArrayList<TrainingSample<T>> e;
		int mini, maxi, minj, maxj;
		boolean finished = false;
		
		/**
		 * @param m
		 * @param e
		 * @param mini
		 * @param maxi
		 * @param minj
		 * @param maxj
		 */
		public MatrixThread(double[][] m, ArrayList<TrainingSample<T>> e, int mini, int maxi,
				int minj, int maxj) {
			this.m = m;
			this.e = e;
			this.mini = mini;
			this.maxi = Math.min(maxi, e.size());
			this.minj = minj;
			this.maxj = Math.min(maxj, e.size());
		}



		public void run() {
			
			finished = false;
			ArrayList<Kernel<T>> listOfK = new ArrayList<Kernel<T>>();
			
			synchronized(kernels)
			{
				listOfK.addAll(kernels.keySet());
			}
			
			for(Kernel<T> k : listOfK)
			{
				double[][] matrix = null;
				
				double w = 0.;
				synchronized(kernels)
				{
					w = kernels.get(k);
					if(Double.isNaN(w) || Double.isInfinite(w))
					{
						System.out.println("w error : "+w+" kernels:"+kernels);
						System.exit(3);
					}
				}
				
				
				if(w != 0)
				{
				
					synchronized(matrixMap)
					{
						matrix = matrixMap.get(k);
					}

					for (int i = mini; i < maxi; i++) {
						for (int j = minj; j < maxj; j++) {
							if(!Double.isNaN(matrix[i][j]))
							{
								synchronized(m[i])
								{
									m[i][j] += w*matrix[i][j];
								}
								if(Double.isNaN(m[i][j]) || Double.isInfinite(m[i][j]))
								{
									System.out.println(i+":"+j+" NaN!!! "+k+" "+m[i][j]+" w "+w+" matrixij"+matrix[i][j]);
									System.exit(2);
								}
							}
						}
					}
					
				}
			}
			
			finished = true;
		}
		
		public boolean hasFinished()
		{
			return finished;
		}
	}
}
