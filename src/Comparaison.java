import java.util.function.BiFunction;

public class Comparaison {

    // fonction qui fait jouer 10 parties entre deux evals, et renvoie la meilleure
    // si il n'y a pas de victoire "indiscutable", augmente la profondeur et fait rejouer
    public static BiFunction<Joueur, EtatJeu, Double> trouverMeilleurEval(
            BiFunction<Joueur, EtatJeu, Double> evalA,
            BiFunction<Joueur, EtatJeu, Double> evalB) {

        // répéter sur 3 pro
        for (int prof=1; prof<=3; prof++) {

            // compare les deux evals sur 10 parties
            double resultat = Minimax.comparerEvals(evalA, evalB, prof, 10);

            // si il y a plus de deux victoires d'écart entre les deux
            if (Math.abs(resultat) >= 2) {
                // renvoie le gagnant
                if(resultat>0){
                    return evalA;
                } else {
                    return evalB;
                }
            }
        }

        // sinon renvoie null (égalité ou résultats trop proches
        return null;
    }
}
