// To run the service locally you will need to run psql server locally and create user

    sudo service postgresql start
    sudo -u postgres psql -c "ALTER USER craig PASSWORD 'password';"
    psql registration




// To test the email sending of this application you can run the mail server in a docker image running the following command

    docker run -p 1080:1080 -p 1025:1025 maildev/maildev