#!/bin/bash
DATADIR="./mydata"
LOGDIR="./mylog"
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
		java $JPRM -XX:+UseG1GC -jar MysqlToOracle.jar $DATADIR/$i &> $LOGDIR/$i.log &
done;

