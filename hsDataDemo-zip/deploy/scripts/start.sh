#!/bin/sh

#set($pidId = "$"+"!")
cd ${workspace}/${app_name}
pidFile="${app_name}.pid"
pidTmpFile="${app_name}.tmp.pid"

if [ -f "$pidFile" ]
then
    rm -f $pidFile
fi

jarName=$(ls *.jar)
fullJarPath=${workspace}/${app_name}/
echo $fullJarPath$jarName
#nohup java ${jvm_args} ${log4j2_args} -jar -DAppPID $fullJarPath$jarName >/dev/null  &
#nohup java ${jvm_args} ${log4j2_args} -jar -DAppPID $fullJarPath$jarName >./test.txt 2>&1 &
nohup java ${jvm_args} ${log4j2_args} -jar -DAppPID $fullJarPath$jarName >/dev/null 2>&1 &
#nohup java ${jvm_args} ${log4j2_args} -jar -DAppPID operatorssyn-service-1.0-SNAPSHOT.jar >/dev/null  &

#echo $pidId > ${pidTmpFile}
echo $pidId > $pidTmpFile