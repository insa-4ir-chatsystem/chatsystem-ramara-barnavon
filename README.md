# Chat System RAMARA-BARNAVON

## Détails sur notre projet

### Nous faisons les tests en localhost ce qui implique : 
-on définit une liste de port que nos différent chatsystem utiliseront pour communiquer \n
-Lors du test, il y a beaucoup de Thread qui print en même temps, à ne pas confondre avec un bug \n

### Autres remarques
-Les messages échangés entre les chatsystems sont identifiés grâce à un Header de 4 majuscules.\n
-Il y a le thread principale, puis 1 Thread qui est en écoute de messages et un dernier qui met à jour la liste des contacts toutes les secondes en demandant au réseau.\n
-Comme c'est de l'UDP on a mis un ttl(=time to live) de taille n pour ne pas supprimer un contact si il y a n messages qui ont été perdus(ou non envoyé).\n





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

