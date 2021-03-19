-- ###############################  Users Setup  ###############################
DROP TABLE IF EXISTS USER_BUSINESS;
DROP TABLE IF EXISTS BUSINESS;
DROP TABLE IF EXISTS USER;

CREATE TABLE USER
(
    ID            INT AUTO_INCREMENT PRIMARY KEY,
    FIRST_NAME    VARCHAR(50)  NOT NULL,
    LAST_NAME     VARCHAR(50)  NOT NULL,
    MIDDLE_NAME   VARCHAR(50),
    NICKNAME      VARCHAR(50),
    BIO           VARCHAR(250),
    EMAIL         VARCHAR(50)  NOT NULL UNIQUE,
    DATE_OF_BIRTH DATE         NOT NULL,
    PHONE_NUMBER  VARCHAR(18),
    HOME_ADDRESS  VARCHAR(250) NOT NULL,
    CREATED       DATETIME     NOT NULL,
    ROLE          VARCHAR(30)  NOT NULL,
    PASSWORD      CHAR(60)     NOT NULL
);

CREATE TABLE BUSINESS
(
    ID            INT AUTO_INCREMENT PRIMARY KEY,
    NAME          VARCHAR(50)  NOT NULL,
    DESCRIPTION   VARCHAR(250),
    ADDRESS       VARCHAR(250) NOT NULL,
    BUSINESS_TYPE VARCHAR(50)  NOT NULL,
    CREATED       DATETIME     NOT NULL
);

create table USER_BUSINESS
(
    USER_ID     INT NOT NULL,
    BUSINESS_ID INT NOT NULL,
    CONSTRAINT USER_BUSINESS_PK
        UNIQUE (USER_ID, BUSINESS_ID),
    CONSTRAINT USER_BUSINESS_USER_FK
        FOREIGN KEY (USER_ID) REFERENCES USER (ID),
    CONSTRAINT USER_BUSINESS_BUSINESS_FK
        FOREIGN KEY (BUSINESS_ID) references BUSINESS (ID)
);


-- Inserting test user

INSERT INTO USER(FIRST_NAME,
                 LAST_NAME,
                 MIDDLE_NAME,
                 NICKNAME,
                 BIO,
                 EMAIL,
                 DATE_OF_BIRTH,
                 PHONE_NUMBER,
                 HOME_ADDRESS,
                 CREATED,
                 ROLE,
                 PASSWORD)
VALUES ('Fletcher',
        'Dick',
        'James',
        'Test',
        'This is Fletchers bio',
        'fdi19@uclive.ac.nz',
        '2000-03-10',
        '+64 22 104 1375',
        '123 Street St, Ilam',
        '2020-07-14T14:32:00Z',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       ('Maximilian',
        'Birzer',
        '',
        'mbi47',
        'This is Maxs bio',
        'mbi47@uclive.ac.nz',
        '1986-12-22',
        '+64 21 0266 7255',
        '99 Waimairi Rd, Ilam',
        '2020-07-14T14:32:00Z',
        'ROLE_ADMIN',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       ('Alec',
        'Fox',
        '',
        'amf133',
        'This is Alecs bio',
        'amf133@uclive.ac.nz',
        '2000-10-31',
        '',
        '123 Yoza Terrace',
        '2020-07-14T14:32:00Z',
        'ROLE_ADMIN',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       ('Dawson',
        'Berry',
        'Neil',
        'dnb36',
        'This is a bad bio',
        'dnb36@uclive.ac.nz',
        '2001-11-23',
        '',
        '102 The Moon Rd, Somewhere',
        '2020-07-14T14:32:00Z',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi');
