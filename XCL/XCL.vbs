'current working dir
Dim workDir
 workDir = CreateObject("Scripting.FileSystemObject").GetFolder(".").Path
'class path
Dim classPath
 classPath = workDir + "\bin"
'app mail class
Dim mainClass
 mainClass = "van.xcl.XCLStartup"
'command string
Dim startupPara
 startupPara = "startup=XCLStartup.xcl"
Dim cmd 
 cmd = "CMD /c java -Djava.ext.dirs=" + workDir + " -cp " + classPath + " " + mainClass + " " + startupPara
'create Wscript.Shell object to run the command
CreateObject("Wscript.Shell").run cmd, vbhide