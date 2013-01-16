package upmc.imw.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import upmc.imw.classifier.TrainingSample;

public class DataBaseIO {
	
	public static void generateSplitFile(ArrayList<ArrayList<String>> listimtrain ,ArrayList<ArrayList<String>> listimtest ,ArrayList<String> categories, String bowDir , int nNbImagesTrain){
		// Parsing input directory containing kernels
	    File[] f = (new File(bowDir)).listFiles();
		if (f == null)
			return;
		Random ran = new Random(System.currentTimeMillis());

		for(int j=0;j<categories.size();j++){
			ArrayList<String> imcattot = new ArrayList<String>();
			ArrayList<String> imcattrain = new ArrayList<String>();
			ArrayList<String> imcattest = new ArrayList<String>();
			
			for (int i = 0; i < f.length; i++){
				String filename = f[i].getName();
				if(categories.get(j).contains("-")){
					String[] split = categories.get(j).split("-");
					if(split.length!=2){
						System.err.println("ERREUR !");
					}
					if(filename.contains(split[0]) || filename.contains(split[1]) ){
						imcattot.add(filename);
					}
				}
				else{
					if(filename.contains(categories.get(j))){
						imcattot.add(filename);
					}
				}
			}
			Collections.shuffle(imcattot, ran);
			for(int i=0;i<imcattot.size();i++){
				if(imcattrain.size()<nNbImagesTrain && i<imcattot.size()/2  ){
					imcattrain.add(imcattot.get(i));
				}
				else{
					imcattest.add(imcattot.get(i));
				}
			}
			listimtrain.add(imcattrain);
			listimtest.add(imcattest);
			//System.out.println("categorie "+categories.get(j)+" # im train="+imcattrain.size()+" # im test="+imcattest.size()+" in train 0 : "+listimtrain.get(j).get(0));
		}
	}
	
	

	
	
	public static ArrayList<String> ReadCategories(String filename){
		ArrayList<String> categories = new ArrayList<String>();
		FileInputStream file = null;
		try{
			file = new FileInputStream(filename);
			BufferedReader d = new BufferedReader(new InputStreamReader(file));
			String line= d.readLine();
			while(line!=null && !line.isEmpty()){
				categories.add(line.trim());
				line= d.readLine();
			}
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		
		return categories;
		
	}
	
	public static void ReadAnnotationFile(FileInputStream file , ArrayList<TrainingSample<String>> listts ) throws AnnotationFileException{
		
		BufferedReader d = new BufferedReader(new InputStreamReader(file));
		String line=null;
		int nbPlus=0;
		int nNbMoins=0;
		try{
			line = d.readLine();
			if(line.trim().equals("Query")){
				line = d.readLine();
				listts.add(new TrainingSample<String>(line.trim(),1));
				line = d.readLine();
				nbPlus++;
				if(line == null)
					return;
				if(!line.trim().equals("Annotations")){
					throw new AnnotationFileException(" Mauvais format de Fichier d'annotation  !!");
				}
				line = d.readLine();
				while(line!=null){
					String[] splits= line.split("\\#", 100);
					listts.add(new TrainingSample<String>(splits[0].trim(),Integer.parseInt(splits[1])));
					
					if(Integer.parseInt(splits[1]) == 1)
						nbPlus++;
					else
						nNbMoins++;
					line = d.readLine();
				}

			}
			else{
				throw new AnnotationFileException(" Mauvauis format de Fichier d'annotation  !! Doit commencer par : Query");
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		//System.out.println("# Plus="+nbPlus+" # nNbMoins="+nNbMoins);
	}

	public static ArrayList<String> ReadDescriptorNames(String bowDir){
		ArrayList<String> res = new ArrayList<String>();


		File[] f = (new File(bowDir)).listFiles();
		if (f == null)
			return null;

		for (int i = 0; i < f.length; i++){
			String filename = f[i].getName();
			if(!(filename.charAt(0)=='.')){
				res.add(filename);
			}
		}
		return res;
	}
	
}
