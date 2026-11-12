# SMS Relay

Separate workspace for the SMS relay and the services that grow around it. Allucent stays in its own workspace.

| Folder | What it is |
| --- | --- |
| `mobile-app` | Android app named SMS Forward. Forwards a matching SMS to the Telegram bot. |
| `mac` | Mac listener. Stays running and copies the latest code to the clipboard. |

Add the next service as its own folder here. Keep tokens in the gitignored `.env` and `local.properties` files.

Open this workspace with `sms-relay.code-workspace`, not the Allucent workspace.

## Current behavior

The phone forwards a message when the text contains a word from the list in `mobile-app/app/src/main/java/bd/naim/smsrelay/OtpText.kt`: `OTP`, `IVAC`, or `One Time Password`. Add another phrase as a new line in that list, then build:

```bash
cd mobile-app
./build-apk.sh
```

That writes `mobile-app/sms-forward.apk`. The app name on the phone is SMS Forward.

The Mac script copies the first 4–8 digit number. `228650 is your OTP...` copies `228650`. When there is no digit code, it converts the IVAC word line. `Eight_Two_Nine_Six_One_Five` copies `829615`. A matching message with no code is printed in the terminal, and the script keeps listening.

Run the listener once and leave it open:

```bash
cd mac
node listen.mjs
```
