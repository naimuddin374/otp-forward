# SMS Forward

Android app that posts a matching SMS to a Telegram bot. The Mac script copies the code.

A message is forwarded when it contains `OTP`, `IVAC`, or `One Time Password`. The list is `forwardWords` in `app/src/main/java/bd/naim/smsrelay/OtpText.kt`. Add a phrase there, then build again.

`228650 is your OTP...` copies `228650` on the Mac. `Eight_Two_Nine_Six_One_Five` becomes `829615`. A matching message with no code is printed in the terminal.

An iPhone app cannot receive SMS this way. A later iOS client can send the same text to the bot. The Mac script stays the same.

## Secrets

`local.properties` and `mac/.env` hold the bot token and chat id. Both are gitignored.

If the token was pasted into a chat, revoke it in @BotFather with `/revoke`, then put the new token in both files and build the APK again.

## Build the APK

```bash
cd /Users/naim/code/others/sms-relay/mobile-app
./build-apk.sh
```

The shareable file is `sms-forward.apk`.

## Install the APK

Send `sms-forward.apk` to the phone. Telegram, USB, or any file share is fine.

### 1. Turn off Play Protect

Play Protect blocks this file because it did not come from the Play Store.

1. Open **Play Store**.
2. Tap your profile picture at the top.
3. Tap **Play Protect**.
4. Tap the gear icon.
5. Turn off **Scan apps with Play Protect**.
6. Tap **Turn off** to confirm.

### 2. Install the file

1. Open `sms-forward.apk` from the chat or the Downloads folder.
2. If Android says this app cannot install unknown apps, tap **Settings**, turn **Allow from this source** on, go back, and open the file again.
3. Tap **Install**.
4. Tap **Open**. The app name is **SMS Forward**.

### 3. Allow SMS

1. Tap **Allow SMS**.
2. Tap **Allow**.
3. Leave mobile data or Wi-Fi on. You can close the app. Matching SMS still go to the bot.

**Stop** pauses forwarding only while this screen is open. Leaving the app turns forwarding back on.

### 4. Turn Play Protect back on

Do this before the phone opens any other file. While scanning is off, a different file can install too.

1. Open **Play Store**.
2. Tap your profile picture.
3. Tap **Play Protect**.
4. Tap the gear icon.
5. Turn **Scan apps with Play Protect** back on.

## On the Mac

```bash
cd /Users/naim/code/others/sms-relay/mac
node --test otp.test.mjs
node listen.mjs
```

The bot posts the SMS into the chat. A bot cannot read its own posts, so the script signs in as your Telegram account once.

Add `API_ID`, `API_HASH`, and `PHONE` to `mac/.env`. Get the id and hash from https://my.telegram.org under API development tools. Then run `node listen.mjs` and leave it running. Each new code replaces the clipboard. A message with no code is printed so you can copy it from the terminal.
