import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // lecture des entrées de l'utilisateur
        EtatJeu etat = EtatJeu.ETAT_INITIAL; // e0

        // choix du mode de jeu
        System.out.println("--- BREAKTHROUGH ---");
        System.out.println("1) Humain vs Humain");
        System.out.println("2) Humain vs IA");
        System.out.println("3) IA vs IA");
        System.out.print("Choix du mode : ");
        int mode = Integer.parseInt(sc.nextLine().trim());

        // choix de la profondeur
        int profondeur = 1;
        if (mode == 2 || mode == 3) {
            System.out.print("Profondeur minimax (>0) : ");
            profondeur = Integer.parseInt(sc.nextLine().trim());
        }

        // mode 2 : Humain joue blanc, IA joue noir
        boolean iaJoueNoir = (mode == 2);

        // mode 3 : IA joue blanc et noir
        boolean iaJoueNoirEtBlanc = (mode == 3);


        // tant qu'il n'y a pas de gagnant
        while (etat.etatFinal() == Gagnant.AUCUN) {
            
            etat.afficher(); // affiche l'état en cours

            // stocke le joueur courant
            Joueur joueurCourant = etat.getTourBlanc() ? Joueur.joueurBlanc : Joueur.joueurNoir;

            // c'est au tour de l'IA si elle joue les deux couleurs
            // ou si elle joue les noirs et que c'est au tour des noirs
            boolean tourIA = iaJoueNoirEtBlanc || (iaJoueNoir && joueurCourant == Joueur.joueurNoir);

            // initialisation du successeur choisi
            EtatJeu successeur = EtatJeu.ETAT_ECHEC;

            // si c'est au tour de l'IA
            if (tourIA) {
                System.out.println("C'est au tour de l'IA, réflexion en cours (profondeur " + profondeur + ")...");

                // stocke le temps auquel l'IA commence à calculer
                long msDebut = System.currentTimeMillis();

                // trouve le meilleur successeur (avec profondeur choisie à l'initialisation)
                successeur = Minimax.meilleurCoup(joueurCourant, etat, profondeur, Eval.getEvalParametree(11, 3, 5));

                // stocke le temps auquel l'IA a fini de calculer
                long msFin = System.currentTimeMillis();

                // affiche le temps de calcul
                System.out.println("Temps de calcul : " + (msFin - msDebut) + "ms");
            }

            // sinon c'est au tour d'un joueur Humain
            else {

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

                    Action a = new Action(joueurCourant, l, c, d); // génère l'action associée aux entrées
                    successeur = a.successeur(etat); // génère le successeur via l'action

                    // si c'est un état invalide, erreur
                    if (successeur == EtatJeu.ETAT_ECHEC) {
                        System.out.println("Coup illicite, réessayez!");
                    }
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