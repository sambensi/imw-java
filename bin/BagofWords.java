/**
 * 
 */
package upmc.imw.bin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


import upmc.imw.bow.BoWFactory;
import upmc.imw.io.Descriptor;
import upmc.imw.io.DescriptorReader;

/**
 * @author 2763358
 *
 */
public class BagofWords {

	/**
	 * @param args
	 */
	
	
	@SuppressWarnings("static-access")
	static Option inputDossierDescrip = OptionBuilder.withArgName("inputDossierDescrip")
	.hasArg()
	.withDescription("input directory containing descriptors")
	.withLongOpt("input")
	.create("i");
	
@SuppressWarnings("static-access")
static Option inputfichierDico = OptionBuilder.withArgName("inputfichierDico")
			.hasArg()
			.withDescription("input file containing codebook")
			.withLongOpt("input")
			.create("c");

@SuppressWarnings("static-access")
static Option output = OptionBuilder.withArgName("output")
.hasArg()
.withDescription("nom du dossier de sortie")
.withLongOpt("output")
.create("o");

@SuppressWarnings("static-access")
static Option nbProches = OptionBuilder.withArgName("nbProches")
			.hasArg()
			.withDescription("localite pour le coding")
			.withLongOpt("nb-Proches")
			.create("k");

@SuppressWarnings("static-access")
static Option norm = OptionBuilder.withArgName("norm")
.hasArg()
.withDescription("type normalisation")
.withLongOpt("norm")
.create("n");

static Options options = new Options();

static {
options.addOption(inputDossierDescrip);
options.addOption(output);
options.addOption(inputfichierDico);
options.addOption(nbProches);
options.addOption(norm);
}
public static void main(String[] args ) throws ClassNotFoundException, IOException {
	// TODO Auto-generated method stub
	String inputDossierDescrip = null;
	String output = null;
	String inputFichierDico = null;
	int nbProches = 0;
	String norm = null;
	
	System.out.println("la");
	//option parsing		
    // create the parser
    CommandLineParser parser = new GnuParser();
    try {
        // parse the command line arguments
        CommandLine line = parser.parse( options, args );
        //input
       
        	inputDossierDescrip = line.getOptionValue("i","source/desc");
      
        	output = line.getOptionValue("o","BOW");

        	inputFichierDico = line.getOptionValue("c","dico.obj");
        
        
        nbProches = Integer.parseInt(line.getOptionValue("k", "5"));
        //norm
        norm = line.getOptionValue("n","l1");
        
    }
    catch( ParseException exp ) {
        // oops, something went wrong
        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
    	HelpFormatter formatter = new HelpFormatter();
    	formatter.printHelp( "VisualCodeBook", options );
    	System.exit(-1);
    }

    //printing options
    System.out.println("VisualCodeBook options : ");
    System.out.println("inputDossierDescrip : "+inputDossierDescrip);
    System.out.println("inputFichierDico : "+inputFichierDico);
    System.out.println("output : "+output);
    System.out.println("nbProches : "+nbProches);
    System.out.println("norm : "+norm);
    System.out.println();

    //RECUPERER DICO
	
	double[][]  centers=null;
	double[] sigma = null;
	int[] pop = null;
	
	FileInputStream fis;
	try {
		fis = new FileInputStream(inputFichierDico);
		
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(fis);
			
			
			centers= (double[][]) ois.readObject();
			sigma =(double[]) ois.readObject();
			pop = (int[]) ois.readObject();

			ois.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 
    
  //RECUPERER DESCRIPTEURS
    
    // the list of descriptors
	final ArrayList<File> listOfDescriptorFiles = new ArrayList<File>();
	File[] f = (new File(inputDossierDescrip)).listFiles();
	if (f == null)
		return;
	for (int i = 0; i < f.length; i++){
		listOfDescriptorFiles.add(f[i]);
		System.out.println("------------" + f[i].toString());

	}
	
	BoWFactory bowFact = new BoWFactory();
	
	File dossierSortie = new File(output);
	dossierSortie.mkdirs();
	
	// training instances
	int t=0;
	for(File instance : listOfDescriptorFiles)
	{
		String nom = instance.getName().substring(0, 6);
		boolean isDir = instance.getName().charAt(0) == '.';
		//if(!isDir){
			try{
				//if(Integer.parseInt(nom)>=2000 && Integer.parseInt(nom)<7000){
				//recuperer liste de descripteurs pour l'image courante
				ArrayList<Descriptor> list = DescriptorReader.readFile(instance.getAbsolutePath());
				if(list==null){
					System.err.println("Erreur avec le descripteur : "+instance.getName());
				}
				
				//creer bag of words avec la liste de descripteurs que l'on vient de recuperer
				ArrayList<double[]> BOW = bowFact.createBagofWords(list, centers, sigma);
			
				
					FichierIO.bow2textFile(BOW.get(0), nom);
				//}
				
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	
		//}
		

	}
}
}


