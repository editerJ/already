# Already Release Checklist

## Code-side items completed

- Android target SDK set to 35
- HTTPS-only network security config in app manifest
- Backup and data extraction rules configured
- AdMob IDs injected through local properties / Gradle properties
- Rewarded ads and UMP consent manager wired in code
- Locale config enabled for multilingual delivery
- Release build now fails if privacy policy URL, terms URL, support email, or production AdMob IDs are still placeholders
- Legacy `com.regretdiary` Java sources excluded from compilation

## Must be completed outside the codebase before production

- Publish a real public Privacy Policy URL
- Publish a real public Terms of Service URL
- Set a real production support email
- Complete Play Console developer verification
- Complete Play Console Data safety form
- Complete Play Console app access / ads declarations if prompted
- Configure AdMob Privacy & messaging for EEA/UK consent
- If using a personal developer account that requires pre-production testing, complete the closed test requirement before production

## Final release commands

- `gradlew.bat assembleRelease`
- `gradlew.bat bundleRelease`