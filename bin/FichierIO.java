package upmc.imw.bin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import upmc.imw.kernel.IndexedCacheKernel;

public class FichierIO {
	public static void bow2textFile(double[] bow,String nomFichier) throws IOException{
		BufferedWriter buff = new  BufferedWriter(new FileWriter(new File("BOW"+"/"+nomFichier+".obj")));
		System.err.println();
		for(int i=0;i<bow.length;i++){
			buff.write(i+" "+new Double(bow[i]).toString()+"\n");
		}
		
		buff.close();
	}
	
	public static void bow2objectFile(ArrayList<double[]> bow,String nomFichier) throws IOException{
		FileOutputStream out = new FileOutputStream("BOW"+"/"+nomFichier+".obj");
	
		ObjectOutputStream objout = new ObjectOutputStream(out);
		objout.writeObject(bow);
		objout.flush();
		objout.close();
		out.close();
	}
	
	public static double[] textFile2bow(String nomFichier) throws IOException{
		System.out.println(nomFichier+"lkj");
		FileReader fr = new FileReader("BOW/"+nomFichier+".obj");
		BufferedReader in = new BufferedReader(fr);
		String buf = "";
		try {
			buf = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] tab =buf.split(" ");
		
		double[] bow = new double[tab.length];
		for(int i=0;i<tab.length;i++){
			
			bow[i]= new Double(tab[i]);
		}
		fr.close(); in.close();
		return bow;
	}
	//parse un fichier texte de type .dat (indice sur la premiere colonne et valeur correspondante sur la deuxieme)
	public static double[] textFile2bow2(String nomFichier) throws FileNotFoundException{
		FileReader fr = new FileReader("BOW/"+nomFichier);
		BufferedReader in = new BufferedReader(fr);
		double[] bow = new double[200];
		String buf = "";
		try {
			int i=0;
			buf = in.readLine();
			while(buf!=null){
				String[] tab =buf.split(" ");
				bow[i] = new Double(tab[1]);
				i++;
				buf = in.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bow;
	}
	
	public static double[] textFile2bow3(String nomFichier) throws FileNotFoundException, ClassNotFoundException{
		FileInputStream fis;
		ArrayList<double[]> bow = null;
		try {
			fis = new FileInputStream("BOW/"+nomFichier+".obj");
		
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(fis);
			
			
				bow = (ArrayList<double[]>) ois.readObject();

				ois.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bow.get(0);
		
		
	}
}
