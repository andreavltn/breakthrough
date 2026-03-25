import java.util.List;

public class Minimax {

    // eval0 naïve : différence de nombre de pions entre j_courant et j_adverse
    public static int eval0(Joueur j, EtatJeu etat) {
        Plateau p = etat.getPlateau(); // récupère le plateau
        int largeur = p.getLargeur(); // recupère la largeur du plateau
        int score = 0; // score initial 0

        // pour chaque case du plateau
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int col = 0; col < largeur; col++) {
                Joueur occupant = p.getCase(ligne, col); // récupère l'occupant de la case

                // si la case est occupée par le joueur courant
                if (occupant == j) {
                    score++; // augmente le score
                }

                // sinon si la case est occupé par le joueur adverse
                else if (occupant == j.autreJoueur()) {
                    score--; // baisse le score
                }

                // sinon la case est vide : ne fait rien
            }
        }

        return score; // renvoie le score de l'état
    }

    // algorithme minimax : pour un état, renvoie le score du joueur j avec une profondeur
    // cad : si les deux joueurs jouent de façon optimale pendant encore "profondeur" coup
    // -> quel est le score du joueur j?
    public static int minimax(Joueur j, EtatJeu etat, int profondeur) {

        // si profondeur = 0 ou si l'état actuel est final
        if (profondeur == 0 || etat.etatFinal() != Gagnant.AUCUN) {
            return eval0(j, etat); // renvoie son eval0 (évaluation directe)
        }

        // récupère le joueur courant
        Joueur joueurCourant = etat.getTourBlanc() ? Joueur.joueurBlanc : Joueur.joueurNoir;

        // génère les successeurs de l'état actuel
        List<EtatJeu> successeurs = etat.successeurs(joueurCourant);

        // si il n'y a aucun successeur (état final)
        if (successeurs.isEmpty()) {
            return eval0(j, etat); // renvoie son eval0 (évaluation directe)
        }

        // sinon si on arrive ici, il y a des successeurs

        // si c'est au tour du joueur j, on maximise son score
        if (joueurCourant == j) {

            // max = -infini au début
            int max = Integer.MIN_VALUE;

            // pour chaque successeur généré
            for (EtatJeu s : successeurs) {
                int score = minimax(j, s, profondeur - 1); // calcule son score
                if (score>max) { // si le score est meilleur que le max
                    max = score; // met à jour le max
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
                int score = minimax(j, s, profondeur-1); // calcule son score
                if (score<min) { // si le score est moins bon que le min
                    min = score; // met à jour le min
                }
            }

            return min; // on renvoie le score minimal trouvé
        }
    }

    // fonction qui renvoie le meilleur coup possible pour un joueur j dans un état
    public static EtatJeu meilleurCoup(Joueur j, EtatJeu etat, int profondeur) {

        // récupère le joueur courant
        Joueur joueurCourant = etat.getTourBlanc() ? Joueur.joueurBlanc : Joueur.joueurNoir;

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

            // calcule son score minimax
            int score = minimax(j, s, profondeur-1);

            // si le score est meilleur que le max actuel
            if (score>max) {
                max = score; // met à jour le max
                meilleur = s; // met à jour le meilleur état
            }
        }

        return meilleur; // on renvoie l'état avec le meilleur score
    }




    // algorithme minimax avec élagage alpha/beta
    // même algorithme mais on arrête de chercher si la branche est inutile
    // cad : si on a déjà trouvé une meilleure branche
    public static int minimaxAB(Joueur j, EtatJeu etat, int profondeur, int alpha, int beta) {

        // si profondeur = 0 ou si l'état actuel est final
        if (profondeur == 0 || etat.etatFinal() != Gagnant.AUCUN) {
            return eval0(j, etat);
        }

        // récupère le joueur courant
        Joueur joueurCourant = etat.getTourBlanc() ? Joueur.joueurBlanc : Joueur.joueurNoir;

        // génère les successeurs de l'état actuel
        List<EtatJeu> successeurs = etat.successeurs(joueurCourant);

        // si il n'y a aucun successeur (état final)
        if (successeurs.isEmpty()) {
            return eval0(j, etat); // renvoie son éval0 (évaluation directe)
        }

        // sinon si on arrive ici, il y a des successeurs

        // si c'est au tour du joueur j, on maximise son score
        if (joueurCourant == j) {

            // max = -infini au début
            int max = Integer.MIN_VALUE;

            // pour chaque successeur généré
            for (EtatJeu s : successeurs) {

                // calcule son score en transmettant les paramètres alpha/beta
                int score = minimaxAB(j, s, profondeur-1, alpha, beta);

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

                // calcule son score en transmettant les paramètres alpha/beta
                int score = minimaxAB(j, s, profondeur-1, alpha, beta);

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
    public static EtatJeu meilleurCoupAB(Joueur j, EtatJeu etat, int profondeur) {

        // récupère le joueur courant
        Joueur joueurCourant = etat.getTourBlanc() ? Joueur.joueurBlanc : Joueur.joueurNoir;

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
            int score = minimaxAB(j, s, profondeur - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);

            // si le score est meilleur que le max actuel
            if (score>max) {
                max = score; // met à jour le max
                meilleur = s; // met à jour le meilleur état
            }
        }

        return meilleur; // on renvoie l'état avec le meilleur score
    }
}


