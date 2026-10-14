# OSINT Crawler — Android APK

A SpiderFoot-inspired OSINT (Open Source Intelligence) crawler for Android.  
Crawls the **surface web**, queries the **Wayback Machine / archive.org CDX API**, and
crawls **.onion / dark web** sites through the Tor network via Orbot.

---

## Features

| Module | Source | What it finds |
|--------|--------|---------------|
| Wayback CDX | Archive.org | All archived URLs, MIME types, snapshot dates |
| Wayback Availability | Archive.org | Whether a URL has an archived snapshot |
| Surface Link Crawler | Target site | All hyperlinks (BFS with depth/page limits) |
| Email Harvester | Target site | Email addresses in page text and mailto: links |
| Phone Extractor | Target site | Phone numbers in page text |
| Metadata Extractor | Target site | Title, description, OG tags, generator |
| Form Analyzer | Target site | Login forms, input fields, POST endpoints |
| JS Endpoint Finder | Target site | API endpoints embedded in JavaScript files |
| Social Media Finder | Target site | Twitter/X, GitHub, LinkedIn, Instagram, YouTube |
| Document Finder | Target site | PDF, DOC, XLS, ZIP files linked from pages |
| DNS/IP Leak Finder | Target site | IP addresses in page source |
| Onion Crawler | Tor (.onion) | Page content, title, text |
| Hidden Service Enum | Tor (.onion) | Linked .onion addresses |
| Onion Content Scanner | Tor (.onion) | Emails, PGP keys, headings |
| Market Detector | Tor (.onion) | Marketplace / forum pattern detection |

---

## Requirements

- Android 8.0+ (API 26)
- **Orbot** (Tor for Android) — required for `.onion` / dark web crawling only
  - Install from F-Droid or Google Play
  - Start Orbot and connect before enabling Dark Web mode
  - The app routes all dark web traffic through Orbot's SOCKS5 proxy at `127.0.0.1:9050`

---

## Building

```bash
# Clone / open in Android Studio
# Or build from command line:
./gradlew assembleDebug

# APK output:
app/build/outputs/apk/debug/app-debug.apk
```

Requirements: Android SDK, JDK 17+, Gradle 8.1+

---

## Architecture

```
app/src/main/java/com/osint/crawler/
├── models/
│   └── ScanResult.kt        — Data models (ScanResult, ScanConfig, CrawlerModule enums)
├── modules/
│   ├── CrawlerEngine.kt     — Orchestrator: runs all modules, emits Flow<ScanResult>
│   ├── WaybackMachineModule.kt  — CDX API + availability check
│   ├── SurfaceWebModule.kt  — OkHttp + JSoup surface web crawler
│   └── DarkWebModule.kt     — Tor/Orbot SOCKS5 proxy .onion crawler
├── ui/
│   ├── MainActivity.kt      — Tab host (New Scan / History)
│   ├── NewScanFragment.kt   — Target input + module picker
│   ├── ScanActivity.kt      — Live results view with real-time Flow collection
│   ├── ResultDetailActivity.kt  — Full result detail + share/copy/open
│   ├── HistoryFragment.kt   — Past scans (Room DB extension point)
│   └── SettingsActivity.kt  — Tor proxy host/port, user agent, timeout
├── adapters/
│   ├── ResultsAdapter.kt    — RecyclerView adapter with source color badges
│   └── MainPagerAdapter.kt
└── utils/
    ├── HttpClientFactory.kt — OkHttp clients (surface, Tor proxy, Wayback)
    └── ExportUtils.kt       — JSON + CSV export via FileProvider share sheet
```

---

## Tor / Dark Web Notes

- All `.onion` traffic goes through Orbot's **SOCKS5 proxy on 127.0.0.1:9050**
- OkHttp's SOCKS5 implementation sends the hostname unresolved to the proxy,
  so `.onion` names are resolved by Tor — they never hit Android DNS
- The app never auto-opens `.onion` URLs in the browser — you must use Tor Browser
  (Orfox or Firefox for Android with Tor proxy configured)
- Dark web modules are disabled by default and require explicit opt-in

---

## Export

Results can be exported as **JSON** or **CSV** via the action menu during/after a scan.
Files are saved to `Android/data/com.osint.crawler/files/Documents/OSINTCrawler/`
and shared via the Android share sheet.

---

## Legal Notice

This tool is for **authorized security research, OSINT investigation, and educational
use only**. Always obtain proper authorization before crawling targets.
Dark web crawling modules are provided for threat intelligence and research purposes.
