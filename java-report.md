# Projet Chat System
RAMARA Matis

BARNAVON Jules-Ian

# Pile Technologique

## Protocole de communication

Pour que notre système de chat soit en mesure de communiquer nous avons du choisir quel protocole utiliser entre UDP et TCP. D'un point de vue réseau, notre système comporte deux phases : une phase de découverte des contacts et une phase où l'utilisateur peut communiquer avec les contacts de sa liste. Pour la première, étant donné que l'application ne connaît pas les utilisateurs présents sur le réseau, elle ne peut pas établir de connexion TCP avec chacun d'eux. C'est pourquoi nous avons utilisé UDP ici avec des communications majoritairement en broadcast. Une fois que l'application détient une liste de contacts avec leur adresse IP, il est possible de les contacter individuellement en utilisant le protocole TCP. TCP permet d'être sûr que les utilisateurs recevront bien tous les messages qui leur étaient destinés.


## Base de données

Afin de sauvegarder les messages échangés entre l'utilisateur et ses contacts nous avons eu recours à l'utilisation du base de données. L'utilisation du système de gestion de bases de données SQLite s'est rapidement imposée pour plusieurs raisons. SQLite permet de créer des bases de données locales sans avoir de serveur tournant sur la machine. Il a été très rapide de déployer la base de données, après avoir ajouté la dépendance et écrit une classe pour générer les requêtes, la base de données pouvait déjà être intégrée à l'application. De plus notre projet ne nécessitait pas de technologie plus performante comme MySQL qui permet de gérer beaucoup plus d'accès concurrents et une meilleure scalabilité.

## Interface graphique

Pour l'interface graphique, nous avons décidé d'utiliser Swing, car nous possédions des bases dans l'utilisation de cette bibliothèque depuis le cours de PDLA/Conduite de projet. Une alternative à Swing était JavaFX mais notre objectif n'était pas de faire l'interface graphique la plus esthétique et Swing était largement suffisant pour cela. De par son ancienneté, il a été très facile de trouver de l'aide en ligne pour certains problèmes rencontrés avec Swing.


# Politique de Test

Au commencement du projet, nous avions seulement fait le système de chat en localhost, en simulant plusieurs machines sur des ports différents. A ce moment là, un avantage remarquable qu'on avait a été que nous pouvions effectuer des tests en fonction de toute les instances du ChatSystem sur la machine car notre classe **Main** avait un accès direct à l'état de chaque **ChatSystem**. Nous avons donc pu écrire des tests pour les fonctions principales du **ChatSystem** telles que choosePseudo et chooseId cependant lorsque nous avons du passer sur un vrai réseau local avec des machines différentes, impossible de connaître l'état des autres machines lors du test sur notre machine, et ainsi nous avons retiré ces tests qui n'avaient plus de sens.
Cependant, nous pouvions toujours faire les tests locaux à la machine, comme ceux sur la database et l'historique des messages, que nous avons mis en place après avoir créé la fonction à tester.

# Points Forts

Toutes les requêtes sur la base de données sont faites grâce aux méthodes (qui ne sont pas synchronisées car SQLite s'en charge lui-même) de la class ChatHistoryManager de manière à éviter toute injection SQL par l'utilisation de PreparedStatements.

Lors de la création de l'interface graphique nous avons eu à faire un choix quant à la gestion de l'affichage de l'historique. La première option était de garder un ChatHistory (JPanel) par contact et d'afficher seulement celui qui correspondait au contact sélectionné. Cette option était envisageable pour un environnement avec peu d'utilisateurs simultanés, mais aurait requis beaucoup de mémoire si l'on avait beaucoup d'utilisateurs. Nous avons préféré utiliser un seul composant qui est rafraîchi avec les messages de la base de données à chaque fois que l'utilisateur sélectionne un nouveau contact. Ce système est ainsi moins gourmand en mémoire vive et peut être amélioré par la suite si besoin.

