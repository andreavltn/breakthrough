public class Eval {

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

        return score;
    }

    // eval0 évoluée, prend en compte l'avancée des pions sur le plateau
    public static int evalEvoluee(Joueur j, EtatJeu etat) {
        Plateau p = etat.getPlateau();
        int largeur = p.getLargeur();
        int score = 0;

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
                    avancement *= avancement; // au carré pour avoir une progression exponentielle

                    score += avancement; // incrémente pour le joueur courant

                }

                // sinon c'est le joueur adverse
                else if (occupant == j.autreJoueur()) {

                    score-=10; // -10 par pion adverse

                    // valorise l'avancement du pion sur le plateau
                    // plus il est loin plus il est important
                    int avancementAdverse = (j == Joueur.joueurNoir) ? ligne : (7-ligne);
                    avancementAdverse *= avancementAdverse; // au carré pour avoir une progression exponentielle

                    score -= avancementAdverse; // décrémente pour le joueur adverse
                }
            }
        }

        return score;
    }


    // eval qui renvoie le même score pour tous les états
    public static int evalBasique(Joueur j, EtatJeu etat) {
        return 0;
    }
}
