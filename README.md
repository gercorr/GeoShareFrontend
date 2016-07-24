# GeoShareFrontend

###Features: Android, Google Maps API, Rest API calls

This is the frontend to the android GeoShare app. It calls two rest services hosted by the backend:
#####/rest/addNote?text={text}&lat={latitude}&long={longitude}
#####/rest/getAllNotes

###Instructions:
Open as a gradle application in Android Studio

Add a config.properties file to app/res/raw

###config.properties
```xml
#this should point at wherever your GeoShareBackend is running
rest_url=http://localhost:8080/

#use this instead of localhost if using android emulator
#rest_url=http://10.0.2.2:8080/

#use your local pc ip if debugging on your phone with local backend
#rest_url=http://192.X.X.X:8080/

#rest_key is not in use yet
rest_key=123456
