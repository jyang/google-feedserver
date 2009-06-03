# uploadGadget.sh {gadgetSpecFilePath}

TEMP_ENTITY_FILE=___temp-entity.xml

specFilePath=$1
specFileName=`basename $1`

cat > $TEMP_ENTITY_FILE <<EOF
<entity xmlns="">
  <name>$specFileName</name>
  <specContent>@$specFilePath</specContent>
</entity>
EOF

./deleteUserEntry.sh PrivateGadgetSpec $specFileName 2>/dev/null
./insertUserEntry.sh PrivateGadgetSpec $TEMP_ENTITY_FILE
rm $TEMP_ENTITY_FILE
