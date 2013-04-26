if [ $# -lt 7]
then
echo "./testSuite.sh <ServerIP:Port> <Maximum number of Nodes> <Log Location(Disk,RamDisk,AWS EBS> <SnapCount(Default,0)> <Machine(LocalMachine, AWS)> <ZookeeperVersion(Zookeeper-x.y.z)> <Number of Instances in Ensemble>"
else
echo "Running Zookeeper Performance tests"
#ZK Performance Test-Write
echo "===========ZK Performance Test-Write=========="
java ZKPerformanceTest $1 $2 $3 $4 $5 $6 $7
#ZK Performance Test-Read
echo "===========ZK Performance Test-Read=========="
java getPerformance $1 $2 $3 $4 $5 $6 $7
#ZK Performance Test-Producer-Consumer
echo "===========ZK Performance Test-Producer-Consumer=========="
java Test $1 $2 $3 $4 $5 $6 $7
#ZK Performance Test-move
echo "===========ZK Performance Test-move=========="
java moveTest $1 $2 $3 $4 $5 $6 $7
#ZK Performance Test-ZKmove
echo "===========ZK Performance Test-ZKmove=========="
java MovePerformanceTest $1 $2 $3 $4 $5 $6 $7
echo "++++++++++Performance Test Results++++++++++++"
cat PerformanceTestResults.csv
fi
