#### [Bonjour WebRTC](https://github.com/warren-bank/Android-Bonjour-WebRTC)

Android app that allows device discovery on WLAN (w/ Bonjour) and video calls to be placed between devices on WLAN (w/ WebRTC) without any external server.

#### Screenshots

![Bonjour-WebRTC](./screenshots/1-mainactivity.png)
![Bonjour-WebRTC](./screenshots/2-mainactivity-actionbar.png)
![Bonjour-WebRTC](./screenshots/3-mainactivity-update-alias-dialog.png)
![Bonjour-WebRTC](./screenshots/4-settingsactivity-webrtc-settings.png)
![Bonjour-WebRTC](./screenshots/5-mainactivity-bonjour-discovery-list.png)
![Bonjour-WebRTC](./screenshots/6-callactivity.png)

#### Download

[<img alt="Get it on IzzyOnDroid" height="80" src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png">](https://apt.izzysoft.de/fdroid/index/apk/com.amaze.filemanager)

[<img alt="Get it on GitHub" height="80" src="https://i.ibb.co/q0mdc4Z/get-it-on-github.png">](https://github.com/warren-bank/Android-Bonjour-WebRTC/releases/latest)


#### Notes

* minimum supported version of Android:
  * Android 5.0 (API level 21)

#### Usage

* when the Service is not running
  - your app displays a list of other devices on the WLAN that are running the Service
  - you can initiate a call to any of these devices
* when the Service is running
  - other devices on the WLAN will see your device in their lists
  - any of these devices can initiate a call to you
* when the app is launched
  - the Service is not running
* when you click this icon ![alias icon](./android-studio-project/Bonjour-WebRTC/src/main/res/drawable-hdpi/menu_update_server_alias.png) in the top menu
  - you can specify the alias that other users will see in their lists that identifies your device on the WLAN
  - if not customized, the default value is your IP address on the WLAN
* when you click this icon ![network icon](./android-studio-project/Bonjour-WebRTC/src/main/res/drawable-hdpi/menu_toggle_server.png) in the top menu
  - the Service will toggle between _on_ and _off_
    * in the _on_ state
      - a notification will always be visible, which will reopen the app when clicked
      - resource locks will be active to:
        * keep the CPU awake
        * keep the WiFi radio awake and connected
        * enable listening for multicast packets
    * in the _off_ state
      - the notification is dismissed
      - all resource locks are removed

#### Credits

* [jmDNS library](https://github.com/jmdns/jmdns)
  - handles all Bonjour service registration/discovery
* [WebRTC prebuilt native libraries with JNI wrapper](./notes/webrtc-android-native-library.txt)
* WebRTC _AppRTCMobile_ example Android application
  - [official source](https://webrtc.googlesource.com/src/+/master/examples/androidapp/)
  - [unofficial build](https://github.com/warren-bank/Android-AppRTCMobile)
  - [methodology to apply updates](https://github.com/warren-bank/Android-Bonjour-WebRTC/tree/apprtc/2019-09-06)

#### Legal:

* copyright: [Warren Bank](https://github.com/warren-bank)
* license: [GPL-2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)
