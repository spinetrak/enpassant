# En Passant - Chess / Schach Data API


Status
---
[![Build Status](https://travis-ci.org/spinetrak/enpassant.svg?branch=master)](https://travis-ci.org/spinetrak/enpassant)


Prerequisites (Ubuntu)
---
```
sudo apt-get install postgresql
sudo apt-get install openjdk-8-jdk openjdk-8-doc
sudo apt-get install openjdk-8-jre-headless openjdk-8-source
sudo -u postgres createuser -P -d enpassant
sudo -u postgres createdb -O enpassant enpassant
git config --global user.name spinetrak
git config --global user.email spinetrak@spinetrak.net
mkdir $HOME/prod
cd $HOME/prod/
git clone https://github.com/spinetrak/enpassant.git
```

Build / Install
---
```
cd $HOME/prod/enpassant/
git pull https://github.com/spinetrak/enpassant.git
mvn install
```

Start Manually
---
```
export PORT=80
export INITIAL_STATE_API_KEY=whateveryourkeyis
sudo PORT=$PORT INITIAL_STATE_API_KEY=$INITIAL_STATE_API_KEY java -jar target/enpassant-1.0-SNAPSHOT.jar server config.yml
```

Run as Service
---
```
cd /etc/systemd/system/
sudo ln -s $HOME/prod/enpassant/enpassant.service
sudo systemctl start enpassant
```