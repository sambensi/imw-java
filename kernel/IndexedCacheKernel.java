package upmc.imw.kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class IndexedCacheKernel <S,T> extends Kernel<S> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1706425748759430692L;
	
	protected double[][] matrix;
	protected HashMap<S, Integer> map;
	private Kernel<T> kernel;
	
	public IndexedCacheKernel(Kernel<T> k, Map<S, T> signatures)
	{
		this.kernel = k;
		
		matrix = new double[signatures.size()][signatures.size()];
	
		//adding index
		map = new HashMap<S, Integer>(signatures.size());
		int index = 0;
		for(S s : signatures.keySet())
		{
			map.put(s, index);
			index++;
		}
		
		//computing matrix

		int nbc = ((int)Math.sqrt(Runtime.getRuntime().availableProcessors()+1));
		int icrem = signatures.size()/nbc ;
		
		ArrayList<MatrixThread> threads = new ArrayList<MatrixThread>();
		
		for(int i = 0 ; i < signatures.size() ; i+=icrem)
		for(int j = 0 ; j < signatures.size() ; j+=icrem)
		{
			MatrixThread t = new MatrixThread(matrix, signatures, i, i+icrem, j, j+icrem);
			threads.add(t);
			t.start();
		}
		
		boolean cont = true;
		while(cont)
		{
			cont = false;
			for(MatrixThread t : threads)
				if(!t.hasFinished() && t.isAlive())
					cont = true;
			
			Thread.yield();
		}
		
		
	}
	
	@Override
	public double valueOf(S t1, S t2) {
		//return 0 if doesn't know of
		if(!map.containsKey(t1) || !map.containsKey(t2))
		{
			System.err.println("<"+t1+","+t2+"> not in matrix !!!");
			return 0;
		}
		int id1 = map.get(t1);
		int id2 = map.get(t2);
		
		return matrix[id1][id2];
	}

	@Override
	public double valueOf(S t1) {
		//return 0 if doesn't know of
		if(!map.containsKey(t1))
		{
			System.err.println("<"+t1+","+t1+"> not in matrix !!!");
			return 0;
		}
		
		int id = map.get(t1);
		return matrix[id][id];
	}

	
	private class MatrixThread extends Thread
	{
		double[][] m;
		Map<S, T> e;
		int mini, maxi, minj, maxj;
		boolean finished = false;
		
		/**
		 * @param m
		 * @param signatures
		 * @param mini
		 * @param maxi
		 * @param minj
		 * @param maxj
		 */
		public MatrixThread(double[][] m, Map<S, T> signatures, int mini, int maxi,
				int minj, int maxj) {
			this.m = m;
			this.e = signatures;
			this.mini = mini;
			this.maxi = Math.min(maxi, signatures.size());
			this.minj = minj;
			this.maxj = Math.min(maxj, signatures.size());
		}



		public void run() {

			finished = false;

			for(S s1 : e.keySet())
			{
				int i = map.get(s1);
				if(i >= mini && i < maxi)
				{
					T t1 = e.get(s1);
					
					for(S s2 : e.keySet())
					{
						int j = map.get(s2);
						if(j >= minj && j < maxj)
						{	
							T t2 = e.get(s2);


							double v = kernel.valueOf(t1, t2);
							if(!Double.isNaN(v))
							{
								m[i][j] = v;
							}
							else
							{
								System.out.println("NAN : v="+v);
								System.out.println("t1="+Arrays.toString((double[])t1));
								System.out.println("t1="+Arrays.toString((double[])t2));
								System.exit(0);
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
