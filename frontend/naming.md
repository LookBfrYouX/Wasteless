## The Great Frontend Renaming of 2021

Rio the Great Renamer, in partnership with Niko the Great Re-modularizer has swooped in and renamed and moved basically everything on the frontend in the name of knights of the consistently round table.

### Separation of internal and external names

When we look for the Vue files, we should able to quickly find what we are looking for. This means:

- Consistency in naming
- Subfolders
- Sortability

This, however, does not fit well with names we want to show the user in the page title, buttons etc..: the names used internally and externally can be different.
### Hierarchy
#### Views and Components

- `views`: for pages a user visits
- `components`: for files used within one or more views (e.g. Pagination)

Views may be in subfolders, called **subsections**:

Components should be in subfolders (kebab-case) as you see fit. However, **the component should start with the name of the subfolder** as when the component is imported, you only the see the name of the file.

For example, the `cards` folder contains card components that will be displayed in lists.

#### Component Sections

For big chunks of the app. Currently there are two:

- `business`: business-related views
- `marketplace`: marketplace-related views

Use kebab-case for component names.

#### Component Naming

The name should be in the EntityVerb format (in PascalCase):

- Entity: name of entity the page is viewing/modifying (e.g. `User`, `Listing`)
- Verb: one of:
  - `Create`: make a new entity (e.g. `UserCreate`). Do NOT use `Register`, `Entry` or `Add`
  - `Detail`: view a single entity (e.g. `UserDetail`)
  - `Edit`: edit entity (e.g. `ProductImagesEdit`)
  - No verb, but plural: view a list of entities (e.g. `Listings`, `Products`)

Having the entity name first is unnatural but allows for easy scanning as files for a specific entity are sorted together.

### Router

The name of the route should be the component name + file name (in PascalCase) (e.g. business/Detail.vue => BusinessDetail). There may be a suffix such as `admin` if a view has a separate route just for admins.

The path should use **only singular nouns** and make sense from a hierarchy perspective e.g. `BusinessListings.vue` should be `/business/1/listing`

Use lowercase for the whole path and do not use any separators (e.g. `SignIn` is just `signin`).

The router name should be name the of the section + the name of the file. Plus optional modifier (e.g. admin has its own route)

### Exceptions

- `SignIn`: we had no idea what to call this
- Marketplace main page called `Marketplace.vue` even though it is in the marketplace folder
- `MarkplaceCardCreate` has two routes: one for normal user and one for admins (as the admin must be able to do everything). The latter is called `MarketplaceCardCreateAdmin`
- `ProductImagesEdit`: not sure if it should be plural or not
