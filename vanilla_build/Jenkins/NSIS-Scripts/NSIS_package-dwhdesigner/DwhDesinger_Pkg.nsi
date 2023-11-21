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
	!define DWH "BiDwhDesigner"
	!define PROF "BiProfiler"
	!define VERSION "4.0"
	!define PCKG "DwhDesigners"
	!define SERIE "Serie4"
	!define REG_ROOT "Software\Vanilla\${SERIE}\${PCKG}"
	
	
	;Name and file
	Name "Vanilla ${PCKG}"
	OutFile "VanillaDwhDesginer_${VERSION}.exe"

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


Section "${PROF}" SECTION_${PROF}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${PROF} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${PROF} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${PROF}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${PROF}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${PROF}"
				;Set sources path
				File /r "${PROF}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${PROF}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${PROF}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${PROF}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${PROF}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${PROF}\${PROF}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${PROF}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${PROF}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd
	
Section "${DWH}" SECTION_${DWH}
		;If output folder allready exist ask for confirmation
		IfFileExists $INSTDIR\${SERIE}\${PCKG}\${DWH} 0 doinstall
			MessageBox MB_YESNO "Directory allready exist at :$\r$\n $INSTDIR\${SERIE}\${PCKG}\${DWH} $\r$\nDo you want to proceed ?" IDYES true IDNO false
			true:
				;Remove directory
				RMDir /r "$INSTDIR\${SERIE}\${PCKG}\${DWH}"
				RMDir "$INSTDIR\${SERIE}\${PCKG}\${DWH}"
				Goto doinstall
			
			false:
				; Do nothing
				Goto end
			
			doinstall:
				;Set output path
				SetOutPath "$INSTDIR\${SERIE}\${PCKG}\${DWH}"
				;Set sources path
				File /r "${DWH}\*.*"
				;Write key registery for the installation dir and version
				WriteRegStr HKCU "${REG_ROOT}\Components\${DWH}" "INST_LOC" "$INSTDIR\${SERIE}\${PCKG}\${DWH}"
				WriteRegStr HKCU "${REG_ROOT}\Components\${DWH}" "VERSION" "${VERSION}"
				
				;Create shortcut
				!insertmacro MUI_STARTMENU_WRITE_BEGIN Application
					CreateShortCut "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${DWH}.lnk" "$INSTDIR\${SERIE}\${PCKG}\${DWH}\${DWH}.exe"
				!insertmacro MUI_STARTMENU_WRITE_END
				
				;Write key registery for shortcutPath
				WriteRegStr HKCU "${REG_ROOT}\Components\${DWH}" "SHORT_LOC" "$SMPROGRAMS\$StartMenuFolder\${PCKG}\${DWH}.lnk"
				
				;Write uninstall
				WriteUninstaller "$INSTDIR\${SERIE}\${PCKG}\Uninstall.exe"
			end:
	SectionEnd


	LangString DESC_${PROF} ${LANG_ENGLISH} "Install Vanilla ${PROF} ${VERSION}"
	LangString DESC_${DWH} ${LANG_ENGLISH} "Install  Vanilla ${DWH} ${VERSION}"
	

	!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${PROF}} $(DESC_${PROF})
	  !insertmacro MUI_DESCRIPTION_TEXT ${SECTION_${DWH}} $(DESC_${DWH})
	!insertmacro MUI_FUNCTION_DESCRIPTION_END


	
; Uninstall Sections		
Section "un.${PROF}"  un.${PROF}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${PROF}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${PROF}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${PROF}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${PROF}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${PROF}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${PROF}"
SectionEnd	

Section "un.${DWH}"  un.${DWH}
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${DWH}" "INST_LOC"
	RMDir /r "$0"
	RMDir "$0"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${DWH}\INST_LOC"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${DWH}\VERSION"	
	ReadRegStr $1 HKCU "${REG_ROOT}\Components\${DWH}" "SHORT_LOC"
	Delete "$1"
	DeleteRegKey HKCU "${REG_ROOT}\Components\${DWH}\VERSION\SHORT_LOC"	
	DeleteRegKey HKCU "${REG_ROOT}\Components\${DWH}"
SectionEnd	



	LangString UDESC_${PROF} ${LANG_ENGLISH} "Uninstall Vanilla ${PROF} ${VERSION}"
	LangString UDESC_${DWH} ${LANG_ENGLISH} "Uninstall  Vanilla ${DWH} ${VERSION}"

	!insertmacro MUI_UNFUNCTION_DESCRIPTION_BEGIN
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${PROF}} $(UDESC_${PROF})
	  !insertmacro MUI_DESCRIPTION_TEXT ${un.${DWH}} $(UDESC_${DWH})
	!insertmacro MUI_UNFUNCTION_DESCRIPTION_END

;Unistall general
Function un.onInit
	
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${DWH}" "INST_LOC"
	StrCmp $0 "" 0 +2
		SectionSetText un.${DWH} ""
	
	ReadRegStr $0 HKCU "${REG_ROOT}\Components\${PROF}" "INST_LOC"
	StrCmp $0 "" 0 +2
		SectionSetText un.${PROF} ""
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

