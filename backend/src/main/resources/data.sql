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
    BUSINESS_TYPE            ENUM (
        'ACCOMMODATION_AND_FOOD',
        'RETAIL',
        'CHARITY',
        'NON_PROFIT')                    NOT NULL,
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
    FAT              ENUM ('LOW', 'MODERATE','HIGH'),
    SATURATED_FAT    ENUM ('LOW', 'MODERATE','HIGH'),
    SUGARS           ENUM ('LOW', 'MODERATE','HIGH'),
    SALT             ENUM ('LOW', 'MODERATE','HIGH'),
    NUTRI_SCORE      ENUM ('A','B','C','D','E'),
    NOVA_GROUP       INT CHECK (NOVA_GROUP <= 4 AND NOVA_GROUP >= 1),
    IS_GLUTEN_FREE   BOOL,
    IS_DAIRY_FREE    BOOL,
    IS_VEGETARIAN    BOOL,
    IS_VEGAN         BOOL,
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
    DESCRIPTION        VARCHAR(1000),
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
    BUSINESS_ID  BIGINT   NOT NULL,
    AMOUNT       DOUBLE   NOT NULL,
    CONSTRAINT TRANSACTION_PRODUCT_FK
        FOREIGN KEY (PRODUCT_ID) REFERENCES product (ID),
    CONSTRAINT TRANSACTION_BUSINESS_FK
        FOREIGN KEY (BUSINESS_ID) REFERENCES business (ID)
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
        'New Zealand'),
       (6006,
        '65',
        'Blenheim Road',
        'Addington',
        '8011',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6007,
        '6/1063',
        'Ferry Road',
        'Ferrymead',
        '8063',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6008,
        '34',
        'Madras Street',
        'St Albans',
        '8011',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6009,
        '92',
        'Wilsons Road',
        'St Martins',
        '8024',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6010,
        '382',
        'Moorhouse Avenue',
        'Central City',
        '8011',
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
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       (5006,
        'Rick',
        'Muir',
        '',
        'rnm47',
        'Hi, my names Rick but some people call me Rick. I enjoy hunting and surfing and own 2 dogs.',
        'rnm47@uclive.ac.nz',
        '2000-10-10',
        '',
        6006,
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       (5007,
        'Finn',
        'Russel',
        '',
        'frs122',
        'I am a third year sport coaching student at UC. I love the crusaders and have a crush on Dan Carter',
        'frs122@uclive.ac.nz',
        '2001-02-21',
        '',
        6007,
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       (5008,
        'Timothy',
        'Clarke',
        '',
        'tjc14',
        'Im a plumber for Whitehead Plumbing. I also fit gas tanks and love sleeping on the job',
        'tjc14@uclive.ac.nz',
        '1998-10-15',
        '',
        6008,
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       (5009,
        'Tony',
        'Carleton',
        '',
        'tca123',
        'Im an electrician for Caterpillar construction working on generators that will keep this country running in case of disaster.',
        'bigtony@gmail.com',
        '1997-02-11',
        '',
        6009,
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
        '$2y$12$WfyxRpooIc6QjYxvPPH7leapKY.tKFSMZdT/W1oWcTro/FutOzqQi'),
       (5010,
        'Sam',
        'Dawson',
        '',
        'smd66',
        'I am a returning UC student planning on studying Computer Science. I initially spent 4 years at UC studying accounting before realizing how boring it is.',
        'smd66@uclive.ac.nz',
        '1997-07-04',
        '',
        6010,
        '2020-07-14T14:32:00.000000',
        'ROLE_USER',
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
                     CREATED,
                     FAT,
                     SATURATED_FAT,
                     SUGARS,
                     SALT,
                     NUTRI_SCORE,
                     NOVA_GROUP,
                     IS_GLUTEN_FREE,
                     IS_DAIRY_FREE,
                     IS_VEGETARIAN,
                     IS_VEGAN,
                     IS_PALM_OIL_FREE)
VALUES (5001,
        'Anchor Milk Standard Blue Top',
        'Essential goodness for your family. Anchor blue top is the milk that new zealanders grow up on. Its brimming with important nutrients, and it delivers more energy than most types of milk. Enjoy a glassful of great nz tradition. Number shown on pack is a guide only and not indicative of what will be supplied.',
        'North Korean Milk GmbH',
        4.67,
        '2020-07-14T14:32:00.000000',
        'LOW',
        'MODERATE',
        'LOW',
        null,
        'A',
        4,
        false,
        false,
        false,
        false,
        true
       ),
       (5002,
        'Meadow Fresh Milk Standard Family Fresh Homogenised',
        'At meadow fresh we believe the more goodness the better. That''s why our milk is less processed and permeate free for natural levels of protein and calcium.',
        'Cows teat co.',
        4.62,
        '2020-07-14T14:32:00.000000',
        'LOW',
        'LOW',
        'MODERATE',
        'LOW',
        'B',
        2,
        null,
        null,
        null,
        null,
        null
       ),
       (5003,
        'Sanitarium So Good Almond Milk Unsweetened Long Life',
        'So good almond milk unsweetened from sanitarium new zealand is a delicious plant-based milk made from almonds. So good almond unsweetened can be enjoyed by the glass, on cereal, or in your favourite recipe. It’s a good source of calcium and a source of vitamins e, b2 and b12. It''s low in fat and is also 100% lactose, gluten and dairy free. Plus it contains no added sugar!.',
        'Ozi ozi ozi',
        3.00,
        '2020-07-14T14:32:00.000000',
        'LOW',
        'LOW',
        'LOW',
        'LOW',
        'A',
        4,
        true,
        true,
        true,
        true,
        true
       ),
       (5004,
        'Lewis Road Creamery Milk Standard Homogenised Jersey Milk',
        'Homogenised jersey milk is naturally better. High in protein, calcium and a2 beta casein with a full bodied flavour. Bottled in 100% recycled plastic, contains no permeate and no pke used to supplement feed.',
        'Creamy co.',
        5.90,
        '2020-07-14T14:32:00.000000',
        'MODERATE',
        'MODERATE',
        'MODERATE',
        'MODERATE',
        'C',
        null,
        false,
        false,
        false,
        false,
        false
       ),
       (5005,
        'Sanitarium So Good Oat Milk No Added Sugar',
        'Made in Australia from at least 97% Australian ingredients.',
        'Ozi ozi ozi',
        3.30,
        '2020-07-14T14:32:00.000000',
        'LOW',
        null,
        null,
        null,
        'B',
        3,
        null,
        null,
        null,
        null,
        true
       ),
       (5006,
        'Mr Macs Pies',
        'Made in Ilam from at least 97% Cows.',
        'Sweenies corpses co.',
        4.70,
        '2020-07-14T14:32:00.000000',
        'HIGH',
        'HIGH',
        'HIGH',
        'HIGH',
        'D',
        1,
        false,
        null,
        null,
        false,
        null
       );

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
        '2021-08-15', '2021-08-16'),
       (5004, 5002, 1002, 10, 10.00, 70.00, '2021-10-12', '2021-10-16',
        '2021-10-28', '2021-10-30'),
       (5005, 5004, 1001, 10, 10.00, 70.00, '2021-10-12', '2021-10-16',
        '2021-10-28', '2021-10-30'),
       (5006, 5006, 1003, 7, 30.00, 205.99, '2021-10-13', '2021-10-18',
        '2021-10-23', '2021-10-31');


-- Inserting listing data

INSERT INTO listing (ID, INVENTORY_ITEM_ID, QUANTITY, PRICE, MORE_INFO, CREATED, CLOSES)
VALUES (5001, 5001, 2, 9.00, 'fletcher was here RAWR XD', '2021-05-16 21:16:17',
        '2021-06-16 21:16:26'),
       (5002, 5001, 3, 12.00, null, '2021-05-16 21:16:17', '2021-06-16 21:16:26'),
       (5003, 5002, 9, 45.00, null, '2021-05-16 21:16:17', '2021-06-16 21:16:26'),
       (5004, 5003, 15, 45.00, null, '2021-05-16 21:16:17', '2021-06-16 21:16:26'),
       (5005, 5004, 15, 45.00, null, '2021-05-16 21:16:17', '2021-06-16 21:16:26'),
       (5006, 5005, 15, 45.00, null, '2021-05-16 21:16:17', '2021-06-16 21:16:26'),
       (5007, 5006, 15, 45.00, null, '2021-05-16 21:16:17', '2021-06-16 21:16:26');

INSERT INTO marketlisting (ID, CREATOR_ID, SECTION, CREATED, DISPLAY_PERIOD_END, TITLE, DESCRIPTION)
VALUES (2501, 5001, 'ForSale', '2021-10-13 15:34:20', '2021-11-13 15:34:20', 'Shoddy web app',
        'Wanting to sell Wasteless, no longer needed or wanted.'),
       (2529, 5001, 'Wanted', '2021-10-13 15:34:20', '2021-11-13 15:34:20', 'Fresh motivation',
        'Will pay for motivation.'),
       (2530, 5001, 'Exchange', '2021-10-13 15:34:20', '2021-11-13 15:34:20', 'Nothing for something',
        'I will pay you in 100% nothing and I expect something good in return. No time wasters'),

       (2502, 5002, 'Wanted', '2021-10-14 15:34:20', '2021-11-14 15:34:20', 'Pool Table',
        'Looking for a 7ft pool table with accessories.'),
       (2503, 5002, 'ForSale', '2021-10-14 15:34:20', '2021-11-14 15:34:20', 'Al Merrick 5''9 Surfboard',
        'Channel Islands Dumpster Diver Surfboard. 5''9 9 3/4 2 3/8. Surf including fins. I''am selling this awesome board because I dont have enough experience to surf on the board..'),
       (2504, 5002, 'Exchange', '2021-10-14 15:34:20', '2021-11-14 15:34:20', 'Toothpick for toothpaste',
        'I have this toothpick that im not really using, I just got a toothbrush and it feels like a waste when I dont have any toothpaste.'),

       (2505, 5003, 'Wanted', '2021-10-12 15:34:20', '2021-11-12 15:34:20', 'RCA cable',
        'After a 2m+ cheap RCA cable.'),
       (2506, 5003, 'ForSale', '2021-10-12 15:34:20', '2021-11-12 15:34:20', 'Recliner gaming chair',
        'Good condition, right arm lose, just needs to be fixed with screws.'),
       (2507, 5003, 'Exchange', '2021-10-12 15:34:20', '2021-11-12 15:34:20', 'Pokemon cards',
        'I have the first 17 limited edition pokemon cards and am getting sick of them. Looking to swap for a Yu Gi Oh set.'),

       (2508, 5004, 'Wanted', '2021-10-01 15:34:20', '2021-11-01 15:34:20', 'Pennywise - IT Movie Memorabilia',
        'After a Pennywise costume for an upcoming party.'),
       (2509, 5004, 'ForSale', '2021-10-01 15:34:20', '2021-11-01 15:34:20', 'O Scott World Hoodie',
        'Hoodie with Matrix Print by O Scott World. Awesome local Designer! Size  M depending on desired fit. Inquire for measurements if needed'),
       (2510, 5004, 'Exchange', '2021-10-01 15:34:20', '2021-11-01 15:34:20', 'Court Purple Sb Dunks',
        'Looking to swap my dusty shoes for some less dusty ones.'),

       (2511, 5005, 'Wanted', '2021-10-11 15:34:20', '2021-11-11 15:34:20', 'Allblacks flag',
        'Want a large allblacks flag to hang on my flagpole outside. If anyone knows anyone who has or makes one please get in contact.'),
       (2512, 5005, 'ForSale', '2021-10-11 15:34:20', '2021-11-11 15:34:20', 'Whitebait',
        'Whitebait fresh and frozen $40 pound, $20 half pound'),
       (2513, 5005, 'Exchange', '2021-10-11 15:34:20', '2021-11-11 15:34:20', 'Toothpick for toothpaste',
        'I have this toothpick that im not really using, I just got a toothbrush and it feels like a waste when I dont have any toothpaste.'),

       (2514, 5006, 'Wanted', '2021-10-05 15:34:20', '2021-11-05 15:34:20', 'Bonsai tree',
        'Looking for a small Bonsai under $60.'),
       (2515, 5006, 'ForSale', '2021-10-05 15:34:20', '2021-11-05 15:34:20', 'Red Bull energy drink 250ml x 3',
        'Brand new.'),
       (2516, 5006, 'Exchange', '2021-10-05 15:34:20', '2021-11-05 15:34:20', 'Bataleon "push" up 143cm snowboard',
        'Bataleon “push up” 143cm snowboard women’s in good condition used for less than 1 season. Accepting swaps for a surfboard'),

       (2517, 5007, 'Wanted', '2021-09-25 15:34:20', '2021-10-25 15:34:20', 'Sunglasses',
        'Hi all, I just gave birth to 7 kids and need size 0 sunnies for them all as we are about to go on a massive ski trip.'),
       (2518, 5007, 'ForSale', '2021-09-25 15:34:20', '2021-10-25 15:34:20', 'Camelbak drink bottle',
        'Fair used. $15 ONO'),
       (2519, 5007, 'Exchange', '2021-09-25 15:34:20', '2021-10-25 15:34:20', 'PS4 for Xbox One',
        'Just got a PS5 for myself and looking to trade my old PS4 for an Xbox One for my little brother.'),

       (2520, 5008, 'Wanted', '2021-09-21 15:34:20', '2021-10-21 15:34:20', 'North face jacket',
        'Looking for a size M North face jacket or similar.'),
       (2521, 5008, 'ForSale', '2021-09-21 15:34:20', '2021-10-21 15:34:20', 'Beginners plant pack, fresh cuttings',
        '7 different cuttings all decent size in a pack, perfect for beginners. $15 and happy to drop off locally for a fee 😊'),
       (2522, 5008, 'Exchange', '2021-09-21 15:34:20', '2021-10-21 15:34:20', 'Free bike',
        'I do not need anything in exchange but didnt know where else to list this.'),

       (2523, 5009, 'Wanted', '2021-10-01 15:34:20', '2021-11-01 15:34:20', 'Flatmate wanted',
        'Hey all. I''m looking for someone to fill a room in this modern home with myself and a mate. $150 p/w or $220 for a couple (+ expenses). It''s located in Redwood off Grimseys Road (highway noise isn''t an issue) just down the road from Northlands mall, about 10-15 minutes drive from Uni. You would be living with a couple of easy going guys in their early 20''s that enjoy a few drinks and games in the weekend. One of us is a student and the other is a full time worker. Small pets negotiable - no dogs. Move in date ASAP'),
       (2524, 5009, 'ForSale', '2021-10-01 15:34:20', '2021-11-01 15:34:20', 'Poker Chips',
        'Poker Chips, PMO.'),
       (2525, 5009, 'Exchange', '2021-10-01 15:34:20', '2021-11-01 15:34:20', 'Coffee table',
        'Looking to trade my bunnings coffee table ($688 RRP) for a nice dining table.'),

       (2526, 5010, 'Wanted', '2021-10-11 15:34:20', '2021-11-11 15:34:20', 'FLATMATE WANTED',
        'Kia Ora Team, Looking for someone to join me in a two bed two bath flat in the Soho apartment block. Key deets: Rent - $300/week, Expenses - $30/week, Double room - comes with your own ensuite, Super sunny and central!'),
       (2527, 5010, 'ForSale', '2021-10-11 15:34:20', '2021-11-11 15:34:20', 'Fresh Fadez',
        'I am an up and coming barber learning how to do sick fadez and lines. Will daps you right up on entry. $5'),
       (2528, 5010, 'Exchange', '2021-10-11 15:34:20', '2021-11-11 15:34:20', 'Fake rolex',
        'Accepting swaps for my fake rolex. Will consider fake Gucci/Louis Vuitton jacket.');


INSERT INTO keyword (ID, NAME, CREATED)
VALUES (1, 'Cheap', '2021-04-23 15:34:20'),
       (2, 'Electronic', '2021-04-23 15:34:20'),
       (3, 'Supplement', '2021-04-23 15:34:20'),
       (4, 'Food', '2021-04-23 15:34:20'),
       (5, 'Flatmate', '2021-04-23 15:34:20'),
       (6, 'Surf', '2021-04-23 15:34:20'),
       (7, 'Clothing', '2021-04-23 15:34:20'),
       (8, 'Free', '2021-04-23 15:34:20'),
       (9, 'Furniture', '2021-04-23 15:34:20');

INSERT INTO marketlisting_keyword (MARKETLISTING_ID, KEYWORD_ID)
VALUES (2501, 1),
       (2505, 1),
       (2510, 1),
       (2518, 1),
       (2529, 1),
       (2505, 2),
       (2519, 2),
       (2515, 3),
       (2515, 4),
       (2526, 5),
       (2523, 5),
       (2503, 6),
       (2508, 7),
       (2509, 7),
       (2517, 7),
       (2520, 7),
       (2528, 7),
       (2522, 8),
       (2530, 8),
       (2502, 9),
       (2506, 9),
       (2525, 9);

INSERT INTO transaction (ID, SALE_DATE, LISTING_DATE, PRODUCT_ID, BUSINESS_ID, AMOUNT)
VALUES (5001, '2020-01-02 15:34:20', '2021-01-21 15:34:20', 5001, 1001, 5.25),
       (5002, '2021-01-22 15:34:20', '2021-01-21 15:34:20', 5001, 1001, 6.60),
       (5003, '2021-02-21 15:34:20', '2021-02-20 15:34:20', 5001, 1001, 15.50),
       (5004, '2021-02-11 15:34:20', '2021-02-20 15:34:20', 5003, 1001, 11.50),
       (5005, '2021-02-21 15:34:20', '2021-02-20 15:34:20', 5003, 1001, 2.00),
       (5006, '2021-02-22 15:34:20', '2021-02-21 15:34:20', 5003, 1001, 24.00),
       (5007, '2021-02-22 15:34:20', '2021-02-21 15:34:20', 5003, 1001, 10.50),
       (5008, '2021-03-20 15:34:20', '2021-03-19 15:34:20', 5003, 1001, 13.25),
       (5009, '2021-03-21 15:34:20', '2021-03-20 15:34:20', 5005, 1001, 6.00),
       (5010, '2021-03-20 15:34:20', '2021-03-19 15:34:20', 5005, 1001, 7.00);