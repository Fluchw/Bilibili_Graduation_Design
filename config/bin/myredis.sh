#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Usage: $0 {start|stop|restart|ll}"
    exit 1
fi

case $1 in
    start)
        ssh hadoop102 "redis-server /opt/module/redis-cluster/conf/redis_7000.conf --bind hadoop102"
        ssh hadoop102 "redis-server /opt/module/redis-cluster/conf/redis_7001.conf --bind hadoop102"
        ssh hadoop103 "redis-server /opt/module/redis-cluster/conf/redis_7002.conf --bind hadoop103"
        ssh hadoop103 "redis-server /opt/module/redis-cluster/conf/redis_7003.conf --bind hadoop103"
        ssh hadoop104 "redis-server /opt/module/redis-cluster/conf/redis_7004.conf --bind hadoop104"
        ssh hadoop104 "redis-server /opt/module/redis-cluster/conf/redis_7005.conf --bind hadoop104"
        ;;
    stop)
        ssh hadoop102 "redis-cli -h hadoop102 -p 7000 shutdown"
        ssh hadoop102 "redis-cli -h hadoop102 -p 7001 shutdown"
        ssh hadoop103 "redis-cli -h hadoop103 -p 7002 shutdown"
        ssh hadoop103 "redis-cli -h hadoop103 -p 7003 shutdown"
        ssh hadoop104 "redis-cli -h hadoop104 -p 7004 shutdown"
        ssh hadoop104 "redis-cli -h hadoop104 -p 7005 shutdown"
        ;;
    restart)
        $0 stop
        $0 start
        ;;
    ll)
        ssh hadoop102 "ps -ef | grep redis"
        ssh hadoop103 "ps -ef | grep redis"
        ssh hadoop104 "ps -ef | grep redis"
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|ll}"
        exit 1
esac


