#!/bin/bash
log="/var/log/startup.log"
err="/var/log/error.log"
touch $log
touch $err
readonly project="git@gitlab.com:ui-gen/citeofideas-website.git"
readonly gitlabaccestoken=dUmzqyzkLzmQTACxUyk_

readonly databaseip=35.205.243.193
readonly databaseuser=UI-gen
readonly databaseuserpassword=test
readonly databasename=cityofideasdb

#MQTT 
readonly MQQTTUser=ui-gen
readonly MQQTTPassword=Test

sudo su -
export HOME=/root/

#Install Git 
apt-get install -y git >>$log 2>>$err

#Create ssh key
ssh-keygen -t rsa -N "" -f /root/.ssh/id_rsa >>$log 2>>$err

#Post shh key on Gitlab
curl -X POST https://gitlab.com/api/v4/user/keys -H 'Content-Type: application/json' -H 'Private-Token: '$gitlabaccestoken'' -H 'cache-control: no-cache' -d "{\"title\":\"$(hostname)\",\"key\":\"$(cat ~/.ssh/id_rsa.pub)\"}" >>$log 2>>$err

#Clone project from Gitlab
ssh -o StrictHostKeyChecking=no $(whoami)@gitlab.com >>$log 2>>$err
git clone $project /root/cityofideas >>$log 2>>$err

#Install .NET Core sdk 
wget -q https://packages.microsoft.com/config/ubuntu/16.04/packages-microsoft-prod.deb >>$log 2>>$err
dpkg -i packages-microsoft-prod.deb >>$log 2>>$err
apt-get install apt-transport-https >>$log 2>>$err
apt-get update >>$log 2>>$err
apt-get install -y dotnet-sdk-2.2 >>$log 2>>$err

#Install Node.js
curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash - >>$log 2>>$err
apt install -y nodejs >>$log

#Instal Nginx
apt-get install -y nginx >>$log 2>>$err

#Start and configure Nginx 
service nginx start >>$log 2>>$err

cat <<EOF >/etc/nginx/sites-available/default
server {
    listen        80;
    server_name   cityofideas.tk *.cityofideas.tk;
    location / {
        proxy_pass         https://localhost:5001;
        proxy_http_version 1.1;
        proxy_set_header   Upgrade \$http_upgrade;
        proxy_set_header   Connection keep-alive;
        proxy_set_header   Host \$host;
        proxy_cache_bypass \$http_upgrade;
        proxy_set_header   X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto \$scheme;
    }
}
EOF

nginx -t >>$log 2>>$err
nginx -s reload >>$log 2>>$err

#Configure HTTPS using Certbot
# apt-get update >>$log 2>>$err
# apt-get install software-properties-common >>$log 2>>$err
# add-apt-repository universe >>$log 2>>$err
# add-apt-repository ppa:certbot/certbot -y >>$log 2>>$err
# apt-get update >>$log 2>>$err
# apt-get install -y certbot python-certbot-nginx >>$log 2>>$err

# certbot --nginx -n --agree-tos -m sam.geens@student.kdg.be --domains cityofideas.tk,*.cityofideas.tk --redirect --hsts >>$log 2>>$err


#Replace connectionstring with connection values
sed -i 's/"DefaultConnection".*/"DefaultConnection": "Server='$databaseip';Database='$databasename';Uid='$databaseuser';Password='$databaseuserpassword'"/' /root/cityofideas/UI-MVC/appsettings.json  >>$log 2>>$err
# sed -i 's/"DefaultConnection".*/"DefaultConnection": "Server=db4free.net;Database=cityofideas;Uid=ui_gen;Password=ui-g-pw-1"/' /root/cityofideas/UI-MVC/appsettings.json  >>$log 2>>$err

#Publish .NET application
dotnet publish --configuration Release /root/cityofideas/ -o /root/publish/ >>$log 2>>$err

#Configure Kestrel service
cat <<EOF >/etc/systemd/system/kestrel-cityofideas.service
[Unit]
Description=.NET Core CityOfIdeas Application

[Service]
WorkingDirectory=/root/publish
ExecStart=/usr/bin/dotnet /root/publish/UI-MVC.dll
Restart=always
RestartSec=10
SyslogIdentifier=dotnet-coretest
User=root
Environment=ASPNETCORE_ENVIRONMENT=Production

[Install]
WantedBy=multi-user.target
EOF

#Start service
sudo systemctl enable kestrel-cityofideas.service >>$log 2>>$err
sudo systemctl start kestrel-cityofideas.service >>$log 2>>$err

#Install Mosquitto broker 
# apt-get install -y mosquitto

#Set Mosquitto user and password
# mosquitto_passwd -b /etc/mosquitto/passwd $MQQTTUser $MQQTTPassword

#Create a configuration file for Mosquitto pointing to the password
# cat <<EOF >>/etc/mosquitto/conf.d/default.conf
# allow_anonymous false
# password_file /etc/mosquitto/passwd
# EOF

#Restart Mosquitto server
# systemctl restart mosquitto