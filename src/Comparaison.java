import java.util.Random;
import java.util.function.BiFunction;

public class Comparaison {

    // fonction qui fait jouer des parties entre deux evals, et renvoie la meilleure
    // si il n'y a pas de victoire "indiscutable", augmente la profondeur et fait rejouer
    public static BiFunction<Joueur, EtatJeu, Double> trouverMeilleurEval(
            BiFunction<Joueur, EtatJeu, Double> evalA,
            BiFunction<Joueur, EtatJeu, Double> evalB,
            int nbParties, int profondeur) {

        // répéter sur plusieurs profondeurs
        for (int prof=1; prof<=profondeur; prof++) {

            double score = Minimax.comparerEvals(evalA, evalB, prof, nbParties); // compare les deux evals sur nbParties

            // si une deux des evals gagne 15% de plus que l'autre
            if (Math.abs(score) >= nbParties * 0.15) {
                // renvoie le gagnant
                if(score>0){
                    return evalA;
                } else {
                    return evalB;
                }
            }
        }

        // sinon renvoie null (égalité ou résultats trop proches)
        return null;
    }

    // cherche le meilleur vecteur (a, b, c) avec la méthode 2
    public static double[] optimiser(double[] x, double pasInitial, int nbIter, int nbParties, int profondeurMax) {

        Random rand = new Random();

        // valeurs initiales de evalEvoluee
        double a = x[0];
        double b = x[1];
        double c = x[2];

        // pas initial
        double pas = pasInitial;

        // compteur de fois ou (a,b) n'a pas changé (d'affilée)
        int serie = 0;

        for (int iter=0; iter<nbIter; iter++) {

            // génère a' et b' dans une boule de rayon "pas"
            double aPrime = a + (rand.nextDouble()*2 - 1) * pas;
            double bPrime = b + (rand.nextDouble()*2 - 1) * pas;
            double cPrime = c + (rand.nextDouble()*2 - 1) * pas;

            // contraint les valeurs dans le positif
            aPrime = Math.max(0, aPrime);
            bPrime = Math.max(0, bPrime);
            cPrime = Math.max(0, cPrime);

            // construit les deux evals
            BiFunction<Joueur, EtatJeu, Double> evalX = Eval.getEvalParametree(a,b,c);
            BiFunction<Joueur, EtatJeu, Double> evalXPrime = Eval.getEvalParametree(aPrime,bPrime,cPrime);

            // compare les deux en récupérant le gagnant
            BiFunction<Joueur, EtatJeu, Double> gagnant =
                    Comparaison.trouverMeilleurEval(evalX, evalXPrime, nbParties, profondeurMax);

            // si x' est meilleur, on se déplace vers x' (null aussi pour inciter à explorer)
            if (gagnant == evalXPrime || gagnant == null) {

                // met à jour les coefficients
                a = aPrime;
                b = bPrime;
                c = cPrime;

                serie = 0; // réinitialise la série

            } else { // sinon
                serie++; // incrémente la série

                // si la série dépasse les 10
                if (serie >= 10) {
                    System.out.println("Convergence! Arrêt."); // arrête
                    break;
                }
            }

            pas *= 0.95; // diminue le pas

            /* System.out.printf("Itération %d : a=%.3f b=%.3f c=%.3f pas=%.3f %n", iter+1, a, b, c, pas); */
        }

        System.out.printf("Vecteur optimal : a=%.3f b=%.3f c=%.3f %n", a, b, c);

        // renvoie le couple (a,b,c) optimal
        return new double[]{a, b, c};
    }

    public static void main(String[] args) {

        // nombre de vecteurs à comparer
        int nbVecteurs = 10;

        // totaux des coefficients
        double totalA = 0;
        double totalB = 0;
        double totalC = 0;

        // vecteur initial
        double[] x = new double[]{10, 1, 5};

        for (int i=0; i<nbVecteurs; i++){

            System.out.printf("Essai " + (i+1) + " | ");
            double[] resultat = optimiser(x, 5, 50, 100, 2);
            totalA += resultat[0];
            totalB += resultat[1];
            totalC += resultat[2];
        }

        // moyenne des coefficients
        double moyenneA = totalA/nbVecteurs;
        double moyenneB = totalB/nbVecteurs;
        double moyenneC = totalC/nbVecteurs;

        System.out.printf("Moyenne : a=%.3f b=%.3f c=%.3f %n", moyenneA, moyenneB, moyenneC);
        // NB: la moyenne n'est pas une bonne estimation du vecteur optimal
    }
}
