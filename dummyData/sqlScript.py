import codecs

newFile = codecs.open("result.sql", "x", "utf-8")

# This list needs to be in order
itemsToAdd = ["address", "user", "business", "user_business", "product", "catalogue",
              "inventory_item", "listing", "marketlisting", "keyword", "marketlisting_keyword",
              "transaction"]

# Add items to new newFile
for item in itemsToAdd:
    file = open("database.sql")
    for line in file:
        if line.startswith("INSERT INTO `{}`".format(item)):
            newFile.write(line)

    file.close()


file = open("database.sql")
# Country names are different between the SQL generator and restcountries.eu, so this is a manual map
# made by subtacting restcountries countries from sql output countries.
# Saint Barthélemy: UTF encoding is horrible so just switch to france
newFile.write("""
UPDATE `address` SET COUNTRY = 'Antarctica' WHERE COUNTRY = 'Antarctica (the territory South of 60 deg S)';
UPDATE `address` SET COUNTRY = 'Bolivia (Plurinational State of)' WHERE COUNTRY = 'Bolivia';
UPDATE `address` SET COUNTRY = 'Norway' WHERE COUNTRY = 'Bouvet Island (Bouvetoya)';
UPDATE `address` SET COUNTRY = 'British Indian Ocean Territory' WHERE COUNTRY = 'British Indian Ocean Territory (Chagos Archipelago)';
UPDATE `address` SET COUNTRY = 'Virgin Islands (British)' WHERE COUNTRY = 'British Virgin Islands';
UPDATE `address` SET COUNTRY = 'France' WHERE COUNTRY = 'French Southern Territories';
UPDATE `address` SET COUNTRY = 'Australia' WHERE COUNTRY = 'Heard Island and McDonald Islands';
UPDATE `address` SET COUNTRY = 'Italy' WHERE COUNTRY = 'Holy See (Vatican City State)';
UPDATE `address` SET COUNTRY = 'Iran (Islamic Republic of)' WHERE COUNTRY = 'Iran';
UPDATE `address` SET COUNTRY = 'Korea (Republic of)' WHERE COUNTRY = 'Korea';
UPDATE `address` SET COUNTRY = 'Kyrgyzstan' WHERE COUNTRY = 'Kyrgyz Republic';
UPDATE `address` SET COUNTRY = 'Libya' WHERE COUNTRY = 'Libyan Arab Jamahiriya';
UPDATE `address` SET COUNTRY = 'Macedonia (the former Yugoslav Republic of)' WHERE COUNTRY = 'Macedonia';
UPDATE `address` SET COUNTRY = 'Netherlands' WHERE COUNTRY = 'Netherlands Antilles';
UPDATE `address` SET COUNTRY = 'Palestine, State of' WHERE COUNTRY = 'Palestinian Territory';
UPDATE `address` SET COUNTRY = 'Pitcairn' WHERE COUNTRY = 'Pitcairn Islands';
UPDATE `address` SET COUNTRY = 'France' WHERE COUNTRY = 'Reunion';
UPDATE `address` SET COUNTRY = 'France' WHERE COUNTRY = 'Saint Barthelemy';
UPDATE `address` SET COUNTRY = 'Saint Helena, Ascension and Tristan da Cunha' WHERE COUNTRY = 'Saint Helena';
UPDATE `address` SET COUNTRY = 'Saint Martin (French part)' WHERE COUNTRY = 'Saint Martin';
UPDATE `address` SET COUNTRY = 'Slovakia' WHERE COUNTRY = 'Slovakia (Slovak Republic)';
UPDATE `address` SET COUNTRY = 'Svalbard and Jan Mayen' WHERE COUNTRY = 'Svalbard & Jan Mayen Islands';
UPDATE `address` SET COUNTRY = 'Tanzania, United Republic of' WHERE COUNTRY = 'Tanzania';
UPDATE `address` SET COUNTRY = 'United Kingdom of Great Britain and Northern Ireland' WHERE COUNTRY = 'United Kingdom';
UPDATE `address` SET COUNTRY = 'United States of America' WHERE COUNTRY = 'United States Minor Outlying Islands';
UPDATE `address` SET COUNTRY = 'Virgin Islands (U.S.)' WHERE COUNTRY = 'United States Virgin Islands';
UPDATE `address` SET COUNTRY = 'Venezuela (Bolivarian Republic of)' WHERE COUNTRY = 'Venezuela';
UPDATE `address` SET COUNTRY = 'Viet Nam' WHERE COUNTRY = 'Vietnam';
UPDATE `transaction` SET BUSINESS_ID = 1001;""")
file.close()