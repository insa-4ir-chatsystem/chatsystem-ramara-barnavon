# Projet Chat System
RAMARA Matis

BARNAVON Jules-Ian

*Ce projet a pour but de créer un système de chat décentralisé, utilisable sur un réseau local pour une entreprise par exemple. Notre implémentation a pour but de satisfaire un maximum de fonctionnalités du [cahier des charges](https://arbimo.github.io/insa-4ir-advanced-prog/docs/requirements.pdf).*


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
- Possibilité d'envoyer une message à tous les contacts en ligne de la liste 
- L'historique de chaque conversation est conservé dans une base de données locale
- Même après déconnexion d'un contact, il n'est pas possible pour quelqu'un d'autre de se connecter avec le même pseudo pour éviter l'usurpation d'identité
