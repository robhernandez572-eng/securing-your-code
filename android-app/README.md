# HackPath — Learn to Code & Secure

A boot.dev-style Android app that teaches programming from zero to red-team offensive security skills — so learners understand how attacks work and can effectively defend against them.

## Curriculum

| Track | Difficulty | Topics |
|-------|-----------|--------|
| 🐍 Foundations | Beginner | Python 101, Data Structures, Algorithms, OOP |
| 🌐 Networking | Intermediate | OSI model, TCP/IP, TCP flags, Linux CLI |
| 🕸️ Web Security | Intermediate | OWASP Top 10: SQLi, XSS, CSRF, Broken Auth |
| 🔴 Red Team | Advanced/Expert | OSINT, port scanning, buffer overflows, privesc, password attacks |
| 🛡️ Blue Team | Advanced/Expert | Firewalls, iptables, incident response, log analysis |

## Features

- **Interactive lessons** with syntax-highlighted code examples
- **Knowledge-check quizzes** with explanations for every answer
- **XP + levelling system** (Script Kiddie → Elite Hacker)
- **Progress tracking** persisted locally with Room DB
- **Locked lessons** — complete prior lessons to unlock the next
- **Dark hacker aesthetic** with monospace fonts and green-on-black terminal feel

## Tech Stack

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Hilt** for dependency injection
- **Room** for local persistence
- **Navigation Compose** for screen navigation
- **MVVM** with `StateFlow` + `ViewModel`

## Build

Requirements: Android Studio Ladybug (2024.2+), JDK 17, Android SDK 35

```bash
git clone <repo>
cd android-app
./gradlew assembleDebug
```

Open `android-app/` in Android Studio for the full IDE experience.

## Ethical Note

All offensive security content is taught for **defensive purposes only** — understanding how attacks work is the only way to build effective defences. Every red-team lesson includes the defender's countermeasures. Always test only on systems you own or have explicit written permission to test.
