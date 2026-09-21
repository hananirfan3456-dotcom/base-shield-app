# Base Shield Scanner

> **Real-time Smart Contract Security Auditor & DEX Tracker for Base Network (Chain ID: 8453)**

Base Shield Scanner is a modern, high-performance security dashboard and decentralized exchange tracker built for the **Base Network**. It provides automated discovery of active and newly launched tokens, coupled with real-time smart contract audits powered by the **GoPlus Security API**, **DexScreener API**, and **GeckoTerminal API**.

---

## Key Features

- **Automated Dynamic Token Feed (Base Chain ID: 8453)**:
  - Automatically fetches and displays active and newly launched tokens on Base DEXes (Aerodrome, Uniswap v3, SushiSwap).
  - Displays live 24h volume, liquidity, price changes, and direct one-tap security scans.
  - Interactive filter chips: *All Tokens*, *Newly Launched*, *Top Volume*, and *Top Gainers*.

- **Live GoPlus Security Audit & Real-time Safety Score (0–100)**:
  - Real-time search bar accepting any Base contract address (`0x...`).
  - Comprehensive 3-parameter evaluation engine:
    1. **Honeypot & Malicious Code Detection (Max 40 Pts)**: Checks `is_honeypot`, `cannot_buy`, `cannot_sell_all`, pausable transfers, blacklist functions, and creator history.
    2. **Liquidity Lock Status (Max 30 Pts)**: Scans LP total supply, top LP holders, and lock contracts (Uncx, PinkSale, Team Finance, Burn addresses).
    3. **Excessive Buy/Sell Taxes (Max 30 Pts)**: Flags hidden tax modifications, slippage triggers, and excessive fees (>10% warning, >25% critical).

- **Strict Liquidity Lock (`lp_locked`) Verification**:
  - Strictly reads the `lp_locked` / `is_locked` parameters from the GoPlus audit data and displays explicit English text warnings:
    - **Safe Condition**: `"Liquidity Status: Funds are locked in the smart contract. The owner cannot withdraw them."`
    - **High Risk Condition**: `"CRITICAL WARNING: Funds are NOT locked! The owner retains full control and can withdraw all money at any time."`

- **Instant Red Warning Alerts**:
  - High-visibility banner alerts flash instantly for any detected threat (Honeypot trap, unlocked liquidity, abnormal taxes, proxy contracts, or mintable supplies).

- **Quick Scan Preset Shortcuts**:
  - Pre-configured shortcuts for verified Base tokens (**BRETT**, **DEGEN**, **WETH**, **AERO**).
  - One-tap test shortcuts for **Unlocked LP Rug Demo** and **HoneyRug Honeypot Demo** to simulate and verify instant warnings.

---

## Project Architecture

```
├── app/                              # Android Application (Kotlin + Jetpack Compose)
│   ├── src/main/java/com/example/
│   │   ├── MainActivity.kt           # Edge-to-edge Compose Entry Point
│   │   ├── data/
│   │   │   ├── api/                  # Retrofit Clients (GoPlus, DexScreener, GeckoTerminal)
│   │   │   ├── model/                # SecurityReport, TokenSecurityResponse, DexTokenModels
│   │   │   └── repository/           # BaseShieldRepository (Cache, Presets & Fallbacks)
│   │   └── ui/
│   │       ├── components/           # SafetyScoreGauge, RedWarningAlertBanner, ParameterCards
│   │       ├── screen/               # BaseShieldDashboardScreen (Single-screen Dashboard)
│   │       ├── theme/                # Cyberpunk Base Dark Palette (Color, Type, Theme)
│   │       └── viewmodel/            # BaseShieldViewModel (StateFlow & Coroutines)
│   └── src/test/                     # Robolectric CUJ Tests & Parameter Verification
├── server/                           # Node.js / Express Backend Microservice & Proxy
│   └── server.js                     # Express API (GoPlus & DEX proxy endpoints)
├── package.json                      # Node.js Backend Dependencies & Scripts
├── requirements.txt                  # Backend Dependencies Specification
├── build.gradle.kts                  # Root Gradle Configuration
└── settings.gradle.kts               # Gradle Project Settings
```

---

## Installation & Setup Instructions

### Option 1: Android Application Setup (Mobile)

#### Prerequisites
- **Android Studio** Ladybug (2024.2+) or newer
- **JDK 17** or **JDK 21**
- **Android SDK Platform 36** (Min SDK: 24, Target SDK: 36)

#### Steps
1. **Clone or Extract the Archive**:
   ```bash
   unzip base_shield_scanner.zip -d base_shield_scanner
   cd base_shield_scanner
   ```
2. **Open in Android Studio**:
   - Launch Android Studio, choose **File > Open**, and select the `base_shield_scanner` directory.
   - Allow Gradle to sync dependencies automatically.
3. **Build the APK**:
   ```bash
   gradle :app:assembleDebug
   ```
   The compiled APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.
4. **Run Unit & Robolectric Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

### Option 2: Backend Microservice Setup (Node.js & Express)

The project includes an optional standalone backend proxy service that exposes REST endpoints for token discovery and GoPlus smart contract analysis.

#### Prerequisites
- **Node.js** v18.0.0 or higher
- **npm** v9.0.0 or higher

#### Dependencies (`requirements.txt` / `package.json`)
- `express` (>=4.19.2) - Fast HTTP server and REST routing
- `axios` (>=1.7.2) - HTTP client for GoPlus, DexScreener, and GeckoTerminal
- `cors` (>=2.8.5) - Cross-Origin Resource Sharing
- `dotenv` (>=16.4.5) - Environment variable management
- `helmet` (>=7.1.0) - HTTP security headers
- `morgan` (>=1.10.0) - Request logging

#### Steps
1. **Install Dependencies**:
   ```bash
   npm install
   ```
2. **Start the Development Server**:
   ```bash
   npm start
   ```
   The server will bind to `http://localhost:3000`.

#### Backend API Endpoints
- `GET /api/health` — System status and network verification (Base Chain ID: 8453).
- `GET /api/tokens/active` — Returns trending and active Base liquidity pools.
- `GET /api/scan/:address` — Audits the specified contract address, calculates the 0–100 Safety Score, and strictly checks `lp_locked` status.

---

### Option 3: Direct 1-Click Hosting on Vercel

The project is pre-configured for instant deployment on **Vercel** (`vercel.json`, `public/index.html`, and `api/index.js`).

#### Steps to Deploy on Vercel:
1. **Push to GitHub**:
   - Push this extracted project repository to your GitHub account:
     ```bash
     git init
     git add .
     git commit -m "Initial Base Shield Scanner commit"
     git remote add origin https://github.com/YOUR_USERNAME/base-shield-scanner.git
     git push -u origin main
     ```
2. **Deploy on Vercel Dashboard**:
   - Go to [vercel.com](https://vercel.com) and log in.
   - Click **"Add New..." > "Project"**.
   - Import your `base-shield-scanner` repository.
   - Leave the root directory and build settings as default (Framework Preset: **Other**).
   - Click **"Deploy"**.
3. **Or Deploy via Vercel CLI**:
   ```bash
   npm i -g vercel
   vercel
   ```
4. **Live Result**:
   - Your live Vercel URL will instantly host both the **Web Security Dashboard** and the serverless **GoPlus Audit API** (`https://your-project.vercel.app`), while offering direct downloads for the Android APK and source ZIP.

---

## Safety Score Methodology (100 Points Total)

| Parameter | Max Points | Evaluation Criteria |
| :--- | :---: | :--- |
| **1. Honeypot & Malicious Code** | **40 pts** | `is_honeypot == 0`, `cannot_sell_all == 0`, pausable transfers disabled, creator honeypot history clean. Failure results in immediate 0 pts and critical red alert banner. |
| **2. Liquidity Lock Status** | **30 pts** | Reads `lp_locked` data from GoPlus. If `lp_locked == 1` or ≥50% locked/burned: 25–30 pts and display safe message. If `lp_locked == 0` or unlocked: 0 pts, critical red alert, and displays: *"CRITICAL WARNING: Funds are NOT locked! The owner retains full control and can withdraw all money at any time."* |
| **3. Buy & Sell Taxes** | **30 pts** | 0–5% tax: 30 pts; 5–10% tax: 20 pts; 10–25% tax: 10 pts; >25% tax: 0 pts and critical warning. Flagged if taxes can be modified by owner. |

---

## Verified Test Contracts on Base

You can test the scanner with these addresses directly from the search bar:

- **BRETT** (Top Base Memecoin): `0x532f27101965dd16442e59d40670faf5ebb142e4` (Safe, 0% tax, 100% LP locked)
- **DEGEN** (Farcaster Ecosystem): `0x4ed4e862860bed51a9570b96d89af5e1b0efefed` (Safe, verified contract)
- **WETH** (Base Native Wrapper): `0x4200000000000000000000000000000000000006` (Safe, core infrastructure)
- **AERO** (Aerodrome DEX): `0x940181a94A35A4569E4529A3CDfB74e38FD98631` (Safe, core DEX protocol)
- **Unlocked LP Demo**: `0xRUG200000000000000000000000000000000RUG2` (Triggers Unlocked Liquidity Warning)
- **HoneyRug Demo**: `0xBAD100000000000000000000000000000000BAD1` (Triggers Honeypot, 99% Tax & Red Alerts)

---

## License

This project is licensed under the MIT License.
