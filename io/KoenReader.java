package upmc.imw.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class KoenReader {
	

	@SuppressWarnings(value={"unchecked"})
	public static ArrayList<Descriptor> readXMLStream(LineNumberReader iStream) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		

		//dimension
		String line = iStream.readLine().trim();
		int dimension = Integer.parseInt(line);

		//nbpoints
		line = iStream.readLine().trim();
		int nbPoints = Integer.parseInt(line);

		ArrayList<Descriptor> list = new ArrayList<Descriptor>();
	
		
		//get the points
		for(int i = 0 ; i < nbPoints; i++)
		{
			float[] point = new float[dimension];
			
			String ligne = iStream.readLine();
			if (ligne == null){
				throw new IOException("Erreur a la lecture du "+i+" eme point !!!!");
			}

			line = ligne.trim();

			int firstIndex = line.indexOf(";");
			if(firstIndex <= 0)
			{
				//System.err.println("invalid line (first ';') : "+line);
				throw new IOException("invalid line (first ';') : "+line);
				//continue;
			}
			int secondIndex = line.substring(firstIndex+1).indexOf(";") + firstIndex + 1;
			if(secondIndex <= firstIndex)
			{
				//System.err.println("invalid line (second ';') : "+line);
				throw new IOException("invalid line (second ';') : "+line);
				//continue;
			}

			//header
			String head = line.substring(0, firstIndex).trim();
			StringTokenizer headTokens = new StringTokenizer(head);

			if(!headTokens.nextToken().equalsIgnoreCase("<CIRCLE"))
			{
				//System.err.println("Error in points, continuing...");
				throw new IOException("Error in points, continuing...");
				//continue;
			}
			
			float x = Float.parseFloat(headTokens.nextToken());
			float y = Float.parseFloat(headTokens.nextToken()); 
			float s = Float.parseFloat(headTokens.nextToken()); 
			float a = Float.parseFloat(headTokens.nextToken()); 

			

			//body
			String body = line.substring(firstIndex+1, secondIndex).trim();
			StringTokenizer bodyTokens = new StringTokenizer(body);

			for(int j = 0; j < dimension ; j++)
			{
				try{
					point[j] = Float.parseFloat(bodyTokens.nextToken());
				}
				catch (Exception e){
					throw new IOException("Error reading descriptor....");
				}
			}

			SIFTDescriptor sift = new SIFTDescriptor();
			sift.setD(point);
			sift.setXmin((int) (x-s));
			sift.setXmax((int) (x+s));
			sift.setYmin((int) (y-s));
			sift.setYmax((int) (y+s));
			sift.setShape("CIRCLE");
			list.add(sift);
		}


		iStream.close();


		return list;
		
	}

}
