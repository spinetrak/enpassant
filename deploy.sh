#!/usr/bin/env bash

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $USER@$IP <<EOF
cd /Home/spinetrak/prod/enpassant/
git pull https://github.com/spinetrak/enpassant.git
mvn install
sudo systemctl restart enpassant
EOF