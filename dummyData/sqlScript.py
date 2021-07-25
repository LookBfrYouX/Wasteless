newFile = open("result.sql", "x")

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