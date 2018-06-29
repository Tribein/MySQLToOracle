#!/bin/bash
DATADIR="./mydata"
LOGDIR="./mylog"
ORAUSER="scott"
ORAPWD="tiger"
ORASTR="example.net:1521/example_db"
JPRM=""

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
		java $JPRM -XX:+UseG1GC -jar MysqlToOracle.jar "$ORAUSER" "$ORAPWD" "$ORASTR" $DATADIR/$i &> $LOGDIR/$i.log &
done;

