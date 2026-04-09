import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiFunction;

public class Minimax {

    // algorithme minimax avec élagage alpha/beta
    // même algorithme mais on arrête de chercher si la branche est inutile
    // cad : si on a déjà trouvé une meilleure branche
    public static double minimaxAB(Joueur j, EtatJeu etat, int profondeur, double alpha, double beta, BiFunction<Joueur, EtatJeu, Double> eval) {

        // si profondeur = 0 ou si l'état actuel est final
        if (profondeur == 0 || etat.etatFinal() != Gagnant.AUCUN) {
            return eval.apply(j, etat); // renvoie son évaluation directe (eval0)
        }

        // récupère le joueur courant
        Joueur joueurCourant = etat.getTourBlanc() ? Joueur.joueurBlanc : Joueur.joueurNoir;

        // génère les successeurs de l'état actuel
        List<EtatJeu> successeurs = etat.successeurs(joueurCourant);

        // si il n'y a aucun successeur (état final)
        if (successeurs.isEmpty()) {
            return eval.apply(j, etat); // renvoie son évaluation directe (eval0)
        }

        // sinon si on arrive ici, il y a des successeurs

        // si c'est au tour du joueur j, on maximise son score
        if (joueurCourant == j) {

            // max = -infini au début
            double max = Double.NEGATIVE_INFINITY;

            // pour chaque successeur généré
            for (EtatJeu s : successeurs) {

                // calcule son score en transmettant les paramètres alpha/beta (et la fonction eval0)
                double score = minimaxAB(j, s, profondeur-1, alpha, beta, eval);

                if (score > max) { // si le score est meilleur que le max
                    max = score; // met à jour le max
                }

                if (max > alpha) { // si le max est supérieur à alpha
                    alpha = max;  // met à jour alpha
                }

                // si alpha>beta (i.e on a déjà trouvé une branche plus prometteuse)
                if (alpha >= beta) {
                    break; // élagage (on arrête de chercher plus loin)
                }
            }
            return max; // on renvoie le score maximal trouvé
        }

        // sinon c'est au tour du joueur adverse, on minimise son score
        else {

            // min = +infini au début
            double min = Double.POSITIVE_INFINITY;

            // pour chaque successeur généré
            for (EtatJeu s : successeurs) {

                // calcule son score en transmettant les paramètres alpha/beta (et la fonction eval0)
                double score = minimaxAB(j, s, profondeur-1, alpha, beta, eval);

                if (score<min) { // si le score est moins bon que le min
                    min = score; // met à jour le min
                }

                if (min < beta) { // si le min est inférieur à beta
                    beta = min; // met à jour beta
                }

                // si alpha>beta (i.e on a déjà trouvé une branche plus prometteuse)
                if (alpha >= beta) {
                    break; // élagage (on arrête de chercher plus loin)
                }
            }

            return min; // on renvoie le score minimal trouvé
        }
    }


    // fonction qui renvoie le meilleur coup possible pour un joueur j dans un état
    // avec élagage alpha/beta cette fois-ci
    public static EtatJeu meilleurCoupAB(Joueur j, EtatJeu etat, int profondeur, BiFunction<Joueur, EtatJeu, Double> eval) {

        // génère les successeurs de l'état
        List<EtatJeu> successeurs = etat.successeurs(j);

        // si il n'y aucun successeur
        if(successeurs.isEmpty()){
            return etat; // renvoie l'état actuel
        }

        List<EtatJeu> meilleurs = new ArrayList<>(); // liste des etats avec le meilleur score
        double max = Double.NEGATIVE_INFINITY; // max = -infini
        // pour chaque successeur
        for (EtatJeu s : successeurs) {

            // calcule son score minimax avec élagage alpha/beta
            double score = minimaxAB(j, s, profondeur - 1,
                    Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, eval);

            // si le score est meilleur que le max actuel
            if (score>max) {
                max = score; // met à jour le max
                meilleurs.clear(); // réinitialise la liste des meilleurs
                meilleurs.add(s); // ajoute l'état aux meilleurs
            }

            // si le score est identique
            else if (score==max){
                meilleurs.add(s);  // ajoute aussi l'état aux meilleurs (car il est équivalent)
            }
        }

        // génère un indice aléatoire entre 0 et n-1
        int randomValue = new Random().nextInt(meilleurs.size());
        return meilleurs.get(randomValue); // renvoie le meilleur coup choisi aléatoirement
    }


    // fait jouer deux eval l'une contre l'autre sur une partie, et renvoie le gagnant
    public static Gagnant unePartie(BiFunction<Joueur, EtatJeu, Double> evalBlanc,
                                    BiFunction<Joueur, EtatJeu, Double> evalNoir,
                                    int profondeur) {

        // crée un état initial (c'est l'état qui va être utilisé toute la partie)
        EtatJeu etat = new EtatJeu(8);

        // tant que l'état n'est pas final
        while (etat.etatFinal() == Gagnant.AUCUN) {

            // récupère le joueur courant
            Joueur joueurCourant = etat.getTourBlanc() ? Joueur.joueurBlanc : Joueur.joueurNoir;

            // si c'est au tour du joueur blanc
            if(etat.getTourBlanc()){
                etat = meilleurCoupAB(joueurCourant, etat, profondeur, evalBlanc); // joue avec evalBlanc
            }

            // sinon c'est au tour du joueur noir
            else {
                etat = meilleurCoupAB(joueurCourant, etat, profondeur, evalNoir); // joue avec evalNoir
            }
        }

        return etat.etatFinal(); // renvoie le joueur gagnant
    }



    // fait jouer deux evals sur un certain nombre de parties, et renvoie le nomnbre de victoires
    // positif si evalA>evalB, sinon négatif (ou nul)
    public static double comparerEvals(BiFunction<Joueur, EtatJeu, Double> evalA,
                                    BiFunction<Joueur, EtatJeu, Double> evalB,
                                    int profondeur, int nbParties) {

        // compteur du nombre de victoires
        // +1 si evalA gagne, -1 si evalB gagne
        int victoires = 0;

        // fait jouer n parties
        // alterne le joueur qui commence par équité
        for (int i=0; i<nbParties; i++) {

            Gagnant resultat; // le gagnant de la partie

            if (i%2 == 0) {
                resultat = unePartie(evalA, evalB, profondeur); //evalA = blanc donc commence
                if (resultat == Gagnant.BLANC) victoires++; // incrémente si evalA gagne
                if (resultat == Gagnant.NOIR)  victoires--; // décrémente si evalB gagne
            } else {
                resultat = unePartie(evalB, evalA, profondeur); // evalB commence, evalB = blancs
                if (resultat == Gagnant.NOIR)  victoires++; // incrémente si evalA gagne
                if (resultat == Gagnant.BLANC) victoires--; // décrémente si evalB gagne
            }
        }

        return victoires;
    }

}



