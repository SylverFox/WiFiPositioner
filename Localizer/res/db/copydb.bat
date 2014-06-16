@ECHO off
ECHO requesting root access
adb root
ECHO Copying database.
adb pull /data/data/nl.utwente.wifipositioner/databases/CaptureDatabase
pause