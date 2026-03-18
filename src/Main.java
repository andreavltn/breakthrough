import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // lecture des entrées de l'utilisateur
        EtatJeu etat = EtatJeu.ETAT_INITIAL; // e0

        // tant qu'il n'y a pas de gagnant
        while (etat.etatFinal() == Gagnant.AUCUN) {
            etat.afficher(); // affiche l'état en cours

            // stocke le joueur courant
            Joueur joueurCourant = etat.getTourBlanc() ? Joueur.joueurBlanc : Joueur.joueurNoir;

            EtatJeu successeur = EtatJeu.ETAT_ECHEC; // état d'échec initialement

            // tant que le successeur est un état échec (coup invalide)
            while (successeur == EtatJeu.ETAT_ECHEC) {
                System.out.print("Entrez votre coup (ligne colonne décalage) : ");

                String entree = sc.nextLine().trim(); // récupère ce qui a été entré
                String[] valeurs = entree.split(" "); // sépare les valeurs

                // si on n'a pas entré 3 valeurs, erreur
                if (valeurs.length != 3) {
                    System.out.println("Format invalide, réessayez!");
                    continue;
                }

                int l = Integer.parseInt(valeurs[0]); // ligne
                int c = Integer.parseInt(valeurs[1]); // colonne
                int d = Integer.parseInt(valeurs[2]); // décalage

                Action a = new Action(joueurCourant, l, c, d); // récupère l'action associées aux entrées
                successeur = a.successeur(etat); // génère le successeur associé à l'action

                // si c'est un état invalide, erreur
                if (successeur == EtatJeu.ETAT_ECHEC) {
                    System.out.println("Coup illicite, réessayez!");
                }
            }

            etat = successeur; // on a donc le successeur
            System.out.println("==================");
        }

        // si on arrive ici c'est qu'un joueur a gagné

        etat.afficher(); // affiche l'état

        // affiche le message de victoire
        String message = (etat.etatFinal() == Gagnant.BLANC) ? "Le joueur blanc a gagné!" : "Le joueur noir a gagné!";
        System.out.println(message);

        sc.close(); // ferme la lecture des entrées
    }
}