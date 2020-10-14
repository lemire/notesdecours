/**
*
* Outil de vérification de motif par les expressions régulières.
* Écrit par Daniel Lemire, professeur
* Université du Québec, TÉLUQ
*
*************
*  Utilisation:
*
*   java Motif monexpression textes
*
*   par exemple,
*     
*     java Motif "foo.*" "foolalala"
*/


import java.util.regex.*;


public class Motif {
	public static void main(String[] args) {
		Pattern RegexCompile = Pattern.compile(args[0]);
		System.out.println(RegexCompile.matcher(args[1]).matches());
    }
}
