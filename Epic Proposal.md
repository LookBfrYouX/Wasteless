# Team 300 - Epic Proposal

## Epic Summary

## Proposal description

### Introduction

Our goal is to provide a platform for people with special dietary needs. We believe that this can be
achieved by providing our customers with more detailed information about the food they are
purchasing.

Our vision includes using data from a trusted external source to be integrated with our
current system. This will provide our users with nutrient information on the food they are
purchasing, the ability to search for and filter by their dietary requirements, allow businesses and
individuals to upload products by scanning barcodes and much more!

### Initial plan

This epic will be composed of various stories and features around the use of the [Open Food Facts API](https://world.openfoodfacts.org/). This
will provide us with information such as nutrient facts, ratings, images and ingredients that we can
use to implement our desired features.

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

As an administrator of a business, I can add a product with nutrition facts via an appropriate form,
so that the nutrition information are visible to potential customers on sales listings.

AC1: As a business admin, I can enter a barcode number in a form field to get nutrition facts from
a food API.

AC2: Input field of nutrition facts are auto completed if the product exists in the food API.
The filled values can be overridden.

AC3: If a product has or not have factors related to the three criteria below, they have to be
declared.

- Food allergy
- Special dietary requirement
- Religious reasons

For example, a gluten-free cheese berger may have declaration of "Gluten-free", "Diary",
"Non-vegan", "Beef", "Honey-free". If there is no declaration, "Unknown" is set as default.

AC4: All the mandatory field (see U15) should be filled. Otherwise, the form is prevented
from submitting with appropriate message.

AC5: As a logged in user, I can view all of the nutrition facts of a product listed on sale.
For simplicity, full details of product is not visible on a sales listing view.
Pop-up on clicking the sales listing card or any suitable interaction will reveal the detail.

*Note: Future stories might include upvote and downvote on products. For example, vegan users could
'upvote' a product as 'vegan' if they believe it is vegan or vice-versa if it is not.*