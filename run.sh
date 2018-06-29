#!/bin/bash
DATADIR="./mydata"
LOGDIR="./mylog"
ORAUSER="scott"
ORAPWD="tiger"
ORASTR="ecample.net:1521/service"
JARPATH="./dist"
JAVAOPTS="-cp $JARPATH/lib"

if [ ! -d $DATADIR ];
then
	mkdir -p $DATADIR
fi

if [ ! -d $LOGDIR ];
then
	mkdir -p $LOGDIR
fi

for i in $(ls ./$DATADIR/);
do
		java $JAVAOPTS -XX:+UseG1GC -jar $JARPATH/MysqlToOracle.jar "$ORAUSER" "$ORAPWD" "$ORASTR" $DATADIR/$i &> $LOGDIR/$i.log &
done;

