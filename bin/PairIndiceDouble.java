package upmc.imw.bin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PairIndiceDouble implements Comparable{

	/**
	 * @param args
	 */
	
	private double valeur;
	private int indice;
	
	public PairIndiceDouble(double val, int ind){
		this.valeur = val;
		this.indice = ind;
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		
		PairIndiceDouble pair = (PairIndiceDouble) arg0;
		if(this.valeur > pair.getValeur()){
			return 1;
		}
		else
			if(this.valeur < pair.getValeur()){
				return -1;
			}
			else
				return 0;
		
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		PairIndiceFloat pair1 = new PairIndiceFloat(13.1f,0);
	//	PairIndiceFloat pair2 = new PairIndiceFloat(13.2f,1);
		float[] tab = {13.1f,15.2f,3f,100.5f,15.2f,15.2f,15.2f,15.2f,15.2f,15.2f};
		ArrayList<PairIndiceDouble> list = new ArrayList<PairIndiceDouble>();
		for(int i=0;i<tab.length;i++){
			list.add(new PairIndiceDouble(tab[i], i));
		}
		Collections.sort(list);
		for(PairIndiceDouble p:list){
			System.out.println(p);
		}
		
	}
	
	public double getValeur() {
		return valeur;
	}
	public void setValeur(float valeur) {
		this.valeur = valeur;
	}
	public int getIndice() {
		return indice;
	}
	public void setIndice(int indice) {
		this.indice = indice;
	}

	@Override
	public String toString() {
		return "PairIndiceFloat [valeur=" + valeur + ", indice=" + indice + "]";
	}
	
	

}
