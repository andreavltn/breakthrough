# Breakthrough — IA Minimax

Jeu de plateau à deux joueurs avec intelligence artificielle.
Basé sur l'algorithme minimax avec élagage alpha-bêta.

---

## Présentation

Breakthrough est un jeu de stratégie sur plateau de taille 8x8. Chaque joueur commence avec deux rangées de pions (blancs en haut, noirs en bas). Le but est de faire avancer un de ses pions jusqu'à la dernière rangée.

### Règles des déplacements

- Un pion avance d'une case en avant (vers le bas pour les blancs, vers le haut pour les noirs)
- Il peut se décaler en diagonale (décalage +1 ou -1) ou avancer tout droit (décalage 0)
- On ne peut pas avancer tout droit si la case est occupée
- On peut capturer uniquement en diagonale
- On ne peut pas se déplacer sur une case occupée par un de ses propres pions

### Conditions de victoire

- Atteindre la dernière rangée adverse avec un pion
- (Capturer tous les pions adverses)

---

## Structure du projet

```
src\
├── Action.java        # Représente un coup + génère l'état successeur
├── Case.java          # État d'une case (occupée ou non, par qui)
├── Plateau.java       # Grille 8×largeur + initialisation
├── EtatJeu.java       # Configuration du plateau + du tour courant
├── Joueur.java        # Constantes joueurBlanc et joueurNoir
├── Gagnant.java       # Enumération AUCUN/BLANC/NOIR
├── Eval.java          # Fonctions d'évaluation
├── Minimax.java       # Algorithme minimax + élagage alpha-bêta
├── Comparaison.java   # Optimisation des paramètres d'évaluation
└── Main.java          # Jeu dans le terminal
```

---

## Lancement

### Main.main() `Main`

Au lancement, trois modes sont disponibles :

- Humain vs Humain (jouer les deux joueurs, via le terminal)
- Humain vs IA (jouer un seul des deux joueurs, idem)
- IA vs IA (joue tout seul, on peut choisir la profondeur de recherche)

Pour saisir un coup (joueur humain)

```
Entrez votre coup (ligne colonne décalage) : 1 3 -1
```

- `ligne` : indice de la ligne du pion à déplacer
- `colonne` : indice de la colonne du pion
- `décalage` : -1 (gauche), 0 (tout droit), 1 (droite)

---

### Comparaison.main() `Comparaison`

Au lancement, optimisation via la méthode 2
- À chaque itération, un nouveau vecteur (a', b', c') est tiré dans une boule de rayon pas autour du vecteur courant
- Les deux evals s'affrontent sur n parties ; si l'une gagne ≥ 15% de plus, elle remplace l'autre
- Le pas décroît de 5% par itération (*0.95)
- Convergence détectée si 10 itérations sans améliorations

Il est possible de modifier les paramètres suivants :
- Le pas de départ de la recherche (pasInitial)
- Le nombre d'itérations de la recherche de vecteur (nbIter)
- Le nombre de parties jouées à chaque comparaison (nbParties)
- La profondeur maximum de recherche (profondeurMax)

Ils sont utilisés dans la fonction `optimiser(pasInitial, nbIter, nbParties, profondeurMax)`, à la ligne suivante :
- double[] resultat = optimiser(x, 5, 50, 100, 2)

## Fonctions d'évaluation (`Eval.java`)

L'IA utilise une fonction heuristique pour trouver le meilleur coup. Plusieurs sont disponibles :

| Fonction | Calcul du score                          | Paramètres                        |
|----------|------------------------------------------|-----------------------------------|
| `evalBasique` | Renvoie toujours 0 (IA aveugle)          | Aucun                             |
| `eval0` | Différence du nombre de pions            | Aucun                             |
| `evalEvoluee` | Pions + avancement + proximité du centre | fixes : 10 / 1 / 3.5              |
| `evalParametree` | Même logique, coefficients configurables | a (pion), b (avancée), c (centre) |

Par défaut, l'IA joue avec `evalParametree(11, 3, 5)` dans Main.main().

### Détail de `evalParametree`

Pour chaque pion du joueur courant :
- `+a` par pion présent sur le plateau
- `+b × avancement` (distance parcourue depuis le départ)
- `+c × (centre - distanceAuCentre)` (proximité de la colonne centrale)

Les coefficients trouvés par optimisation sont : **a ≈ 11, b ≈ 3, c ≈ 5**.

---

## Algorithme de recherche (`Minimax.java`)

L'IA utilise l'algorithme **minimax avec élagage alpha-bêta** :
- Génére tous les coups réalisables depuis l'état courant
- Appliquer `minimaxAB()` récursivement jusqu'à la profondeur choisie 
- À chaque nœud : maximiser le score pour le joueur j, minimiser pour l'adversaire 
- Couper la branche dès que α ≥ β (élagage alpha-bêta)
- Si égalité de score, choisir aléatoirement parmi les meilleurs coups

### Fonctions principales

```java
// calcule le score minimax d'un état pour le joueur j (avec élagage alpha-bêta)
double minimaxAB(Joueur j, EtatJeu etat, int profondeur, double alpha, double beta, BiFunction<Joueur, EtatJeu, Double> eval)

// renvoie le successeur de l'état ayant le meilleur score (le meilleur coup pour le joueur j)
EtatJeu meilleurCoup(Joueur j, EtatJeu etat, int profondeur, BiFunction<Joueur, EtatJeu, Double> eval)

// fait jouer une partie complète entre deux fonctions eval, et renvoie le gagnant
Gagnant unePartie(BiFunction<...> evalBlanc, BiFunction<...> evalNoir, int profondeur)

// fait jouer n parties à deux evals, renvoie le score. Si score>0, evalA meilleure, sinon evalB
double comparerEvals(BiFunction<...> evalA, BiFunction<...> evalB, int profondeur, int nbParties)
```

---
