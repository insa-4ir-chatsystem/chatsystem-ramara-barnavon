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
- Lancer l'application depuis des chemins distincts, sinon la même base de données sera utilisée par les deux instances et les messages apparaîtront en double dans l'interface. Par exemple `~/instance1$ java -jar target/ChatSystem-1.0-jar-with-dependencies.jar` et `~/instance2$ java -jar target/ChatSystem-1.0-jar-with-dependencies.jar`.

# Fonctionnalités
# Pile Technologique
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

