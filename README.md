# Navbar Pigeons

Basic project template using `gradle`, `npm`, `Spring Boot`, `Vue.js` and `Gitlab CI`.

## Basic Project Structure

A frontend sub-project (web GUI):

- `frontend/src` Frontend source code (Vue.js)
- `frontend/public` publicly accessible web assets (e.g., icons, images, style sheets)
- `frontend/dist` Frontend production build

A backend sub-project (business logic and persistence server):

- `backend/src` Backend source code (Java - Spring)
- `backend/out` Backend production build

## How to run

### Frontend / GUI

    $ cd frontend
    $ npm install
    $ npm run serve

Running on: http://localhost:9500/ by default

### Backend / server

    cd backend
    ./gradlew bootRun

Running on: http://localhost:9499/ by default

## Todo (S2)

- Decide on a LICENSE

## Contributors

- Alec Fox (amf133)
- Fletcher Dick (fdi19)
- Haruka Ichinose (hic21)
- Jordan Pyott (jpy19)
- Maximilian Birzer (mbi47)
- Niko Tainui (nbt25)
- Rio Ogino (rog19)
- Dawson Berry (dnb36)

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Vue docs](https://vuejs.org/v2/guide/)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=10577&section=11)
