;NSIS Modern User Interface
;Start Menu Folder Selection Example Script
;Written by Joost Verburg

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"
  !include "LogicLib.nsh"


;--------------------------------
;General

	;Constants
	!define LAU "BiLauncher"
	!define FA "FreeAnalysis"
	
	!define VERSION "4.0"
	!define PCKG "Users"
	!define SERIE "Serie4"
	!define REG_ROOT "Software\Vanilla\${SERIE}\${PCKG}"
	
	
	;Name and file
	Name "Vanilla ${PCKG}"
	OutFile "VanillaUsers_${VERSION}.exe"

	;Default installation folder
	InstallDir "$PROGRAMFILES\Vanilla"

	;Get installation folder from registry if available
	InstallDirRegKey HKCU "${REG_ROOT}" ""

	;Request application privileges for Windows Vista
	RequestExecutionLevel user

	;Icons
	!define MUI_ICON "Img\orange-install.ico"
	!define MUI_UNICON "Img\orange-uninstall.ico"

	;Web Site
	!define WEBSITE "http://www.bpm-conseil.com"

	!define MUI_HEADERIMAGE
	!define MUI_HEADERIMAGE_RIGHT
	!define MUI_HEADERIMAGE_BITMAP "Img\splash.bmp"
	!define MUI_HEADERIMAGE_UNBITMAP "Img\splash.bmp"

	;Declaration du style du Wizard
	!define MUI_WELCOMEFINISHPAGE_BITMAP "Img\InstWizard.bmp"
	!define MUI_UNWELCOMEFINISHPAGE_BITMAP "Img\UninstWizard.bmp"

;--------------------------------
;Variables

  Var StartMenuFolder

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages
  !insertmacro MUI_PAGE_WELCOME
  !define MUI_LICENSEPAGE_CHECKBOX
  !insertmacro MUI_PAGE_LICENSE "License\Vanilla_Public_license1.1.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  
  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT HKCU
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "${REG_ROOT}" 
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  !define MUI_STARTMENUPAGE_DEFAULTFOLDER "Vanilla"
  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_COMPONENTS
  !insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections
Section "-hidden Brandding" 

	;Write Internet shortcut
	WriteINIStr "$INSTDIR\${SERIE}\${PCKG}\Vanilla Website.url" "InternetShortcut" "URL" ${WEBSITE}
	
	; Create Shortcuts dir
	CreateDirectory "$SMPROGRAMS\$StartMenuFolder\${PCKG}"
	
	; write setup version
	WriteRegStr HKCU "${REG_ROOT}" "Setup-Version" "${VERSION}"
	
	;Create shortcuts
	!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
		CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\Vanilla Website.lnk" "$INSTDIR\${SERIE}\${PCKG}\Vanilla Website.url"
		CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\Uninstall.lnk" "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
	!insertmacro MUI_STARTMENU_WRITE_END
	;Write Uninstaller
	WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"

SectionEnd


	
Section "${LAU}" SECTION_${LAU}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${LAU} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${LAU} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${LAU}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${LAU}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${LAU}"
				;Set sources path
				File /r "${LAU}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${LAU}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${LAU}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${LAU}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${LAU}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${LAU}\${LAU}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${LAU}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${LAU}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd

Section "${FA}" SECTION_${FA}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${FA} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${FA} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${FA}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${FA}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${FA}"
				;Set sources path
				File /r "${FA}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${FA}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${FA}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${FA}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${FA}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${FA}\${FA}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${FA}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${FA}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd


	LangString DESC_${LAU} ${LANG_ENGLISH} "Install  Vanilla ${LAU} ${VERSION}"
	LangString DESC_${FA} ${LANG_ENGLISH} "Install Vanilla ${FA} ${VERSION}"

	!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${LAU}} $(DESC_${LAU})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${FA}} $(DESC_${FA})
	!insertmacro MUI_FUNCTION_DESCRIPTION_END


	
; Uninstall Sections		

Section "un.${LAU}"  un.${LAU}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${LAU}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${LAU}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${LAU}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${LAU}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${LAU}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${LAU}"
SectionEnd	

Section "un.${FA}"  un.${FA}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${FA}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FA}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FA}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${FA}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FA}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FA}"
SectionEnd	

	LangString UDESC_${LAU} ${LANG_ENGLISH} "Uninstall  Vanilla ${LAU} ${VERSION}"
	LangString UDESC_${FA} ${LANG_ENGLISH} "Uninstall Vanilla ${FA} ${VERSION}"

	!insertmacro MUI_UNFUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${LAU}} $(UDESC_${LAU})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${FA}} $(UDESC_${FA})
	!insertmacro MUI_UNFUNCTION_DESCRIPTION_END

;Unistall general
Function un.onInit
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${LAU}" "INST_LOC"
	StrCmp  $0  "" 0 +2
		SectionSetText un.${LAU} ""
	
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${FA}" "INST_LOC"
	StrCmp $0 "" 0 +2
		SectionSetText un.${FA} ""
FunctionEnd 	

Function un.onUninstSuccess
	EnumRegKey $1 HKCU "${REG_ROOT}\Components" 0
	${If} $1 == ''
		!insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
			Delete "$SMPROGRAMS\$StartMenuFolder\${PCKG}\Vanilla Website.lnk"
			Delete "$SMPROGRAMS\$StartMenuFolder\${PCKG}\Uninstall.lnk"
			RMDir "$SMPROGRAMS\$StartMenuFolder\${PCKG}\"	
		DeleteRegKey HKCU "${REG_ROOT}\Components" 
		DeleteRegValue HKCU "${REG_ROOT}" "Setup-Version"	
		DeleteRegValue HKCU "${REG_ROOT}" "Start Menu Folder"
		Delete "$INSTDIR\Uninstall.exe"
		Delete "$INSTDIR\Vanilla Website.url"
		RMDir "$INSTDIR" 	
	${EndIf}
FunctionEnd

