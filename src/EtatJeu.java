/*
EtatJeu.java
Classe décrivant une configuration du jeu (le joueur dont c'est le tour ainsi que l'état du plateau de jeu).
Auteur: Rémi PALLEN.
Date de la dernière version: 03/02/2026.
 */

import java.util.ArrayList;
import java.util.List;

public class EtatJeu {
    private int largeur; // désigne le nombre de colonne du plateau de jeu
    private Plateau  plateau; // le plateau du jeu
    private boolean tourBlanc; // true si c'est le tour du Joueur blanc, false sinon

    public int getLargeur() {
		return this.largeur;
	}

    public boolean getTourBlanc() {
		return this.tourBlanc;
	}

    public Plateau getPlateau() {
		return this.plateau;
	}

    public EtatJeu (int largeur) {
		this.largeur = largeur;
        this.plateau = new Plateau(largeur);
		this.tourBlanc = true; // le joueur blanc commence
    }

	// Remplie la case de la ligne x et de la colonne y par le pion du joueur j.
	// Si j=null, alors vide la case.
    public void modifie(int x,int y, Joueur j) {
		getPlateau().modifie(x,y,j);
    }

	// Renvoie AUCUN si l'état n'est pas final, le gagnant sinon (NOIR/BLANC)
    public Gagnant etatFinal() {
		for (int k=0;k<getLargeur();k++) {
			if (getPlateau().getCase(0,k)==Joueur.joueurNoir) {
				return Gagnant.NOIR;
			}
		}
		for (int k=0;k<largeur;k++) {
			if (getPlateau().getCase(7,k)==Joueur.joueurBlanc) {
				return Gagnant.BLANC;
			}
		}
		return Gagnant.AUCUN;
    }

    /*---------------------------------------------Ne programmez qu'en dessous de cette ligne là! (Sauf pour l'importation de modules)------------------------------------------------------------------*/


	public static final EtatJeu ETAT_INITIAL = new EtatJeu(8); // état e0
	public static final EtatJeu ETAT_ECHEC = new EtatJeu(8); // état illicite (impossible)

	// méthode qui affiche le jeu
	void afficher(){

		// afficher le plateau
		for(int i=0; i<9; i++){ // hauteur
			for(int j=0; j<getLargeur()+1; j++){ //
				if(i==0 && j==0){ // coin en haut à gauche
					System.out.print("  ");
				} else if (i==0){ // première ligne (affiche l'id de la colonne)
					System.out.print(j-1 + " ");
				} else if (j==0){ // première colonne (affiche l'id de la ligne)
					System.out.print(i-1 + " ");
				} else { // sinon affiche le contenu de la case
					Case c = plateau.getPlateau()[i-1][j-1]; // récupère la case (i-1,j-1)
					// décalé car on rajouté les indices de ligne/colonne
					System.out.print(c.toString()+" "); // affiche le contenu de la case
				}
			}
			System.out.println();
		}

		// afficher à qui est le tour
		if(getTourBlanc()){
			System.out.println("Joueur courant : B");
		} else {
			System.out.println("Joueur courant : N");
		}
	}

	// méthode qui génère dans une liste l'ensemble des successeurs VALIDES d'un état
	public List<EtatJeu> successeurs(Joueur j) {
		List<EtatJeu> resultat = new ArrayList<>();
		Plateau p = this.plateau;

		// pour chaque case du plateau
		for (int ligne=0; ligne<8; ligne++) {
			for (int col=0; col<this.largeur;col++) {
				if (p.getCase(ligne, col) == j) { // si la case est occupée par le joueur
					for (int d = -1; d <= 1; d++) { // pour chaque décalage possible (-1,0,1)
						Action a = new Action(j, ligne, col, d); // génère l'action associée au décalage
						EtatJeu s = a.successeur(this); // récupère le successeur de l'état via l'action
						if (s != EtatJeu.ETAT_ECHEC) { // si le successeur est valide
							resultat.add(s); // l'ajoute à la liste
						}
					}
				}
			}
		}
		return resultat;
	}
}
