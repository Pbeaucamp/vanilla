;-------------------------------- Informations Générale ------------------------------------------------------------------------------------------
#definition du nom de l'application
!define APP_NAME "VanillaDisconnected"
!define APP_Version "3.4"
Name ${APP_NAME}

!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\Vanilla\Serie3\${APP_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"
!define PRODUCT_STARTMENU_REGVAL "BPM:Start Menu Folder"

!define PRODUCT_INSTALL_DIR "$PROGRAMFILES\Vanilla\Serie3\${APP_NAME}"

#Caption 'installer caption'

#Nom en sortie
outfile "${APP_NAME}-${APP_Version}.exe"
  
#LicenseBkColor 0xFFFFFF
#InstallButtonText "Next >"

#Site de FreeMetrics
!define WEBSITE "http://www.bpm-conseil.com"

#Afficher les details de l'installation et de la desinstallation
ShowInstDetails show
ShowUninstDetails show

#Taille max de l'application une fois decompresse
!define SIZE "70" 

XpStyle on
 
 #Dossier d'installation
InstallDir ${PRODUCT_INSTALL_DIR}

#Cle de registre
InstallDirRegKey HKCU "Software\Vanilla\Serie3\${APP_NAME}" ""

#Vista : Necessite les droits admin
RequestExecutionLevel admin

;---------------------------------  Fin Informations Générale  ----------------------------------------------------------------------------------------

;---------------------------------  Définition de l'interface Graphique  ----------------------------------------------------------------------------
# fichier d'entete de Interface Graphique de l'installeur (inclu dans une DLL)
!include "MUI.nsh"

#Declaration des icones
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\orange-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\orange-uninstall.ico"
 
#Declaration de la baniere d'entete
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_RIGHT
!define MUI_HEADERIMAGE_BITMAP "Icons\splash.bmp"
!define MUI_HEADERIMAGE_UNBITMAP "Icons\splash.bmp"
 
# Declaration du style du Wizard
!define MUI_WELCOMEFINISHPAGE_BITMAP "Icons\InstWizard.bmp"
!define MUI_UNWELCOMEFINISHPAGE_BITMAP "Icons\UninstWizard.bmp"

#
!define MUI_ABORTWARNING
;---------------------------------  Fin Définition de l'interface Graphique ------------------------------------------------------------------
 
;--------------------------------  Inclusion de fichier d'entete  ------------------------------------------------------------------------------------
!include WinMessages.nsh

!include FileFunc.nsh
;--------------------------------  Fin Inclusion de fichier d'entete  --------------------------------------------------------------------------------

;-------------------------------- Declaration des pages du Wizard  ----------------------------------------------------------------------------------
#Page d'acceuil qui implement la macro "MUI_PAGE_WELCOME" de "MUI.nsh"
!insertmacro MUI_PAGE_WELCOME

#Page affichant la license
!insertmacro MUI_PAGE_LICENSE "VanillaDisconnected\license\License.txt"

# Page de selection du repertoire cible
!insertmacro MUI_PAGE_DIRECTORY

# Page d'edition du nom de l'entree dans le start menu
#Variables
Var MUI_TEMP
Var STARTMENU_FOLDER

!define MUI_STARTMENUPAGE_NODISABLE
#Configuration des raccourci dans le menu demarrer
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "Vanilla\${APP_NAME}"
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
!define MUI_STARTMENUPAGE_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "${PRODUCT_STARTMENU_REGVAL}"

!insertmacro MUI_PAGE_STARTMENU Application $STARTMENU_FOLDER

#Page de suivi d'installation
!insertmacro MUI_PAGE_INSTFILES

#Configuratioon de la finish page avec creation ou pas d'un "desktop shortcut"
    !define MUI_FINISHPAGE_NOAUTOCLOSE
    !define MUI_FINISHPAGE_RUN "$DESKTOP\${APP_NAME}.lnk"
	
	!define MUI_FINISHPAGE_LINK "Visit the Vanilla website for the latest updates."
	!define MUI_FINISHPAGE_LINK_LOCATION ${WEBSITE}

    !define MUI_FINISHPAGE_RUN_NOTCHECKED
    !define MUI_FINISHPAGE_RUN_TEXT "Create a shortcut"
    !define MUI_FINISHPAGE_RUN_FUNCTION "CreateDeskLink"
	
    #!define MUI_FINISHPAGE_SHOWREADME "$INSTDIR\Documentation\README.txt"
	#!define MUI_FINISHPAGE_SHOWREADME_NOTCHECKED
	
   
 #Page de fin d'installation
  !insertmacro MUI_PAGE_FINISH	

 #Definition des pages de desinstallation
  !define MUI_UNFINISHPAGE_NOAUTOCLOSE

  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH	

#Definition du language
!insertmacro MUI_LANGUAGE "English"
;-------------------------------- Fin Declaration des pages du Wizard    -----------------------------------------------------------------------------

 
;--------------------------------  Section d'installation  --------------------------------------------------------------------------------------------
#Section de creation de la nouvelle application Windows
Section Create_FAJAVA

  ;SectionIn RO

#Copie des fichier du RCP
  SetOutPath "$INSTDIR"
  File /r "VanillaDisconnected\*.*"

  ;SetOutPath "$PROGRAMFILES\BPM-Conseil\FreeMetrics\"
  ; File /r "hibernate.cfg.xml"
  ;File /r "system\*.*"
   
  #Creation d'un raccourci internet vers le site de FreeMetrics
  WriteINIStr "$INSTDIR\${APP_NAME} Website.url" "InternetShortcut" "URL" ${WEBSITE}

  SetOutPath "$INSTDIR"

  #Inscrire l'installation dans la base de registre
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} SOFTWARE\SimpleText "Install_Dir_${APP_NAME}" "$INSTDIR"
  
#Créer les cléfs de desinstalation  dans la base de registre
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "${APP_NAME}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" '"$INSTDIR\Uninstall ${APP_NAME}.exe"'
  WriteRegDWORD ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "NoModify" 0
  WriteRegDWORD ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "NoRepair" 0
   
SectionEnd

# Section de creation des raccourcis
Section Create_Start_Menu_Shortcut
  SetOutPath "$INSTDIR"
 # File readme.txt
 
 #Inscription a la base de registre du desinstalleur
  WriteRegStr HKCU "Software\Vanilla\Serie3\${APP_NAME}" "" $INSTDIR
  
  #Creation du desinstalleur
  WriteUninstaller "$INSTDIR\Uninstall ${APP_NAME}.exe"
  
 !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    
#creation des raccourcis
    CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"
	CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\${APP_NAME}.lnk" "$INSTDIR\${APP_NAME}.exe"
    CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall ${APP_NAME}.lnk" "$INSTDIR\Uninstall ${APP_NAME}.exe"
	WriteINIStr "$SMPROGRAMS\$STARTMENU_FOLDER\${APP_NAME} Website.url" "InternetShortcut" "URL" ${WEBSITE}

  !insertmacro MUI_STARTMENU_WRITE_END

  DetailPrint "All shortcuts are succesfully created !"
  
SectionEnd

#Section ou toutes documentation est copie
Section Documentation
		SetOutPath "$INSTDIR\Documentation\"
		#File "VanillaDisconnected\Readme.txt"
		File "VanillaDisconnected\license\License.txt"
		;File "BPM_FreeMetrics_Guide_Utilisation.pdf"
		;File "FM_1.1RC1Newfeatures.pdf"
		;File "FreeMetricsFaceo20080429.sql"
SectionEnd

#Section de configuration du desinstalleur
Section Uninstall

  #Suppression du contenu du repertoire de l'application
  RMDir /r "$INSTDIR"
    #Suppression du repertoire de l'application
  RMDir "$INSTDIR"
    #Suppression du raccourci bureau s'il y en a un 
  Delete "$DESKTOP\${APP_NAME}.lnk" 
  
  !insertmacro MUI_STARTMENU_GETFOLDER Application $STARTMENU_FOLDER
    
	 ;MessageBox MB_OK "STARTMENU_FOLDER: $STARTMENU_FOLDER"
	 
  #Suppression des entrees dans le menu demarrer
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall ${APP_NAME}.lnk"
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\${APP_NAME}.lnk"
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\${APP_NAME} Website.url"
  
  RMDir "$SMPROGRAMS\$STARTMENU_FOLDER"
 #Suppression du menu  parent s'il est vide
  StrCpy $MUI_TEMP "$SMPROGRAMS\$STARTMENU_FOLDER"
  
  ;MessageBox MB_OK "Mui_Temp: $MUI_TEMP"
  
 startMenuDeleteLoop:
	ClearErrors
    RMDir $MUI_TEMP
    GetFullPathName $MUI_TEMP "$MUI_TEMP\.."
    
   IfErrors startMenuDeleteLoopDone
	
  ; MessageBox MB_OK "Mui_Temp: $MUI_TEMP"
   
    StrCmp $MUI_TEMP $SMPROGRAMS startMenuDeleteLoopDone startMenuDeleteLoop
  startMenuDeleteLoopDone:
   
  
  #Nettoyage de la base de registre
  DeleteRegKey /ifempty HKCU "Software\Vanilla\Serie3\${APP_NAME}"
  
SectionEnd
;--------------------------------  Fin Section d'installation  --------------------------------------------------------------------------------------------

;--------------------------------  Fonction de l'installeur   --------------------------------------------------------------------------------------------
#Fonction qui cree le raccourci bureau 
Function CreateDeskLink

	;SetOutPath "$PROGRAMFILES\BPM-Conseil\FreeMetrics\${APP_NAME}\"
	SetOutPath "$INSTDIR"

	CreateShortCut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${APP_NAME}.exe" ""

	nsExec::Exec "$DESKTOP\${APP_NAME}.lnk" 
	
FunctionEnd

#Fonction qui empeche de lancer 2 installeurs simultanéement, et qui declenche le splash
Function .onInit

;--------------------------------------------- Splash -----------------------------------------------------------
InitPluginsDir
 
File /oname=$PLUGINSDIR\splash.bmp "Icons\FreeMetrics_logo.bmp"
File /oname=$PLUGINSDIR\splash.wav "FmDesigner_SplashSound.wav"

advsplash::show 4000 600 400 0x04025C $PLUGINSDIR\splash
 Pop $0 

Delete $PLUGINSDIR\splash.bmp
;------------------------------------------Fin splash -----------------------------------------------------------------

  
#Algo:
#Verifi s'il y en a deja un de lance
#si c'est le cas il n'en relance pas un mais ramene le courrant au premier plan

  System::Call "kernel32::CreateMutexA(i 0, i 0, t '$(^Name)') i .r0 ?e" ;creation d'un mutex sur les thread de l'installeur
  Pop $0
  StrCmp $0 0 launch
   StrLen $0 "$(^Name)"
   IntOp $0 $0 + 1
  loop:
    FindWindow $1 '#32770' '' 0 $1
    IntCmp $1 0 +4
    System::Call "user32::GetWindowText(i r1, t .r2, i r0) i."
    StrCmp $2 "$(^Name)" 0 loop
    System::Call "user32::ShowWindow(i r1,i 9) i."         ;Si il est minimiser l'agrandi
    System::Call "user32::SetForegroundWindow(i r1) i."    ;s'il n'est pas au premier plan l'y ramene
    Abort
  launch:
FunctionEnd
;------------------------------------------------------------------------  Fin Fonctions de l'installeur   --------------------------------------------------------------------------------------------