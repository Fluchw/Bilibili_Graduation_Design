#!/bin/bash
# 启动 Flink 集群
function start_flink() {
    /opt/module/flink-1.13.0/bin/start-cluster.sh
}

# 停止 Flink 集群
function stop_flink() {
    /opt/module/flink-1.13.0/bin/stop-cluster.sh
}

# 检查脚本参数
if [ "$1" == "start" ]; then
    start_flink
elif [ "$1" == "stop" ]; then
    stop_flink
else
    echo "Usage: $0 start|stop"
    exit 1
fi

