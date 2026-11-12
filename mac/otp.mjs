const digit = {
  zero: "0",
  one: "1",
  two: "2",
  three: "3",
  four: "4",
  five: "5",
  six: "6",
  seven: "7",
  eight: "8",
  nine: "9",
}

export function otpFromSms(text) {
  try {
    const source = String(text ?? "")
    const numeric = source.match(/\b(\d{4,8})\b/)
    if (numeric) return numeric[1]
    const match = source.match(/prompted\s+([A-Za-z_]+)/i)
    if (!match) return ""
    const words = match[1].split("_").filter(Boolean)
    const code = words.map((word) => digit[word.toLowerCase()] ?? "").join("")
    return code.length === words.length && /^\d+$/.test(code) ? code : ""
  } catch {
    return ""
  }
}
