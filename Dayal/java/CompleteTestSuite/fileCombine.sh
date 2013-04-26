if [ $# -ne 3]
then
echo "./fileCombine.sh <file1.csv> <file2.csv> <outputFile.csv>"
else
sed 1d $2 > inter.csv
cat $1 inter.csv > $3
fi
