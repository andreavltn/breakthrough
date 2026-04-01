import java.util.function.BiFunction;

public class Comparaison {

    public static void main(String[] args) {
        BiFunction<Joueur, EtatJeu, Integer> evalA = Eval::eval0;
        BiFunction<Joueur, EtatJeu, Integer> evalB = Eval::evalEvoluee;

        // pour n niveau de profondeur
        for (int prof = 1; prof <= 3; prof++) {

            // fait jouer 10 parties avec profondeur n
            int resultat = Minimax.comparerEvals(evalA, evalB, prof, 10);

            // affiche le score
            System.out.println("Profondeur " + prof + " : " + resultat);
        }
    }
}
