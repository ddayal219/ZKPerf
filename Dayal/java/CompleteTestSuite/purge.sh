#! /bin/sh
i=5
while [ $i -gt 0 ]; do
cd /home/dayal/Desktop/ZkPerf 
sudo java PurgeSnap /tmp/zookeeper2/ -n 3 
sudo java PurgeSnap /var/zookeeper-3.4.5/ -n 3 
sudo java PurgeSnap /var/zookeeper-3.4.52/ -n 3 
sudo java PurgeSnap /var/zookeeper-3.4.53/ -n 3 
sleep 1m 
done

