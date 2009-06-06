# publishUserGadget.sh {gadgetName}

TEMP_ENTITY_FILE=$0___temp-entity.xml

read -s -p password: password

./getUserEntry.sh PrivateGadgetSpec $1 -password $password | sed 's/.*<id>.*<\/id>.*//' > $TEMP_ENTITY_FILE && ./deleteEntry.sh PrivateGadgetSpec $1 -password $password && ./insertEntry.sh PrivateGadgetSpec $TEMP_ENTITY_FILE -password $password && ./publishGadget.sh $1 -password $password
rm -f $TEMP_ENTITY_FILE
