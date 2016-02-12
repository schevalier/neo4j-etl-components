DROP DATABASE IF EXISTS javabase;
CREATE DATABASE javabase DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
GRANT ALL ON javabase.* TO '<DBUser>'@'localhost' IDENTIFIED BY '<DBPassword>';

# DROP TABLE IF EXISTS javabase.Person;
# DROP TABLE IF EXISTS javabase.Address;

CREATE TABLE javabase.Address
  (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  postcode TEXT NOT NULL);
GRANT ALL ON javabase.Address TO '<DBUser>'@'localhost' IDENTIFIED BY '<DBPassword>';

CREATE TABLE javabase.Person
  (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username TEXT NOT NULL,
  addressId INT NOT NULL,
  FOREIGN KEY (addressId) REFERENCES javabase.Address(id));
GRANT ALL ON javabase.Person TO '<DBUser>'@'localhost' IDENTIFIED BY '<DBPassword>';

INSERT INTO javabase.Address (postcode) VALUES ('AB12 1XY');
INSERT INTO javabase.Address (postcode) VALUES ('XY98 9BA');
INSERT INTO javabase.Address (postcode) VALUES ('ZZ1 0MN');

INSERT INTO javabase.Person (username, addressId) SELECT 'user-1', id FROM javabase.Address WHERE javabase.Address.postcode = 'AB12 1XY';
INSERT INTO javabase.Person (username, addressId) SELECT 'user-2', id FROM javabase.Address WHERE javabase.Address.postcode = 'AB12 1XY';
INSERT INTO javabase.Person (username, addressId) SELECT 'user-3', id FROM javabase.Address WHERE javabase.Address.postcode = 'AB12 1XY';
INSERT INTO javabase.Person (username, addressId) SELECT 'user-4', id FROM javabase.Address WHERE javabase.Address.postcode = 'XY98 9BA';
INSERT INTO javabase.Person (username, addressId) SELECT 'user-5', id FROM javabase.Address WHERE javabase.Address.postcode = 'XY98 9BA';
INSERT INTO javabase.Person (username, addressId) SELECT 'user-6', id FROM javabase.Address WHERE javabase.Address.postcode = 'XY98 9BA';
INSERT INTO javabase.Person (username, addressId) SELECT 'user-7', id FROM javabase.Address WHERE javabase.Address.postcode = 'ZZ1 0MN';
INSERT INTO javabase.Person (username, addressId) SELECT 'user-8', id FROM javabase.Address WHERE javabase.Address.postcode = 'ZZ1 0MN';
INSERT INTO javabase.Person (username, addressId) SELECT 'user-9', id FROM javabase.Address WHERE javabase.Address.postcode = 'ZZ1 0MN';
