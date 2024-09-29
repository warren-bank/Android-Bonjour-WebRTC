-------------------------------------------------------------------------------- branch: apprtc/2019-09-06

git rm -rf .
git checkout --orphan 'apprtc/2019-09-06'

--------------------------------------------------------------------------------

git add --all .
git commit
git push origin 'apprtc/2019-09-06'

----------------------------------------

original apprtc example app: 2019-09-06

https://webrtc.googlesource.com/src/
https://webrtc.googlesource.com/src/+/refs/heads/main/examples/androidapp/src/org/appspot/apprtc
https://webrtc.googlesource.com/src/+log/refs/heads/main/examples/androidapp/src/org/appspot/apprtc

https://webrtc.googlesource.com/src/+/4d6b2691bd5d3b9bcdf3c11ac9cf5544e65f9d25
  commit: 4d6b2691bd5d3b9bcdf3c11ac9cf5544e65f9d25
  date:   2019-09-06

https://webrtc.googlesource.com/src/+/4d6b2691bd5d3b9bcdf3c11ac9cf5544e65f9d25/examples/aarproject/app/build.gradle
  minSdkVersion: 16
  implementation 'org.webrtc:google-webrtc:1.0.+'
  implementation 'com.android.support:appcompat-v7:26.1.0'

https://webrtc.googlesource.com/src/+archive/4d6b2691bd5d3b9bcdf3c11ac9cf5544e65f9d25.tar.gz
  snapshot

--------------------------------------------------------------------------------

git add --all .
git commit
git push origin 'apprtc/2019-09-06'

----------------------------------------

modifications made by this app for release: v01.03.09

https://github.com/warren-bank/Android-Bonjour-WebRTC/tree/master/android-studio-project/Bonjour-WebRTC/src/main/java/org/appspot/apprtc
https://github.com/warren-bank/Android-Bonjour-WebRTC/commits/master/android-studio-project/Bonjour-WebRTC/src/main/java/org/appspot/apprtc

https://github.com/warren-bank/Android-Bonjour-WebRTC/releases/tag/v01.03.09
  commit: 2c3d90e76abc9f5c5aef2ad7ee23c4aefa37480e
  date:   2023-01-23

https://github.com/warren-bank/Android-Bonjour-WebRTC/archive/refs/tags/v01.03.09.zip
  snapshot

-------------------------------------------------------------------------------- branch: apprtc/2024-09-28

git rm -rf .
git checkout --orphan 'apprtc/2024-09-28'

--------------------------------------------------------------------------------

git add --all .
git commit
git push origin 'apprtc/2024-09-28'

----------------------------------------

original apprtc example app: 2024-09-28

https://webrtc.googlesource.com/src/
https://webrtc.googlesource.com/src/+/refs/heads/main/examples/androidapp/src/org/appspot/apprtc
https://webrtc.googlesource.com/src/+log/refs/heads/main/examples/androidapp/src/org/appspot/apprtc

https://webrtc.googlesource.com/src/+/4ab100d4a6c3b7848ae33b4c88fdf995da2f8922
  commit: 4ab100d4a6c3b7848ae33b4c88fdf995da2f8922
  date:   2024-09-28

https://webrtc.googlesource.com/src/+/4ab100d4a6c3b7848ae33b4c88fdf995da2f8922/examples/aarproject/app/build.gradle
  minSdkVersion: 21
  implementation fileTree(dir: project.aarDir, include: ['google-webrtc-*.aar'])
  implementation 'androidx.annotation:annotation:1.2.0'

https://webrtc.googlesource.com/src/+archive/4ab100d4a6c3b7848ae33b4c88fdf995da2f8922.tar.gz
  snapshot

--------------------------------------------------------------------------------

git cherry-pick 'apprtc/2019-09-06'

# -----
# https://stackoverflow.com/a/30770796
# -----
# git status
# git add file-which-had-conflicts
# git rm file-which-had-conflicts
# git cherry-pick --continue
# -----

git rm apprtc/ConnectActivity.java
git rm apprtc/RoomParametersFetcher.java
git rm apprtc/WebSocketChannelClient.java
git rm apprtc/WebSocketRTCClient.java
git rm apprtc/util/AsyncHttpURLConnection.java

# -----
# git status
# -----
# both modified:   apprtc/AppRTCBluetoothManager.java
# both modified:   apprtc/DirectRTCClient.java
# -----

# -----
# git mergetool
# -----

# -----
# git status
# -----
# modified:   apprtc/DirectRTCClient.java
# -----

git add apprtc/AppRTCBluetoothManager.java
git add apprtc/DirectRTCClient.java

git cherry-pick --continue
git push origin 'apprtc/2024-09-28'

----------------------------------------

modifications made by this app based upon release: v01.03.09

https://github.com/warren-bank/Android-Bonjour-WebRTC/tree/master/android-studio-project/Bonjour-WebRTC/src/main/java/org/appspot/apprtc
https://github.com/warren-bank/Android-Bonjour-WebRTC/commits/master/android-studio-project/Bonjour-WebRTC/src/main/java/org/appspot/apprtc

https://github.com/warren-bank/Android-Bonjour-WebRTC/releases/tag/v01.03.09
  commit: 2c3d90e76abc9f5c5aef2ad7ee23c4aefa37480e
  date:   2023-01-23

https://github.com/warren-bank/Android-Bonjour-WebRTC/archive/refs/tags/v01.03.09.zip
  snapshot

--------------------------------------------------------------------------------
