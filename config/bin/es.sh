#!/bin/bash
es_home=/opt/module/elasticsearch-7.8.0
kibana_home=/opt/module/kibana-7.8.0

if [ $# -lt 1 ]; then
    echo "用法: es.sh {start|stop|restart}"
    exit
fi

case $1 in
    "start")
        #启动 ES
        for i in hadoop102 hadoop103 hadoop104; do
            ssh $i "source /etc/profile; nohup ${es_home}/bin/elasticsearch >/dev/null 2>&1 &"
        done
        #启动 Kibana
        nohup ${kibana_home}/bin/kibana > ${kibana_home}/logs/kibana.log 2>&1 &
        ;;
    "stop")
        #停止 Kibana
        sudo netstat -nltp | grep 5601 | awk '{print $7}' | awk -F / '{print $1}' | xargs kill
        #停止 ES
        for i in hadoop102 hadoop103 hadoop104; do
            ssh $i "ps -ef | grep $es_home | grep -v grep | awk '{print \$2}' | xargs kill" >/dev/null 2>&1
        done
        ;;
    "restart")
        #停止 Kibana
        sudo netstat -nltp | grep 5601 | awk '{print $7}' | awk -F / '{print $1}' | xargs kill
        #停止 ES
        for i in hadoop102 hadoop103 hadoop104; do
            ssh $i "ps -ef | grep $es_home | grep -v grep | awk '{print \$2}' | xargs kill" >/dev/null 2>&1
        done

        #启动 ES
        for i in hadoop102 hadoop103 hadoop104; do
            ssh $i "source /etc/profile; nohup ${es_home}/bin/elasticsearch >/dev/null 2>&1 &"
        done
        #启动 Kibana
        nohup ${kibana_home}/bin/kibana > ${kibana_home}/logs/kibana.log 2>&1 &
        ;;
    *)
        echo "用法: es.sh {start|stop|restart}"
        exit
        ;;
esac

