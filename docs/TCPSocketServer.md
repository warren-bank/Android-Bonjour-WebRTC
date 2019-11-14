#### WebRTC signaling through TCPSocketServer

1. when `ServerService` starts:
   * immediately calls:
     ```java
       new DirectRTCClient(...)
       DirectRTCClient.connect(RoomConnectionParameters connectionParameters)
         -> IP: "0.0.0.0:8888"
     ```
   * immediately calls:
     ```java
       new TCPChannelClient(..., ip, port)
     ```
   * immediately calls:
     ```java
       new TCPChannelClient.TCPSocketServer(address, port)
     ```
2. when `TCPChannelClient.TCPSocketServer` receives connection from `TCPChannelClient.TCPSocketClient`:
   * immediately calls:
     ```java
       DirectRTCClient.onTCPConnected(boolean server)
     ```
   * immediately calls:
     ```java
       ServerSignalingEvents.onConnectedToRoom(SignalingParameters parameters)
     ```
   * immediately starts:
     ```java
       CallActivity
     ```
   * immediately calls:
     ```java
       ServerSignalingEvents.setCallActivity(CallActivity eventListener)
     ```
   * immediately calls:
     ```java
       CallActivity.onConnectedToRoom(SignalingParameters parameters)
     ```
   * immediately calls:
     ```java
       peerConnectionClient.createPeerConnection(localProxyVideoSink, remoteSinks, videoCapturer, signalingParameters)
       peerConnectionClient.createOffer()
     ```
   * immediately calls:
     ```java
       ServerPeerConnectionEvents.onLocalDescription(SessionDescription sdp)
     ```
   * immediately calls:
     ```java
       DirectRTCClient.sendOfferSdp(SessionDescription sdp)
         -> type: "offer"
     ```
   * immediately calls:
     ```java
       TCPChannelClient.TCPSocketServer.send(String message)
         -> format: JSON
     ```
3. when `TCPChannelClient.TCPSocketServer` receives a message from connected `TCPChannelClient.TCPSocketClient`:
   * immediately calls:
     ```java
       DirectRTCClient.onTCPMessage(String message)
         -> type: "answer"
     ```
   * immediately calls:
     ```java
       ServerSignalingEvents.onRemoteDescription(SessionDescription sdp)
     ```
   * immediately calls:
     ```java
       CallActivity.onRemoteDescription(SessionDescription sdp)
     ```
   * immediately calls:
     ```java
       peerConnectionClient.setRemoteDescription(sdp)
     ```
