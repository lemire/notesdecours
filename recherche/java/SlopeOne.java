import java.util.HashMap;
import java.util.Map;

/**
 * Daniel Lemire
 * Implémentation simple de l'algorithme Slope One pondéré en Java pour la recommandation collaborative basée sur les articles.
 * <p>
 * Exemple d'utilisation dans la méthode main.
 */

public class SlopeOne {
  
    /**
     * Exemple d'utilisation de l'algorithme Slope One.
     * On crée une base de données d'utilisateurs et de notes, puis on prédit des notes manquantes.
     */
    public static void main(String[] args) {
        Map<UserId, Map<ItemId, Float>> data = new HashMap<>();
        // Création des articles
        ItemId item1 = new ItemId("candy");
        ItemId item2 = new ItemId("dog");
        ItemId item3 = new ItemId("cat");
        ItemId item4 = new ItemId("war");
        ItemId item5 = new ItemId("strange food");
        // Création des utilisateurs et de leurs notes
        HashMap<ItemId, Float> user1 = new HashMap<>();
        HashMap<ItemId, Float> user2 = new HashMap<>();
        HashMap<ItemId, Float> user3 = new HashMap<>();
        HashMap<ItemId, Float> user4 = new HashMap<>();
        user1.put(item1, 1.0f);
        user1.put(item2, 0.5f);
        user1.put(item4, 0.1f);
        data.put(new UserId("Bob"), user1);
        user2.put(item1, 1.0f);
        user2.put(item3, 0.5f);
        user2.put(item4, 0.2f);
        data.put(new UserId("Jane"), user2);
        user3.put(item1, 0.9f);
        user3.put(item2, 0.4f);
        user3.put(item3, 0.5f);
        user3.put(item4, 0.1f);
        data.put(new UserId("Jo"), user3);
        user4.put(item1, 0.1f);
        user4.put(item4, 1.0f);
        user4.put(item5, 0.4f);
        data.put(new UserId("StrangeJo"), user4);
        // Création du moteur de prédiction
        SlopeOne so = new SlopeOne(data);
        System.out.println("Données accumulées :");
        so.printData();
        // Prédiction de notes pour un nouvel utilisateur
        HashMap<ItemId, Float> user = new HashMap<>();
        System.out.println("Prédiction...");
        user.put(item5, 0.4f);
        System.out.println("Entrée :");
        SlopeOne.print(user);
        System.out.println("Résultat :");
        SlopeOne.print(so.predict(user));
        user.put(item4, 0.2f);
        System.out.println("Entrée :");
        SlopeOne.print(user);
        System.out.println("Résultat :");
        SlopeOne.print(so.predict(user));
    }
  
    /**
     * Données utilisateurs : chaque utilisateur est associé à une map d'articles et de notes.
     */
    Map<UserId, Map<ItemId, Float>> mData;
    /**
     * Matrice des différences moyennes entre les articles.
     */
    Map<ItemId, Map<ItemId, Float>> mDiffMatrix;
    /**
     * Matrice des fréquences d'observation des paires d'articles.
     */
    Map<ItemId, Map<ItemId, Integer>> mFreqMatrix;

    /**
     * Constructeur : initialise les matrices à partir des données utilisateurs.
     * @param data Données utilisateurs
     */
    public SlopeOne(Map<UserId, Map<ItemId, Float>> data) {
        mData = data;
        buildDiffMatrix();
    }
  
    /**
     * Prédit toutes les notes manquantes pour un utilisateur donné, en utilisant les pondérations (fréquences).
     * Pour chaque item inconnu, la prédiction est la moyenne pondérée des différences observées entre les articles connus de l'utilisateur
     * et les autres articles, additionnée à la note de l'utilisateur pour chaque item connu. La pondération est le nombre d'observations (fréquence).
     *
     * @param user Map des notes connues de l'utilisateur (clé : item, valeur : note)
     * @return Map des prédictions pour tous les articles (y compris ceux déjà notés)
     */
    public Map<ItemId, Float> predict(Map<ItemId, Float> user) {
        HashMap<ItemId, Float> predictions = new HashMap<>();
        HashMap<ItemId, Integer> frequencies = new HashMap<>();
        for (ItemId j : mDiffMatrix.keySet()) {
            predictions.put(j, 0.0f);
            frequencies.put(j, 0);
        }
        for (ItemId j : user.keySet()) {
            for (ItemId k : mDiffMatrix.keySet()) {
                try {
                    float nouvelleValeur = (mDiffMatrix.get(k).get(j).floatValue() + user.get(j).floatValue()) * mFreqMatrix.get(k).get(j).intValue();
                    predictions.put(k, predictions.get(k) + nouvelleValeur);
                    frequencies.put(k, frequencies.get(k) + mFreqMatrix.get(k).get(j).intValue());
                } catch (NullPointerException nu) {
                    // On ignore les cas où il n'y a pas de cooccurrence
                }
            }
        }
        for (ItemId j : predictions.keySet()) {
            if (frequencies.get(j) != 0) {
                predictions.put(j, predictions.get(j) / frequencies.get(j));
            }
        }
        for (ItemId j : user.keySet()) {
            predictions.put(j, user.get(j));
        }
        return predictions;
    }
  
    /**
     * Prédit toutes les notes manquantes pour un utilisateur donné, sans utiliser de pondérations.
     * Cette méthode applique l'algorithme Slope One non pondéré : pour chaque item inconnu, la prédiction est la moyenne des différences observées
     * entre les articles connus de l'utilisateur et les autres articles, additionnée à la note de l'utilisateur pour chaque item connu.
     *
     * @param user Map des notes connues de l'utilisateur (clé : item, valeur : note)
     * @return Map des prédictions pour tous les articles (y compris ceux déjà notés)
     */
    public Map<ItemId, Float> weightlesspredict(Map<ItemId, Float> user) {
        HashMap<ItemId, Float> predictions = new HashMap<>();
        for (ItemId j : mDiffMatrix.keySet()) {
            predictions.put(j, 0.0f);
        }
        for (ItemId j : user.keySet()) {
            for (ItemId k : mDiffMatrix.keySet()) {
                Float diff = mDiffMatrix.get(k).get(j);
                if (diff != null) {
                    float nouvelleValeur = diff + user.get(j);
                    predictions.put(k, predictions.get(k) + nouvelleValeur);
                }
            }
        }
        for (ItemId j : predictions.keySet()) {
            predictions.put(j, predictions.get(j) / user.size());
        }
        for (ItemId j : user.keySet()) {
            predictions.put(j, user.get(j));
        }
        return predictions;
    }


    /**
     * Affiche les données utilisateurs et leurs notes.
     */
    public void printData() {
        for (UserId user : mData.keySet()) {
            System.out.println(user);
            print(mData.get(user));
        }
    }

    /**
     * Affiche les notes d'un utilisateur.
     * @param user Map des notes (clé : item, valeur : note)
     */
    public static void print(Map<ItemId, Float> user) {
        for (ItemId j : user.keySet()) {
            System.out.println(" " + j + " --> " + user.get(j));
        }
    }

    /**
     * Construit les matrices de différences et de fréquences à partir des données utilisateurs.
     */
    public void buildDiffMatrix() {
        mDiffMatrix = new HashMap<>();
        mFreqMatrix = new HashMap<>();
        // Parcours des utilisateurs
        for (Map<ItemId, Float> user : mData.values()) {
            // Parcours des notes de l'utilisateur
            for (Map.Entry<ItemId, Float> entry : user.entrySet()) {
                if (!mDiffMatrix.containsKey(entry.getKey())) {
                    mDiffMatrix.put(entry.getKey(), new HashMap<>());
                    mFreqMatrix.put(entry.getKey(), new HashMap<>());
                }
                for (Map.Entry<ItemId, Float> entry2 : user.entrySet()) {
                    int oldcount = 0;
                    if (mFreqMatrix.get(entry.getKey()).containsKey(entry2.getKey()))
                        oldcount = mFreqMatrix.get(entry.getKey()).get(entry2.getKey());
                    float olddiff = 0.0f;
                    if (mDiffMatrix.get(entry.getKey()).containsKey(entry2.getKey()))
                        olddiff = mDiffMatrix.get(entry.getKey()).get(entry2.getKey());
                    float observeddiff = entry.getValue() - entry2.getValue();
                    mFreqMatrix.get(entry.getKey()).put(entry2.getKey(), oldcount + 1);
                    mDiffMatrix.get(entry.getKey()).put(entry2.getKey(), olddiff + observeddiff);
                }
            }
        }
        for (ItemId j : mDiffMatrix.keySet()) {
            for (ItemId i : mDiffMatrix.get(j).keySet()) {
                float oldvalue = mDiffMatrix.get(j).get(i);
                int count = mFreqMatrix.get(j).get(i);
                mDiffMatrix.get(j).put(i, oldvalue / count);
            }
        }
    }
}

/**
 * Identifiant d'utilisateur.
 */
class UserId {
    String content;

    public UserId(String s) {
        content = s;
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public String toString() {
        return content;
    }
}

/**
 * Identifiant d'un article.
 */
class ItemId {
    String content;

    public ItemId(String s) {
        content = s;
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public String toString() {
        return content;
    }
}


