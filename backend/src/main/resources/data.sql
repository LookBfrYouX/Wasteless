DROP TABLE IF EXISTS USER_BUSINESS;
DROP TABLE IF EXISTS ADDRESS;
DROP TABLE IF EXISTS BUSINESS;
DROP TABLE IF EXISTS USER;

CREATE TABLE ADDRESS
(
    ID INT AUTO_INCREMENT PRIMARY KEY,
    STREET_NUMBER VARCHAR(100) NOT NULL,
    STREET_NAME VARCHAR(200) NOT NULL,
    POSTCODE VARCHAR(30) NOT NULL,
    CITY VARCHAR(200) NOT NULL,
    REGION VARCHAR(200) NOT NULL,
    COUNTRY VARCHAR(100) NOT NULL
);

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

    CREATED       DATETIME     NOT NULL,
    ROLE          VARCHAR(30)  NOT NULL,
    PASSWORD      CHAR(60)     NOT NULL,

    ADDRESS_ID    INT          NOT NULL,
    CONSTRAINT    ADDRESS_FK   FOREIGN KEY (ADDRESS_ID) REFERENCES ADDRESS (ID)
);

CREATE TABLE BUSINESS
(
    ID            INT AUTO_INCREMENT PRIMARY KEY,
    NAME          VARCHAR(50)  NOT NULL,
    DESCRIPTION   VARCHAR(250),
    ADDRESS       VARCHAR(250) NOT NULL,
    BUSINESS_TYPE VARCHAR(50)  NOT NULL,
    CREATED       DATETIME     NOT NULL,
);

CREATE TABLE USER_BUSINESS
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

INSERT INTO ADDRESS(
    ID,
    STREET_NUMBER,
    STREET_NAME,
    POSTCODE,
    CITY,
    REGION,
    COUNTRY
) VALUES (
    0,
    "0",
    "Void Street",
    "-1",
    "Nil City",
    "Undefined Plains",
    "Null Island"
), (
    1
    "10",
    "Downing Street",
    "SW1A 2AA",
    "Westminster",
    "London",
    "United Kingdom"
), (
    2,
    "99",
    "Waimari Road",
    "8041",
    "Christchurch",
    "Canterbury",
    "New Zealand"
), (
    3,
    "53",
    "Ilam Road",
    "8041",
    "Christchurch",
    "Canterbury",
    "New Zealand"
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
                 ADDRESS_ID,
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
        0,
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
        1,
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
        2,
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
        3,
        '2020-07-14T14:32:00Z',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi');
