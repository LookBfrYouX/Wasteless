@echo off
setlocal
  set DB_URL=jdbc:mariadb://localhost:3360/seng302
  set DB_URL_TEST=jdbc:mariadb://localhost:3360/seng302-test
  set DB_USERNAME=seng302
  set DB_PASSWORD=navbara
  cd .\backend
  .\gradlew.bat bootrun
endlocal
