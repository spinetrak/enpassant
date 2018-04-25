#!/usr/bin/env bash

ssh $USER@$IP <<EOF
cd $HOME/prod/enpassant/
git pull https://github.com/spinetrak/enpassant.git
mvn install
sudo systemctl restart enpassant
EOF