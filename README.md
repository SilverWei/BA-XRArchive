# BA-XRArchive

An Android XR Application Created as Blue Archive Fan-made/Secondary Creation

---
An experimental Blue Archive fan app developed with Jetpack XR SDK and Android XR, mainly for learning and testing AndroidXR native 3D / spatial application development.

The APK can be sideloaded directly onto a Samsung Galaxy XR device running Android XR, or installed in the Android XR emulator via Android Studio.

Currently implemented features can be seen in the upcoming demo video.

This demo exists purely as an experimental study to understand the current limits of 3D spatial app development with Jetpack XR SDK. No further updates are planned unless Jetpack XR SDK adds more 3D model-related APIs in the future.

![Demo Preview 1](Screenshot1.png)
![Demo Preview 2](Screenshot2.png)
*(Demo preview screenshot - in Android XR emulator)*

### 3D Models / Assets 

**The source code in this repository has had all 3D model files ripped from the game removed** (for compliance).

If you need to run the full code, please obtain the following model files through your own means and place them in the corresponding locations:
- app/src/main/assets/himari.glb
- app/src/main/assets/kei.glb
- app/src/main/assets/midori.glb
- app/src/main/assets/momoi.glb
- app/src/main/assets/netzach.glb
- app/src/main/assets/rio.glb
- app/src/main/assets/tendou_aris_battle.glb
- app/src/main/assets/toki.glb
- app/src/main/assets/yuzu.glb

The character models require shader and animation modifications to run properly in the demo. Please search for `fun CharacterSelector` in the project for reference.

### Tech Stack
- Jetpack XR SDK
- Android XR
- Kotlin
- Android Studio

### Copyright & Disclaimer
- All copyrights for the images, videos, and 3D models used in this project belong to **NEXON Games & Yostar**.  
- The materials used for secondary creation in this project are strictly prohibited from being used for any commercial purposes (including any form of monetization, distribution, sales, etc.).  
- When using any materials from this project intended for secondary creation, you must strictly comply with the official Blue Archive fan creation guidelines: [二次創作・ゲーム実況配信及び動画投稿に関するガイドライン](https://bluearchive.jp/news/newsJump/116) (Japanese).  
- This repository is an unofficial fan secondary creation project. All risks arising from the use of this project are borne solely by the user; it is provided only for learning, appreciation, and testing purposes.