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
}
