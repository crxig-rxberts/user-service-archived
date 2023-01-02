
    sudo service postgresql start
    sudo -u postgres psql -c "Create USER user PASSWORD 'password';"
    psql registration



    docker run -p 1080:1080 -p 1025:1025 maildev/maildev