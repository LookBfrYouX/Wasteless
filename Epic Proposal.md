# Team 300 - Epic Proposal

## Epic Summary

## Proposal description

### Introduction

Our goal is to provide a platform for people with special dietary needs. We believe that this can be
achieved by providing our customers with more detailed information about the food they are
purchasing.

Our vision includes using data from a trusted external source to be integrated with our current
system. This will provide our users with nutrient information on the food they are purchasing, the
ability to search for and filter by their dietary requirements, allow businesses and individuals to
upload products by scanning barcodes and much more!

### Initial plan

This epic will be composed of various stories and features around the use of
the [Open Food Facts API](https://world.openfoodfacts.org/). This will provide us with information
such as nutrient facts, ratings, images and ingredients that we can use to implement our desired
features.

We believe this epic will be able to be expanded on over the coming sprints, and we already have
some ideas in mind. These stories could include features such as searching/filtering by diet,
barcode scanning for uploading/editing and encouraging the use of this feature through a reward
system.

## Value Added

### Introduction

The number of people on specific diets is on the rise. Whether the goal in mind is to lose weight or
to live a healthier life, it is clear that the number of diets is increasing. From 2017 to 2018,
there has been an increase of 22 percent of people on specific diets, from 14% to
36% ([and more recently 43](https://foodinsight.org/wp-content/uploads/2020/06/2020-Food-and-Health-Survey-.pdf))
according to
[Food & Health Survey](https://foodinsight.org/one-third-of-americans-are-dieting-including-one-in-10-who-fast-while-consumers-also-hunger-for-organic-natural-and-sustainable/)
.

There is a clear need for a diet recognising sales platform that the Epic can provide. This will
ultimately **differentiate our application from our competitors and create an advantage**.
Specifically, as there is a gap in the market for a health focussed wholesaler and by focussing on a
more niche market segment. Additionally, the Epic can provide features that make product uploading
easier for Business Owners.

### Value added to Business Owners

By implementing an external API with information on products it decreases the amount of data that
the Business Owner needs to manually input into the application. By simply inserting the products
barcode an external API can return information such as:

- Product Title (e.g. "Marmite - Sanitarium - 250 g")
- Images of the product
    - Saves the Owner time by not needing to upload their own
- Nutritional, ingredient & diet information that can be displayed on the product listing page
    - Additionally, this can increase the likelihood of a sale as the products can cater to the
      user's diet

### Value added to Users

As mentioned in the introduction, the amount of users with specific dietary requirements is
constantly increasing. This Epic would add value to the application by implementing features that
are more important for the user than ever before.

By implementing an external API with information on products we can use vitamin, mineral and
ingredient information to help filter down the search results to what the user would like to
purchase. This also includes filtering by diets like vegan, Paleo or Keto. Users can also avoid
specific ingredients such as nuts or certain preservatives. Value could also be added in future
stories such as implementing a calorie counter into the application.

## Stories Draft

### EU1 Products with nutrition facts

As an administrator of a business, when I add a product, I can add nutrition and allergy information
from a Food API by manually entering the barcode number (EAN-13) number, so that the nutrition
information is visible to potential customers on the sales listings.

AC1: As a business admin, I have the option when adding a product to the catalogue to enter a
barcode number ([EAN-13](https://en.wikipedia.org/wiki/International_Article_Number)) to add
nutritional information from a Food API.

AC2: Text fields for adding a product are automatically filled with any existing data from the Food
API. The filled-in values can be overridden by the business administer.

AC3: All of the mandatory fields (see U15) must be filled-in before continuing. Otherwise, the user
is prevented from submitting the form.

AC4: As a logged-in user, I can view the nutritional facts of a product listed for sale on the
listing's details page.

AC5: The nutritional information is collapsable on the listing's details page, so that I can save
screen real estate if I am not interested in the information.

AC6: The user can choose not to use the Barcode input field and instead enter data as previously
possible.

AC7: Products can have a 'Nutrient Score' from A (being good) to E (being Bad nutritional quality).
This should reflect the ['Nutri-Score' rating system](https://en.wikipedia.org/wiki/Nutri-Score).

AC7: Products can have a 'NOVA classification' from 1 (unprocessed) to 4 (ultra-processed). This
should reflect the [NOVA groups for food processing](https://world.openfoodfacts.org/nova).

AC8: Products can have simple quantity measurements (low, moderate and high) for the amounts of Fat,
Saturated Fat, Sugars and Salt in the product.

### EU2 Search sale listings by nutrition facts

As a logged-in individual user, I want to search for sale listings based on the nutrition facts of
the product. E.g. Only return results that are Gluten Free.

AC1: I can select one or more diets (e.g. GF, Vegetarian, etc.) to filter all listings by. Only the
listings that match the selected diets appear.

AC2: I can filter listings by specifying a min and
max [Nutri-Score](https://en.wikipedia.org/wiki/Nutri-Score), being from A to E. By default the
filter values are set to include all ratings and products without ratings.

AC3: I can filter listings by specifying a min and
max [NOVA group](https://world.openfoodfacts.org/nova), being from group 1 to group 4. By default,
the filter values are set to include all groups and products without a NOVA specification.

AC4: I can select quantity measurements for the amounts of Fat, Saturated Fat, Sugars and Salt in
the product to filter by. For each nutrient I can choose from Low, Moderate, High and All.

AC5: The default filter settings are to search for **all** listings. A user should not be required
to enter any nutritional filtering information to search.

### EU3 Barcode scanning

As an administrator of a business, I can add a product by either scanning a barcode from my device
or by inputting a barcode number (EAN-13) manually. This is so that I can quickly add products and
their nutritional information

AC1: Business Administrators can scan EAN-13 barcodes (either by uploading an image or from a live
camera).

AC2: The EAN-13 number can be used by the Food API to add products (from EU1).

AC3: The Business Administrator can also manually type in the EAN-13.

AC4: On a successful scan, the EAN-13 number is applied to the barcode text field.

AC5: After a successful scan I can manually override the EAN-13 text field.

AC6: The cameras image should not display on page load.There is a button to activate the device's
camera.

## API changes

### EU1 Products with nutrition facts

*/businesses/{id}/products, /businesses/{id}/inventory, /businesses/{id}/listings*:  
This endpoint may include these additions:

- Dietary Requirements: Gluten free, Dairy free, Vegan, Vegetarian
- [Nutri-score](https://en.wikipedia.org/wiki/Nutri-Score): From A (good) to E (bad)
- [NOVA group](https://world.openfoodfacts.org/nova): From group 1 to group 4
- Nutrition facts: Energy, Fat (Saturated fat), Carbohydrates (Sugars), Salt (Sodium), Proteins

### EU2 Search sale listings by nutrition facts

*listings/search*:  
This endpoint may include these additions:

- Filter for Dietary Requirements
- Filter for Nutri-score
- Filter for NOVA group
- Sort by nutrition facts
