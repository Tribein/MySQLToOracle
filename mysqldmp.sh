MYHOST="localhost"
MYUSER="myuser"
MYPWD="mysecret"
MYDB="mydb"
DATADIR="./mydata"


if [ ! -d $DATADIR ];
then
	echo "Directory $DATADIR not found!"
	exit
fi

for i in $(mysql -h${MYHOST} -u${MYUSER} -p${MYPWD} --silent ${MYDB} <<< "select table_name from information_schema.tables where table_schema='"$MYDB"';");
do

	echo "Running mysqldump for table $i"
	mysqldump -h${MYHOST} -u${MYUSER} -p${MYPWD} \
	-t --extended-insert --no-set-names --compatible=ansi \
	--skip-triggers --skip-disable-keys --skip-triggers --skip-add-drop-table --no-create-db --no-create-info --skip-opt --skip-quote-names \
	--default-character-set=utf8 --xml --databases ${MYDB} --tables "$i" | tr -dc '[:print:]\n\t' > "$DATADIR/$i".xml 

done;

