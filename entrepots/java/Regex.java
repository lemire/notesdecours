/**
*
* Outil de traitement de fichiers par les expressions régulières.
* Écrit par Daniel Lemire, professeur, professeur
* Université du Québec, TÉLUQ
*
*************
*  Utilisation:
*
*   java Regex monexpression mesfichiers
* 
*  par exemple: 
*
*         java Regex '".*"' toto.txt 
*
*  donne tout texte entre guillemets dans le fichier toto.txt. Le
*  traitement se fait ligne par ligne. Pour avoir le texte sans les 
*  guillemets, faire 
*
*         java Regex '"(.*)"' toto.txt
*  
*  Pour avoir plus d'information, vous pouvez utiliser le drapeau "-v"
*  comme ceci : 
*
*         java Regex -v '".*"' toto.txt
*
**/ 

import java.util.regex.*;
import java.io.*;

public class Regex {

  public static void main(String[] args) {
    boolean verbose = false;
    int pos = 0;
    if(args[pos].equals("-v")) {
      verbose = true;
      ++pos;
    }
    if(verbose) System.out.println("Expression régulières: "+args[pos]);
    Pattern RegexCompile = Pattern.compile(args[pos]);
    ++pos;
    long debut = System.currentTimeMillis();
    for(; pos < args.length ; ++pos) {
      try {
        if(verbose) System.out.println("Je traite le fichier: "+args[pos]);
        match(new File(args[pos]),RegexCompile, verbose);
        if(verbose) System.out.println("J'ai traité le fichier: "+args[pos]);
      } catch (IOException ioe) {
        System.out.println("Impossible de traiter le fichier: "+args[pos]);
        ioe.printStackTrace();
      }
    }
    long fin = System.currentTimeMillis();
    System.out.println("Temps écoulé: "+ ((fin-debut)/1000.0)+" s");
  }

  public static void match(File f, Pattern RegexCompile, boolean verbose) throws IOException {
    if(!f.isFile()) return;
    BufferedReader br = new BufferedReader(new FileReader(f));
    try {
      String ligne;
      boolean trouve = false;
      while((ligne = br.readLine()) != null) {
        Matcher m = RegexCompile.matcher(ligne);
        while( m.find() ) {
          trouve = true;
          System.out.println(f.getPath()+": "+m.group(m.groupCount()));
        }
      }
      if(verbose && !trouve) 
        System.out.println("Le motif n'a pas été trouvé!");
    } finally {
      br.close();
    }
  }
}



