import codecs

newFile = codecs.open("result.sql", "x", "utf-8")

# This list needs to be in order
itemsToAdd = ["address", "user", "business", "user_business", "product",
              "catalogue", "inventory_item", "listing", "marketlisting"]

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
UPDATE ADDRESS SET COUNTRY = 'Antarctica' WHERE COUNTRY = 'Antarctica (the territory South of 60 deg S)';
UPDATE ADDRESS SET COUNTRY = 'Bolivia (Plurinational State of)' WHERE COUNTRY = 'Bolivia';
UPDATE ADDRESS SET COUNTRY = 'Norway' WHERE COUNTRY = 'Bouvet Island (Bouvetoya)';
UPDATE ADDRESS SET COUNTRY = 'British Indian Ocean Territory' WHERE COUNTRY = 'British Indian Ocean Territory (Chagos Archipelago)';
UPDATE ADDRESS SET COUNTRY = 'Virgin Islands (British)' WHERE COUNTRY = 'British Virgin Islands';
UPDATE ADDRESS SET COUNTRY = 'France' WHERE COUNTRY = 'French Southern Territories';
UPDATE ADDRESS SET COUNTRY = 'Australia' WHERE COUNTRY = 'Heard Island and McDonald Islands';
UPDATE ADDRESS SET COUNTRY = 'Italy' WHERE COUNTRY = 'Holy See (Vatican City State)';
UPDATE ADDRESS SET COUNTRY = 'Iran (Islamic Republic of)' WHERE COUNTRY = 'Iran';
UPDATE ADDRESS SET COUNTRY = 'Korea (Republic of)' WHERE COUNTRY = 'Korea';
UPDATE ADDRESS SET COUNTRY = 'Kyrgyzstan' WHERE COUNTRY = 'Kyrgyz Republic';
UPDATE ADDRESS SET COUNTRY = 'Libya' WHERE COUNTRY = 'Libyan Arab Jamahiriya';
UPDATE ADDRESS SET COUNTRY = 'Macedonia (the former Yugoslav Republic of)' WHERE COUNTRY = 'Macedonia';
UPDATE ADDRESS SET COUNTRY = 'Netherlands' WHERE COUNTRY = 'Netherlands Antilles';
UPDATE ADDRESS SET COUNTRY = 'Palestine, State of' WHERE COUNTRY = 'Palestinian Territory';
UPDATE ADDRESS SET COUNTRY = 'Pitcairn' WHERE COUNTRY = 'Pitcairn Islands';
UPDATE ADDRESS SET COUNTRY = 'France' WHERE COUNTRY = 'Reunion';
UPDATE ADDRESS SET COUNTRY = 'France' WHERE COUNTRY = 'Saint Barthelemy';
UPDATE ADDRESS SET COUNTRY = 'Saint Helena, Ascension and Tristan da Cunha' WHERE COUNTRY = 'Saint Helena';
UPDATE ADDRESS SET COUNTRY = 'Saint Martin (French part)' WHERE COUNTRY = 'Saint Martin';
UPDATE ADDRESS SET COUNTRY = 'Slovakia' WHERE COUNTRY = 'Slovakia (Slovak Republic)';
UPDATE ADDRESS SET COUNTRY = 'Svalbard and Jan Mayen' WHERE COUNTRY = 'Svalbard & Jan Mayen Islands';
UPDATE ADDRESS SET COUNTRY = 'Tanzania, United Republic of' WHERE COUNTRY = 'Tanzania';
UPDATE ADDRESS SET COUNTRY = 'United Kingdom of Great Britain and Northern Ireland' WHERE COUNTRY = 'United Kingdom';
UPDATE ADDRESS SET COUNTRY = 'United States of America' WHERE COUNTRY = 'United States Minor Outlying Islands';
UPDATE ADDRESS SET COUNTRY = 'Virgin Islands (U.S.)' WHERE COUNTRY = 'United States Virgin Islands';
UPDATE ADDRESS SET COUNTRY = 'Venezuela (Bolivarian Republic of)' WHERE COUNTRY = 'Venezuela';
UPDATE ADDRESS SET COUNTRY = 'Viet Nam' WHERE COUNTRY = 'Vietnam';""")
file.close()