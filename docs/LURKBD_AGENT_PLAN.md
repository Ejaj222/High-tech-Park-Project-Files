# LurkBD Agent — Full Product Plan

**Voice-first Android app** for Bangladesh: daily হিসাব (accounts), shop credit & stock, study/class routines, medicine, prayer, and Ramadan alarms — controlled mostly by Bangla speech.

## Vision

LurkBD Agent keeps personal, student, and small-business daily life in one place. Speaking in Bangla is the primary interface; screens are for review and correction.

## Modules

| Module | Job | Example voice | Alarms |
|--------|-----|---------------|--------|
| Home | Today summary + voice entry | আজকের হিসাব দেখাও | — |
| হিসাব | Income, expense, channels, loans | বাজারে ৩০০ টাকা খরচ | Loan installment day |
| দোকান | Customer credit (বাকি) + stock | করিমের বাকি ৫০০ যোগ | Low-stock alert |
| **পড়াশোনা** | Class routine, study routine, notes, scan docs, results | আগামীকাল গণিত ক্লাসের অ্যালার্ম | Class / study alarms |
| ওষুধ | Medicine list + schedule | সকাল ৮টায় মেটফরমিন | Dose reminders |
| ইবাদত | 5 prayers + Sehri/Iftar | মাগরিবের অ্যালার্ম চালু | Prayer / Ramadan |
| Voice | Intent parse for all modules | শুনছি… | Confirm before write |

## পড়াশোনা (Study) — detail

For students: class schedule, personal study plan, subject notes, scanned documents, and academic results — with alarms and Bangla voice.

### Sub-features

| Feature | What it does |
|---------|----------------|
| **ক্লাস রুটিন** | Weekly class timetable (subject, day, time, room/teacher optional). Toggle alarm per class (e.g. 15–30 min before). |
| **পড়াশোনার রুটিন** | Custom study blocks (subject + time range + days). Separate from school class routine. Own alarms. |
| **সাবজেক্ট নোট** | Text notes grouped by subject (and optionally by chapter/topic). |
| **বই / খাতা / নোট** | Catalog of books, khatas, and note packs per subject. |
| **স্ক্যান / ডকুমেন্ট** | Camera scan or file import → store PDF/image under subject (notes, assignments, handouts). Optional OCR later. |
| **অ্যাসাইনমেন্ট** | Assignment title, due date, attached scanned docs, done/pending status + due alarm. |
| **রেজাল্ট** | Exam / class-test / assignment marks: subject, exam type, score, total, date, note. |

### Study screens

1. **পড়াশোনা Home** — আজকের ক্লাস + আজকের পড়ার ব্লক + quick actions (নোট, স্ক্যান, রেজাল্ট)  
2. **রুটিন** — tabs: ক্লাস রুটিন | পড়ার রুটিন (with alarm toggles)  
3. **সাবজেক্ট** — subject list → notes, books/khatas, documents  
4. **স্ক্যান** — camera / gallery / file → crop → save to subject + tag (note / assignment / book page)  
5. **রেজাল্ট** — list + add mark; filter by subject / exam type  
6. **অ্যাসাইনমেন্ট** — due list + attachments  

### Study voice examples

| User says | Intent | Result |
|-----------|--------|--------|
| সোমবার সকাল ১০টায় গণিত ক্লাস যোগ করো | class.add | Class routine entry + optional alarm |
| গণিত ক্লাসের অ্যালার্ম চালু রাখো | class.alarm | Alarm before গণিত |
| রাত ৯টায় ইংরেজি পড়ার রুটিন সেট করো | study.routine | Study block + alarm |
| গণিতে অধ্যায় ৫ এর নোট খোলো | note.open | Open subject note |
| এই পৃষ্ঠা স্ক্যান করে গণিতে রাখো | doc.scan | Attach scan to গণিত |
| পদার্থ ক্লাস টেস্টে ১৮ পেয়েছি ২০ এর মধ্যে | result.add | Class test result saved |
| অ্যাসাইনমেন্ট জমা দেওয়ার তারিখ মনে করাও | assignment.alarm | Due reminder |

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
- `lurkbd-hisab.jpg` — হিসাব: আয় / খরচ / লেনদেন  
- `lurkbd-shop.jpg` — বাকি + স্টক অ্যালার্ট  
- `lurkbd-study.jpg` — পড়াশোনা: রুটিন, নোট, স্ক্যান, রেজাল্ট  
- `lurkbd-reminders.jpg` — ওষুধ / নামাজ / রোজা  
- `lurkbd-voice.jpg` — Voice listening overlay  

## Target users

1. **Individuals** — daily spend/income, loans, medicine, prayer, fasting times  
2. **Students** — class & study routines, subject notes, scanned docs, results  
3. **Shopkeepers** — customer credit ledger, payment channels, stock + low-stock alerts  

## Voice architecture

1. **Capture** — Android SpeechRecognizer / on-device ASR (Bangla primary)  
2. **Intent parse** — rules + LLM extract action, amount, person/product/subject, time, channel  
3. **Confirm & write** — preview → user yes/no → Room DB + schedule alarms  

Every voice action requires on-screen confirmation before saving.

## Suggested tech stack

- **Client:** Kotlin, Jetpack Compose, Navigation, Room, WorkManager, AlarmManager  
- **Voice:** SpeechRecognizer; optional on-device / cloud LLM for intent  
- **Study docs:** CameraX + ML Kit Document Scanner (or similar); local file store; optional OCR later  
- **Prayer/Ramadan:** local calculation or Adhan-style API; default city Dhaka  
- **Data:** local-first Room; optional cloud backup later  
- **Privacy:** process voice on-device by default; scanned docs stay on device unless backup enabled  

## Core data entities

- `Transaction` — type, amount, category, channel, note, date  
- `Loan` — party, principal, paid, dueDate  
- `Customer` / `CreditEntry` — name, phone, balance  
- `Product` — name, qty, unit, lowThreshold  
- `Subject` — name, color/code  
- `ClassPeriod` — subjectId, dayOfWeek, start, end, room?, alarmOffset  
- `StudyBlock` — subjectId, dayOfWeek, start, end, alarmOffset  
- `StudyNote` — subjectId, title, body, updatedAt  
- `StudyResource` — subjectId, type (book/khata/notePack), title  
- `StudyDocument` — subjectId, tag (note/assignment/handout), uri, createdAt  
- `Assignment` — subjectId, title, dueAt, status, documentIds[]  
- `ResultEntry` — subjectId, examType (classTest/exam/assignment), score, total, date  
- `Medicine` — name, dose, times[], active  
- `PrayerSettings` / `RamadanSettings` — city, waqts, offsets  
- `VoiceLog` — transcript, intent, confirmed  

## Delivery phases

| Phase | Scope | Demo |
|-------|-------|------|
| P0 Foundation | Project, theme, nav, Room | Branded shell Home |
| P1 হিসাব | Income/expense/channels/loans | Manual entry + today summary |
| P2 Voice Core | Bangla ASR → intent → confirm → save | Speak to add expense |
| P3 দোকান | Credit ledger + stock + low-stock | Customer বাকি + stock alert |
| **P3.5 পড়াশোনা** | Class/study routine, notes, scan, results, assignments | Today's classes + scan + result entry |
| P4 রিমাইন্ডার | Medicine, 5 prayers, Sehri/Iftar | Live alarms |
| P5 Polish | Offline, backup, a11y, Play Store | Beta release |

**MVP recommendation:** ship P1 + P2 first, then দোকান / পড়াশোনা / ইবাদত by priority.

## Risks

- Bangla ASR can struggle with accent/noise → always confirm  
- Wrong city → wrong prayer/Sehri times → default Dhaka + manual override  
- Document scan storage can grow large → compression + per-subject quotas  
- Scope is large → cut modules for MVP  

## Next step

Approve design direction, then scaffold Kotlin/Compose (P0) and build হিসাব + Voice Core (P1–P2).
