package upmc.imw.io;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class Dessin {
	
	
	public static void DrawArrow(Graphics g, double xdeb, double ydeb, double taille, double theta , double theta1 ){
		
		int xfin,yfin;
		double xdec1;
		double ydec1;
		double xdec;
		double ydec;
		
		
		xdec1 = taille * Math.cos(theta + Math.PI);
		ydec1 = -taille*Math.sin(theta + Math.PI) ;
		
		//System.out.println("xdec1="+xdec1+" ydec1="+ydec1);
		
		xdec = xdec1* Math.cos(theta1) - ydec1*Math.sin(theta1);
		ydec =  xdec1* Math.sin(theta1)  + ydec1*Math.cos(theta1);
		
		xfin = (int)Math.round(xdeb + xdec);
		yfin = (int)Math.round(ydeb + ydec);
		
		//System.out.println("xdeb="+xdeb+" ydeb="+ydeb+" xfin="+xfin+" yfin="+yfin);
		
		g.drawLine((int)Math.round(xdeb), (int)Math.round(ydeb), xfin, yfin);
		
		
		xdec1 = taille * Math.cos(Math.PI-theta);
		ydec1 = -taille*Math.sin(Math.PI-theta) ;
		//System.out.println("xdec1="+xdec1+" ydec1="+ydec1);
		
		xdec = xdec1* Math.cos(theta1) - ydec1*Math.sin(theta1);
		ydec = xdec1* Math.sin(theta1) + ydec1*Math.cos(theta1);
		
		xfin = (int)Math.round(xdeb + xdec);
		yfin = (int)Math.round(ydeb + ydec);
		
		
		g.drawLine((int)Math.round(xdeb), (int)Math.round(ydeb), xfin, yfin);
		
		
		
	}
	
	public static void Write(Graphics g, double xdeb, double ydeb, double taille, String Name){
	
		Font f=new Font("arial",Font.PLAIN,(int)Math.round(taille));
		g.setFont(f);
		g.drawString( Name, (int)Math.round(xdeb), (int)Math.round(ydeb));
		
	}
	
	public static void InitTraceRecallPrecisionCurve(double nbCat, BufferedImage Image, double sx , double stx ,double sty){
		int w=Image.getWidth();
		int h=Image.getHeight();

		
		Graphics2D g = (Graphics2D) (Image.getGraphics());
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, w, h);
		
		double wi = (double)(w);
		double hi = (double)(h);

		
		g.setColor(new Color(0.1f,0.1f,0.1f));
		
		int taille = 30;
		// On trace les axes en blanc
		g.draw(new Line2D.Double(stx, hi-sty, wi-stx, hi-sty)); // axe x = FP (False Positives) 
		g.draw(new Line2D.Double(stx, hi-sty, stx, sty));	// axe y = TP (True Positives) 	
		Dessin.DrawArrow(g, wi-stx,  hi-sty, taille, Math.PI/7, 0);
		Dessin.DrawArrow(g, stx, sty, taille, -Math.PI/8, -Math.PI/2);
		
		Dessin.Write(g, wi-stx+10, hi-sty+10, 30, "R");
		Dessin.Write(g,stx-5, sty-5, 30, "P");
		
		// On trace la diagonale en rouge
		g.setColor(new Color(1.0f,0.0f,0.0f));
		g.draw(new Line2D.Double(stx, hi-sty-(hi-sty)/nbCat, sx+stx,hi-sty-(hi-sty)/nbCat)); // axe x = FP (False Positives) 
		//g2.draw(new Line2D.Double(stx, hi-sty, stx+sx, hi-(sy+sty)));
		//g2.draw(new Line2D.Double(stx, sty, stx+sx, hi-sty));
	}
	
	public static void TraceRecallPrecisionCurve(double nbCat, double[][] ROC, int NbPts, BufferedImage Image, Color c){
		int w=Image.getWidth();
		int h=Image.getHeight();
		double wi = (double)(w);
		double hi = (double)(h);

		double sx = wi*0.75;
		double sy = hi*0.75;
		double stx = wi/10.0;
		double sty = hi/15.0;
		
		InitTraceRecallPrecisionCurve(nbCat,Image,sx,stx,sty);
		
		Graphics2D g2 = (Graphics2D) (Image.getGraphics());
		
		//System.out.println("NbPts="+NbPts);

		
		double x1,y1,x2,y2;
		
		// On trace la courbe ROC avec la couleur c
		g2.setColor(c);
		for(int i=1;i<NbPts;i++){
			x1= stx+sx*ROC[0][i-1];
			y1= hi-(sty+sy*ROC[1][i-1]);
			x2= stx+sx*ROC[0][i];
			y2= hi-(sty+sy*ROC[1][i]);
			g2.draw(new Line2D.Double(x1,y1,x2,y2));
		}
		
		
	}
	
	
	public static void TraceROCCurve(double[][] ROC, int NbPts, BufferedImage Image, Color c){
		int w=Image.getWidth();
		int h=Image.getHeight();
		double wi = (double)(w);
		double hi = (double)(h);

		double sx = wi*0.75;
		double sy = hi*0.75;
		double stx = wi/10.0;
		double sty = hi/15.0;
		
		InitTraceROCCurve(Image,sx,stx,sty);
		
		Graphics2D g2 = (Graphics2D) (Image.getGraphics());
		
		//System.out.println("NbPts="+NbPts);

		
		double x1,y1,x2,y2;
		
		// On trace la courbe ROC avec la couleur c
		g2.setColor(c);
		for(int i=1;i<NbPts;i++){
			x1= stx+sx*ROC[0][i-1];
			y1= hi-(sty+sy*ROC[1][i-1]);
			x2= stx+sx*ROC[0][i];
			y2= hi-(sty+sy*ROC[1][i]);
			g2.draw(new Line2D.Double(x1,y1,x2,y2));
		}
		
		
	}
	
	public static void InitTraceROCCurve(BufferedImage Image, double sx , double stx ,double sty){
		int w=Image.getWidth();
		int h=Image.getHeight();

		
		Graphics2D g = (Graphics2D) (Image.getGraphics());
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, w, h);
		
		double wi = (double)(w);
		double hi = (double)(h);
		double sy = hi*0.75;

		
		g.setColor(new Color(0.1f,0.1f,0.1f));
		
		int taille = 30;
		// On trace les axes en blanc
		g.draw(new Line2D.Double(stx, hi-sty, wi-stx, hi-sty)); // axe x = FP (False Positives) 
		g.draw(new Line2D.Double(stx, hi-sty, stx, sty));	// axe y = TP (True Positives) 	
		Dessin.DrawArrow(g, wi-stx,  hi-sty, taille, Math.PI/7, 0);
		Dessin.DrawArrow(g, stx, sty, taille, -Math.PI/8, -Math.PI/2);
		
		Dessin.Write(g, wi-stx+10, hi-sty+10, 30, "FP");
		Dessin.Write(g,stx-5, sty-5, 30, "TP");
		
		// On trace la diagonale en rouge
		g.setColor(new Color(1.0f,0.0f,0.0f));
		g.draw(new Line2D.Double(stx, hi-sty, stx+sx , hi-(sty+sy) ) /*sty + (hi-sty)*0.25)*/ ); // axe x = FP (False Positives) 
		//g2.draw(new Line2D.Double(stx, hi-sty, stx+sx, hi-(sy+sty)));
		//g2.draw(new Line2D.Double(stx, sty, stx+sx, hi-sty));
	}
	
	

	public static BufferedImage falsecolorImage(double [][] val){


		BufferedImage res = null;

		double valMax = 4.0;

		// Normalisation
		double [][] valn = normalize(val, 0.0, valMax);

		res = new BufferedImage(val.length,val[0].length,BufferedImage.TYPE_INT_RGB );

		float valr,valg,valb;
		for(int i=0;i<val.length;i++){
			for(int j=0;j<val[i].length;j++){
				if(valn[i][j] < valMax/4){
					// Entre bleu et cyan
					// valn entre 0->1
					valr = 0.0f;
					valb=1.0f ;
					valg= (float)(valn[i][j]);
					Color c = new Color(valr,valg,valb);
					int rgb = c.getRGB();
					res.setRGB(i, j, rgb);
				}
				else if(valn[i][j] >= valMax/4 && valn[i][j] < valMax/2){
					// Entre cyan et vert
					// valn entre 1->2 
					valr = 0.0f;
					valb= -1.0f * (float)(valn[i][j]) +2.0f;
					valg= 1.0f;
					Color c = new Color(valr,valg,valb);
					int rgb = c.getRGB();
					res.setRGB(i, j, rgb);

				}
				else if(valn[i][j] >= valMax/2 && valn[i][j] < 3*valMax/4){
					// Entre  vert et jaune
					// valn entre 2->3 
					valr = (float)(valn[i][j])  - 2.0f;
					valb= 0.0f;
					valg= 1.0f;
					Color c = new Color(valr,valg,valb);
					int rgb = c.getRGB();
					res.setRGB(i, j, rgb);
				}
				else{
					// Entre  jaune et rouge
					// valn entre 3->4 
					valr = 1.0f;
					valb= 0.0f;
					valg= -1.0f * (float)(valn[i][j]) +4.0f;;
					Color c = new Color(valr,valg,valb);
					int rgb = c.getRGB();
					res.setRGB(i, j, rgb);
				}
			}
		}


		return res;
	}


	public static double[][] normalize(double[][] tab , double newMin, double newMax){

		double [][] tabn = new double[tab.length][tab[0].length];

		// Normalisation
		double max = -Double.MAX_VALUE;
		double min = Double.MAX_VALUE;
		for(int i=0;i<tab.length;i++){
			for(int j=0;j<tab[i].length;j++){
				if(tab[i][j]>max)
					max = tab[i][j];
				if(tab[i][j]<min)
					min = tab[i][j];
			}			
		}

		for(int i=0;i<tab.length;i++){
			for(int j=0;j<tab[i].length;j++){
				tabn[i][j] = (tab[i][j]-min) * (newMax-newMin)/(max-min) + newMin;
			}
		}

		return tabn;
	}


	

}
