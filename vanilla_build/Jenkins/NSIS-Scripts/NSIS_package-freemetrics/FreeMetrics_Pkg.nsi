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
	!define DESIGNER "FmDesigner"
	!define ADMIN "FmAdmin"
	!define LOADER "FmLoader"
	
	!define VERSION "V4.0"
	!define PCKG "FreeMetrics"
	!define SERIE "Serie4"
	!define REG_ROOT "Software\Vanilla\${SERIE}\${PCKG}"
	
	
	;Name and file
	Name "Vanilla ${PCKG}"
	OutFile "FreeMetrics_${VERSION}.exe"

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


	
Section "${DESIGNER}" SECTION_${DESIGNER}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${DESIGNER} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${DESIGNER} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${DESIGNER}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${DESIGNER}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${DESIGNER}"
				;Set sources path
				File /r "${DESIGNER}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${DESIGNER}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${DESIGNER}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${DESIGNER}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${DESIGNER}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${DESIGNER}\${DESIGNER}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${DESIGNER}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${DESIGNER}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd

Section "${ADMIN}" SECTION_${ADMIN}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${ADMIN} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${ADMIN} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${ADMIN}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${ADMIN}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${ADMIN}"
				;Set sources path
				File /r "${ADMIN}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${ADMIN}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${ADMIN}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${ADMIN}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${ADMIN}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${ADMIN}\${ADMIN}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${ADMIN}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${ADMIN}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd
	
	Section "${LOADER}" SECTION_${LOADER}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${LOADER} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${LOADER} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${LOADER}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${LOADER}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${LOADER}"
				;Set sources path
				File /r "${LOADER}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${LOADER}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${LOADER}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${LOADER}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${LOADER}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${LOADER}\${LOADER}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${LOADER}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${LOADER}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd


	LangString DESC_${DESIGNER} ${LANG_ENGLISH} "Install  Vanilla ${DESIGNER} ${VERSION}"
	LangString DESC_${ADMIN} ${LANG_ENGLISH} "Install Vanilla ${ADMIN} ${VERSION}"
	LangString DESC_${LOADER} ${LANG_ENGLISH} "Install Vanilla ${LOADER} ${VERSION}"

	!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${DESIGNER}} $(DESC_${DESIGNER})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${ADMIN}} $(DESC_${ADMIN})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${LOADER}} $(DESC_${LOADER})
	!insertmacro MUI_FUNCTION_DESCRIPTION_END


	
; Uninstall Sections		

Section "un.${DESIGNER}"  un.${DESIGNER}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${DESIGNER}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${DESIGNER}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${DESIGNER}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${DESIGNER}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${DESIGNER}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${DESIGNER}"
SectionEnd	

Section "un.${ADMIN}"  un.${ADMIN}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${ADMIN}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${ADMIN}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${ADMIN}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${ADMIN}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${ADMIN}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${ADMIN}"
SectionEnd	

Section "un.${LOADER}"  un.${LOADER}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${LOADER}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${LOADER}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${LOADER}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${LOADER}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${LOADER}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${LOADER}"
SectionEnd	

	LangString UDESC_${DESIGNER} ${LANG_ENGLISH} "Uninstall  Vanilla ${DESIGNER} ${VERSION}"
	LangString UDESC_${ADMIN} ${LANG_ENGLISH} "Uninstall Vanilla ${ADMIN} ${VERSION}"
	LangString UDESC_${LOADER} ${LANG_ENGLISH} "Uninstall Vanilla ${LOADER} ${VERSION}"

	!insertmacro MUI_UNFUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${DESIGNER}} $(UDESC_${DESIGNER})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${ADMIN}} $(UDESC_${ADMIN})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${LOADER}} $(UDESC_${LOADER})
	!insertmacro MUI_UNFUNCTION_DESCRIPTION_END

;Unistall general
Function un.onInit
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${DESIGNER}" "INST_LOC"
	StrCmp  $0  "" 0 +2
		SectionSetText un.${DESIGNER} ""
	
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${ADMIN}" "INST_LOC"
	StrCmp $0 "" 0 +2
		SectionSetText un.${ADMIN} ""
		
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${LOADER}" "INST_LOC"
	StrCmp $0 "" 0 +2
		SectionSetText un.${LOADER} ""
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

