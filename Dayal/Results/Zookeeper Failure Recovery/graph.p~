unset log
unset label
set term jpeg
set output "Graph.jpeg"
set title "ZooKeeper - Failure Recovery Throughput Evaluation"
set xlabel "Time in Seconds"
set ylabel "Throughput in Request/s (Write)"
set label "1" at 12, 2300
set label "2" at 15, 4000
set label "3" at 25, 150
set label "4a" at 32, 3100
set label "4b" at 33, 2
plot "graphInput.dat" using 1:3 title 'Diskless Mode throughput' with lines



