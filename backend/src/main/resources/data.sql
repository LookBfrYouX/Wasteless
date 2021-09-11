/* MAIN SCHEMA DEFINITION */

DROP TABLE IF EXISTS transaction CASCADE;
DROP TABLE IF EXISTS marketlisting_keyword CASCADE;
DROP TABLE IF EXISTS keyword CASCADE;
DROP TABLE IF EXISTS marketlisting CASCADE;
DROP TABLE IF EXISTS listing CASCADE;
DROP TABLE IF EXISTS inventory_item CASCADE;
DROP TABLE IF EXISTS product_image CASCADE;
DROP TABLE IF EXISTS catalogue CASCADE;
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS image CASCADE;
DROP TABLE IF EXISTS user_business CASCADE;
DROP TABLE IF EXISTS business CASCADE;
DROP TABLE IF EXISTS user CASCADE;
DROP TABLE IF EXISTS address CASCADE;

CREATE TABLE address
(
    ID            BIGINT AUTO_INCREMENT PRIMARY KEY,
    STREET_NUMBER VARCHAR(100),
    STREET_NAME   VARCHAR(200),
    SUBURB        VARCHAR(200),
    POSTCODE      VARCHAR(30),
    CITY          VARCHAR(200),
    REGION        VARCHAR(200),
    COUNTRY       VARCHAR(100) NOT NULL
);

CREATE TABLE user
(
    ID              BIGINT AUTO_INCREMENT PRIMARY KEY,
    FIRST_NAME      VARCHAR(50) NOT NULL,
    LAST_NAME       VARCHAR(50) NOT NULL,
    MIDDLE_NAME     VARCHAR(50),
    NICKNAME        VARCHAR(50),
    BIO             VARCHAR(250),
    EMAIL           VARCHAR(50) NOT NULL UNIQUE,
    DATE_OF_BIRTH   DATE        NOT NULL,
    PHONE_NUMBER    VARCHAR(25),
    IMAGE_NAME      VARCHAR(30),
    CREATED         DATETIME    NOT NULL,
    ROLE            VARCHAR(30) NOT NULL,
    PASSWORD        VARCHAR(60) NOT NULL,
    HOME_ADDRESS_ID BIGINT      NOT NULL,
    CONSTRAINT user_address_fk FOREIGN KEY (HOME_ADDRESS_ID) REFERENCES address (ID)
);

CREATE TABLE business
(
    ID                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    NAME                     VARCHAR(50) NOT NULL,
    DESCRIPTION              VARCHAR(250),
    BUSINESS_TYPE ENUM (
        'ACCOMMODATION_AND_FOOD',
        'RETAIL',
        'CHARITY',
        'NON_PROFIT') NOT NULL,
    CREATED                  DATETIME    NOT NULL,
    PRIMARY_ADMINISTRATOR_ID BIGINT      NOT NULL,
    ADDRESS_ID               BIGINT      NOT NULL,
    CONSTRAINT business_address_fk FOREIGN KEY (ADDRESS_ID) REFERENCES address (ID)
);

CREATE TABLE image
(
    ID                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    FILENAME           VARCHAR(100) NOT NULL,
    THUMBNAIL_FILENAME VARCHAR(120) NOT NULL
);

CREATE TABLE product
(
    ID               BIGINT AUTO_INCREMENT PRIMARY KEY,
    NAME             VARCHAR(100) NOT NULL,
    DESCRIPTION      VARCHAR(500),
    CURRENCY         VARCHAR(4),
    MANUFACTURER     VARCHAR(100),
    RRP              DOUBLE,
    CREATED          DATETIME,
    PRIMARY_IMAGE_ID BIGINT,
    FAT              ENUM('LOW', 'MODERATE','HIGH'),
    SATURATED_FAT    ENUM('LOW', 'MODERATE','HIGH'),
    SUGARS           ENUM('LOW', 'MODERATE','HIGH'),
    SALT             ENUM('LOW', 'MODERATE','HIGH'),
    NUTRI_SCORE      ENUM('A','B','C','D','E'),
    NOVA_GROUP       INT CHECK (NOVA_GROUP <= 4 AND NOVA_GROUP >= 1),
    IS_GLUTEN_FREE BOOL,
    IS_DAIRY_FREE BOOL,
    IS_VEGETARIAN BOOL,
    IS_VEGAN BOOL,
    IS_PALM_OIL_FREE BOOL,
    CONSTRAINT product_image_fk FOREIGN KEY (PRIMARY_IMAGE_ID) REFERENCES image (ID)
);

CREATE TABLE product_image
(
    PRODUCT_ID BIGINT NOT NULL,
    IMAGE_ID   BIGINT NOT NULL,
    CONSTRAINT product_image_pk
        UNIQUE (PRODUCT_ID, IMAGE_ID),
    CONSTRAINT product_fk
        FOREIGN KEY (PRODUCT_ID) REFERENCES product (ID),
    CONSTRAINT image_fk
        FOREIGN KEY (IMAGE_ID) REFERENCES image (ID)
);

CREATE TABLE user_business
(
    USER_ID     BIGINT NOT NULL,
    BUSINESS_ID BIGINT NOT NULL,
    CONSTRAINT user_business_pk
        UNIQUE (USER_ID, BUSINESS_ID),
    CONSTRAINT user_business_user_fk
        FOREIGN KEY (USER_ID) REFERENCES user (ID),
    CONSTRAINT user_business_business_fk
        FOREIGN KEY (BUSINESS_ID) REFERENCES business (ID)
);

CREATE TABLE catalogue
(
    PRODUCT_ID  BIGINT NOT NULL,
    BUSINESS_ID BIGINT NOT NULL,
    CONSTRAINT catalogue_pk
        UNIQUE (PRODUCT_ID, BUSINESS_ID),
    CONSTRAINT catalogue_product_fk
        FOREIGN KEY (PRODUCT_ID) REFERENCES product (ID),
    CONSTRAINT catalogue_business_fk
        FOREIGN KEY (BUSINESS_ID) REFERENCES business (ID)
);

CREATE TABLE inventory_item
(
    ID             BIGINT AUTO_INCREMENT PRIMARY KEY,
    PRODUCT_ID     BIGINT NOT NULL,
    BUSINESS_ID    BIGINT NOT NULL,
    -- WARNING: BUSINESS ID IS DUPLICATED DATA - CAN BE FOUND FROM PRODUCT
    QUANTITY       BIGINT NOT NULL,
    PRICE_PER_ITEM DOUBLE NOT NULL,
    TOTAL_PRICE    DOUBLE NOT NULL,
    EXPIRES        DATE   NOT NULL,
    MANUFACTURED   DATE,
    SELL_BY        DATE,
    BEST_BEFORE    DATE,

    CONSTRAINT inventory_item_product_fk
        FOREIGN KEY (PRODUCT_ID) REFERENCES product (ID),
    CONSTRAINT inventory_item_business_fk
        FOREIGN KEY (BUSINESS_ID) REFERENCES business (ID)
);


CREATE TABLE listing
(
    ID                BIGINT AUTO_INCREMENT PRIMARY KEY,
    INVENTORY_ITEM_ID BIGINT NOT NULL,
    QUANTITY          BIGINT NOT NULL,
    PRICE             DOUBLE NOT NULL,
    MORE_INFO         VARCHAR(50),
    CREATED           DATETIME,
    CLOSES            DATETIME,
    CONSTRAINT inventory_item_fk
        FOREIGN KEY (INVENTORY_ITEM_ID) REFERENCES inventory_item (ID)
);

CREATE TABLE marketlisting
(
    ID                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    CREATOR_ID         BIGINT                               NOT NULL,
    SECTION            ENUM ('ForSale','Wanted','Exchange') NOT NULL,
    CREATED            DATETIME,
    DISPLAY_PERIOD_END DATETIME,
    TITLE              VARCHAR(50)                          NOT NULL,
    DESCRIPTION        VARCHAR(250),
    CONSTRAINT user_fk FOREIGN KEY (CREATOR_ID) REFERENCES user (ID)
);

CREATE TABLE keyword
(
    ID      BIGINT AUTO_INCREMENT PRIMARY KEY,
    NAME    VARCHAR(50) NOT NULL UNIQUE,
    CREATED DATETIME    NOT NULL
);

CREATE TABLE marketlisting_keyword
(
    MARKETLISTING_ID BIGINT NOT NULL,
    KEYWORD_ID       BIGINT NOT NULL,
    CONSTRAINT marketlisting_keyword_pk
        UNIQUE (MARKETLISTING_ID, KEYWORD_ID),
    CONSTRAINT marketlisting_keyword_fk
        FOREIGN KEY (MARKETLISTING_ID) REFERENCES marketlisting (ID),
    CONSTRAINT keyword_marketlisting_fk
        FOREIGN KEY (KEYWORD_ID) REFERENCES keyword (ID)
);

CREATE TABLE transaction
(
    ID           BIGINT AUTO_INCREMENT PRIMARY KEY,
    SALE_DATE    DATETIME NOT NULL,
    LISTING_DATE DATETIME NOT NULL,
    PRODUCT_ID   BIGINT   NOT NULL,
    AMOUNT       DOUBLE   NOT NULL,
    CONSTRAINT TRANSACTION_PRODUCT_FK
        FOREIGN KEY (PRODUCT_ID) REFERENCES product (ID)
);

-- INSERTING TEST DATA BELOW

-- Inserting address data

INSERT INTO address(ID,
                    STREET_NUMBER,
                    STREET_NAME,
                    SUBURB,
                    POSTCODE,
                    CITY,
                    REGION,
                    COUNTRY)
VALUES (6001,
        '10',
        'Downing Street',
        'Ilam',
        'SW1A 2AA',
        'Westminster',
        'London',
        'United Kingdom of Great Britain and Northern Ireland'),
       (6002,
        '99',
        'Waimari Road',
        'Ilam',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6003,
        '53',
        'Ilam Road',
        'Ilam',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6004,
        '123',
        'Fake Street',
        'Ilam',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6005,
        '79',
        'Place Road',
        'Ilam',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand');

-- Inserting user data

INSERT INTO user(ID,
                 FIRST_NAME,
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
VALUES (5001,
        'Fletcher',
        'Dick',
        'James',
        'fdi19',
        'Hello! I am Fletcher and I am currently studying Software Engineering.',
        'fdi19@uclive.ac.nz',
        '2000-03-10',
        '+64 22 104 1375',
        6001,
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       (5002,
        'Maximilian',
        'Birzer',
        '',
        'mbi47',
        'This is Maxs bio',
        'mbi47@uclive.ac.nz',
        '1986-12-22',
        '+64 21 0266 7255',
        6001,
        '2020-07-14T14:32:00.000000',
        'ROLE_ADMIN',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       (5003,
        'Alec',
        'Fox',
        '',
        'amf133',
        'This is Alecs bio',
        'amf133@uclive.ac.nz',
        '2000-10-31',
        '',
        6002,
        '2020-07-14T14:32:00.000000',
        'ROLE_ADMIN',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       (5004,
        'Dawson',
        'Berry',
        'Neil',
        'dnb36',
        'This is a bad bio',
        'dnb36@uclive.ac.nz',
        '2001-11-23',
        '',
        6003,
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       (5005,
        'Haruka',
        'Ichinose',
        '',
        'hic21',
        'This is a fantastic bio',
        'hic21@uclive.ac.nz',
        '1998-05-14',
        '',
        6003,
        '2020-07-14T14:32:00.000000',
        'ROLE_ADMIN',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi');

-- Inserting business data

INSERT INTO business(ID,
                     NAME,
                     PRIMARY_ADMINISTRATOR_ID,
                     DESCRIPTION,
                     ADDRESS_ID,
                     BUSINESS_TYPE,
                     CREATED)
VALUES (1001,
        'TestName',
        5004,
        'A Good business',
        6005,
        'RETAIL',
        '2020-07-14T14:32:00.000000'),
       (1002,
        'Fake Business',
        5004,
        'Shh very secret',
        6004,
        'RETAIL',
        '2020-07-14T14:32:00.000000'),
       (1003,
        'Pie Palace',
        5002,
        'Pies for all',
        6001,
        'RETAIL',
        '2020-07-14T14:32:00.000000');

-- Inserting user-business data

INSERT INTO user_business(USER_ID,
                          BUSINESS_ID)
VALUES (5004, 1001),
       (5004, 1002),
       (5002, 1003);

-- Inserting product data

INSERT INTO product (ID,
                     NAME,
                     DESCRIPTION,
                     MANUFACTURER,
                     RRP,
                     CREATED)
VALUES (5001,
        'Anchor Milk Standard Blue Top',
        'Essential goodness for your family. Anchor blue top is the milk that new zealanders grow up on. Its brimming with important nutrients, and it delivers more energy than most types of milk. Enjoy a glassful of great nz tradition. Number shown on pack is a guide only and not indicative of what will be supplied.',
        'North Korean Milk GmbH',
        4.67,
        '2020-07-14T14:32:00.000000'),
       (5002,
        'Meadow Fresh Milk Standard Family Fresh Homogenised',
        'At meadow fresh we believe the more goodness the better. That''s why our milk is less processed and permeate free for natural levels of protein and calcium.',
        'Cows teat co.',
        4.62,
        '2020-07-14T14:32:00.000000'),
       (5003,
        'Sanitarium So Good Almond Milk Unsweetened Long Life',
        'So good almond milk unsweetened from sanitarium new zealand is a delicious plant-based milk made from almonds. So good almond unsweetened can be enjoyed by the glass, on cereal, or in your favourite recipe. It’s a good source of calcium and a source of vitamins e, b2 and b12. It''s low in fat and is also 100% lactose, gluten and dairy free. Plus it contains no added sugar!.',
        'Ozi ozi ozi',
        3.00,
        '2020-07-14T14:32:00.000000'),
       (5004,
        'Lewis Road Creamery Milk Standard Homogenised Jersey Milk',
        'Homogenised jersey milk is naturally better. High in protein, calcium and a2 beta casein with a full bodied flavour. Bottled in 100% recycled plastic, contains no permeate and no pke used to supplement feed.',
        'Creamy co.',
        5.90,
        '2020-07-14T14:32:00.000000'),
       (5005,
        'Sanitarium So Good Oat Milk No Added Sugar',
        'Made in Australia from at least 97% Australian ingredients.',
        'Ozi ozi ozi',
        3.30,
        '2020-07-14T14:32:00.000000'),
       (5006,
        'Mr Macs Pies',
        'Made in Ilam from at least 97% Cows.',
        'Sweenies corpses co.',
        4.70,
        '2020-07-14T14:32:00.000000');

-- Inserting catalogue data

INSERT INTO catalogue (PRODUCT_ID, BUSINESS_ID)
VALUES (5001, 1001),
       (5002, 1002),
       (5003, 1001),
       (5004, 1002),
       (5005, 1001),
       (5006, 1003);

-- Inserting inventory_item data

INSERT INTO inventory_item (ID, PRODUCT_ID, BUSINESS_ID, QUANTITY, PRICE_PER_ITEM, TOTAL_PRICE,
                            EXPIRES, MANUFACTURED, SELL_BY, BEST_BEFORE)
VALUES (5001, 5001, 1001, 20, 4.67, 20.00, '2021-08-16', '2021-08-13',
        '2021-08-15', '2021-08-16'),
       (5002, 5003, 1001, 10, 4.62, 20.00, '2021-08-16', '2021-08-13',
        '2021-08-15', '2021-08-16'),
       (5003, 5005, 1001, 15, 3.00, 20.00, '2021-08-16', '2021-08-13',
        '2021-08-15', '2021-08-16');

-- Inserting listing data

INSERT INTO listing (ID, INVENTORY_ITEM_ID, QUANTITY, PRICE, MORE_INFO, CREATED, CLOSES)
VALUES (5001, 5001, 2, 9.00, 'fletcher was here RAWR XD', '2021-05-16 21:16:17',
        '2021-06-16 21:16:26'),
       (5002, 5001, 3, 12.00, null, '2021-05-16 21:16:17', '2021-06-16 21:16:26'),
       (5003, 5002, 9, 45.00, null, '2021-05-16 21:16:17', '2021-06-16 21:16:26'),
       (5004, 5003, 15, 45.00, null, '2021-05-16 21:16:17', '2021-06-16 21:16:26');

INSERT INTO marketlisting (ID, CREATOR_ID, SECTION, CREATED, DISPLAY_PERIOD_END, TITLE, DESCRIPTION)
VALUES (2501, 5002, 'ForSale', '2021-05-23 15:34:20', '2021-06-23 15:34:20', 'Shoddy web app',
        'Wanting to sell Wasteless, no longer needed or wanted.'),
       (2502, 5002, 'Wanted', '2021-05-23 15:34:20', '2021-06-23 15:34:20', 'Fresh motivation',
        'Will pay for motivation.');


INSERT INTO keyword (NAME, CREATED)
VALUES ('Tea', '2021-04-23 15:34:20'),
       ('Electronic', '2021-04-23 15:34:20'),
       ('Supplement', '2021-04-23 15:34:20'),
       ('Dairy', '2021-04-23 15:34:20');
