# Review App

Android App for Google Business Reviews with OpenRouter AI integration.

## Features
- Google Account Login (without Client ID)
- Bottom Navigation: Dashboard, QR Code, Reviews, Account
- AI Review & Reply generation via OpenRouter
- QR Code generation for review links
- Dashboard statistics

## Build APK

Push to `main` branch and GitHub Actions will automatically build the APK.
Download from Actions tab → Artifacts → `app-debug-apk`.

## API Key
Set your OpenRouter API key in `app/src/main/java/com/reviewapp/ApiService.kt` (replace `YOUR_OPENROUTER_API_KEY_HERE`). Do not commit real keys.