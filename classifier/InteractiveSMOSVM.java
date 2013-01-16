/**
 * 
 */
package upmc.imw.classifier;
import java.util.ArrayList;
import java.util.Collections;

import upmc.imw.bin.Trie;
import upmc.imw.kernel.Kernel;
/**
 * @author 2763358
 *
 */


public class InteractiveSMOSVM extends SMOSVM<String>{

	
	
	public InteractiveSMOSVM(Kernel<String> k) {
		super(k);
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<String> bestExamples(ArrayList<String> listimages){
		int taille = listimages.size();
		double[] score = new double[taille];
		for(int i=0;i<taille;i++){
			
			score[i]=super.valueOf(listimages.get(i));
			
		}
		ArrayList<Integer> listeIndices =Trie.trieVect(score);
		
		ArrayList<String> meilleurs = new ArrayList<String>();
		for(int i=0;i<listeIndices.size();i++){
			
			meilleurs.add(listimages.get(listeIndices.get(i)));
			
		}
		return meilleurs;
		
	}
		public ArrayList<String> uncertainSamples(ArrayList<String> listimages){
			int taille = listimages.size();
			double[] score = new double[taille];
			for(int i=0;i<taille;i++){
				score[i]=Math.abs(this.valueOf(listimages.get(i)));
			}
			ArrayList<Integer> listeIndices =Trie.trieVect(score);
			ArrayList<String> plusIncertain = new ArrayList<String>();
			for(int i=0;i<listeIndices.size();i++){
				plusIncertain.add(listimages.get(listeIndices.get(listeIndices.size()-i-1)));
			}
			return plusIncertain;
			
		}
	}
	

}
