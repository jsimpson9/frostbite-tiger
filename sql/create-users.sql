
USE     frostytiger;

CREATE TABLE user (
    id              INT             AUTO_INCREMENT,
    username        VARCHAR(30)     NOT NULL,
    password        VARCHAR(128)    NOT NULL,
    salt            BLOB            NOT NULL,
    email           VARCHAR(250)    NOT NULL,
    joindate        TIMESTAMP,
    admin           BOOLEAN         DEFAULT FALSE, 

    CONSTRAINT userpk   PRIMARY KEY (id)

);

