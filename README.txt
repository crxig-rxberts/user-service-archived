// Start psql server locally

sudo service start postgresql
sudo -u postgres createuser craig
psql registratiion




// To run this mail server enter this comman in terminal

docker run -p 1080:1080 -p 1025:1025 maildev/maildev