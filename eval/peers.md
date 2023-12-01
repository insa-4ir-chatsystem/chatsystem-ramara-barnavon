---
Evaluated team:
 - Jules-Ian BARNAVON
 - Matis RAMARA
Evaluators: 
 - Alan DUTEMS
 - Benjamin ZOLVER
...

# Functionalities
  
## discovery
<!-- Connection and contact discovery phase -->
grade: B \n
comments: 
Piste d'amélioration : Pas possible de rentrer son propre nom d'utilisateur. On est en localhost. Il faudrait utiliser l'envoie des requetes UDP en broadcast sur un seul port. L'affichage peut être un peu amélioré (sans les {} pour plus de lisibilté). 
Point positif : time to live. 

## Presentation of contacts/error 
<!-- How readable and user friendly is the presented output. -->
grade: C
comments: difficile de comprendre se qui est afficher à l'écran.  



# Quality

## README
<!-- Presence and completeness of the README -->
grade: A
comments: petite coquille avec un \n qui traine. il manque l'installation de mvn au cas où l'utilisateur ne l'a sur sa machine. 


## maven
<!-- Does the project compiles and run based on the `pom.xml` file only. -->
grade: C
comments: oui ça compile mais le fichier .jar n'est pas créé. Problème avec mvn package car tous les tests ne passent pas avec succès. 


## tests
<!-- Proportion of the code covered by the tests. Are the tests sensible, correct and well organized -->
grade: B
comments: Il y a deux tests qui ne fonctionnent pas car le port est déja utilisé.


## repository
<!-- Structure of the git repository (directories, gitignore, presence of undesired files) -->
grade: A
comments: rien à redire chef


## structure
<!-- Structure of the code into sensible and independent packages -->
grade: A
comments: Plusieurs classes et un package. Un dossier pour les tests.


## reliability
<!-- Thread safety and error handling -->
grade: A
comments: Il y a des thread safety. IL y a des try catch. 


## style
<!-- Variable naming, indentation, comments, ... -->
grade: B
comments: Indentation OK. Nom de variable OK. Bouts de code commentés qui traine. De preference mettre des commentaires global à une méthode plutot que des commentaires directement ecrit dans les lignes de codes. 

