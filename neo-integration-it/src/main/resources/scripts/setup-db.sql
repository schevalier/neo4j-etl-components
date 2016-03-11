DROP DATABASE IF EXISTS javabase;
CREATE DATABASE javabase
  DEFAULT CHARACTER SET utf8
  COLLATE utf8_unicode_ci;
GRANT ALL ON javabase.* TO '<DBUser>'@'localhost'
IDENTIFIED BY '<DBPassword>';

CREATE TABLE javabase.Address
(
  id       INT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
  postcode TEXT NOT NULL
);
GRANT ALL ON javabase.Address TO '<DBUser>'@'localhost'
IDENTIFIED BY '<DBPassword>';

CREATE TABLE javabase.Person
(
  id        INT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username  TEXT NOT NULL,
  addressId INT  NOT NULL,
  FOREIGN KEY (addressId) REFERENCES javabase.Address (id)
);
GRANT ALL ON javabase.Person TO '<DBUser>'@'localhost'
IDENTIFIED BY '<DBPassword>';

CREATE TABLE javabase.Numeric_Table
(
  id              INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  tinyint_field   TINYINT(4),
  smallint_field  SMALLINT,
  mediumint_field MEDIUMINT,
  bigint_field    BIGINT,
  float_field     FLOAT,
  double_field    DOUBLE,
  decimal_field   DECIMAL
);
GRANT ALL ON javabase.Numeric_Table TO '<DBUser>'@'localhost'
IDENTIFIED BY '<DBPassword>';

CREATE TABLE javabase.String_Table
(
  id               INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  numericId        INT NOT NULL,
  FOREIGN KEY (numericId) REFERENCES javabase.Numeric_Table (id),
  char_field       CHAR(20),
  text_field       TEXT,
  blob_field       BLOB,
  tinytext_field   TINYTEXT,
  tinyblob_field   TINYBLOB,
  mediumtext_field MEDIUMTEXT,
  mediumblob_field MEDIUMBLOB,
  longtext_field   LONGTEXT,
  longblob_field   LONGBLOB,
  enum_field       ENUM('val-1', 'val-2'),
  varchar_field    VARCHAR(200)
);
GRANT ALL ON javabase.String_Table TO '<DBUser>'@'localhost'
IDENTIFIED BY '<DBPassword>';


CREATE TABLE javabase.Date_Table
(
  id              INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  numericId       INT NOT NULL,
  FOREIGN KEY (numericId) REFERENCES javabase.Numeric_Table (id),
  date_field      DATE,
  datetime_field  DATETIME,
  timestamp_field TIMESTAMP,
  time_field      TIME,
  year_field      YEAR(4)
);
GRANT ALL ON javabase.Date_Table TO '<DBUser>'@'localhost'
IDENTIFIED BY '<DBPassword>';

CREATE TABLE javabase.Student
(
  id       INT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username TEXT NOT NULL
);
GRANT ALL ON javabase.Student TO '<DBUser>'@'localhost'
IDENTIFIED BY '<DBPassword>';

CREATE TABLE javabase.Course
(
  id   INT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name TEXT NOT NULL
);
GRANT ALL ON javabase.Course TO '<DBUser>'@'localhost'
IDENTIFIED BY '<DBPassword>';

CREATE TABLE javabase.Student_Course
(
  studentId INT  NOT NULL,
  courseId  INT  NOT NULL,
  credits      INT NOT NULL,
  FOREIGN KEY (studentId) REFERENCES javabase.Student (id),
  FOREIGN KEY (courseId) REFERENCES javabase.Course (id)
);
GRANT ALL ON javabase.Student_Course TO '<DBUser>'@'localhost'
IDENTIFIED BY '<DBPassword>';

INSERT INTO javabase.Address (postcode) VALUES ('AB12 1XY');
INSERT INTO javabase.Address (postcode) VALUES ('XY98 9BA');
INSERT INTO javabase.Address (postcode) VALUES ('ZZ1 0MN');

INSERT INTO javabase.Person (username, addressId) SELECT
                                                    'user-1',
                                                    id
                                                  FROM javabase.Address
                                                  WHERE javabase.Address.postcode = 'AB12 1XY';
INSERT INTO javabase.Person (username, addressId) SELECT
                                                    'user-2',
                                                    id
                                                  FROM javabase.Address
                                                  WHERE javabase.Address.postcode = 'AB12 1XY';
INSERT INTO javabase.Person (username, addressId) SELECT
                                                    'user-3',
                                                    id
                                                  FROM javabase.Address
                                                  WHERE javabase.Address.postcode = 'AB12 1XY';
INSERT INTO javabase.Person (username, addressId) SELECT
                                                    'user-4',
                                                    id
                                                  FROM javabase.Address
                                                  WHERE javabase.Address.postcode = 'XY98 9BA';
INSERT INTO javabase.Person (username, addressId) SELECT
                                                    'user-5',
                                                    id
                                                  FROM javabase.Address
                                                  WHERE javabase.Address.postcode = 'XY98 9BA';
INSERT INTO javabase.Person (username, addressId) SELECT
                                                    'user-6',
                                                    id
                                                  FROM javabase.Address
                                                  WHERE javabase.Address.postcode = 'XY98 9BA';
INSERT INTO javabase.Person (username, addressId) SELECT
                                                    'user-7',
                                                    id
                                                  FROM javabase.Address
                                                  WHERE javabase.Address.postcode = 'ZZ1 0MN';
INSERT INTO javabase.Person (username, addressId) SELECT
                                                    'user-8',
                                                    id
                                                  FROM javabase.Address
                                                  WHERE javabase.Address.postcode = 'ZZ1 0MN';
INSERT INTO javabase.Person (username, addressId) SELECT
                                                    'user-9',
                                                    id
                                                  FROM javabase.Address
                                                  WHERE javabase.Address.postcode = 'ZZ1 0MN';

INSERT INTO javabase.Numeric_Table (tinyint_field, smallint_field, mediumint_field, bigint_field,
                                    float_field, double_field, decimal_field)
VALUES (1, 123, 123, 123, 123.2, 12323434.45, 18.10);

INSERT INTO javabase.String_Table (char_field, varchar_field, text_field, blob_field, tinytext_field, tinyblob_field,
                                   mediumtext_field, mediumblob_field, longtext_field, longblob_field,
                                   enum_field, numericId)
  SELECT
    'char-field',
    'varchar-field',
    'text_field',
    'blob_field',
    'tinytext_field',
    'tinyblob_field',
    'mediumtext_field',
    'mediumblob_field',
    'longtext_field',
    'longblob_field',
    'val-1',
    id
  FROM javabase.Numeric_Table
  WHERE javabase.Numeric_Table.tinyint_field = 1;

INSERT INTO javabase.Date_Table (date_field, datetime_field, timestamp_field, time_field, year_field, numericId)
  SELECT
    '1988-01-23',
    '2038-01-19 03:14:07.123456',
    '1989-01-23',
    '22:34:35',
    1987,
    id
  FROM javabase.Numeric_Table
  WHERE javabase.Numeric_Table.tinyint_field = 1;

INSERT INTO javabase.Student (username) VALUES ('jim');
INSERT INTO javabase.Student (username) VALUES ('mark');
INSERT INTO javabase.Student (username) VALUES ('eve');

INSERT INTO javabase.Course (name) VALUES ('Maths');
INSERT INTO javabase.Course (name) VALUES ('Science');
INSERT INTO javabase.Course (name) VALUES ('English');
INSERT INTO javabase.Course (name) VALUES ('Theology');

INSERT INTO javabase.Student_Course (studentId, courseId, credits)
  SELECT
    s.id,
    c.id,
    1
  FROM javabase.Student s, javabase.Course c
  WHERE s.username = 'jim' AND c.name = 'Maths';

INSERT INTO javabase.Student_Course (studentId, courseId, credits)
  SELECT
    s.id,
    c.id,
    2
  FROM javabase.Student s, javabase.Course c
  WHERE s.username = 'jim' AND c.name = 'Science';

INSERT INTO javabase.Student_Course (studentId, courseId, credits)
  SELECT
    s.id,
    c.id,
    3
  FROM javabase.Student s, javabase.Course c
  WHERE s.username = 'mark' AND c.name = 'Maths';

INSERT INTO javabase.Student_Course (studentId, courseId, credits)
  SELECT
    s.id,
    c.id,
    4
  FROM javabase.Student s, javabase.Course c
  WHERE s.username = 'mark' AND c.name = 'English';
