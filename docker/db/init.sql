CREATE USER auth_user WITH PASSWORD 'auth_pwd';
CREATE DATABASE auth_db OWNER auth_user;
GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;

CREATE USER user_user WITH PASSWORD 'user_pwd';
CREATE DATABASE user_db OWNER user_user;
GRANT ALL PRIVILEGES ON DATABASE user_db TO user_user;

CREATE USER device_user WITH PASSWORD 'device_pwd';
CREATE DATABASE devices_db OWNER device_user;
GRANT ALL PRIVILEGES ON DATABASE devices_db TO device_user;
