#!/bin/bash
#comment next lines and set your own vars otside this script
#or just change vars below with your values
ORAUSER="scott"
ORAPWD="tiger"
ORASTR="ecample.net:1521/service"
#
DATADIR="./mydata"
LOGDIR="./mylog"
JARPATH="./dist"
JAVAOPTS="-cp $JARPATH/lib"

if [ ! -d $DATADIR ];
then
	mkdir -p $DATADIR || { echo "Directory $DATADIR not available" && exit 2 ; }
fi

if [ ! -d $LOGDIR ];
then
	mkdir -p $LOGDIR || { echo "Directory $LOGDIR not available" && exit 4  ; }
fi

for i in $(ls ./$DATADIR/);
do
		java $JAVAOPTS -XX:+UseG1GC -jar $JARPATH/MysqlToOracle.jar "$ORAUSER" "$ORAPWD" "$ORASTR" $DATADIR/$i &> $LOGDIR/$i.log &
done;

