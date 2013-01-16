package upmc.imw.bin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Trie {

	/**
	 * @param args
	 */
	
	public static double[] trie1(int k,double[] vecteur){
		TreeSet<Double> tree = new TreeSet<Double>();
		Map<Double,Integer> map = new HashMap<Double, Integer>();
		double[] vecS = new double[vecteur.length];
		for(int i=0;i<vecteur.length;i++){
			tree.add((Double) vecteur[i]);
			map.put((Double) vecteur[i], i);
			
		}
	
		ArrayList<Double> meilleuresVoisins= new ArrayList<Double>();
		ArrayList<Integer> meilleuresIndices= new ArrayList<Integer>();
		for (int i=0;i<5;i++){
		double v1=tree.pollLast();
		meilleuresVoisins.add(v1);
		meilleuresIndices.add(map.get(v1));
		vecS[map.get(v1)]=1;
		
		}
		
		return vecS;
	}
	
	public static double[] trie2(int k,double[] vecteur){
		ArrayList<PairIndiceDouble> list = new ArrayList<PairIndiceDouble>();
		for(int i=0;i<vecteur.length;i++){
			list.add(new PairIndiceDouble(vecteur[i], i));
		}
		Collections.sort(list);
		
		double[] vect = new double[vecteur.length];
		for(int i=1;i<=k;i++){
			vect[list.get(i-1).getIndice()]=1;
		}
		return vect;
		
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Integer> trieVect(double[] vecteur){
		ArrayList<PairIndiceDouble> list = new ArrayList<PairIndiceDouble>();
		for(int i=0;i<vecteur.length;i++){
			list.add(new PairIndiceDouble(vecteur[i], i));
		}
		Collections.sort(list);
		//System.out.println(list);
		
		ArrayList<Integer> listeIndice = new ArrayList<Integer>();
		for(int i=list.size()-1;i>=0;i--){
			int indice = list.get(i).getIndice();
			listeIndice.add(indice);
			
		}
		
		return listeIndice;
		
	}
	
	public static void TraceRecallPrecisionCurve(double nbCat, double[][] ROC, int NbPts, BufferedImage Image, Color c){
//		int w=Image.getWidth();
//		int h=Image.getHeight();
//		double wi = (double)(w);
//		double hi = (double)(h);
//
//		double sx = wi*0.75;
//		double sy = hi*0.75;
//		double stx = wi/10.0;
//		double sty = hi/15.0;
//		
//		InitTraceRecallPrecisionCurve(nbCat,Image,sx,stx,sty);
//		
//		Graphics2D g2 = (Graphics2D) (Image.getGraphics());
//		
//		//System.out.println("NbPts="+NbPts);
//
//		
//		double x1,y1,x2,y2;
//		
//		// On trace la courbe ROC avec la couleur c
//		g2.setColor(c);
//		for(int i=1;i<NbPts;i++){
//			x1= stx+sx*ROC[0][i-1];
//			y1= hi-(sty+sy*ROC[1][i-1]);
//			x2= stx+sx*ROC[0][i];
//			y2= hi-(sty+sy*ROC[1][i]);
//			g2.draw(new Line2D.Double(x1,y1,x2,y2));
//		}
		//Graphics2D g3 = new Graphics2D();
		
	}

	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] c = {4,5,9,3,6,9,15,14,15};
		double[] a=trie1(4,c);
		double[] b=trie2(4,c);
		
		for(int i=0;i<a.length;i++){
			System.out.println(a[i]+" "+b[i]);
		}

	}

}
