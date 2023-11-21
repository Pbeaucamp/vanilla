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
	!define ES "EnterpriseServices"
	!define SCHED "BiProcessManager"
	!define CONF "VanillaConfigurator"
	!define VERSION "4.0"
	!define PCKG "Admin"
	!define SERIE "Serie4"
	!define REG_ROOT "Software\Vanilla\${SERIE}\${PCKG}"
	
	;Name and file
	Name "Vanilla ${PCKG}"
	OutFile "VanillaAdmin_${VERSION}.exe"

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
  !insertmacro MUI_PAGE_LICENSE "licence\Licence.txt""
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
	
Section "${ES}" SECTION_${ES}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${ES} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${ES} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${ES}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${ES}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${ES}"
				;Set sources path
				File /r "${ES}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${ES}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${ES}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${ES}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${ES}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${ES}\${ES}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${ES}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${ES}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd

Section "${SCHED}" SECTION_${SCHED}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${SCHED} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${SCHED} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${SCHED}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${SCHED}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${SCHED}"
				;Set sources path
				File /r "${SCHED}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${SCHED}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${SCHED}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${SCHED}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${SCHED}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${SCHED}\Bi ProcessManager.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${SCHED}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${SCHED}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd
	
	Section "${CONF}" SECTION_${CONF}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${CONF} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${CONF} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${CONF}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${CONF}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${CONF}"
				;Set sources path
				File /r "${CONF}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${CONF}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${CONF}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${CONF}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${CONF}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${CONF}\Bi ProcessManager.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${CONF}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${CONF}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd



	
	LangString DESC_${ES} ${LANG_ENGLISH} "Install  Vanilla ${ES} ${VERSION}"
	LangString DESC_${SCHED} ${LANG_ENGLISH} "Install Vanilla ${SCHED} ${VERSION}"
	LangString DESC_${CONF} ${LANG_ENGLISH} "Install Vanilla ${CONF} ${VERSION}"

	!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${ES}} $(DESC_${ES})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${SCHED}} $(DESC_${SCHED})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${CONF}} $(DESC_${CONF})
	!insertmacro MUI_FUNCTION_DESCRIPTION_END


	
; Uninstall Sections		
Section "un.${ES}"  un.${ES}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${ES}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${ES}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${ES}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${ES}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${ES}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${ES}"
SectionEnd	

Section "un.${SCHED}"  un.${SCHED}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${SCHED}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${SCHED}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${SCHED}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${SCHED}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${SCHED}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${SCHED}"
SectionEnd	

Section "un.${CONF}"  un.${CONF}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${CONF}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${CONF}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${CONF}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${CONF}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${CONF}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${CONF}"
SectionEnd	


	
	LangString UDESC_${ES} ${LANG_ENGLISH} "Uninstall  Vanilla ${ES} ${VERSION}"
	LangString UDESC_${SCHED} ${LANG_ENGLISH} "Uninstall Vanilla ${SCHED} ${VERSION}"
	LangString UDESC_${CONF} ${LANG_ENGLISH} "Uninstall Vanilla ${CONF} ${VERSION}"
	

	!insertmacro MUI_UNFUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${ES}} $(UDESC_${ES})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${SCHED}} $(UDESC_${SCHED})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${CONF}} $(UDESC_${CONF})
	!insertmacro MUI_UNFUNCTION_DESCRIPTION_END

;Unistall general
Function un.onInit	
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${ES}" "INST_LOC"
	StrCmp $0 "" 0 +2
		SectionSetText un.${ES} ""
	
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${SCHED}" "INST_LOC"
	StrCmp $0 "" 0 +2
		SectionSetText un.${SCHED} ""
		
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${CONF}" "INST_LOC"
	StrCmp $0 "" 0 +2
		SectionSetText un.${CONF} ""
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

