package upmc.imw.io;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import upmc.imw.classifier.TrainingSample;
import upmc.imw.kernel.IndexedCacheKernel;

public class VOC2006IO {
	
	
	private static final String[] cat = {"bicycle","bus","car","cat","cow","dog","horse","motorbike","person","sheep"};
	
	
	public static String category(int index){
		return cat[index];
	}
	
	public static int nbCat(){
		return cat.length;
	}
	
	
	
	public static ArrayList<Integer> getCategory(ArrayList<String> bownames , String csDirAnnotation , String category){


		String csAnnotationFileTrain = csDirAnnotation + category+"_trainval.txt";
		ArrayList<TrainingSample<String>> list = new ArrayList<TrainingSample<String>> ();
		readAnnotation(csAnnotationFileTrain,list);

		ArrayList<Integer> labels = new ArrayList<Integer>();

		for(int j=0;j<bownames.size();j++){
			for(int i=0 ;i<list.size();i++){

				TrainingSample<String> t = list.get(i);
				if(t.sample.equals(bownames.get(j))){
					labels.add(t.label);
					break;
				}
			}
		}

		return labels;

	}

	public static void generateTraingTestingSample(int index , ArrayList<TrainingSample<String>> train , ArrayList<TrainingSample<String>> test , String csDirAnnotation){
		
			String csAnnotationFileTrain = csDirAnnotation + cat[index]+"_train.txt";
			System.out.println("Train annotation file: "+csAnnotationFileTrain);
			readAnnotation(csAnnotationFileTrain,train);
			
			
			String csAnnotationFileTest = csDirAnnotation + cat[index]+"_val.txt";
			System.out.println("Test annotation file: "+csAnnotationFileTest);
			readAnnotation(csAnnotationFileTest,test);
			
	}

	private static void readAnnotation(String csAnnotationFile, ArrayList<TrainingSample<String>> files){

		try{
			BufferedReader in  = new BufferedReader(new FileReader(csAnnotationFile));
			String line=in.readLine();
			int cptP = 0;
			int cptM=0;
			while (line != null) {
				String[] splits = line.split(" +" ,10);
				if(splits.length!=2){
					System.err.println("ERROR in ANNOTATION !!");
				}
				String bow = splits[0]+".obj";
				int label = Integer.parseInt(splits[1]);

				if(label==1)
					cptP++;
				else
					cptM++;

				TrainingSample <String> t  = new TrainingSample<String>(bow, label);
				files.add(t);
				
				
				line=in.readLine();
			}
			System.out.println("# plus ="+cptP+" # moins ="+cptM);

		}

		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public static BufferedImage imageKernel(IndexedCacheKernel<String, double[]> k , String annotationPath){
		
		ArrayList<TrainingSample<String>> train = new ArrayList<TrainingSample<String>>();
		ArrayList<TrainingSample<String>> test = new ArrayList<TrainingSample<String>>();
		VOC2006IO.generateTraingTestingSample(0, train, test, annotationPath);
		
		ArrayList<TrainingSample<String>> all = new ArrayList<TrainingSample<String>> ();
		
		for(int i=0;i<train.size();i++){
			TrainingSample<String> ts = new TrainingSample<String>(train.get(i).sample, train.get(i).label);
			all.add(ts);
		}
		for(int i=0;i<test.size();i++){
			TrainingSample<String> ts = new TrainingSample<String>(test.get(i).sample, test.get(i).label);
			all.add(ts);
		}
		
		double[][] gram = k.getKernelMatrix(all);
		
		BufferedImage im = Dessin.falsecolorImage(gram);
		
		return im;
		
		
	}

}
