if [ $# -lt 2]
then
echo "./PerformanceTest.sh <Upper Bound on the Number Of ZK nodes> <Number Of Times to repeat Test> <path to logDir>"
else
max=$2
for ((i = 1 ; i <= $max ; i++)); do
java PerformanceTest $1 $i $3
sudo rm -rf $3
done
fi

