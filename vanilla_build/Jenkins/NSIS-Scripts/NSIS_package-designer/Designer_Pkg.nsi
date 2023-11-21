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
	!define FASD "FreeAnalysisSchemaDesigner"
	!define FD "FreeDashboard"
	!define BIG "BiWorkflow"
	!define FMDT "FreeMetadata"
	!define GAT "BiGateway"
	!define VERSION "4.0"
	!define PCKG "Designers"
	!define SERIE "Serie4"
	!define REG_ROOT "Software\Vanilla\${SERIE}\${PCKG}"
	
	
	;Name and file
	Name "Vanilla ${PCKG}"
	OutFile "VanillaDesginer_${VERSION}.exe"

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

Section "${BIG}" SECTION_${BIG}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${BIG} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${BIG} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${BIG}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${BIG}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${BIG}"
				;Set sources path
				File /r "${BIG}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${BIG}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${BIG}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${BIG}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${BIG}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${BIG}\${BIG}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${BIG}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${BIG}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd
	
Section "${FASD}" SECTION_${FASD}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${FASD} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${FASD} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${FASD}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${FASD}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${FASD}"
				;Set sources path
				File /r "${FASD}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${FASD}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${FASD}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${FASD}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${FASD}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${FASD}\${FASD}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${FASD}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${FASD}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd

Section "${FD}" SECTION_${FD}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${FD} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${FD} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${FD}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${FD}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${FD}"
				;Set sources path
				File /r "${FD}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${FD}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${FD}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${FD}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${FD}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${FD}\${FD}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${FD}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${FD}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd

Section "${FMDT}" SECTION_${FMDT}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${FMDT} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${FMDT} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${FMDT}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${FMDT}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${FMDT}"
				;Set sources path
				File /r "${FMDT}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${FMDT}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${FMDT}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${FMDT}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${FMDT}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${FMDT}\${FMDT}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${FMDT}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${FMDT}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd

	Section "${GAT}" SECTION_${GAT}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${GAT} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${GAT} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${GAT}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${GAT}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${GAT}"
				;Set sources path
				File /r "${GAT}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${GAT}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${GAT}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${GAT}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${GAT}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${GAT}\${GAT}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${GAT}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${GAT}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd
	

	LangString DESC_${BIG} ${LANG_ENGLISH} "Install Vanilla ${BIG} ${VERSION}"
	LangString DESC_${FASD} ${LANG_ENGLISH} "Install  Vanilla ${FASD} ${VERSION}"
	LangString DESC_${FD} ${LANG_ENGLISH} "Install Vanilla ${FD} ${VERSION}"
	LangString DESC_${FMDT} ${LANG_ENGLISH} "Install Vanilla ${FMDT} ${VERSION}"
	LangString DESC_${GAT} ${LANG_ENGLISH} "Install Vanilla ${GAT} ${VERSION}"

	!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${BIG}} $(DESC_${BIG})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${FASD}} $(DESC_${FASD})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${FD}} $(DESC_${FD})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${FMDT}} $(DESC_${FMDT})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${GAT}} $(DESC_${GAT})
	!insertmacro MUI_FUNCTION_DESCRIPTION_END

; Uninstall Sections		

Section "un.${FASD}"  un.${FASD}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${FASD}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FASD}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FASD}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${FASD}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FASD}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FASD}"
SectionEnd	

Section "un.${FD}"  un.${FD}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${FD}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FD}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FD}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${FD}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FD}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FD}"
SectionEnd	

Section "un.${FMDT}"  un.${FMDT}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${FMDT}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FMDT}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FMDT}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${FMDT}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FMDT}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${FMDT}"
SectionEnd	

Section "un.${BIG}"  un.${BIG}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${BIG}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${BIG}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${BIG}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${BIG}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${BIG}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${BIG}"
SectionEnd	

Section "un.${GAT}"  un.${GAT}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${GAT}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${GAT}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${GAT}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${GAT}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${GAT}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${GAT}"
SectionEnd	

	LangString UDESC_${BIG} ${LANG_ENGLISH} "Uninstall Vanilla ${BIG} ${VERSION}"
	LangString UDESC_${FASD} ${LANG_ENGLISH} "Uninstall  Vanilla ${FASD} ${VERSION}"
	LangString UDESC_${FD} ${LANG_ENGLISH} "Uninstall Vanilla ${FD} ${VERSION}"
	LangString UDESC_${FMDT} ${LANG_ENGLISH} "Uninstall Vanilla ${FMDT} ${VERSION}"
	LangString UDESC_${GAT} ${LANG_ENGLISH} "Uninstall Vanilla ${GAT} ${VERSION}"

	!insertmacro MUI_UNFUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${BIG}} $(UDESC_${BIG})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${FASD}} $(UDESC_${FASD})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${FD}} $(UDESC_${FD})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${FMDT}} $(UDESC_${FMDT})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${GAT}} $(UDESC_${GAT})
	!insertmacro MUI_UNFUNCTION_DESCRIPTION_END

;Unistall general
Function un.onInit	
	
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${FASD}" "INST_LOC"
	StrCmp $0 "" +1 +2
		SectionSetText un.${FASD} ""
	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${FD}" "INST_LOC"
	StrCmp $1 "" +1 +2
		SectionSetText un.${FD} ""
	
	ReadRegStr $2 HKCU "${REG_ROOT}\Components\${FMDT}" "INST_LOC"
	StrCmp $2 "" +1 +2
		SectionSetText un.${FMDT} ""
	
	ReadRegStr $3 HKCU "${REG_ROOT}\Components\${BIG}" "INST_LOC"
	StrCmp $3 "" +1 +2
		SectionSetText un.${BIG} ""
		
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${GAT}" "INST_LOC"
	StrCmp $0 "" 0 +2
		SectionSetText un.${GAT} ""
FunctionEnd 	

Function un.onUninstSuccess
	EnumRegKey $1 HKCU "${REG_ROOT}\Components" 0
	${If} $1 == ''
		!insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder
			Delete "$SMPROGRAMS\$StartMenuFolder\${PCKG}\Vanilla Website.lnk"
			Delete "$SMPROGRAMS\$StartMenuFolder\${PCKG}\Uninstall.lnk"
			RMDir "$SMPROGRAMS\$StartMenuFolder\${PCKG}"	
		DeleteRegKey HKCU "${REG_ROOT}\Components" 
		DeleteRegValue HKCU "${REG_ROOT}" "Setup-Version"	
		DeleteRegValue HKCU "${REG_ROOT}" "Start Menu Folder"
		Delete "$INSTDIR\Uninstall.exe"
		Delete "$INSTDIR\Vanilla Website.url"
		RMDir "$INSTDIR"	
	${EndIf}
FunctionEnd

