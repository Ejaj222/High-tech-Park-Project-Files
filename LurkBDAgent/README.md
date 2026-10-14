# LurkBD Agent (Android)

Voice-first Bangla Android app — **হিসাব**, দোকান, পড়াশোনা, রিমাইন্ডার (ওষুধ/নামাজ/রোজা).

## Stack

- Kotlin · Jetpack Compose · Material 3  
- Room (offline-first)  
- SpeechRecognizer (bn-BD) + rule-based intent parser  
- AlarmManager (`setAlarmClock`) + BootReceiver  
- ML Kit Document Scanner + CameraX fallback  

## Modules

| Tab | Features |
|-----|----------|
| হোম | Brand hero, today summary, voice CTA |
| হিসাব | Income/expense/channels/loans |
| দোকান | Customer credit (বাকি), stock + low-stock highlight |
| পড়াশোনা | Class/study routine + alarms, notes, **document scan**, results |
| রিমাইন্ডার | Medicine, 5 prayers (Dhaka), Sehri/Iftar — real device alarms |
| Voice FAB | Speak or tap sample phrases → confirm → save |

## Build (debug)

```bash
# requires Android SDK
echo "sdk.dir=$ANDROID_HOME" > local.properties
./gradlew :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Play Store signing (release)

```bash
chmod +x scripts/create-release-keystore.sh
./scripts/create-release-keystore.sh   # writes keystore/ + keystore.properties (gitignored)
./gradlew :app:assembleRelease :app:bundleRelease
```

- Signed APK: `app/build/outputs/apk/release/app-release.apk`
- Play upload AAB: `app/build/outputs/bundle/release/app-release.aab`
- Template: `keystore.properties.example`
- **Never commit** `keystore.properties` or `*.jks`

Open the `LurkBDAgent` folder in Android Studio (Giraffe+/SDK 35) to run on emulator or device.
