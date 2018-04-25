#!/usr/bin/env bash

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $MYUSER@$IP <<EOF
cd /home/$MYUSER/prod/enpassant/
git pull https://github.com/spinetrak/enpassant.git
mvn install
sudo systemctl restart enpassant
EOF