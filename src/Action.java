public class Action {

    private Joueur j; // joueur qui joue le coup
    private int l; // ligne du pion
    private int c; // colonne du pion
    private int d; // décalage (-1, 0 ou 1)

    public static final Action ACTION_NULLE = new Action(null, 0, 0, 0);

    public Action(Joueur j, int l, int c, int d) {
        this.j = j;
        this.l = l;
        this.c = c;
        this.d = d;
    }

    // génère le successeur d'un état, ou ETAT_ECHEC si le coup n'est pas valide
    public EtatJeu successeur(EtatJeu e) {
        Plateau p = e.getPlateau(); // récupère le plateau de l'état
        int largeur = p.getLargeur(); // récupère sa largeur

        // crée une copie de l'état
        EtatJeu s = new EtatJeu(largeur);
        for (int x=0; x<8; x++) {
            for (int y=0; y<largeur; y++) {
                s.modifie(x, y, p.getCase(x,y)); // copie la case (x,y) de e, dans s
            }
        }

        int a;
        if(j==Joueur.joueurNoir){
            a=1;
        } else {
            a=-1;
        }

        int nouvelleLigne = l+a; // nouvelle ligne (+1 ou -1) en fonction de noir/blanc
        int nouvelleColonne = c+d; // nouvelle colonne (avec le décalage)

        // si la case de départ est en dehors du plateau
        if (l<0 || l>7 || c<0 || c>=largeur)
            return EtatJeu.ETAT_ECHEC; // échec

        // si la case de départ n'est pas occupée par un pion du joueur courant
        if (p.getCase(l, c)!=j)
            return EtatJeu.ETAT_ECHEC; // échec

        // si la case d'arrivée est en dehors du plateau
        if (nouvelleLigne<0 || nouvelleLigne>7 || nouvelleColonne<0 || nouvelleColonne>=largeur)
            return EtatJeu.ETAT_ECHEC; // échec

        Joueur occupant = p.getCase(nouvelleLigne, nouvelleColonne); // récupère le joueur qui occupe la case d'arrivée

        // si il y a un joueur dans la case
        if (occupant != null) {

            // si la case est déjà occupée par le joueur qui joue le coup
            if (occupant == j){
                return EtatJeu.ETAT_ECHEC; // échec
            }

            // sinon la case est occupée par l'autre joueur
            // si c'est un coup sans décalage (d=0)
            else if (d == 0){
                return EtatJeu.ETAT_ECHEC; // échec
            }
        }

        // sinon si on arrive ici, le coup est valide
        s.modifie(l, c, null); // vide la case de départ
        s.modifie(nouvelleLigne, nouvelleColonne, j); // ajoute le pion à la case d'arrivée

        return s; // renvoie l'état successeur
    }
}