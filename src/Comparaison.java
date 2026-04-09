import java.util.Random;
import java.util.function.BiFunction;

public class Comparaison {

    // fonction qui fait jouer 10 parties entre deux evals, et renvoie la meilleure
    // si il n'y a pas de victoire "indiscutable", augmente la profondeur et fait rejouer
    public static BiFunction<Joueur, EtatJeu, Double> trouverMeilleurEval(
            BiFunction<Joueur, EtatJeu, Double> evalA,
            BiFunction<Joueur, EtatJeu, Double> evalB) {

        int profondeur = 1; // la profondeur maximale de recherche
        int nbParties = 200; // le nombre de parties
        int nbRepetitions = 1; // le nombre de "tournois"

        // répéter sur plusieurs profondeurs
        for (int prof=1; prof<=profondeur; prof++) {

            double total = 0; // total du score du tournoi
            int nbRepetitionsEffectuees = 0; // compteur de répétitions effectuées

            // répète le "tournoi" nbRepetitions fois
            for(int i=0; i<nbRepetitions; i++){
                total += Minimax.comparerEvals(evalA, evalB, prof, nbParties); // compare les deux evals sur nbParties
                nbRepetitionsEffectuees++; // incrémente le compteur

                // si jamais une eval domine largement (+ de 80% de victoire)
                if(Math.abs(total) >= nbRepetitionsEffectuees*nbParties*0.8){
                    break; // on arrete (optimisation)
                }
            }

            double moyenne = total/nbRepetitionsEffectuees;

            System.out.println("Score du tournoi :" + moyenne);

            // si une deux des evals gagne 15% de plus que l'autre
            if (Math.abs(moyenne) >= nbParties * 0.15) {
                // renvoie le gagnant
                if(moyenne>0){
                    return evalA;
                } else {
                    return evalB;
                }
            }
        }

        // sinon renvoie null (égalité ou résultats trop proches)
        return null;
    }

    // cherche le meilleur vecteur (a, b) avec la méthode 2
    public static double[] optimiser() {

        Random rand = new Random();

        // valeurs initiales de evalEvoluee
        double a = 10;
        double b = 1;

        // pas initial
        double pas = 5;

        // le nombre d'itérations du pas
        int nbIter = 50;

        // compteur de fois ou (a,b) n'a pas changé (d'affilée)
        int serie = 0;

        for (int iter=0; iter<nbIter; iter++) {

            // génère a' et b' dans une boule de rayon "pas"
            double aPrime = a + (rand.nextDouble()*2 - 1) * pas;
            double bPrime = b + (rand.nextDouble()*2 - 1) * pas;

            // contraint les valeurs dans le positif
            aPrime = Math.max(0, aPrime);
            bPrime = Math.max(0, bPrime);

            // construit les deux evals
            BiFunction<Joueur, EtatJeu, Double> evalX = Eval.getEvalParametree(a,b);
            BiFunction<Joueur, EtatJeu, Double> evalXPrime = Eval.getEvalParametree(aPrime,bPrime);

            // compare les deux en récupérant le gagnant
            BiFunction<Joueur, EtatJeu, Double> gagnant = Comparaison.trouverMeilleurEval(evalX, evalXPrime);

            // si x' est meilleur, on se déplace vers x' (null aussi pour inciter à explorer)
            if (gagnant == evalXPrime || gagnant == null) {

                // met à jour les coefficients
                a = aPrime;
                b = bPrime;

                serie = 0; // réinitialise la série

            } else { // sinon
                serie++; // incrémente la série

                // si la série dépasse les 10
                if (serie >= 10) {
                    System.out.println("Convergence détectée!"); // arrête
                    break;
                }
            }

            pas *= 0.95; // diminue le pas
            System.out.println("iter " + (iter+1) + " : a=" + a + " b=" + b + " pas=" + pas);
        }

        System.out.println("Vecteur optimal : a=" + a + " b=" + b);

        // renvoie le couple (a,b) optimal
        return new double[]{a, b};
    }

    public static void main(String[] args) {
        double[] resultat = optimiser();
    }
}
