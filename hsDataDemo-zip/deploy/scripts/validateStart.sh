#!/bin/bash

cd ${workspace}/${app_name}
pidFile="${app_name}.pid"
pidTmpFile="${app_name}.tmp.pid"

function validate(){
    pidFile=$1
    loopCnt=$2
    while [[ $loopCnt -gt 0 ]]; do
        if [ -f "$pidFile" ]; then
            sleep 4
            pid=`head -n +1 $pidFile`
            check_start=`ps axu | grep java | grep -w $pid |grep -v grep| awk '{printf $2}'`
            if [[ -n "$check_start" && "$pid" == "$check_start" ]]; then
                return 0
            else
                return 1
            fi
        fi
        let "loopCnt--"
	sleep 5
    done
    return 1
}

if [ "$1" == "false" ] ;then
	if [ -f "$pidFile" ]; then
		pid=`head -n +1 $pidFile`
		check_start=`ps axu | grep java | grep -w $pid |grep -v grep| awk '{printf $2}'`
		if [[ -n "$check_start" && "$pid" == "$check_start" ]]; then
			echo 0
		else
			echo "server is not start" 1>&2
		fi
	else
		echo "server is not start" 1>&2
	fi
else
	validate ${app_name}.pid 10
	if [ "$?" == "0" ];then
		echo "0"
	else  
		#启动失败后，需要看一下是否有临时pid文件，需要把该残留的进程也删除掉
		if [ -f "$pidTmpFile" ];then
			PID=$(cat ${pidTmpFile})
			alias rm='rm'
			kill -9 $PID 2>&1
			rm -rf $pidTmpFile 2>&1
		fi
		echo "start failed." 1>&2
	fi
fi