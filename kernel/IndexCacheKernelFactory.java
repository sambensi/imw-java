package upmc.imw.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IndexCacheKernelFactory {

	/**
	 * @param args
	 */




	
	private static Map<String,double[]> map =new HashMap<String,double[]>();
	private static IndexedCacheKernel<String,double[]> indexedCachKernel;
	private static boolean constructed=false;
	
	public static  IndexedCacheKernel<String,double[]> createIndexCacheKernel(ArrayList<String> files,ArrayList<ArrayList<double[]>> bow){
		
		DoubleGaussChi2 chi2 = new DoubleGaussChi2();
		ArrayList<double[]> bow2 = bow.get(0);
		System.out.println(bow2.size());
	
		int taille = bow2.size();
		double[][] matriceD = new double[taille][taille];
		
		double somme =0;
		for(int i=0;i<taille;i++){
			for(int j=0;j<taille;j++){
				if(i<j){
					double dist = chi2.distChi2(bow2.get(i), bow2.get(j));
					matriceD[i][j]= dist;
					matriceD[j][i]=matriceD[i][j];
					
					somme+=dist*2;
				}
				
			}
		}
		
		double moyenne = somme/(taille*taille);
		double gamma = 1/moyenne;
		chi2 = new DoubleGaussChi2(gamma);
		
		Iterator<String> it1 = files.iterator();
		Iterator<double[]> it2 = bow2.iterator();
		while(it1.hasNext()&&it2.hasNext()){
			map.put(it1.next(),it2.next());
		}
		if(!constructed){
		indexedCachKernel = new IndexedCacheKernel<String, double[]>(chi2,map);
		constructed=true;
		return indexedCachKernel;
		
		}
		
		else {
			return indexedCachKernel;
		}
	}
}