/*
Case.java
Classe décrivant l'état d'une case du plateau de jeu de Breakthrough.
Auteur: Rémi PALLEN.
Date de la dernière version: 03/02/2026.
 */

public class Case {

    private boolean occupe; // true si la case est occupée, false sinon
    private Joueur pion; // donne le joueur occupant la case (si celle-ci est occupée)

    public Case(){
	    this.occupe=false;
	    this.pion=new Joueur();
    }

    public boolean getOccupe(){
        return this.occupe;
    }
    public Joueur getPion(){
        return this.pion;
    }

    private void setOccupe(boolean b){
        this.occupe = b;
    }

    private void setPion(Joueur j){
	    this.pion = j;
    }

    public void libere(){
        setOccupe(false);
    }
    public void prendre(){
        setOccupe(true);
    }

    // met le joueur j dans la case
    public void remplace(Joueur j){
	    prendre();
	    setPion(j);
    }

    public String toString(){
        if (getOccupe()) {
            return this.pion.toString();
        } else {
            return "_";
        }
    }
}
