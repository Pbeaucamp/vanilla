# Vanilla

Vanilla est une plateforme décisionnel open source.

## Build de la plateforme

### Pré-requis

 - Installer une JDK 1.8 (actuellement 1.8.0_202)
 - Créer les variables d'environnements suivantes

    BASE_ECLIPSE
    WORKSPACE
    BUILD_DIRECTORY
    GWT_SDK
    BUILD_HOST
    BUILD_PORT
    BUILD_USER
    BUILD_PASSWORD
    BUILD_MAIL_DESTINATION

 - Récupérer une version d'eclipse compatible (actuellement eclipse-rcp-2018-12-R-win32-x86_64.zip - disponible sur le FTP dans /lyon/dev)
 - Dézipper le fichier zip dans C:/BPM/eclipse

 - Récupérer la dernière version de GWT utilisée par le projet (actuellement gwt-2.8.2.zip - disponible sur le FTP dans /lyon/dev)
 - Dézipper le fichier zip dans C:/BPM/SDK

 - Récupérer la dernière version de Tomcat utilisée par le projet (actuellement apache-tomcat-9.0.5.zip - disponible sur le FTP dans /lyon/dev)
 - Dézipper le fichier zip dans C:/BPM/SDK
 
 - Installer git sur windows (https://git-scm.com/) - Attention il faut garder les sauts de ligne linux (autocrlf=input)
 
 git config --global core.autocrlf true

 - Se placer dans le dossier C:/BPM/workspaces et cloner le projet Vanilla

 git clone https://gitlab.bpm-conseil.com/bpm/vanilla.git workspace_vanilla

 - Lancer eclipse sur le workspace nouvellement créé
 - La configuration du workspace est la suivante

Windows -> Preferences -> GWT -> GWT Settings
 Add...
 Choisir la version de GWT 2.8.1 (il existe par default la 2.7 et 2.8.1) présente dans C:/BPM/SDK

Windows -> Preferences -> Java -> Compiler -> Errors/Warnings
 Deprecated and restricted API -> Forbidden reference (access rules) -> Info

Windows -> Preferences -> Plug-in Development -> API Baselines
 Options -> Missing API baseline -> Mettre Ignore

Windows -> Preferences -> Server -> Runtime Environnements
 Add...
 Choisir Apache Tomcat v9.0
 Choisir la version de Tomcat présente dans C:/BPM/SDK

Windows -> Preferences -> Validation
 Disable All

 - Importer les projets dans le workspace

File -> Import -> General -> Existing project into workspace (et sélectionner le dossier)

 - Créer un server Apache Tomcat
 
File -> New -> Server -> Apache Tomcat 9


### Pour build la plateforme Vanilla et les clients lourds

 - Créer les variables d'environnements suivantes (à adapter en fonction de votre environnement)

BASE_ECLIPSE=C:\BPM\eclipse\eclipse-rcp-2018-12-R-win32-x86_64
BUILD_DIRECTORY=C:\BPM\BUILD\Script
BUILD_HOST=smtp.bpm-conseil.com
BUILD_PASSWORD=Voir sur keepass
BUILD_PORT=587
BUILD_USER=noreply@bpm-conseil.com
BUILD_MAIL_DESTINATION=XXX@XXXX.com
WORKSPACE=C:\BPM\workspaces\workspace_vanilla
GWT_SDK=C:\BPM\SDK\gwt-2.8.2

 - Dans le fichier vanilla_build/buildVanillaPlatform.xml, mettre à jour le numéro de version de la plateforme

 - Dans eclipse, clique droit sur le fichier vanilla_build/buildVanillaPlatform.xml -> Run as...
 - Choisir les éléments que l'on veut build
 - Onglet JRE - Run in the same JRE as the workspace
 - Onglet classpath -> Add JARs -> vanilla_build/libs/javax.mail
 - Run
