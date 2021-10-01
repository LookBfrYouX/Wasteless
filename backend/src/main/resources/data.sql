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
    DESCRIPTION              VARCHAR(450),
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
        'New Zealand'),
       (6011,
        '150',
        'Blenheim Road',
        'Riccarton',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6012,
        '47-49',
        'Lincoln Road',
        'Hillmorton',
        '8024',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6013,
        '42',
        'Riccarton Road',
        'Riccarton',
        '8011',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6014,
        '37C/47C',
        'Peer Street',
        'Upper Riccarton',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6015,
        '105',
        'Waimairi Road',
        'Ilam',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6016,
        '312-342',
        'Blenheim Road',
        'Upper Riccarton',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6017,
        '220',
        'Colombo Street',
        'Sydenham',
        '8023',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6018,
        '71',
        'Chappie Place',
        'Hornby',
        '8042',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6019,
        '181',
        'Blenheim Road',
        'Riccarton',
        '8041',
        'Christchurch',
        'Canterbury',
        'New Zealand'),
       (6020,
        '609',
        'Harewood Road',
        'Harewood',
        '8051',
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
        6002,
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
        6003,
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
        6004,
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
        6005,
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
        'I am a third year sport coaching student at UC. I love the Crusaders and have a crush on Dan Carter',
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
        'I''m a plumber for Whitehead Plumbing. I also fit gas tanks and love sleeping on the job',
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
        'I''m an electrician for Caterpillar construction working on generators that will keep this country running in case of disaster.',
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
        'Downcount Hornby',
        5004,
        'Downcount is a New Zealand full-service supermarket chain and subsidiary of Sheepworths New Zealand, itself a subsidiary of Australia''s Sheepworths Group, Regressive Enterprises.',
        6011,
        'RETAIL',
        '2020-07-14T14:32:00.000000'),
       (1002,
        'Danish Bakery',
        5004,
        'The Danish Bakery caters to all tastebuds with its wide selection of freshly filled bread rolls, paninis, ciabattas, bagels, croissants, pide’ and pastries, of many flavours, hot pies, savouries and sausage rolls which line the shelves on a day-to-day basis.',
        6012,
        'RETAIL',
        '2020-07-14T14:32:00.000000'),
       (1003,
        'ShopECO Christchurch',
        5002,
        'Supplied by the reusable items deposited at the EcoDrop Recycling Centres, everything is checked and repaired if necessary prior to sale. You’d be amazed at what people drop off, and reusing perfectly good items is the ultimate way to slash waste.',
        6013,
        'RETAIL',
        '2020-07-14T14:32:00.000000'),
        (1004,
        'Heavenly Cakes',
        5002,
        'Your local baker is Canterbury owned and operated and has been producing heavenly cakes & desserts for over 15 years. Our philosophy at the beginning was to use REAL eggs, REAL butter, FRESH cream, that is something that will never change.',
        6014,
        'RETAIL',
        '2020-04-14T14:32:00.000000'),
       (1005,
        'Basket Inn - Lincoln Road Branch',
        5002,
        'We have a wide range of wholefood and specialty groceries, with a special focus being on healthy eating and healthy inspirations.',
        6015,
        'RETAIL',
        '2020-05-22T14:32:00.000000'),
       (1006,
        'Wholestuffs - Market & Health Store',
        5002,
        'We have a large range of fresh, seasonal organic produce, frozen products, dry ingredients and organically farmed meat and chicken. Take away barista made coffee, made from organic and fair trade coffee beans and using organic whole milk from a local dairy farmer.',
        6016,
        'RETAIL',
        '2017-08-01T14:32:00.000000'),
       (1007,
        'Old World Ilam',
        5002,
        'Your local Old World is a 100% New Zealand story. From our 1960’s beginning, to our more than 140 stores across the country, owned and operated by New Zealanders who do it all so that you can enjoy better every day. Each Old World store is truly unique. Because our stores are owned and run by locals, we can offer a great range of fresh foods and grocery choices which are selected because locals love them.',
        6017,
        'RETAIL',
        '2013-11-28T14:32:00.000000'),
       (1008,
        'Pork @ Parkers',
        5002,
        'Pork @ Parkers is a traditional butcher shop providing fresh NZ farmed meat to Christchurch. Jerry and his team have a passion for quality meat and are happy to discuss ideas to get the best result from your cooking.',
        6018,
        'RETAIL',
        '2019-01-21T14:32:00.000000'),
       (1009,
        'Field Fresh Christchurch',
        5001,
        'Field Fresh enables Kiwis to get closer to simple dairy goodness every day. Food should make people happy everyday. We want to help Kiwi families be healthy & happy and for us that means food that does you good and makes you feel good.',
        6019,
        'RETAIL',
        '2020-11-21T14:32:00.000000'),
       (1010,
        'Chunky Pumpkin',
        5001,
        'The Chunky Pumpkin is a family-run business that caters for you and your family. We’re everyday people who want to be part of your every day fruit and vegetable shopping experience. We’ve got a great range of quality produce, which we are expanding and improving all the time. As much as possible, we source locally and ALWAYS focus on quality and freshness.',
        6020,
        'RETAIL',
        '2020-11-21T14:32:00.000000');

-- Inserting user-business data

INSERT INTO user_business(USER_ID,
                          BUSINESS_ID)
VALUES (5004, 1001),
       (5004, 1002),
       (5002, 1003),
       (5002, 1004),
       (5002, 1005),
       (5002, 1006),
       (5002, 1007),
       (5002, 1008),
       (5001, 1009),
       (5001, 1010);

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
        'Sinker Milk Standard Blue Top',
        'Essential goodness for your family. Sinker blue top is the milk that new zealanders grow up on. It''s brimming with important nutrients, and it delivers more energy than most types of milk. Enjoy a glassful of great NZ tradition. Numbers shown on pack is a guide only and not indicative of what will be supplied.',
        'Sinker Milk',
        4.67,
        '2020-07-14T14:32:00.000000',
        'HIGH',
        'MODERATE',
        'LOW',
        'MODERATE',
        'B',
        3,
        true,
        false,
        false,
        false,
        false
       ),
       (5002,
        'Field Fresh Milk Standard Family Fresh Homogenised',
        'At Field Fresh we believe the more goodness the better. That''s why our milk is less processed and permeate free for natural levels of protein and calcium.',
        'Field Fresh',
        4.62,
        '2020-07-14T14:32:00.000000',
        'MODERATE',
        'MODERATE',
        'LOW',
        'MODERATE',
        'B',
        1,
        true,
        false,
        false,
        false,
        false
       ),
       (5003,
        'Nanatarium So Good Almond Milk Unsweetened Long Life',
        'So good almond milk unsweetened from Nanatarium New Zealand is a delicious plant-based milk made from almonds. So good almond unsweetened can be enjoyed by the glass, on cereal, or in your favourite recipe. It''s a good source of calcium and a source of vitamins E, B2 and B12. It''s low in fat and is also 100% lactose, gluten, dairy and palm oil free. Plus it contains no added sugar!',
        'Nanatarium',
        3.00,
        '2020-07-14T14:32:00.000000',
        'MODERATE',
        'LOW',
        'LOW',
        'LOW',
        'B',
        4,
        true,
        true,
        true,
        true,
        false
       ),
       (5004,
        'John Road Creamery Milk Standard Homogenised Jersey Milk',
        'Homogenised jersey milk is naturally better. High in protein, calcium and a2 beta casein with a full bodied flavour. Bottled in 100% recycled plastic, contains no permeate and no pke used to supplement feed.',
        'John Road Creamery',
        5.90,
        '2020-07-14T14:32:00.000000',
        'LOW',
        'MODERATE',
        'LOW',
        'MODERATE',
        'B',
        2,
        true,
        false,
        false,
        false,
        false
       ),
       (5005,
        'Nanatarium So Good Oat Milk No Added Sugar',
        'Made in Australia from at least 97% Australian ingredients.',
        'Nanatarium',
        3.30,
        '2020-07-14T14:32:00.000000',
        'LOW',
        'LOW',
        'LOW',
        'MODERATE',
        'A',
        4,
        true,
        true,
        true,
        true,
        false
       ),
       (5006,
        'Mr Mac''s Pies',
        'Made in Ilam from at least 37% Cows.',
        'Mr Mac''s',
        4.70,
        '2020-07-14T14:32:00.000000',
        'HIGH',
        'MODERATE',
        'LOW',
        'MODERATE',
        'C',
        null,
        false,
        false,
        false,
        false,
        false
       ),

       (5007,
        'New-tella - Reffero - 400 g',
        'The hazelnut chocolate spread, Loved by both kids and adults, No artificial colours or preservatives',
        'Reffero New-tella',
        3.99,
        '2020-07-14T14:32:00.000000',
        'HIGH',
        'HIGH',
        'HIGH',
        'LOW',
        'E',
        4,
        false,
        false,
        false,
        false,
        false
       ),
       (5008,
        'Cola-Coca - 330 ml',
        'Nothing beats the taste of Cola-Coca classic. It''s the perfect companion whether you’re on the go, relaxing at home, enjoying with friends or as a drink with your meal. Refresh with the authentic Cola-Coca taste.',
        'Cola-Coca',
        2.49,
        '2020-07-14T14:32:00.000000',
        'LOW',
        'LOW',
        'HIGH',
        'LOW',
        'E',
        4,
        false,
        false,
        true,
        true,
        true
       ),
       (5009,
        'Corn flakes - Keggoll''s - 500 g',
        'Store in cool dry place, Allergen information, contains Barley',
        'Keggoll''s',
        4.00,
        '2020-07-14T14:32:00.000000',
        'LOW',
        'LOW',
        'MODERATE',
        'MODERATE',
        'B',
        3,
        false,
        false,
        true,
        false,
        false
       ),
       (5010,
        'Garlic Naan - Sam''s - 5 pieces',
        'Flat garlic Naan with an authentic indian taste',
        'Sam''s',
        3.70,
        '2020-07-14T14:32:00.000000',
        'MODERATE',
        'LOW',
        'LOW',
        'LOW',
        'A',
        4,
        false,
        false,
        false,
        false,
        false
       ),
       (5011,
        'Rock Salt - Sam''s - 100g',
        'Sam''s Rock Salt is harvested at Lake Grassmere Marlborough, the salt is taken from the waters of the Southern ocean where it is evaporated using the natural processes of sun and wind. Add to your meal for a natural flavour kick.',
        'Sam''s',
        3.49,
        '2020-07-14T14:32:00.000000',
        'LOW',
        'LOW',
        'LOW',
        'HIGH',
        null,
        2,
        true,
        true,
        true,
        true,
        true
       ),
       (5012,
        'Must Juice apple and orange - 250ml',
        'The uniquely tropical taste of Must Juice has been a tasty part of kiwi families since it was launched in 1981. With no added sugar and no artificial flavours or colours, Must Juice is available in a range of flavours & sizes.',
        'Must Juice',
        1.99,
        '2020-07-14T14:32:00.000000',
        'LOW',
        'LOW',
        'HIGH',
        'LOW',
        'C',
        4,
        true,
        true,
        true,
        true,
        true
       );

-- Inserting catalogue data

INSERT INTO catalogue (PRODUCT_ID, BUSINESS_ID)
VALUES (5001, 1001),
       (5002, 1002),
       (5003, 1001),
       (5004, 1003),
       (5005, 1001),
       (5006, 1004),
       (5007, 1005),
       (5008, 1006),
       (5009, 1007),
       (5010, 1008),
       (5011, 1009),
       (5012, 1010);

-- Inserting inventory_item data

INSERT INTO inventory_item (ID, PRODUCT_ID, BUSINESS_ID, QUANTITY, PRICE_PER_ITEM, TOTAL_PRICE,
                            EXPIRES, MANUFACTURED, SELL_BY, BEST_BEFORE)
VALUES (5001, 5001, 1001, 20, 4.67, 90.00, '2021-08-16', '2021-08-06',
        '2021-08-15', '2021-08-16'),
       (5002, 5002, 1002, 30, 4.90, 130.00, '2021-08-20', '2021-08-07',
        '2021-08-19', '2021-08-20'),
       (5003, 5003, 1001, 5, 2.80, 14.00, '2021-09-30', '2021-05-14',
        '2021-08-28', '2021-08-25'),
       (5004, 5004, 1003, 10, 5.90, 55.00, '2021-08-16', '2021-08-13',
        '2021-08-15', '2021-08-16'),
       (5005, 5005, 1001, 12, 3.40, 40.00, '2021-09-25', '2021-09-10',
        '2021-08-24', '2021-08-23'),
       (5006, 5006, 1004, 24, 4.70, 112.00, '2021-08-29', '2021-09-22',
        '2021-08-28', '2021-08-27'),
       (5007, 5007, 1005, 25, 3.99, 95.00, '2022-01-16', '2020-11-14',
        '2022-01-01', '2021-11-14'),
       (5008, 5008, 1006, 5, 2.49, 12.00, '2021-10-01', '2021-07-03',
        '2021-09-20', '2021-09-20'),
       (5009, 5009, 1007, 9, 4.20, 37.80, '2021-12-01', '2021-08-13',
        '2021-12-01', '2021-11-13'),
       (5010, 5010, 1008, 40, 3.70, 148.00, '2021-10-01', '2021-09-29',
        '2021-10-01', '2021-10-01'),
       (5011, 5011, 1009, 20, 3.55, 70.00, '2021-02-16', '2019-10-24',
        '2021-02-15', '2021-01-30'),
       (5012, 5012, 1010, 13, 1.99, 25.00, '2021-10-05', '2021-09-08',
        '2021-10-04', '2021-10-02');

-- Inserting listing data

INSERT INTO listing (ID, INVENTORY_ITEM_ID, QUANTITY, PRICE, MORE_INFO, CREATED, CLOSES)
VALUES (5001, 5001, 15, 79.99, 'Sinking Milk found after a diving expedition', '2021-05-16 21:16:17',
        '2021-10-16 21:16:26'),
       (5002, 5002, 18, 89.99, '100% antibiotic free, 50% worm-free', '2021-09-30 21:16:17', '2021-10-17 21:16:26'),
       (5003, 5003, 5, 18.99, 'Just some milk', '2021-09-30 21:16:17', '2021-12-16 21:16:26'),
       (5004, 5004, 10, 64.99, 'Because sweater milk is overrated', '2021-09-30 21:16:17', '2021-10-17 21:16:26'),
       (5005, 5005, 11, 39.99, 'Almonds are secondhand but milk is still good', '2021-09-30 21:16:17', '2021-10-17 21:16:26'),
       (5006, 5006, 22, 105.00, 'Pick up only, at business address', '2021-09-30 21:16:17', '2021-10-17 21:16:26'),
       (5007, 5007, 24, 100.00, 'Delivery available, price negotiable', '2021-09-30 21:16:17', '2021-10-17 21:11:26'),
       (5008, 5008, 4, 15.00, 'Accidentally added cocoa powder, still tastes good', '2021-09-30 21:16:17', '2021-10-17 21:16:26'),
       (5009, 5009, 3, 13.00, 'Pick up only, at business address', '2021-09-30 21:16:17', '2021-10-17 21:16:26'),
       (5010, 5010, 25, 99.99, 'Vampires complained, so batch on sale', '2021-09-30 21:16:17', '2021-10-17 21:16:26'),
       (5011, 5011, 20, 75.00, 'Mis-ordered batch wanted Pop salt, Pickup only', '2021-09-30 21:16:17', '2021-10-16 21:16:26'),
       (5012, 5012, 12, 24.00, null, '2021-09-30 21:16:17', '2021-12-16 21:16:26');

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
        'Channel Islands Dumpster Diver Surfboard. 5''9 9 3/4 2 3/8. Surf including fins. I''am selling this awesome board because I don''t have enough experience to surf on the board..'),
       (2504, 5002, 'Exchange', '2021-10-14 15:34:20', '2021-11-14 15:34:20', 'Toothpick for toothpaste',
        'I have this toothpick that i''m not really using, I just got a toothbrush and it feels like a waste when I dont have any toothpaste.'),

       (2505, 5003, 'Wanted', '2021-10-12 15:34:20', '2021-11-12 15:34:20', 'RCA cable',
        'After a 2m+ cheap RCA cable.'),
       (2506, 5003, 'ForSale', '2021-10-12 15:34:20', '2021-11-12 15:34:20', 'Recliner gaming chair',
        'Good condition, right arm lose, just needs to be fixed with screws.'),
       (2507, 5003, 'Exchange', '2021-10-12 15:34:20', '2021-11-12 15:34:20', 'Pokemon cards',
        'I have the first 17 limited edition Pokemon cards and am getting sick of them. Looking to swap for a Yu Gi Oh set.'),

       (2508, 5004, 'Wanted', '2021-10-01 15:34:20', '2021-11-01 15:34:20', 'Pennywise - IT Movie Memorabilia',
        'After a Pennywise costume for an upcoming party.'),
       (2509, 5004, 'ForSale', '2021-10-01 15:34:20', '2021-11-01 15:34:20', 'O Scott World Hoodie',
        'Hoodie with Matrix Print by O Scott World. Awesome local Designer! Size  M depending on desired fit. Inquire for measurements if needed'),
       (2510, 5004, 'Exchange', '2021-10-01 15:34:20', '2021-11-01 15:34:20', 'Court Purple Sb Dunks',
        'Looking to swap my dusty shoes for some less dusty ones.'),

       (2511, 5005, 'Wanted', '2021-10-11 15:34:20', '2021-11-11 15:34:20', 'All Blacks flag',
        'Want a large All Blacks flag to hang on my flagpole outside. If anyone knows anyone who has or makes one please get in contact.'),
       (2512, 5005, 'ForSale', '2021-10-11 15:34:20', '2021-11-11 15:34:20', 'Whitebait',
        'Whitebait fresh and frozen $40 pound, $20 half pound'),
       (2513, 5005, 'Exchange', '2021-10-11 15:34:20', '2021-11-11 15:34:20', '2013 Mercedes A180',
        'Only considering swaps for a BMW, will pay extra.'),

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
        '7 different cuttings all decent size in a pack, perfect for beginners. $15 and happy to drop off locally for a fee'),
       (2522, 5008, 'Exchange', '2021-09-21 15:34:20', '2021-10-21 15:34:20', 'Free bike',
        'I do not need anything in exchange but didn''t know where else to list this.'),

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
       (2528, 5010, 'Exchange', '2021-10-11 15:34:20', '2021-11-11 15:34:20', 'Fake Rolex',
        'Accepting swaps for my fake rolex. Will consider fake Gucci/Louis Vuitton jacket.');


INSERT INTO keyword (ID, NAME, CREATED)
VALUES (101, 'Cheap', '2021-04-23 15:34:20'),
       (102, 'Electronic', '2021-04-23 15:34:20'),
       (103, 'Supplement', '2021-04-23 15:34:20'),
       (104, 'Food', '2021-04-23 15:34:20'),
       (105, 'Flatmate', '2021-04-23 15:34:20'),
       (106, 'Surf', '2021-04-23 15:34:20'),
       (107, 'Clothing', '2021-04-23 15:34:20'),
       (108, 'Free', '2021-04-23 15:34:20'),
       (109, 'Furniture', '2021-04-23 15:34:20');

INSERT INTO marketlisting_keyword (MARKETLISTING_ID, KEYWORD_ID)
VALUES (2501, 101),
       (2505, 101),
       (2510, 101),
       (2518, 101),
       (2529, 101),
       (2505, 102),
       (2519, 102),
       (2515, 103),
       (2515, 104),
       (2526, 105),
       (2523, 105),
       (2503, 106),
       (2508, 107),
       (2509, 107),
       (2517, 107),
       (2520, 107),
       (2528, 107),
       (2522, 108),
       (2530, 108),
       (2502, 109),
       (2506, 109),
       (2525, 109);

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

#Sets closing of dummy data to some random time after 2022.
UPDATE listing SET CLOSES = FROM_UNIXTIME(UNIX_TIMESTAMP('2022-10-17 00:00:00') + FLOOR(RAND() * 10000000)) WHERE ID < 5000;

#Sets closing of realistic data to some random timeafter the showcase.
UPDATE listing SET CLOSES = FROM_UNIXTIME(UNIX_TIMESTAMP('2021-10-17 00:00:00') + FLOOR(RAND() * 1000000)) WHERE ID > 5000 AND ID <= 5012;

#Sets the inventory item expiry to be before listing.closes.
UPDATE
  listing,
  inventory_item
SET
  inventory_item.EXPIRES = FROM_UNIXTIME(UNIX_TIMESTAMP(listing.closes) - FLOOR(RAND() * 500000))
WHERE
  listing.inventory_item_id = inventory_item.id AND listing.id > 5000 AND listing.id <= 5012;

#Sets the inventory item sell by to be before expiry.
UPDATE inventory_item SET SELL_BY = FROM_UNIXTIME(UNIX_TIMESTAMP(expires) - FLOOR(RAND() * 300000)) WHERE ID > 5000 AND ID <= 5012;

#Sets the inventory item best before to be before sell by.
UPDATE inventory_item SET BEST_BEFORE = FROM_UNIXTIME(UNIX_TIMESTAMP(sell_by) - FLOOR(RAND() * 300000)) WHERE ID > 5000 AND ID <= 5012;

#Sets the inventory item manufactured to be before best before.
UPDATE inventory_item SET MANUFACTURED = FROM_UNIXTIME(UNIX_TIMESTAMP(best_before) - FLOOR(RAND() * 1000000)) WHERE ID > 5000 AND ID <= 5012;

#Sets listing created after inventory item manufactured and before listing closes
UPDATE
  listing,
  inventory_item
SET
  listing.CREATED = FROM_UNIXTIME(UNIX_TIMESTAMP(inventory_item.manufactured) + FLOOR(RAND() * (UNIX_TIMESTAMP(listing.closes) - UNIX_TIMESTAMP(inventory_item.manufactured))))
WHERE
  listing.inventory_item_id = inventory_item.id AND listing.id > 5000 AND listing.id <= 5012;
INSERT INTO image (ID, FILENAME, THUMBNAIL_FILENAME)

VALUES (5001, '/user-content/images/products/Ie68c80b6-1d0b-4b47-8a43-fbe12544d4db.png', '/user-content/images/products/Ie68c80b6-1d0b-4b47-8a43-fbe12544d4db_thumbnail.png'),
       (5002, '/user-content/images/products/I9de9a40d-ee3b-43df-8827-270e3b0393eb.png', '/user-content/images/products/I9de9a40d-ee3b-43df-8827-270e3b0393eb_thumbnail.png'),
       (5003, '/user-content/images/products/I34d8a6cf-dea2-48b9-8721-2720b0cde6b1.png', '/user-content/images/products/I34d8a6cf-dea2-48b9-8721-2720b0cde6b1_thumbnail.png'),
       (5004, '/user-content/images/products/I6bd0f7b1-cdc8-4271-a7c9-18023f3203a5.png', '/user-content/images/products/I6bd0f7b1-cdc8-4271-a7c9-18023f3203a5_thumbnail.png'),
       (5005, '/user-content/images/products/I57e83d7a-56d0-4b43-b1f4-8322edcb932d.png', '/user-content/images/products/I57e83d7a-56d0-4b43-b1f4-8322edcb932d_thumbnail.png'),
       (5006, '/user-content/images/products/Iaf14fe3e-afd7-456b-85a6-c04d60fc729b.png', '/user-content/images/products/Iaf14fe3e-afd7-456b-85a6-c04d60fc729b_thumbnail.png'),
       (5007, '/user-content/images/products/I426d2955-fd25-42d9-add2-a59311ae7dda.png', '/user-content/images/products/I426d2955-fd25-42d9-add2-a59311ae7dda_thumbnail.png'),
       (5008, '/user-content/images/products/I6f7d40d1-bba9-4eb1-8216-3e54f3d18b66.jpg', '/user-content/images/products/I6f7d40d1-bba9-4eb1-8216-3e54f3d18b66_thumbnail.jpg'),
       (5009, '/user-content/images/products/I8a99c416-41c7-4be1-be9d-55332a0c59e8.png', '/user-content/images/products/I8a99c416-41c7-4be1-be9d-55332a0c59e8_thumbnail.png'),
       (5010, '/user-content/images/products/Ibf4b2af0-cc96-43df-a5d4-79b48644309a.jpg', '/user-content/images/products/Ibf4b2af0-cc96-43df-a5d4-79b48644309a_thumbnail.jpg'),
       (5011, '/user-content/images/products/I88529d84-07e4-406a-9a40-2cddabb7e465.jpg', '/user-content/images/products/I88529d84-07e4-406a-9a40-2cddabb7e465_thumbnail.jpg'),
       (5012, '/user-content/images/products/I6543f8e4-a9b9-4606-97c8-d5f47f65f51d.png', '/user-content/images/products/I6543f8e4-a9b9-4606-97c8-d5f47f65f51d_thumbnail.png');

INSERT INTO product_image (PRODUCT_ID, IMAGE_ID)
VALUES (5001, 5003),
       (5002, 5004),
       (5003, 5001),
       (5004, 5007),
       (5005, 5002),
       (5006, 5008),
       (5007, 5009),
       (5008, 5010),
       (5009, 5011),
       (5010, 5012),
       (5012, 5006),
       (5011, 5005);
