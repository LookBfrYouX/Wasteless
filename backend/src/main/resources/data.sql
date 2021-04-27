-- ###############################  Users Setup  ###############################
DROP TABLE IF EXISTS catalogue;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS user_business;
DROP TABLE IF EXISTS business;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS address;

CREATE TABLE address
(
    ID            INT AUTO_INCREMENT PRIMARY KEY,
    STREET_NUMBER VARCHAR(100),
    STREET_NAME   VARCHAR(200),
    POSTCODE      VARCHAR(30),
    CITY          VARCHAR(200),
    REGION        VARCHAR(200),
    COUNTRY       VARCHAR(100) NOT NULL
);

CREATE TABLE user
(
    ID              INT AUTO_INCREMENT PRIMARY KEY,
    FIRST_NAME      VARCHAR(50) NOT NULL,
    LAST_NAME       VARCHAR(50) NOT NULL,
    MIDDLE_NAME     VARCHAR(50),
    NICKNAME        VARCHAR(50),
    BIO             VARCHAR(250),
    EMAIL           VARCHAR(50) NOT NULL UNIQUE,
    DATE_OF_BIRTH   DATE        NOT NULL,
    PHONE_NUMBER    VARCHAR(18),

    CREATED         DATETIME    NOT NULL,
    ROLE            VARCHAR(30) NOT NULL,
    PASSWORD        CHAR(60)    NOT NULL,

    HOME_ADDRESS_ID INT         NOT NULL,
    CONSTRAINT user_address_fk FOREIGN KEY (HOME_ADDRESS_ID) REFERENCES address (ID)
);

CREATE TABLE business
(
    ID                       INT AUTO_INCREMENT PRIMARY KEY,
    NAME                     VARCHAR(50) NOT NULL,
    DESCRIPTION              VARCHAR(250),
    BUSINESS_TYPE            VARCHAR(50) NOT NULL,
    CREATED                  DATETIME    NOT NULL,

    PRIMARY_ADMINISTRATOR_ID INT         NOT NULL,
    ADDRESS_ID               INT         NOT NULL,
    CONSTRAINT business_address_fk FOREIGN KEY (ADDRESS_ID) REFERENCES address (ID)
);

CREATE TABLE product
(
    ID          INT AUTO_INCREMENT PRIMARY KEY,
    NAME        VARCHAR(100) NOT NULL,
    DESCRIPTION VARCHAR(500),
    RRP         DECIMAL(6, 2),
    CREATED     DATETIME
);

CREATE TABLE user_business
(
    USER_ID     INT NOT NULL,
    BUSINESS_ID INT NOT NULL,
    CONSTRAINT user_business_pk
        UNIQUE (USER_ID, BUSINESS_ID),
    CONSTRAINT user_business_user_fk
        FOREIGN KEY (USER_ID) REFERENCES user (ID),
    CONSTRAINT user_business_business_fk
        FOREIGN KEY (BUSINESS_ID) REFERENCES business (ID)
);

CREATE TABLE catalogue
(
    PRODUCT_ID  INT NOT NULL,
    BUSINESS_ID INT NOT NULL,
    CONSTRAINT catalogue_pk
        UNIQUE (PRODUCT_ID, BUSINESS_ID),
    CONSTRAINT catalogue_product_fk
        FOREIGN KEY (PRODUCT_ID) REFERENCES product (ID),
    CONSTRAINT catalogue_business_fk
        FOREIGN KEY (BUSINESS_ID) REFERENCES business (ID)
);

CREATE TABLE inventory
(
    PRODUCT_ID INT NOT NULL,
    BUSINESS_ID INT NOT NULL,
    QUANTITY INT NOT NULL,
    PRICE DECIMAL(6, 2) NOT NULL,
    EXPIRY DATETIME NOT NULL,
    CONSTRAINT inventory_pk
        UNIQUE (PRODUCT_ID, BUSINESS_ID),
    CONSTRAINT inventory_product_fk
        FOREIGN KEY (PRODUCT_ID) REFERENCES product (ID),
    CONSTRAINT inventory_business_fk
        FOREIGN KEY (BUSINESS_ID) REFERENCES business (ID)
);



-- INSERTING TEST DATA BELOW

-- Inserting test address data

INSERT INTO address(STREET_NUMBER,
                    STREET_NAME,
                    POSTCODE,
                    CITY,
                    REGION,
                    COUNTRY)
VALUES ('10',
        'Downing Street',
        'SW1A 2AA',
        'Westminster',
        'London',
        'United Kingdom'),
       ('99',
        'Waimari Road',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       ('53',
        'Ilam Road',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       ('123',
        'Fake Street',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       ('79',
        'Place Road',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand');

-- Inserting test user data

INSERT INTO user(FIRST_NAME,
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
        'fdi19',
        'Hello! I am Fletcher and I am currently studying Software Engineering.',
        'fdi19@uclive.ac.nz',
        '2000-03-10',
        '+64 22 104 1375',
        1,
        '2020-07-14T14:32:00.000000',
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
        '2020-07-14T14:32:00.000000',
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
        '2020-07-14T14:32:00.000000',
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
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       ('Haruka',
        'Ichinose',
        '',
        'hic21',
        'This is a fantastic bio',
        'hic21@uclive.ac.nz',
        '1998-05-14',
        '',
        3,
        '2020-07-14T14:32:00.000000',
        'ROLE_ADMIN',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi');

-- Inserting test business data

INSERT INTO business(NAME,
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
        '2020-07-14T14:32:00.000000'),
       ('Fake Business',
        4,
        'Shh very secret',
        4,
        'Retail Trade',
        '2020-07-14T14:32:00.000000'),
       ('Pie Palace',
        2,
        'Pies for all',
        4,
        'Retail Trade',
        '2020-07-14T14:32:00.000000');

-- Inserting test user-business data

INSERT INTO user_business(USER_ID,
                          BUSINESS_ID)
VALUES (4, 1),
       (4, 2),
       (2, 3);

-- Inserting product data

INSERT INTO product (NAME, DESCRIPTION, RRP, CREATED)
VALUES ('Anchor Milk Standard Blue Top',
        'Essential goodness for your family. Anchor blue top is the milk that new zealanders grow up on. Its brimming with important nutrients, and it delivers more energy than most types of milk. Enjoy a glassful of great nz tradition. Number shown on pack is a guide only and not indicative of what will be supplied.',
        4.67,
        '2020-07-14T14:32:00.000000'),
       ('Meadow Fresh Milk Standard Family Fresh Homogenised',
        'At meadow fresh we believe the more goodness the better. That''s why our milk is less processed and permeate free for natural levels of protein and calcium.',
        4.62,
        '2020-07-14T14:32:00.000000'),
       ('Sanitarium So Good Almond Milk Unsweetened Long Life',
        'So good almond milk unsweetened from sanitarium new zealand is a delicious plant-based milk made from almonds. So good almond unsweetened can be enjoyed by the glass, on cereal, or in your favourite recipe. It’s a good source of calcium and a source of vitamins e, b2 and b12. It''s low in fat and is also 100% lactose, gluten and dairy free. Plus it contains no added sugar!.',
        3.00,
        '2020-07-14T14:32:00.000000'),
       ('Lewis Road Creamery Milk Standard Homogenised Jersey Milk',
        'Homogenised jersey milk is naturally better. High in protein, calcium and a2 beta casein with a full bodied flavour. Bottled in 100% recycled plastic, contains no permeate and no pke used to supplement feed.',
        5.90,
        '2020-07-14T14:32:00.000000'),
       ('Sanitarium So Good Oat Milk No Added Sugar',
        'Made in Australia from at least 97% Australian ingredients.',
        3.30,
        '2020-07-14T14:32:00.000000'),
       ('Mr Macs Pies',
        'Made in Ilam from at least 97% Cows.',
        4.70,
        '2020-07-14T14:32:00.000000');

-- Inserting the product to business relationship data

INSERT INTO catalogue (PRODUCT_ID, BUSINESS_ID)
VALUES (1, 1),
       (2, 2),
       (3, 1),
       (4, 2),
       (5, 1),
       (6, 3),
       (2, 3);