Projet de Programmation Orientée Objet 2 : __Serveur multi-thread de Loterie__

Ce projet simule le tirage d'une loterie avec deux types de billets:
- les billets de catégorie 1 qui sont achetés en continue par des joueurs avec des numéros aléatoires.
- les billets de catégorie 2 qui sont acheté par des joueurs avec des numéros choisis par l'utilisateur.

Réalisé en binôme : 
* https://github.com/Maxime02290  

  

1. Pour un lancement simple du projet il suffit de lancer un terminal de commande, puis de se mettre dans le répertoire du projet.

2. Ensuite, il suffit simplement de compiler la classe "LotterieServer.java" grâce à la commande: javac LotterieServer.java

3. Puis de l'éxècuter avec la commande: java LotterieServer

   
   Enfin, il faut simplement se laisser guider dans le terminal. Bonne découverte !

NB:  Pour les catégories de prix gagné, exemple:   
--> Si T est initialisé à 2 cela veut dire que pour gagner un prix de catégorie 1 il faut avoir au minimum 2 numéros gagnants sur son billet.  
--> Toujours dans le même exemple, si vous avez 3 numéros gagnants sur votre billet alors vous gagnerez un prix de catégorie 2 et ainsi de suite.  
--> Dernière petite information, à propos des doublons. Si dans votre billet vous avez des doublons alors ils compteront chacun pour 1.
