#!/bin/bash
if [ $# -lt 1 ]
then
  echo "USAGE: zk.sh {start|stop|status}"
  exit
fi  
case $1 in
start)
	for i in hadoop102 hadoop103 hadoop104
	do
	  echo "=================> START $i ZK <================="
	  ssh $i /opt/module/zookeeper-3.5.7/bin/zkServer.sh start
	done
;;
stop)
	for i in hadoop102 hadoop103 hadoop104
	do
	  echo "=================> STOP $i ZK <================="
	  ssh $i /opt/module/zookeeper-3.5.7/bin/zkServer.sh stop
	done
;;

status)
	for i in hadoop102 hadoop103 hadoop104
	do
	  echo "=================> STATUS $i ZK <================="
	  ssh $i /opt/module/zookeeper-3.5.7/bin/zkServer.sh status
	done
;;

*)
  echo "USAGE: zk.sh {start|stop|status}"
  exit
;;
esac

