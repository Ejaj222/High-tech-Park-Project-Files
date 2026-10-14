# LurkBD Agent

Voice-first Android companion for daily **হিসাব**, দোকান, **পড়াশোনা**, medicine, prayer, and Sehri/Iftar — primarily in Bangla.

## Android app

Project: [`LurkBDAgent/`](LurkBDAgent/)

| Tab | Features |
|-----|----------|
| হোম | Brand hero, today summary, voice CTA |
| হিসাব | Income / expense / channels / loans |
| দোকান | Customer credit (বাকি), stock + low-stock |
| পড়াশোনা | Class/study routine, notes, results, assignments |
| রিমাইন্ডার | Medicine, 5 prayers (Dhaka), Sehri/Iftar |
| Voice FAB | Bangla speech or sample phrases → confirm → save |

### Build / install

```bash
cd LurkBDAgent
echo "sdk.dir=$ANDROID_HOME" > local.properties
./gradlew :app:assembleDebug
```

Debug APK:

- `LurkBDAgent/app/build/outputs/apk/debug/app-debug.apk`
- `docs/releases/LurkBD-Agent-debug.apk`

Open `LurkBDAgent` in Android Studio and run on an emulator/device.

### UI preview (browser)

Open [`preview/index.html`](preview/index.html) for an interactive phone-frame walkthrough of the screens.

## Product plan & mockups

- [Full plan](docs/LURKBD_AGENT_PLAN.md)
- [Design mockups](docs/design/)

## Design tokens

Deep teal `#0B4F4A` · Soft sand `#F7F3EC` · Warm gold `#C4A35A`
