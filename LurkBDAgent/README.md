# LurkBD Agent (Android)

Voice-first Bangla Android app — **হিসাব**, দোকান, পড়াশোনা, রিমাইন্ডার (ওষুধ/নামাজ/রোজা).

## Stack

- Kotlin · Jetpack Compose · Material 3  
- Room (offline-first)  
- SpeechRecognizer (bn-BD) + rule-based intent parser  

## Modules

| Tab | Features |
|-----|----------|
| হোম | Brand hero, today summary, voice CTA |
| হিসাব | Income/expense/channels/loans |
| দোকান | Customer credit (বাকি), stock + low-stock highlight |
| পড়াশোনা | Class/study routine + alarms, notes, results, assignments |
| রিমাইন্ডার | Medicine, 5 prayers (Dhaka), Sehri/Iftar |
| Voice FAB | Speak or tap sample phrases → confirm → save |

## Build

```bash
# requires Android SDK
echo "sdk.dir=$ANDROID_HOME" > local.properties
./gradlew :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

Open the `LurkBDAgent` folder in Android Studio (Giraffe+/SDK 35) to run on emulator or device.
