package upmc.imw.bin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import upmc.imw.io.VOC2006IO;
import upmc.imw.kernel.IndexedCacheKernel;
import upmc.imw.kernel.IndexCacheKernelFactory;

public class IndexCacheKernel {

	/**
	 * @param args
	 */
	//option creation
		static Option inputDirectoryBow = OptionBuilder.withArgName("inputDirectoryBow")
										.hasArg()
										.withDescription("input directory containing bow")
										.withLongOpt("input")
										.create("i");
		
		static Option outputFileKernel = OptionBuilder.withArgName("outputFileKernel")
												.hasArg()
												.withDescription("output file for kernel")
												.withLongOpt("output")
												.create("o");
		
		
		static Options options = new Options();
		
		static {
			options.addOption(inputDirectoryBow);
			options.addOption(outputFileKernel);
		}
		
		
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		// TODO Auto-generated method stub
		long before = System.currentTimeMillis();
		String inputDirectoryBow = null;
		String outputFileKernel = null;
		
		//option parsing		
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        //input
	      
	        	inputDirectoryBow = line.getOptionValue("i","BOW");

	        // output
	        outputFileKernel = line.getOptionValue("o", "GRAM.obj");
	        
	      //printing options
		    System.out.println("VisualCodeBook options : ");
		    System.out.println("inputDirectoryBow : "+inputDirectoryBow);
		    System.out.println("outputDirectoryKernel : "+outputFileKernel);

		    File fichierkernel = new File("GRAM.obj");
		    IndexedCacheKernel<String,double[]> gram =null;
		    if(!fichierkernel.exists()){
		    	
		    	//recuperation des noms de fichiers bow
		    	File[] f = (new File(inputDirectoryBow)).listFiles();
		    	ArrayList<String> listNoms = new ArrayList<String>();
		    	ArrayList<double[]> listBow = new ArrayList<double[]>();
		    
		    	for(int i=0;i<f.length;i++){
		    		String nom = f[i].getName();
		    		if(!nom.contentEquals(".DS_Store")){
		    			listNoms.add(f[i].getName());
//		    		    System.out.println("****************************************************");
		    		}
		    	}
//		    System.out.println("****************************************************");
//		    	for(int i=0; i<listNoms.size(); i++)
//		    		listNoms.get(i).toString();
//			    System.out.println("****************************************************");
			    
			    
		    	// recuperation des bow contenus dans des fichiers textes
		    	System.out.println("nombre de bow"+listNoms.size());
		    	for(int i = 0;i<listNoms.size();i++){
		    		double[] bow = FichierIO.textFile2bow2(listNoms.get(i));
		    		listBow.add(bow);
		    	}

		    	
		    	
		    	ArrayList<ArrayList<double[]>> listeListebow = new ArrayList<ArrayList<double[]>>();
		    	listeListebow.add(listBow);
		    
		    
		    	gram =IndexCacheKernelFactory.createIndexCacheKernel(listNoms,listeListebow);
		    
		    	ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(outputFileKernel));
		    	objOut.writeObject(gram);
		    	objOut.close();
			
		    	}
		    	else{
		    		System.out.println("le fichier GRAM.obj existe dj");
		    	
		    		//CHARGEMENT DU NOYAU
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
		    	}
		    
		    
			//affichage du noyau
			BufferedImage bim =VOC2006IO.imageKernel(gram, "ImageSets/");
			File imageSave = new File("imageKernel");
			ImageIO.write(bim,"jpg",imageSave);
		    
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        	HelpFormatter formatter = new HelpFormatter();
        	formatter.printHelp( "VisualCodeBook", options );
        	System.exit(-1);
	    }
	    

	    long after = System.currentTimeMillis();
	    System.out.println(" time to finish "+(after-before)/1000.0);
	}
	

}