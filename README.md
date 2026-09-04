# 421 score

Feuille de score pour le 421 (FR). Local-only Compose scoresheet — not a dice game.

**421 score** is a café paper *feuille*. Players write charges with large +/− steppers. It does **not** roll dice, simulate RNG, or “play 421”. English launcher name: **421 scoreboard**.

## Product (v1)

- 2–6 players, optional names (last names only in DataStore)
- Per-round charge / points with huge +/−, landscape-readable on a phone flat on the table
- House-rule markers (not a national rulebook): **421**, **nénette**, **macque**
- Running totals, next round, undo last change
- Reset round / reset table with confirm
- Cream / oak paper look — not casino 3D dice
- Fully offline: no `INTERNET` in the merged manifest, no ads, analytics, account, or billing

## Build a debug APK

Requires JDK 17+ and Android SDK 36 (`compileSdk` / `targetSdk` 36, `minSdk` 26). From the repo root:

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`  
`applicationId`: `com.eliranrp.score421`

Signed Play upload bundle (local keystore, **not** committed):

```bash
# keystore.properties from keystore.properties.example — never commit it
./gradlew bundleRelease
```

AAB: `app/build/outputs/bundle/release/app-release.aab`

## Pages (support / privacy)

After GitHub Pages is enabled on `main` → `/docs`:

- Support: https://eliranrp.github.io/421-score/
- Privacy: https://eliranrp.github.io/421-score/privacy.html
- FR: https://eliranrp.github.io/421-score/fr/

Contact is GitHub issues. The site collects nothing.

## Unit tests

```bash
./gradlew testDebugUnitTest
```

JVM tests cover totals, undo, reset, and add/remove players (2–6). CI runs **assembleDebug** and unit tests only — no Play upload.

## Not in scope

- Dice / physics / a playable 421 game
- Sjoelen, Belote, Tarot, Coinche, Rami, Yams, Quilles, or other café games
- Play Store publishing
