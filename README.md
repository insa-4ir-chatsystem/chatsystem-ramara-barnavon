# Projet Chat System
RAMARA Matis
BARNAVON Jules-Ian

*Ce projet a pour but de créer un système de chat décentralisé, utilisable sur un réseau local pour une entreprise par exemple. Notre implémentation a pour but de satisfaire un maximum de fonctionnalités du [cahier des charges](https://arbimo.github.io/insa-4ir-advanced-prog/docs/requirements.pdf).*

# Table des Matières

- [Utilisation](#utilisation)
- [Fonctionnalités](#fonctionnalités)
- [Pile Technologique](#pile-technologique)
- [Politique de Test](#politique-de-test)
- [Points Forts](#points-forts)

# Utilisation

## Installation de Maven

Notre projet nécessite Maven pour être compilé, voici comment l'installer sur une machine Linux :

```bash
mkdir -p ~/bin  # create a bin/ directory in your home
cd ~/bin  # jump to it
wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz -O maven.tar.gz  # download maven
tar xf maven.tar.gz  # decompress it
echo 'export PATH=~/bin/apache-maven-3.9.5/bin:$PATH' >> ~/.bashrc  # add mvn's directory to the PATH
source ~/.bashrc  # reload terminal configuration
```

## Compilation
Une fois Maven installé, le projet peut être compilé en se plaçant dans le dossier `chatsystem-ramara-barnavon` puis en exécutant la commande `mvn compile package`

## Exécution 
Après compilation, pour lancer le système de chat il faut éxécuter `java -jar target/ChatSystem-1.0-jar-with-dependencies.jar`

## Utilisation

Pour utiliser notre système de chat de manière optimale, il est important de suivre les recommandations suivantes : 
- Lancer l'application sur plusieurs machines distinctes d'un même réseau local, sinon les applications vont tenter d'utiliser le même port et seule une instance sera lancée. Cela peut être résolu avec la commande `ssh -X <username>@<hostname>`.
- Lancer l'application depuis des chemins distincts, sinon la même base de données sera utilisée par les deux instances et les messages apparaîtront en double dans l'interface. Par exemple `~/instance1/chatsystem-ramara-barnavon$ java -jar target/ChatSystem-1.0-jar-with-dependencies.jar` et `~/instance2/chatsystem-ramara-barnavon$ java -jar target/ChatSystem-1.0-jar-with-dependencies.jar`.


# Fonctionnalités

Notre système de chat inclus les fonctionnalités suivantes à partir d'une interface graphique Swing : 
- Choix d'un pseudo unique
- Affichage de la liste des contacts avec qui il est possible de démarrer un chat
- Mise à jour en temps réel de la liste des contacts avec une pastille indiquant si le contact est en ligne ou non
- Possibilité d'envoyer une message à tous les contacts en ligne de la liste (ET MEME LES INACTIFS ATTENTION)
- L'historique de chaque conversation est conservé dans une base de données locale
- Même après déconnexion d'un contact, il n'est pas possible pour quelqu'un d'autre de se connecter avec le même pseudo (A VOIR AVEC l'IP) pour éviter l'usurpation d'identité

# Pile Technologique

## Protocole de communication

Pour que notre système de chat soit en mesure de communiquer nous avons du choisir quel protocole utiliser entre UDP et TCP. D'un point de vue réseau, notre système comporte deux phases : une phase de découverte des contacts et une phase où l'utilisateur peut communiquer avec les contacts de sa liste. Pour la première, étant donné que l'application ne connaît pas les utilisateurs présents sur le réseau, elle ne peut pas établir de connexion TCP avec chacun d'eux. C'est pourquoi nous avons utilisé UDP ici avec des communications majoritairement en broadcast. Une fois que l'application détient une liste de contacts avec leur adresse IP, il est possible de les contacter en utilisant le protocole TCP. TCP permet d'être sûr que les utilisateurs recevront bien tous les messages qui leur étaient destinés. 


## Base de données

Afin de sauvegarder les messages échangés entre l'utilisateur et ses contacts nous avons eu recours à l'utilisation du base de données. L'utilisation du système de gestion de bases de données SQLite s'est rapidement imposée pour plusieurs raisons. SQLite permet de créer des bases de données locales sans avoir de serveur tournant sur la machine. Il a été très rapide de déployer la base de données, après avoir ajouté la dépendance et écrit une classe pour générer les requêtes, la base de données pouvait déjà être intégrée à l'application. De plus notre projet ne nécessitait pas de technologie plus performante comme MySQL qui permet de gérer beaucoup plus d'accès concurrents et une meilleure scalabilité.

## Interface graphique

Pour l'interface graphique, nous avons décidé d'utiliser Swing, car nous possédions des bases dans l'utilisation de cette bibliothèque depuis le cours de PDLA/Conduite de projet. Une alternative à Swing était JavaFX mais notre objectif n'était pas de faire l'interface graphique la plus esthétique et Swing était largement suffisant pour cela. De par son ancienneté, il a été très facile de trouver de l'aide en ligne pour certains problèmes rencontrés avec Swing.


# Politique de Test
# Points Forts

## Détails sur notre projet

### Nous faisons les tests en localhost ce qui implique : 
-on définit une liste de port que nos différent chatsystem utiliseront pour communiquer <br>
-Lors du test, il y a beaucoup de Thread qui print en même temps, à ne pas confondre avec un bug <br>

### Autres remarques
-Les messages échangés entre les chatsystems sont identifiés grâce à un Header de 4 majuscules.\n
-Il y a le thread principale, puis 1 Thread qui est en écoute de messages et un dernier qui met à jour la liste des contacts toutes les secondes en demandant au réseau.<br>
-Comme c'est de l'UDP on a mis un ttl(=time to live) de taille n pour ne pas supprimer un contact si il y a n messages qui ont été perdus(ou non envoyé).<br>

## Les tests de ChatSystemTest ne peuvent pas encore s'enchaîner ( mauvais reset ) il faut les faire 1 par 1. 
## Compilation du projet et exécution

### Compilation
Pour compiler le projet, il faut se placer dans le dossier **chatsystem-ramara-barnavon** et exécuter la commande `mvn compile package`

### Exécution
Pour exécuter le projet, lancer `java -jar target/ChatSystem-1.0.jar` toujours dans le même dossier



## TO DO AND DELETE : 
As long as you make sure to keep the `metadata.yml` file at the root of this repository, you are free to do anything. Our suggestion would be to have it organized into something like the following:

    .gitignore
    metadata.yml
    pom.xml
    README.md
    src/
      main/
      test/
    doc/
      uml/
      report.pdf

