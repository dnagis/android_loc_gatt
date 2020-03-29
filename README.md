# LocGatt 

### Objectif
Gatt WriteChar vers esp32 gatts triggered par locationUpdate()

### Design
Activity: onCreate() -> startService()
	Service: onStartCommand()
				--> requestLocationUpdates()
				--> startForeground()
				--> connectmGatt()
	onLocationChanged() -> readCharacteristic()

	
### ToDo

Feedback UI (Activity) --> système Binder / Messenger: décrire +++

Déterminer comment tester stabilité reproduisant une session (field data collection, kite, ...)

---> esp32 sur batterie externe fiable pas lifepo4


### Doc

-Binding
https://developer.android.com/guide/components/bound-services
https://stackoverflow.com/questions/8341667/bind-unbind-service-example-android
https://www.cs.dartmouth.edu/~campbell/cs65/lecture19/lecture19.html
development/samples/browseable/BluetoothLeGatt/src/com.example.android.bluetoothlegatt

-Service to Activity
https://stackoverflow.com/questions/2463175/how-to-have-android-service-communicate-with-activity
https://www.c-sharpcorner.com/article/bound-service-using-messenger-in-android-part-3/
le soir:
https://medium.com/mindorks/mastering-android-handler-chapter-2-fb67d94f6327

samples/browseable/messagingservice




### Ergonomie: EveryDay
# build & install

make LocGatt
adb uninstall vvnx.locgatt
adb install out/target/product/mido/system/app/LocGatt/LocGatt.apk

pm grant vvnx.locgatt android.permission.ACCESS_FINE_LOCATION #Suffisant pour loc + bluetooth


# repo / rsync
rsync vers kimsufi
syntaxe:
rsync options source destination

rsync -azvhu /initrd/mnt/dev_save/android/lineageOS/sources/development/samples/LocGatt ks:/home/android	
rsync -azvhu ks:/home/android/LocGatt /initrd/mnt/dev_save/android/lineageOS/sources/development/samples


### Doc
https://developer.android.com/about/versions/oreo/background-location-limits

### Log

1) Activity unique, onLocationChanged() dedans, pas de whitelist
	onLocationChanged() = oui si écran allumé + UI en foreground, rien écran éteint. Même comportement on shelf.

2) Activity qui startService() un Service qui implement LocationListener, pas de whitelist
	même comportement qu'avec 1).
	
3) Activity qui startService() un Service qui implement LocationListener + qui startForeground(), pas de whitelist
	tient même en background. Même comportement que loctrack.
