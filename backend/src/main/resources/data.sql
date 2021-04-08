-- noinspection SqlNoDataSourceInspectionForFile

DROP TABLE IF EXISTS USER_BUSINESS;
DROP TABLE IF EXISTS BUSINESS;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS ADDRESS;

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

    HOME_ADDRESS_ID    INT          NOT NULL,
    CONSTRAINT    USER_ADDRESS_FK   FOREIGN KEY (HOME_ADDRESS_ID) REFERENCES ADDRESS (ID)
);

CREATE TABLE BUSINESS
(
    ID            INT AUTO_INCREMENT PRIMARY KEY,
    NAME          VARCHAR(50)  NOT NULL,
    DESCRIPTION   VARCHAR(250),
    BUSINESS_TYPE VARCHAR(50)  NOT NULL,
    CREATED       DATETIME     NOT NULL,

    PRIMARY_ADMINISTRATOR_ID INT NOT NULL,--unsure if I need a constraint for this
    ADDRESS_ID    INT          NOT NULL,
    CONSTRAINT    BUSINESS_ADDRESS_FK   FOREIGN KEY (ADDRESS_ID) REFERENCES ADDRESS (ID)
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
    '0',
    'Void Street',
    '-1',
    'Nil City',
    'Undefined Plains',
    'Null Island'
), (
    1,
    '10',
    'Downing Street',
    'SW1A 2AA',
    'Westminster',
    'London',
    'United Kingdom'
), (
    2,
    '99',
    'Waimari Road',
    '8041',
    'Christchurch',
    'Canterbury',
    'New Zealand'
), (
    3,
    '53',
    'Ilam Road',
    '8041',
    'Christchurch',
    'Canterbury',
    'New Zealand'
), (
    4,
    '123',
    'Fake Street',
    '8041',
    'Christchurch',
    'Canterbury',
    'New Zealand'
), (
    5,
    '79',
    'Place Road',
    '8041',
    'Christchurch',
    'Canterbury',
    'New Zealand'
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
                 HOME_ADDRESS_ID,
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

INSERT INTO BUSINESS(NAME,
                     PRIMARY_ADMINISTRATOR_ID,
                     DESCRIPTION,
                     ADDRESS_ID,
                     BUSINESS_TYPE,
                     CREATED)
VALUES ('TestName',
        4,
        'A Good business',
        5,
        'Retail Trade',
        '2020-07-14T14:32:00Z'),
        ('Fake Business',
        4,
        'Shh very secret',
        4,
        'Retail Trade',
        '2020-07-14T14:32:00Z');

INSERT INTO USER_BUSINESS(USER_ID,
                          BUSINESS_ID)
VALUES (4,
        1),
        (4,
        2);