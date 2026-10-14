# LurkBD Agent — Full Product Plan

**Voice-first Android app** for Bangladesh: daily money, shop credit & stock, medicine, prayer, and Ramadan alarms — controlled mostly by Bangla speech.

## Vision

LurkBD Agent keeps personal and small-business daily life in one place. Speaking in Bangla is the primary interface; screens are for review and correction.

## Modules

| Module | Job | Example voice | Alarms |
|--------|-----|---------------|--------|
| Home | Today summary + voice entry | আজকের হিসাব দেখাও | — |
| টাকা | Income, expense, channels, loans | বাজারে ৩০০ টাকা খরচ | Loan installment day |
| দোকান | Customer credit (বাকি) + stock | করিমের বাকি ৫০০ যোগ | Low-stock alert |
| ওষুধ | Medicine list + schedule | সকাল ৮টায় মেটফরমিন | Dose reminders |
| ইবাদত | 5 prayers + Sehri/Iftar | মাগরিবের অ্যালার্ম চালু | Prayer / Ramadan |
| Voice | Intent parse for all modules | শুনছি… | Confirm before write |

## Design system

| Token | Value | Use |
|-------|-------|-----|
| Primary | `#0B4F4A` Deep Teal | Brand, headers, primary actions |
| Surface | `#F7F3EC` Soft Sand | Backgrounds |
| Accent | `#C4A35A` Warm Gold | Alerts, voice ring, highlights |
| Type | Hind Siliguri / Noto Sans Bengali | Bangla-first UI |

**UI rules:** Hero = brand + one headline + one support line + voice CTA. No card clutter. Floating mic on every tab.

### Design mockups

See `docs/design/`:

- `lurkbd-home.jpg` — Home / brand + voice CTA  
- `lurkbd-money.jpg` — আয় / খরচ / লেনদেন  
- `lurkbd-shop.jpg` — বাকি + স্টক অ্যালার্ট  
- `lurkbd-reminders.jpg` — ওষুধ / নামাজ / রোজা  
- `lurkbd-voice.jpg` — Voice listening overlay  

## Target users

1. **Individuals** — daily spend/income, loans, medicine, prayer, fasting times  
2. **Shopkeepers** — customer credit ledger, payment channels, stock + low-stock alerts  

## Voice architecture

1. **Capture** — Android SpeechRecognizer / on-device ASR (Bangla primary)  
2. **Intent parse** — rules + LLM extract action, amount, person/product, time, channel  
3. **Confirm & write** — preview → user yes/no → Room DB + schedule alarms  

Every voice action requires on-screen confirmation before saving.

## Suggested tech stack

- **Client:** Kotlin, Jetpack Compose, Navigation, Room, WorkManager, AlarmManager  
- **Voice:** SpeechRecognizer; optional on-device / cloud LLM for intent  
- **Prayer/Ramadan:** local calculation or Adhan-style API; default city Dhaka  
- **Data:** local-first Room; optional cloud backup later  
- **Privacy:** process voice on-device by default  

## Core data entities

- `Transaction` — type, amount, category, channel, note, date  
- `Loan` — party, principal, paid, dueDate  
- `Customer` / `CreditEntry` — name, phone, balance  
- `Product` — name, qty, unit, lowThreshold  
- `Medicine` — name, dose, times[], active  
- `PrayerSettings` / `RamadanSettings` — city, waqts, offsets  
- `VoiceLog` — transcript, intent, confirmed  

## Delivery phases

| Phase | Scope | Demo |
|-------|-------|------|
| P0 Foundation | Project, theme, nav, Room | Branded shell Home |
| P1 টাকা | Income/expense/channels/loans | Manual entry + today summary |
| P2 Voice Core | Bangla ASR → intent → confirm → save | Speak to add expense |
| P3 দোকান | Credit ledger + stock + low-stock | Customer বাকি + stock alert |
| P4 রিমাইন্ডার | Medicine, 5 prayers, Sehri/Iftar | Live alarms |
| P5 Polish | Offline, backup, a11y, Play Store | Beta release |

**MVP recommendation:** ship P1 + P2 first, then shop and worship modules.

## Risks

- Bangla ASR can struggle with accent/noise → always confirm  
- Wrong city → wrong prayer/Sehri times → default Dhaka + manual override  
- Scope is large → cut modules for MVP  

## Next step

Approve design direction, then scaffold Kotlin/Compose (P0) and build Money + Voice Core (P1–P2).
