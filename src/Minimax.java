import java.util.List;
import java.util.function.BiFunction;

public class Minimax {

    // algorithme minimax avec élagage alpha/beta
    // même algorithme mais on arrête de chercher si la branche est inutile
    // cad : si on a déjà trouvé une meilleure branche
    public static int minimaxAB(Joueur j, EtatJeu etat, int profondeur, int alpha, int beta, BiFunction<Joueur, EtatJeu, Integer> eval) {

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
            int max = Integer.MIN_VALUE;

            // pour chaque successeur généré
            for (EtatJeu s : successeurs) {

                // calcule son score en transmettant les paramètres alpha/beta (et la fonction eval0)
                int score = minimaxAB(j, s, profondeur-1, alpha, beta, eval);

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
            int min = Integer.MAX_VALUE;

            // pour chaque successeur généré
            for (EtatJeu s : successeurs) {

                // calcule son score en transmettant les paramètres alpha/beta (et la fonction eval0)
                int score = minimaxAB(j, s, profondeur-1, alpha, beta, eval);

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
    public static EtatJeu meilleurCoupAB(Joueur j, EtatJeu etat, int profondeur, BiFunction<Joueur, EtatJeu, Integer> eval) {

        // génère les successeurs de l'état
        List<EtatJeu> successeurs = etat.successeurs(j);

        // si il n'y aucun successeur
        if(successeurs.isEmpty()){
            return etat; // renvoie l'état actuel
        }

        EtatJeu meilleur = null; // pas de meilleur état pour l'instant
        int max = Integer.MIN_VALUE; // max = -infini

        // pour chaque successeur
        for (EtatJeu s : successeurs) {

            // calcule son score minimax avec élagage alpha/beta
            int score = minimaxAB(j, s, profondeur - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, eval);

            // si le score est meilleur que le max actuel
            if (score>max) {
                max = score; // met à jour le max
                meilleur = s; // met à jour le meilleur état
            }
        }

        return meilleur; // on renvoie l'état avec le meilleur score
    }


    // fait jouer deux eval l'une contre l'autre sur une partie, et renvoie le gagnant
    public static Gagnant unePartie(BiFunction<Joueur, EtatJeu, Integer> evalBlanc,
                                    BiFunction<Joueur, EtatJeu, Integer> evalNoir,
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



    // fait jouer deux evals sur un certain nombre de parties, et renvoie un entier correspondant au gagnant
    public static int comparerEvals(BiFunction<Joueur, EtatJeu, Integer> evalA,
                                    BiFunction<Joueur, EtatJeu, Integer> evalB,
                                    int profondeur, int nbParties) {

        // compteur du nombre de victoires
        // +1 si evalA gagne, -1 si evalB gagne
        int victoires = 0;

        // fait jouer n parties
        // alterne le joueur qui commence par équité
        for (int i = 0; i < nbParties; i++) {
            Gagnant resultat; // le gagnant de la partie

            if (i % 2 == 0) {
                resultat = unePartie(evalA, evalB, profondeur); // evalA commence, evalA = blancs
                if (resultat == Gagnant.BLANC) victoires++; // incrémente si evalA gagne
                if (resultat == Gagnant.NOIR)  victoires--; // décrémente si evalB gagne
            } else {
                resultat = unePartie(evalB, evalA, profondeur); // evalB commence, evalB = blancs
                if (resultat == Gagnant.NOIR)  victoires++; // incrémente si evalA gagne
                if (resultat == Gagnant.BLANC) victoires--; // décrémente si evalB gagne
            }
        }

        // renvoie 1 si victoires > 0 ||| -1 si victoires < 0 ||| 0 si victoires = 0
        return Integer.compare(victoires, 0);
    }

}



