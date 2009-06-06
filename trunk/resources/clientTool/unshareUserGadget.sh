# unshareUserGadget.sh <gadgetName> (<principal>,)+
# e.g.: unshareUserGadget.sh hello.xml one@example.com,two@example.com

source ./setupEnv.sh

gadgetName=$1
acl=""

until [ -z "$2" ]
do
  if [ -z "$acl" ]; then
    acl="r-"$2
  else
    acl=$acl",r-"$2
  fi
  shift
done

./setUserAcl.sh PrivateGadgetSpec/$gadgetName $acl
