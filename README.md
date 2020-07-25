# LocGatt 

### Description
Gatt WriteChar vers esp32 gatts triggered par locationUpdate()

### Ergonomie: EveryDay
# build & install

make LocGatt
adb uninstall vvnx.locgatt
adb install out/target/product/generic_arm64/system/app/LocGatt/LocGatt.apk
adb shell pm grant vvnx.locgatt android.permission.ACCESS_FINE_LOCATION #Suffisant pour loc + bluetooth


### Design

## Fonctionnement de base: locationUpdate() -> read/writeChar()
Activity: onCreate() -> startService()
	Service: onStartCommand()
				--> requestLocationUpdates()
				--> startForeground()
				--> connectmGatt()
	onLocationChanged() -> readCharacteristic()
	
## Binder / Messenger / Handler 
	premier projet dans lequel j'utilise ce système
	but: 2-way communication entre le service (le "server") et l'activity (le "client"), en utilisant une méthode "Kasher" ou "Mainstream"

1) bindService()
	Ressemble à startService(), mais il faut assigner une callback
	Déclenche onBind() dans le service, qui renvoie un Binder dans une callback onServiceConnected().
2) Messenger()
	Un messenger est juste un wrapper autour d'un Handler, qui gère des messages.
	La clé du système c'est la parentalité entre les système Binder et Messenger (que je n'ai pas tout à fait comprise)
		Messenger.getBinder() -> récupérer un Binder à partir d'un Messenger
	Tu crées deux Messenger: un dans l'activity, un dans le service.
	onBind() renvoie le Binder du Messenger du service vers l'activité grâce à getBinder() de Messenger: l'activité récupère dans la callback onServiceConnected() ce Binder,
		donc le Messenger du service: la communication activity -> service devient possible.
3) Astuce pour le reverse communication
	L'astuce pour avoir la communication du server vers le client (service vers activity) c'est que les messages de messenger ont un field "replyTo". Donc
	le premier truc que l'activity fait quand elle reçoit le Message dans la callback, c'est d'envoyer un Message vers le service en mettant une instance
	de son Messenger dans le field replyTo du message. Bingo: le service a chopé une référence du Messenger de l'activité. La communication du service vers l'activité est possible.
	



### Doc

-Binding / Messenger /Handler
https://developer.android.com/guide/components/bound-services
https://stackoverflow.com/questions/8341667/bind-unbind-service-example-android
https://www.cs.dartmouth.edu/~campbell/cs65/lecture19/lecture19.html
samples/browseable/messagingservice
development/samples/browseable/BluetoothLeGatt/src/com.example.android.bluetoothlegatt *************
https://stackoverflow.com/questions/2463175/how-to-have-android-service-communicate-with-activity
https://www.c-sharpcorner.com/article/bound-service-using-messenger-in-android-part-3/
https://medium.com/mindorks/mastering-android-handler-chapter-2-fb67d94f6327 ++++++ --> surtout la partie "The secret sauce is messenger"





### Doc
https://developer.android.com/about/versions/oreo/background-location-limits

### Log

1) Activity unique, onLocationChanged() dedans, pas de whitelist
	onLocationChanged() = oui si écran allumé + UI en foreground, rien écran éteint. Même comportement on shelf.

2) Activity qui startService() un Service qui implement LocationListener, pas de whitelist
	même comportement qu'avec 1).
	
3) Activity qui startService() un Service qui implement LocationListener + qui startForeground(), pas de whitelist
	tient même en background. Même comportement que loctrack.
