#!/bin/bash

# Function: Starts the infrastructure components (instances, firewall rules, …) in Google Cloud and deploys the .Net application and MQQT broker
# Arguments: -d or --delete	    = Deletes the Linux VM instance, SQL instance and firewallrule.
#	         -da or --deleteall	= Deletes the Linux VM instance, SQL instance, firewallrule, storage bucket and reserved IP addresses. 
#	         -i or --import	    = Creates a Linux VM instance (with application), SQL instance (with user, database and grants), firewallrule and imports previous database from Cloud storage bucket.
# 	          No argument	    = Creates a Linux VM instance (with application), SQL instance (with user, database and grants) and firewallrule.
# Author: sam.geens@student.kdg.be
# Copyright: 2019 sam.geens@student.kdg.be
# Version: 1
# Requires: Google Cloud SDK

# readonly firewallRuleName=cityofideasfirewallrule
# readonly BUCKETNAME=ui-gen
readonly gitlabaccestoken=dUmzqyzkLzmQTACxUyk_

readonly BUCKETNAME=ui-genremove
readonly firewallRuleName=cityofideasfirewallruleremove
readonly RESERVED_IP_ADDRESS=extern

#MQTT
readonly MQQTTUser=ui-gen
readonly MQQTTPassword=Test

#Database
readonly databaseInstanceName=cityofideasdb5
readonly databaseInstanceTier=db-g1-small
readonly databaseInstanceRegion=europe-west1
readonly databaseInstanceBackupStartTime=12:00
readonly databaseName=cityofideasdb
readonly databaseUser=UI-gen

#VM
readonly instanceName=cityofideas
readonly imageproject=ubuntu-os-cloud
readonly imagefamily=ubuntu-1604-lts
readonly machinetype=g1-small

#IpAddress
# readonly RESERVED_IP_ADDRESS=cityofideasip
readonly ipaddressregion=europe-west1

#Reads password silent and shows a '*' for each character
function readPassword(){
unset password
prompt=""
while IFS= read -p "$prompt" -r -s -n 1 char 
do
    if [[ $char == $'\0' ]];     then
        break
    fi
    if [[ $char == $'\177' ]];  then
        prompt=$'\b \b'
        password="${password%?}"
    else
        prompt='*'
        password+="$char"
    fi
done
echo $password
}

if [ "$1" != "-d" -a "$1" != "--delete" -a "$1" != "-da" -a "$1" != "--deleteall" -a "$1" != "-i" -a "$1" != "--import" -a -n "$1" ]
then
	echo $1 is not a valid argument
    echo
    echo Valid Arguments
    echo ---------------------------------------------
    echo "-d or --delete = Deletes the Linux VM instance, SQL instance and firewallrule."
	echo "-da or --deleteall = Deletes the Linux VM instance, SQL instance, firewallrule, storage bucket and reserved IP addresses. "
	echo "-i or --import = Creates a Linux VM instance (with application), SQL instance (with user, database and grants), firewallrule and imports previous database from Cloud storage bucket."
	echo "No argument = Creates a Linux VM instance (with application), SQL instance (with user, database and grants) and firewallrule."
	
    exit 1
fi

if [ "$1" == "-d" -o "$1" == "--delete" ]
then
    #Keeps asking to export database to Cloud Storage Bucket intul valid character is given
	while [ "$export" != "n" -a "$export" != "y" ]	
	do
		read -p 'Do you want to export the database to the cloud bucket before deleting? y/n: ' export	
	done

    #Deletes firewall rule
    yes | gcloud compute firewall-rules delete $firewallRuleName

    #Deletes VM instance
    yes | gcloud compute instances delete $instanceName 

    #Exports database to Cloud Storage Bucket
	if [ "$export" == "y" ]
	then
		checkbucket=$(gsutil du -s gs://$BUCKETNAME) 2>/dev/null
        	if [ -n $checkbucket ]
        	then
                	gsutil mb gs://$BUCKETNAME
        	fi
		serviceAccount=$(gcloud sql instances describe $databaseInstanceName | grep service | cut -d ":" -f 2)

		gsutil acl ch -u $serviceAccount:W gs://$BUCKETNAME/

		gcloud sql export sql $databaseInstanceName  gs://$BUCKETNAME/exports/sqldumpfile.gz --database=$databaseName
	fi 

    #Deletes database
    yes | gcloud sql databases delete $databaseName --instance=$databaseInstanceName

    #Deletes database user
	yes | gcloud sql users delete $databaseUser --host=% --instance=$databaseInstanceName

    #Deletes SQl instance
    yes | gcloud sql instances delete $databaseInstanceName

	#Get all ssh keys from GitLab
	allkeys=$(curl -X GET https://gitlab.com/api/v4/user/keys -H 'Content-Type: application/json' -H 'Private-Token: '$gitlabaccestoken'' -H 'cache-control: no-cache')

	#Get string before hostname (keyid included)
	sshid=${allkeys%,\""title\"":\""$instanceName"\"*}

	#Get key id
	sshid=${sshid##*{\""id\"":}

	#Delete ssh key on GitLab
	curl -X DELETE https://gitlab.com/api/v4/user/keys/$sshid -H 'Content-Type: application/json' -H 'Private-Token: '$gitlabaccestoken'' -H 'cache-control: no-cache' 2>>/dev/null

    echo "server, database, firewall rule en ssh-key in Gitlab verwijderd"

elif [ "$1" == "-da" -o "$1" == "--deleteall" ]
then
    #Deteles firwallrule
    yes | gcloud compute firewall-rules delete $firewallRuleName

    #Deletes VM instance
    yes | gcloud compute instances delete $instanceName

    #Deletes database
    yes | gcloud sql databases delete $databaseName --instance=$databaseInstanceName

    #Deletes database user
	yes | gcloud sql users delete $databaseUser --host=% --instance=$databaseInstanceName

    #Deletes SQl instance
    yes | gcloud sql instances delete $databaseInstanceName

    #Deletes reserved ip addres if one exists
	checkstaticip=$(gcloud compute addresses describe $RESERVED_IP_ADDRESS --region=europe-west1 | grep name | cut -d  " " -f2) 2>/dev/null
        if [ -n $checkstaticip ]
        then
                yes | gcloud compute addresses delete $RESERVED_IP_ADDRESS
        fi

    #Deletes Cloud Storage Bucket
    gsutil -m rm -r gs://$BUCKETNAME

    echo "server, database en firewall rule verwijderd, gereserveerde IP adressen en storage bucket verwijderd" 
else
    #Reads passwords silent and returns a '*' for each character
    echo 'Root password: ' 
	while [ -z "$rootpass" ]
	do
		rootpass=$(readPassword)
	done
	echo

        echo "User $databaseUser password: "
	while [ -z "$userpass" ]
        do
		userpass=$(readPassword)
	done	
	echo 

	echo creating VM instace, SQL instance, firewall rule...

    #Create SQL instance
    gcloud sql instances create $databaseInstanceName --tier=$databaseInstanceTier --region=$databaseInstanceRegion --backup-start-time $databaseInstanceBackupStartTime

    #Set root password
    gcloud sql users set-password root --host=% --instance=$databaseInstanceName --password=$rootpass

    #Set create user with user password
    gcloud sql users create $databaseUser --host=% --instance=$databaseInstanceName --password=$userpass

    #Create database
    gcloud sql databases create $databaseName --instance=$databaseInstanceName

    #Set automatic backup time
	#gcloud sql instances patch $databaseInstanceName --backup-start-time 12:00

    #Imports database from bucket when paramater -i or --import is given
    if [ "$1" == "-i" -o "$1" == "--import" ]
    then
        serviceAccount=$(gcloud sql instances describe $databaseInstanceName | grep service | cut -d ":" -f 2)

        gsutil acl ch -u $serviceAccount:W gs://$BUCKETNAME/

        gcloud sql import sql $databaseInstanceName  gs://$BUCKETNAME/exports/sqldumpfile.gz --database=$databaseName
    fi

    #Replace variable in startup.sh 
    databaseip=$(echo $(gcloud sql instances describe $databaseInstanceName | grep ipAddress: | cut -d ":" -f2))
    sed -i '0,/.*databaseip.*/{s/.*databaseip.*/readonly databaseip='"$databaseip"'/}' ~/Desktop/Linux/Integratieproject/startup.sh
    sed -i '0,/.*databasename.*/{s/.*databasename.*/readonly databasename='"$databaseName"'/}' ~/Desktop/Linux/Integratieproject/startup.sh
    sed -i '0,/.*databaseuser.*/{s/.*databaseuser.*/readonly databaseuser='$databaseUser'/}' ~/Desktop/Linux/Integratieproject/startup.sh
    sed -i '0,/.*databaseuserpassword.*/{s/.*databaseuserpassword.*/readonly databaseuserpassword='"$userpass"'/}' ~/Desktop/Linux/Integratieproject/startup.sh

    #MQTT variable
    sed -i '0,/.*MQQTTUser.*/{s/.*MQQTTUser.*/readonly MQQTTUser='"$MQQTTUser"'/}' ~/Desktop/Linux/Integratieproject/startup.sh
    sed -i '0,/.*MQQTTPassword.*/{s/.*MQQTTPassword.*/readonly MQQTTPassword='"$MQQTTPassword"'/}' ~/Desktop/Linux/Integratieproject/startup.sh

    #Create VM instance with reserved ip if variable not empty otherwise create VM with empheral ip address
	if [ -z $RESERVED_IP_ADDRESS ]
	then
		gcloud compute instances create $instanceName --image-project=$imageproject --image-family=$imagefamily --tags=$instanceName,http-server,https-server --metadata-from-file=startup-script=./startup.sh --machine-type=$machinetype
	else 
		checkstaticip=$(gcloud compute addresses describe $RESERVED_IP_ADDRESS --region=europe-west1 | grep name) 2>/dev/null	
		if [ -n $checkstaticip ] 2>/dev/null	
		then
			echo create ip
			gcloud compute addresses create $RESERVED_IP_ADDRESS --region=$ipaddressregion
		fi
		gcloud compute instances create $instanceName --image-project=$imageproject --image-family=$imagefamily --tags=$instanceName,http-server,https-server --address $RESERVED_IP_ADDRESS --metadata-from-file=startup-script=./startup.sh --machine-type=$machinetype

	fi
   
    #Create firewallrule
    gcloud compute firewall-rules create $firewallRuleName --allow=tcp:1883 --target-tags=$instanceName

    #Add VM instance address to SQL instance
    instanceIP=$(echo $(gcloud compute instances describe $instanceName | grep natIP: | cut -d ":" -f2))
    yes | gcloud sql instances patch $databaseInstanceName --authorized-networks=$instanceIP

    #Message
    if [ "$1" == "-i" -o "$1" == "--import" ]
    then
        echo "Linux VM instance (met applicatie), de SQL instance (met user, database en grants) en de firewall rule aangemaakt en database geïmporteerd"
    else
        echo "Linux VM instance (met applicatie), de SQL instance (met user, database en grants) en de firewall rule aangemaakt"
    fi
fi
