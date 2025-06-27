/**
 * Ce programme nécessite Java 11 ou supérieur (requis pour l'utilisation des types modernes et des lambdas).
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Classe représentant un graphe orienté construit à partir d'un fichier de liens.
 */
public class Graphe {
	/**
	 * Liste des liens (arêtes) du graphe.
	 */
	private final List<Lien> donnees = new ArrayList<>();

	/**
	 * Construit un graphe à partir d'un fichier texte.
	 * @param nomdufichier le nom du fichier contenant les liens
	 * @throws IOException en cas d'erreur de lecture du fichier
	 */
	public Graphe(String nomdufichier) throws IOException {
		litFichier(new File(nomdufichier));
	}

	/**
	 * Calcule la matrice d'adjacence du graphe (à normaliser pour obtenir la matrice de transition).
	 * @return matrice d'adjacence booléenne (mat[i][j] == true s'il existe un lien de i vers j)
	 */
	public boolean[][] calculeMatrice() {
		int compteur = 0;
		Map<String, Integer> index = new HashMap<>();
		for (Lien k : donnees) {
			if (!index.containsKey(k.mDebut))
				index.put(k.mDebut, compteur++);
			if (!index.containsKey(k.mFin))
				index.put(k.mFin, compteur++);
		}
		boolean[][] mat = new boolean[compteur][compteur];
		for (Lien e : donnees) {
			int ikey = index.get(e.mDebut);
			int ival = index.get(e.mFin);
			mat[ikey][ival] = true;
		}
		return mat;
	}

	/**
	 * Retourne la liste ordonnée des noeuds du graphe (sans doublons, dans l'ordre d'apparition).
	 * @return liste des noeuds
	 */
	public List<String> noeuds() {
		List<String> n = new ArrayList<>();
		Set<String> index = new HashSet<>();
		for (Lien k : donnees) {
			if (index.add(k.mDebut)) {
				n.add(k.mDebut);
			}
			if (index.add(k.mFin)) {
				n.add(k.mFin);
			}
		}
		return n;
	}

	/**
	 * Lit un fichier texte et ajoute les liens à la liste des données du graphe.
	 * Chaque ligne du fichier doit être de la forme "A->B" (arête de A vers B).
	 * @param fichier le fichier à lire
	 * @throws IOException en cas d'erreur de lecture
	 */
	private void litFichier(File fichier) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] vals = line.split("->");
				if (vals.length != 2) continue;
				donnees.add(new Lien(vals[0].trim(), vals[1].trim()));
				System.out.println(vals[0].trim() + "->" + vals[1].trim());
			}
		}
	}

	/**
	 * Point d'entrée du programme. Affiche la liste des noeuds et la matrice d'adjacence.
	 * @param args args[0] doit contenir le nom du fichier à lire
	 * @throws IOException en cas d'erreur de lecture
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Usage: java Graphe <fichier>");
			System.exit(1);
		}
		Graphe g = new Graphe(args[0]);
		List<String> n = g.noeuds();
		n.forEach(System.out::println);
		boolean[][] m = g.calculeMatrice();
		for (boolean[] ligne : m) {
			for (boolean val : ligne) {
				System.out.print(val ? "1 " : "0 ");
			}
			System.out.println();
		}
	}
}


/**
 * Classe représentant un lien (arête orientée) entre deux noeuds du graphe.
 */
class Lien {
	/**
	 * Noeud de départ du lien.
	 */
	final String mDebut;
	/**
	 * Noeud d'arrivée du lien.
	 */
	final String mFin;

	/**
	 * Construit un lien entre deux noeuds.
	 * @param debut noeud de départ
	 * @param fin noeud d'arrivée
	 */
	public Lien(String debut, String fin) {
		this.mDebut = debut;
		this.mFin = fin;
	}
}