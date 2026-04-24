import java.util.function.BiFunction;

public class Eval {

    // eval0 naïve : différence de nombre de pions entre j_courant et j_adverse
    public static double eval0(Joueur j, EtatJeu etat) {
        Plateau p = etat.getPlateau(); // récupère le plateau
        int largeur = p.getLargeur(); // recupère la largeur du plateau
        double score = 0; // score initial 0

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

        return score;
    }

    // eval0 évoluée, prend en compte l'avancée des pions sur le plateau
    public static double evalEvoluee(Joueur j, EtatJeu etat) {

        Plateau p = etat.getPlateau();
        int largeur = p.getLargeur(); // largeur du plateau
        double score = 0; // score initial 0
        double colonneCentrale = (double) (largeur - 1) /2 ; // centre du plateau (3.5 pour largeur=8)

        // parcours du plateau
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int col = 0; col < largeur; col++) {

                // récupère le joueur sur la case
                Joueur occupant = p.getCase(ligne, col);

                // si c'est le joueur courant
                if (occupant == j) {

                    score+=10; // +10 par pion

                    // valorise l'avancement du pion sur le plateau
                    // plus il est loin plus il est important
                    int avancement = (j == Joueur.joueurBlanc) ? ligne : (7-ligne);
                    score += avancement;

                    // valorise les pions qui sont proches de la colonne centrale du plateau
                    // calcule la distance au centre
                    double distanceCentre = Math.abs(col-colonneCentrale);
                    score += colonneCentrale - distanceCentre; // max quand distanceCentre=0

                }

                // sinon c'est le joueur adverse
                else if (occupant == j.autreJoueur()) {

                    score-=10; // -10 par pion adverse

                    // pénalise l'avancée des pions adverses
                    int avancementAdverse = (j == Joueur.joueurNoir) ? ligne : (7-ligne);
                    score -= avancementAdverse;

                    // pénalise les pions qui sont proches du centre
                    double distanceCentre = Math.abs(col-colonneCentrale);
                    score -= colonneCentrale - distanceCentre;
                }
            }
        }

        return score;
    }


    // eval qui renvoie le même score pour tous les états
    public static double evalBasique(Joueur j, EtatJeu etat) {
        return 0;
    }

    // eval évoluée, mais avec scores paramétrés
    public static double evalParametree(Joueur j, EtatJeu etat,
                                        double scorePion,
                                        double scoreAvancee,
                                        double scoreCentre) {

        Plateau p = etat.getPlateau(); // récupère le plateau
        int largeur = p.getLargeur(); // recupère la largeur du plateau
        double score = 0; // score initial 0
        double colonneCentrale = (largeur-1)/2.0;

        // pour chaque case du plateau
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int col = 0; col < largeur; col++) {

                // récupère l'occupant de la case
                Joueur occupant = p.getCase(ligne, col);

                // si c'est le joueur courant
                if (occupant == j) {

                    score += scorePion;

                    // valorise l'avancement du pion sur le plateau
                    // plus il est loin plus il est important
                    int avancement = (j == Joueur.joueurBlanc) ? ligne : (7 - ligne);
                    score += scoreAvancee * avancement;

                    // valorise les pions qui sont proches de la colonne centrale
                    // calcule la distance au centre
                    double distanceCentre = Math.abs(col-colonneCentrale);
                    score += scoreCentre * (colonneCentrale-distanceCentre);

                }

                // sinon c'est le joueur adverse
                else if (occupant == j.autreJoueur()) {

                    score -= scorePion;

                    // pénalise l'avancement du pion sur le plateau
                    // plus il est loin plus il est important
                    int avancementAdverse = (j == Joueur.joueurNoir) ? ligne : (7 - ligne);
                    score -= scoreAvancee*avancementAdverse;

                    // pénalise les pions qui sont proches du centre
                    double distanceCentre = Math.abs(col-colonneCentrale);
                    score -= scoreCentre*(colonneCentrale-distanceCentre);
                }
            }
        }
        return score;
    }

    // renvoie la fonction eval paramétrée avec a,b,c
    public static BiFunction<Joueur, EtatJeu, Double> getEvalParametree(double a, double b, double c) {
        return (j, e) -> evalParametree(j, e, a, b, c);
    }
}
