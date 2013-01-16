package upmc.imw.bin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import upmc.imw.classifier.Evaluator;
import upmc.imw.classifier.SMOSVM;
import upmc.imw.classifier.TrainingSample;
import upmc.imw.io.Dessin;
import upmc.imw.io.VOC2006IO;
import upmc.imw.kernel.IndexedCacheKernel;

public class SupervisedLearing {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		//CHARGEMENT DU NOYAU
		IndexedCacheKernel<String,double[]> gram=null;
		FileInputStream fis;
		try {
			fis = new FileInputStream("GRAM.obj");
			
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(fis);
				
				
				gram = (IndexedCacheKernel<String,double[]>) ois.readObject();

				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Chargement du noyeau fini
		
		//GENERATION DES EXEMPLES D APPRENTISSAGE ET DE TEST
				ArrayList<ArrayList<TrainingSample<String>>> listeTrain = new ArrayList<ArrayList<TrainingSample<String>>>();
				ArrayList<ArrayList<TrainingSample<String>>> listeTest = new ArrayList<ArrayList<TrainingSample<String>>>();
				
				for(int i=1;i<=VOC2006IO.nbCat();i++){
					listeTrain.add(new ArrayList<TrainingSample<String>>());
					listeTest.add(new ArrayList<TrainingSample<String>>());
				}
				
					for(int i=0;i<VOC2006IO.nbCat();i++){
					VOC2006IO.generateTraingTestingSample(i ,  listeTrain.get(i) ,  listeTest.get(i) , "ImageSets/");
					//System.out.println( "blablabal" + listeTrain.get(i) +"    blablabal   " +  listeTest.get(i));
				}
		//GENERATION DES EXEMPLES D APPRENTISSAGE ET DE TEST FINI
				
				
		//APPRENTISSAGE D UN CLASSIFIEUR SVM BINAIRE POUR CHAQUE CATEGORIE
				//liste de classifieur
				ArrayList<SMOSVM<String>> listeClassifieur = new ArrayList<SMOSVM<String>>();
				//on met nbCat classifieur dans cette liste , chacun de ces classifieurs est construit avec le kernel "gram" recupere
				//plus haut
				for(int i=0;i<VOC2006IO.nbCat();i++){
					listeClassifieur.add(new SMOSVM<String>(gram));
				}
				//on apprend chacun de ces classifieurs avec la liste de TrainingSample de la categorie correspondante
				//convergence on pose setC a 1
					for(int i=0;i<VOC2006IO.nbCat();i++){
					System.out.println(i);
					listeClassifieur.get(i).train(listeTrain.get(i));
					listeClassifieur.get(i).setC(1);
				}
				
				//EVALUATION DES PERFORMANCES DE CLASSIFICATION
				ArrayList<Evaluator<String>> listeEvaluator = new ArrayList<Evaluator<String>>();
				for(int i=0;i<VOC2006IO.nbCat();i++){
					listeEvaluator.add(new Evaluator<String>(listeClassifieur.get(i),listeTrain.get(i)));
				}
				//appel a la methode getTestingMap de chaque Evaluator
				for(int i=0;i<VOC2006IO.nbCat();i++){
					listeEvaluator.get(i).evaluateTestingSet(listeTest.get(i));
					listeEvaluator.get(i).getTestingMAP();
					double[][] rp = listeEvaluator.get(i).getRecallPrecisionCurve();
					System.out.println(rp.length);
					System.out.println(rp[0].length);
					BufferedImage bf = new BufferedImage(500, 500, BufferedImage.TYPE_BYTE_GRAY);
					Dessin.TraceRecallPrecisionCurve(VOC2006IO.nbCat(), rp, rp[0].length, bf, Color.BLUE);
					File dossier = new File("resultats");
					dossier.mkdir();
					File imageSave = new File("./resultats/"+VOC2006IO.category(i)+".jpg");
					try {
						ImageIO.write(bf,"jpg",imageSave);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				

	}

}
