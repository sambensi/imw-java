package upmc.imw.bin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import upmc.imw.dictionary.ThreadedKMeans;
import upmc.imw.io.Descriptor;
import upmc.imw.io.DescriptorReader;
import upmc.imw.io.XMLReader;

public class VisualCodeBook {

	//option creation
	static Option directory = OptionBuilder.withArgName("directory")
									.hasArg()
									.withDescription("input directory containing descriptors")
									.withLongOpt("input")
									.create("i");
	static Option nbclusters = OptionBuilder.withArgName("number")
											.hasArg()
											.withDescription("number of clusters")
											.withLongOpt("nbcluster")
											.create("n");
	static Option nbpoints = OptionBuilder.withArgName("nb points")
											.hasArg()
											.withDescription("number of sampled points per image")
											.withLongOpt("nb-points")
											.create("p");
	static Option maxpoints = OptionBuilder.withArgName("max points")
											.hasArg()
											.withDescription("max number of sampled points")
											.withLongOpt("max-points")
											.create("m");
	static Option output = OptionBuilder.withArgName("codebook file")
											.hasArg()
											.withDescription("output codebook file (default output.obj)")
											.withLongOpt("output")
											.create("o");
	static Option stat = OptionBuilder.withArgName("stat file")
											.hasArg()
											.withDescription("output stat file (default stat.txt)")
											.withLongOpt("stat")
											.create("s");
	static Option l1norm = new Option("l1norm", "normalize descriptors with l1 norm");
	static Option help = new Option("help", "print this message");
	
	static Options options = new Options();
	
	static {
		options.addOption(directory);
		options.addOption(nbclusters);
		options.addOption(nbpoints);
		options.addOption(maxpoints);
		options.addOption(output);
		options.addOption(stat);
		options.addOption(l1norm);
		options.addOption(help);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String directory = null;
		String output = null;
		String stat = null;
		int nbCluster = 0;
		int nbPoints = 0;
		int maxPoints = 0;
		boolean l1norm = false;
		
		//option parsing		
	    // create the parser
	    CommandLineParser parser = new GnuParser();
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        //input
	        if(!line.hasOption("input"))
	        {
	        	// automatically generate the help statement
	        	HelpFormatter formatter = new HelpFormatter();
	        	formatter.printHelp( "VisualCodeBook", options );
	        	System.exit(-1);
	        }
	        else
	        {
	        	directory = line.getOptionValue("i");
	        }
	        // output
	        output = line.getOptionValue("output", "output.obj");
	        //stat
	        stat = line.getOptionValue("stat", "stat.txt");
	        // nb cluster
	        nbCluster = Integer.parseInt(line.getOptionValue("n", "128"));
	        // nb points
	        nbPoints = Integer.parseInt(line.getOptionValue("p", "100"));
	        //max points
	        maxPoints = Integer.parseInt(line.getOptionValue("m", "100000"));
	        //l1norm
	        l1norm = line.hasOption("l1norm");
	        
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
	    System.out.println("input : "+directory);
	    System.out.println("output : "+output);
	    System.out.println("nb clusters : "+nbCluster);
	    System.out.println("nb points : "+nbPoints);
	    System.out.println("max points : "+maxPoints);
	    System.out.println("l1norm : "+l1norm);
	    System.out.println();
	
	    //init random
		Random ran = new Random(System.currentTimeMillis());
		
	    // the list of descriptors
		final ArrayList<File> listOfDescriptorFiles = new ArrayList<File>();
		File[] f = (new File(directory)).listFiles();
		if (f == null)
			return;
		for (int i = 0; i < f.length; i++)
			listOfDescriptorFiles.add(f[i]);

		// training instances		
		float[][] instances;
		ArrayList<float[]> listOfInstances = new ArrayList<float[]>();
		
		int i = 0 ; 
		for(File instance : listOfDescriptorFiles)
		{
			if(i >= maxPoints)
				break;
			boolean isDir = instance.getName().charAt(0) == '.';
			if(!isDir){

				ArrayList<double[]> list2 = new ArrayList<double[]>();
				try{
					ArrayList<Descriptor> list = DescriptorReader.readFile(instance.getAbsolutePath());
					if(list==null){
						System.err.println("Erreur avec le descripteur : "+instance.getName());
					}
					for(Descriptor d : list)
					{
						if(d.getD() instanceof double[])
						{
							double[] sde = (double[])d.getD();
							list2.add(sde);
						}
						else if(d.getD() instanceof float[])
						{
							float[] sde = (float[])d.getD();
							double[] ode = new double[sde.length];
							for(int n = 0 ; n < sde.length; n++)
							{
								ode[n] = sde[n];
							}
							list2.add(ode);
						}
						else if(d.getD() instanceof int[])
						{
							int[] sde = (int[])d.getD();
							double[] ode = new double[sde.length];
							for(int n = 0 ; n < sde.length; n++)
							{
								ode[n] = sde[n];
							}
							list2.add(ode);
						}
						else if(d.getD() instanceof char[])
						{
							char[] sde = (char[])d.getD();
							double[] ode = new double[sde.length];
							for(int n = 0 ; n < sde.length; n++)
							{
								ode[n] = sde[n];
							}
							list2.add(ode);
						}
					}

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}


				if(list2.isEmpty())
					continue;

				for (int j = 0; j < nbPoints; j++) // number of points taken in this image
				{

					if(list2.isEmpty())
						break;

					int r = ran.nextInt(list2.size());
					double[] desc = list2.remove(r);

					if(desc == null)
						continue;

					//l1 norm better for k-means option
					if(l1norm)
					{
						double sum = 0;
						for(int n = 0 ; n < desc.length; n++)
						{
							if(Double.isNaN(desc[n]))
								throw new ArithmeticException("desc is NaN");
							sum += desc[n];
						}
						if(sum == 0)
							sum = 1;

						float[] newDesc = new float[desc.length];
						for (int k = 0; k < newDesc.length; k++)
							newDesc[k] = (float) (desc[k]/sum);

						listOfInstances.add( newDesc);
						i++;
					}
					else 
					{
						float[] newDesc = new float[desc.length];
						for (int k = 0; k < newDesc.length; k++)
						{
							if(Double.isNaN(desc[k]))
								throw new ArithmeticException("desc is NaN");
							newDesc[k] = (float) (desc[k]);
						}
						listOfInstances.add( newDesc);
						i++;
					}

				}
				if (i % 1000 == 0)
				{
					System.out.println(i + " points added.");
					Runtime run = Runtime.getRuntime();
					System.out.println(" free : "+(run.freeMemory()/1000000)+" total : "+(run.totalMemory()/1000000));
				}
			}

		}
		System.out.println("# total points added = "+i);
		instances = new float[listOfInstances.size()][];
		listOfInstances.toArray(instances);
		
		Runtime.getRuntime().gc();
		System.out.println(" free : "+(Runtime.getRuntime().freeMemory()/1000000)+" total : "+(Runtime.getRuntime().totalMemory()/1000000));
		
		// clustering
		long time = System.currentTimeMillis();
		ThreadedKMeans km = new ThreadedKMeans(instances, nbCluster, 5000, Runtime.getRuntime().availableProcessors());
		

		System.out.println("getting clusters...");
		double[][] centers = km.getCenters();
		double[] sigma = km.getMeanDistance();
		int[] pop = km.getPopulationInCluster();
		
		System.out.println("done ("+(System.currentTimeMillis()-time)+").");
		System.out.println("Non empty clusters : "+centers.length);
		
		try
		{
			ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(output));
			objOut.writeObject(centers);
			System.out.println("centers written.");
			objOut.writeObject(sigma);
			System.out.println("standard deviations written");
			objOut.writeObject(pop);
			System.out.println("population written.");
			objOut.close();
			System.out.println("binary write bone.");
		
			PrintStream out = new PrintStream(new FileOutputStream(stat));
			//printing options
			out.println("################################");
			out.println("# VisualCodeBook options : ");
			out.println("# input : "+directory);
			out.println("# output : "+output);
			out.println("# nb clusters : "+nbCluster);
			out.println("# nb points : "+nbPoints);
			out.println("# max points : "+maxPoints);
			out.println("# l1norm : "+l1norm);
			out.println("################################");
			out.println();
			
			for(i = 0 ; i < centers.length; i++)
			{
				out.println("Cluster "+i+"("+pop[i]+")[+"+sigma[i]+"] : "+Arrays.toString(centers[i]));
			}
			out.close();
			System.out.println("stat written.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("codebook done, exiting.");
	}

}
