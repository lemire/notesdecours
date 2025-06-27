/**
 * Ce programme nécessite Java 11 ou supérieur (requis pour Set.of et l'utilisation des types modernes).
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Classe permettant de lire et manipuler des évaluations d'utilisateurs sur des articles.
 * Chaque ligne du fichier d'entrée doit être de la forme :
 * utilisateur article1:note1 article2:note2 ...
 * Exemple :
 * alice livre1:4.5 livre2:3.0
 * bob livre2:5.0
 * <p>
 * Ce programme nécessite Java 11 ou supérieur (requis pour Set.of et l'utilisation des types modernes).
 */
public class Evaluations {
	/**
	 * Map des évaluations : utilisateur → (article → note)
	 */
	private final Map<String, Map<String, Double>> mEvaluations = new HashMap<>();

	/**
	 * Construit l'ensemble des évaluations à partir d'un fichier texte.
	 * @param nomdufichier le nom du fichier à lire
	 * @throws IOException en cas d'erreur de lecture
	 */
	public Evaluations(String nomdufichier) throws IOException {
		litFichier(new File(nomdufichier));
	}

	/**
	 * Lit un fichier texte et remplit la map des évaluations.
	 * Chaque ligne doit être de la forme "utilisateur article:note ...".
	 * @param fichier le fichier à lire
	 * @throws IOException en cas d'erreur de lecture
	 */
	public void litFichier(File fichier) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] vals = line.split(" ");
				if (vals.length == 0) continue;
				String utilisateur = vals[0];
				if (mEvaluations.containsKey(utilisateur)) {
					System.err.println("La clef " + utilisateur + " est utilisée plus d'une fois.");
					continue;
				}
				Map<String, Double> notes = new HashMap<>();
				for (int k = 1; k < vals.length; ++k) {
					String[] n = vals[k].split(":");
					if (n.length != 2) {
						System.err.println("fichier mal formé? " + vals[k]);
						continue;
					}
					try {
						double d = Double.parseDouble(n[1]);
						notes.put(n[0], d);
					} catch (NumberFormatException ex) {
						System.err.println("Note invalide: " + n[1]);
					}
				}
				mEvaluations.put(utilisateur, notes);
			}
		}
	}

	/**
	 * Retourne l'ensemble des utilisateurs ayant des évaluations.
	 * @return ensemble des noms d'utilisateurs
	 */
	public Set<String> utilisateurs() {
		return mEvaluations.keySet();
	}

	/**
	 * Retourne l'ensemble des évaluations (article, note) pour un utilisateur donné.
	 * @param nom le nom de l'utilisateur
	 * @return ensemble des couples (article, note)
	 */
	public Set<Map.Entry<String, Double>> evaluations(String nom) {
		Map<String, Double> evals = mEvaluations.get(nom);
		return evals != null ? evals.entrySet() : Set.of();
	}

	/**
	 * Point d'entrée du programme. Affiche les évaluations de chaque utilisateur.
	 * @param args args[0] doit contenir le nom du fichier à lire
	 * @throws IOException en cas d'erreur de lecture
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Usage: java Evaluations <fichier>");
			System.exit(1);
		}
		Evaluations e = new Evaluations(args[0]);
		for (String nom : e.utilisateurs()) {
			System.out.println("Utilisateur " + nom);
			for (Map.Entry<String, Double> me : e.evaluations(nom)) {
				System.out.println("\taccorde une note de " + me.getValue() + " à l'article " + me.getKey());
			}
		}
	}
}