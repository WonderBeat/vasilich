#!/bin/bash
if [ -n "$PASSWORD" ]; then
    # encrypt -> openssl aes-256-cbc -k "pwd" -in id_rsa -out id_rsa.enc -a
    # decrypt -> openssl aes-256-cbc -k "pwd" -in deploy_key.enc -d -a -out id_rsa
    openssl aes-256-cbc -k $PASSWORD -in dropbox_app_key.enc -d -a -out dropbox_app_key.dec
    chmod +x dropbox_uploader.sh
    ./dropbox_uploader.sh -f dropbox_app_key.dec upload target/vasilich.jar Public/vasilich/vasilich-alpha.jar
else
    echo "Can't deploy without a password"
fi
