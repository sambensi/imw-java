package upmc.imw.classifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Evaluator<T> implements Serializable 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2713343666983051855L;
	
	Classifier<T> classifier;
	List<TrainingSample<T>> ts;
	List<Evaluation<TrainingSample<T>>> tsResults;
	List<Evaluation<TrainingSample<T>>> esResults;
	
	
	public Evaluator(Classifier<T> c, List<TrainingSample<T>> trainingList)
	{
		classifier = c;
		ts = trainingList;
	}
	
	
	public void train()
	{
		classifier.train(ts);
	}
	
	/**
	 * Computes output values for each element of the training set
	 */
	public void evaluateTrainingSet()
	{
		if(tsResults == null)
			tsResults = new ArrayList<Evaluation<TrainingSample<T>>>();

		System.err.print("eval train ");

		numThread = 0;
		
		final int nbcpu = Runtime.getRuntime().availableProcessors();
//		final int nbcpu = 1;
		final int step = (ts.size()+1)/nbcpu +1;
		for(int t = 0 ; t < nbcpu ; t++)
		{
			final int ite = t;
			(new Thread(){
				public void run()
				{
					int start = ite*step;
					int stop = Math.min((ite+1)*step , ts.size());
					for(int j = start; j < stop; j++)
					{
						TrainingSample<T> s = ts.get(j);
						double r = classifier.valueOf(s.sample);
						Evaluation<TrainingSample<T>> e = new Evaluation<TrainingSample<T>>(s, r);
						synchronized(tsResults)
						{
							tsResults.add(e);
						}
					}
					System.out.print(".");
					synchronized(tsResults)
					{
						numThread++;
					}
				}
			}).start();
		}
		while(numThread < nbcpu)
		{
			Thread.yield();
		}
		System.err.println(" done.");
	}
	
	int numThread;
	/**
	 * Computes output values for each of the testing set
	 * @param testingSet
	 */
	public void evaluateTestingSet(final List<TrainingSample<T>> testingSet)
	{
		if(esResults == null)
			esResults = new ArrayList<Evaluation<TrainingSample<T>>>();
		//System.err.print("eval test ");

		numThread = 0;
		
		final int nbcpu = Runtime.getRuntime().availableProcessors();
//		final int nbcpu = 1;
		final int step = testingSet.size()/nbcpu + 1;
		for(int t = 0 ; t < nbcpu ; t++)
		{
			final int ite = t;
			(new Thread(){
				public void run()
				{
					int start = ite*step;
					int stop = Math.min((ite+1)*step, testingSet.size());
					for(int j = start; j < stop; j++)
					{
						TrainingSample<T> s = testingSet.get(j);
						double r = classifier.valueOf(s.sample);
						Evaluation<TrainingSample<T>> e = new Evaluation<TrainingSample<T>>(s, r);
						synchronized(esResults)
						{
							esResults.add(e);
						}
					}
					//System.out.print(".");
					synchronized(esResults)
					{
						numThread++;
					}
				}
			}).start();
		}
		while(numThread < nbcpu)
		{
			Thread.yield();
		}
		
		//System.err.println(" done.");
	}
	
	/**
	 * Computes Mean Average Precision for the training set
	 * @return the MAP
	 */
	public double getTrainingMAP()
	{
		Collections.sort(tsResults);

		int top = 0;
		int i = 1;
		int rec = 0;
		double map = 0.;
		
		for(Evaluation<TrainingSample<T>> e : tsResults)
		{
			if(e.sample.label == 1)
			{
				top++;
				rec++;
				map += top/(double)i;
			}
			i++;
		}
		return map / rec;
	}
	
	/**
	 * computes the Mean average precision for the testing set
	 * @return the MAP
	 */
	public double getTestingMAP()
	{

		Collections.sort(esResults);

		int top = 0;
		int i = 1;
		int rec = 0;
		double map = 0.;
		
		for(Evaluation<TrainingSample<T>> e : esResults)
		{
			if(e.sample.label == 1)
			{
				top++;
				rec++;
				map += top/(double)i;
			}
			i++;
		}
		
		return map / rec;
	}
	
	/**
	 * Computes the precision curve for the training set
	 * @return
	 */
	public double[] getTrainingPrecision()
	{
		ArrayList<Double> precision = new ArrayList<Double>();
		
		Collections.sort(tsResults);
		int top = 0;
		int i = 1;
		for(Evaluation<TrainingSample<T>> e : tsResults)
		{
			if(e.sample.label == 1)
			{
				top++;
				precision.add(top/(double)i);
			}
			i++;
		}
		
		double[] d = new double[precision.size()];
		for(int j = 0 ; j < precision.size(); j++)
			d[j] = precision.get(j);
		
		return d;
	}
	
	/**
	 * Computes the precision curve for the testing set
	 * @return
	 */
	public double[] getTestingPrecision()
	{
		ArrayList<Double> precision = new ArrayList<Double>();
		
		Collections.sort(esResults);
		int top = 0;
		int i = 1;
		for(Evaluation<TrainingSample<T>> e : esResults)
		{
			if(e.sample.label == 1)
			{
				top++;
				precision.add(top/(double)i);
			}
			i++;
		}
		
		double[] d = new double[precision.size()];
		for(int j = 0 ; j < precision.size(); j++)
			d[j] = precision.get(j);
		
		return d;
	}
	
	public double[][] getRecallPrecisionCurve()
	{
		ArrayList<Double> precision = new ArrayList<Double>();
		ArrayList<Double> recall = new ArrayList<Double>();
		
		Collections.sort(esResults);
		int top = 0;
		int i = 1;
		int nbPlus=0;
		for(Evaluation<TrainingSample<T>> e : esResults){
			if(e.sample.label == 1){
				nbPlus++;
			}
		}
		
		for(Evaluation<TrainingSample<T>> e : esResults){
			if(e.sample.label == 1){
				top++;
			}
			precision.add(top/(double)i);
			recall.add(top/(double)nbPlus);
			i++;
		}

		double[][] RP = new double[2][precision.size()+1];
		RP[0][0] = 0.0;
		RP[1][0] = 1.0;
		for(int j = 1 ; j <= recall.size(); j++)
			RP[0][j] = recall.get(j-1);
		
		for(int j = 1 ; j <= recall.size(); j++)
			RP[1][j] = precision.get(j-1);
		

		
		return RP;
	}
	
	public double getAreaAUC(){
		 double[][] ROC = getROCcurve();
			// Calcul du MAP par la méthode des trapèzes
			double AUC = 0.0;
			
			for(int j = 0 ; j < ROC[0].length-1; j++){
				AUC += (ROC[1][j+1]+ROC[1][j]) * (ROC[0][j+1]-ROC[0][j]) /2.0;
				
			}
			return AUC;
	}
	
	public double[][] getROCcurve()
	{
		Collections.sort(esResults);

		int taille =  esResults.size();
		double maxv = esResults.get(0).value;
		double minv = esResults.get(taille-1).value;
		
		
		int nBInt = 100;
		double largeurInt = (maxv-minv)/((double)nBInt);
		
		
		double[][] ROC = new double[2][nBInt+1];
		
		
		for(int t=0;t<=nBInt;t++){
			double seuil = maxv - t* largeurInt;
			
					
			int nbVPlus=0;
			int nbFPlus=0;
			int PlusTot=0;
			int tot = esResults.size();
			
			
			
			for(Evaluation<TrainingSample<T>> e : esResults){
				
				if(e.sample.label == 1)
					PlusTot++;
				
				if(e.value>seuil){
					if(e.sample.label == 1){
						nbVPlus++;
					}
					else
						nbFPlus++;
				}
				
				
			}
			
				
			ROC[0][t] = ((double)nbFPlus) / ((double)tot-PlusTot);
			ROC[1][t] =((double)nbVPlus)/ ((double)PlusTot);

		
		}
		
//		// Calcul du MAP par la méthode des trapèzes
//		double AUC = 0.0;
//		
//		for(int j = 0 ; j < nBInt; j++){
//			AUC += (ROC[1][j+1]+ROC[1][j]) * (ROC[0][j+1]-ROC[0][j]) /2.0;
//			
//		}
		
		//System.out.println("AUC trapèzes="+AUC);
		

		
		return ROC;
	}
	
	
	
	
	/**
	 * returns a map of samples and their associated values for the testing set
	 * @return
	 */
	public HashMap<T, Double> getTestingValues()
	{
		HashMap<T, Double> map = new HashMap<T, Double>();
		for(Evaluation<TrainingSample<T>> e : esResults)
			map.put(e.sample.sample, e.value);
		
		return map;
	}
	
	
	/**
	 * Simple class containing a sample and its evaluation by the classifier
	 * @author dpicard
	 *
	 * @param <U>
	 */
	private class Evaluation<U> implements Comparable<Evaluation<U>>, Serializable
	{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 791024170617779718L;
		
		U sample;
		double value;
		
		public Evaluation(U s, double v)
		{
			sample = s;
			value = v;
		}
		
		@Override
		public int compareTo(Evaluation<U> o) {
			
			return (int) Math.signum(o.value - value);
		}
	}
	
	public int[][] getTestingAccuracy(ArrayList<TrainingSample<T>> testTS){
		int[][] CM = {{0,0},{0,0}};
		
		for(TrainingSample<T> t : testTS){
			if(t.label==1){
				if(classifier.valueOf(t.sample)>=0){
					CM[0][0]++;
				}
				else{
					CM[0][1]++;
				}
			}
			else{
				if(classifier.valueOf(t.sample)>=0){
					CM[1][0]++;
				}
				else{
					CM[1][1]++;
				}
			}
		}
		return CM;
	}
	
	public double[] computeSVMoutputs(ArrayList<TrainingSample<T>> testTS){
		double[] values = new double [testTS.size()];
		int cpt=0;
		for( TrainingSample<T> t : testTS){
			values[cpt] = classifier.valueOf(t.sample);
			cpt++;
		}
		
		return values;
		
	}
	
	


}
