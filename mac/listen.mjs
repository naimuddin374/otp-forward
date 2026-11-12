import { spawn } from "node:child_process"
import { existsSync, readFileSync, writeFileSync } from "node:fs"
import { createInterface } from "node:readline/promises"
import { dirname, join } from "node:path"
import { stdin, stdout } from "node:process"
import { fileURLToPath } from "node:url"
import { TelegramClient } from "telegram"
import { StringSession } from "telegram/sessions/index.js"
import { NewMessage } from "telegram/events/index.js"
import { otpFromSms } from "./otp.mjs"

const here = dirname(fileURLToPath(import.meta.url))

function loadEnv(path) {
  const env = {}
  for (const line of readFileSync(path, "utf8").split("\n")) {
    const trimmed = line.trim()
    if (!trimmed || trimmed.startsWith("#")) continue
    const separator = trimmed.indexOf("=")
    if (separator === -1) continue
    env[trimmed.slice(0, separator)] = trimmed.slice(separator + 1)
  }
  return env
}

function copyToClipboard(code) {
  return new Promise((resolve, reject) => {
    const copy = spawn("pbcopy")
    copy.on("error", reject)
    copy.on("close", (status) => {
      if (status === 0) resolve()
      else reject(new Error(`pbcopy exited ${status}`))
    })
    copy.stdin.end(code)
  })
}

const copiedIds = new Set()

async function take(message) {
  try {
    const id = String(message?.id ?? "")
    if (id && copiedIds.has(id)) return false
    const text = message?.message || ""
    const code = otpFromSms(text)
    if (!code) {
      if (text.trim()) console.log(text)
      return false
    }
    await copyToClipboard(code)
    if (id) copiedIds.add(id)
    console.log(`Copied ${code}`)
    return true
  } catch (error) {
    console.error(error?.message || error)
    return false
  }
}

const env = loadEnv(join(here, ".env"))
const token = env.TELEGRAM_BOT_TOKEN || ""
const apiId = Number(env.API_ID)
const apiHash = env.API_HASH || ""
if (!token || !apiId || !apiHash) {
  console.error(`The bot can post the SMS into the chat, but getUpdates never receives a message the bot sent itself.

Stop this script, then add these lines to mac/.env:

API_ID=the number from https://my.telegram.org
API_HASH=the hash from that same page
PHONE=+8801...

Create them at https://my.telegram.org with the Telegram account you already use: API development tools, then Create new application. Restart with: node listen.mjs`)
  process.exit(1)
}

const sessionPath = join(here, ".session")
const saved = existsSync(sessionPath) ? readFileSync(sessionPath, "utf8").trim() : ""
const client = new TelegramClient(new StringSession(saved), apiId, apiHash, {
  connectionRetries: 5,
})
const prompt = createInterface({ input: stdin, output: stdout })

await client.start({
  phoneNumber: async () => env.PHONE || prompt.question("Phone number: "),
  phoneCode: async () => prompt.question("Telegram login code: "),
  password: async () => prompt.question("Two-step password, or press Enter: "),
  onError: (error) => console.error(error.message || error),
})
prompt.close()
writeFileSync(sessionPath, client.session.save())

const botId = token.split(":")[0]
const dialogs = await client.getDialogs({ limit: 200 })
const botDialog = dialogs.find((dialog) => String(dialog.id) === botId)
if (!botDialog) {
  console.error("Open the bot chat in Telegram, then run this again.")
  process.exit(1)
}
const history = await client.getMessages(botDialog.entity, { limit: 20 })
const newest = history[0]
if (newest && !otpFromSms(newest.message || "")) {
  const text = (newest.message || "").trim()
  if (text) console.log(text)
}
const latest = history.find((message) => otpFromSms(message.message || ""))
if (latest) await take(latest)

console.log("Watching for the next OTP. Each new one replaces the clipboard. Leave this running.")
client.addEventHandler(async (event) => {
  try {
    await take(event.message)
  } catch (error) {
    console.error(error?.message || error)
  }
}, new NewMessage({ incoming: true, chats: [botDialog.id] }))
await new Promise(() => {})
