#!/usr/bin/env bash

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $MYUSER@$IP <<EOF
cd /home/$MYUSER/prod/enpassant/
git fetch --all
git reset --hard origin/master
git pull origin master
mvn install
sudo systemctl restart enpassant
EOF