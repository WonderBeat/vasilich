#!/bin/bash
if [ -n "$PASSWORD" ]; then
    openssl aes-256-cbc -k $PASSWORD -in deploy_key.enc -d -a -out id_rsa
    chmod 600 id_rsa
    ID_FILE=$(pwd)/id_rsa
    echo "Host github.com" >> ~/.ssh/config
    echo "   StrictHostKeyChecking no" >> ~/.ssh/config
    echo "   CheckHostIP no" >> ~/.ssh/config
    echo "   IdentityFile $ID_FILE" >> ~/.ssh/config
    git config --global user.email "WonderBeat@github.com"
    git config --global user.name "WonderBeat"
    git config --global push.default simple
    git clone git@github.com:WonderBeat/mvn-repo.git repo
    cp -R mvn-repo/* repo/
    cd repo
    git add *
    git commit -m "A new shinny snapshot"
    git push
else
    echo "Can't deploy without a password"
fi
