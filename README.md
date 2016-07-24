# GeoShareFrontend

###Features: Android, Google Maps API, Rest API calls

This is the frontend to the android GeoShare app. It calls two rest services hosted by the backend:
#####/rest/addNote?text={text}&lat={latitude}&long={longitude}
#####/rest/getAllNotes

###Instructions:
Open as a gradle application in Android Studio

Add a config.properties file to app/res/raw

###config.properties (rest_url should point at wherever your GeoShareBackend is running. rest_key is not in use yet):
```xml
rest_url=http://localhost:8081/
rest_key=123456
