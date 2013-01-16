package upmc.imw.bow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import upmc.imw.bin.Trie;
import upmc.imw.io.Descriptor;/**
/**
 * @author 2763358
 *
 */
public class BoWFactory {
	

	
	public ArrayList<double[]> createBagofWords(ArrayList<Descriptor> list,double[][] centers,double[] sigma){
		int nbClusters = centers.length;
		int nbDescriptors= list.size();
		
		double[][] matriceD= new double[nbDescriptors][nbClusters];
		
		//boucle de calcul des D(d,c)
		for(int i=0;i<nbClusters;i++){
			double[] center_i = centers[i];
			for(int j=0;j<nbDescriptors;j++){
				Descriptor<float[]> descriptor_j = list.get(j);
				float[] descr_j = descriptor_j.getD();
				double somme_diff =0;
				for(int k=0;k<centers[i].length;k++){
					double a = (double) descr_j[k];
					double b = center_i[k];
					double diff = (a-b);
					somme_diff += (diff*diff);
					
				}
				matriceD[j][i] = somme_diff/(sigma[i]*sigma[i]);
				
			}
		}
		
		//on recupere les k plus proches voisins pour chaque descripteur
		int l=0;
		for(Descriptor<float[]> d:list){
			matriceD[l]=Trie.trie2(5, matriceD[l]);
			l++;
		}
		
		
		//pooling
		double[] pool=new double[nbClusters];
		int sommeT=0;
		for(int i=0;i<nbClusters;i++){
			l=0;
			
			double sumpool=0;
			for(Descriptor<float[]> d:list){
				sumpool+=matriceD[l][i];
				l++;
			}
			pool[i]=sumpool;
			sommeT+=sumpool;
		}
		///////
		
		//normalisation a remplir
		for(int i=0;i<pool.length;i++){
			   BigDecimal bd = new BigDecimal(pool[i]/sommeT);
			   //ici essayer de trouve run autre nombre ...
			    //bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
			    
			
			pool[i]=bd.doubleValue();
		}
		
		//
		ArrayList<double[]> bag= new ArrayList<double[]>();
		bag.add(pool);
		return bag;
	}
	

	
	
}

