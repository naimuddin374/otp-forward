import assert from "node:assert/strict"
import test from "node:test"
import { otpFromSms } from "./otp.mjs"

test("copies the numeric bKash OTP", () => {
  const text =
    "228650 is your OTP for BKASH Fund Transfer and is valid for next 2 minutes. DO NOT SHARE your OTP with ANYONE."
  assert.equal(otpFromSms(text), "228650")
})

test("converts the first IVAC sample", () => {
  const text =
    "(IVACBD) For security, type the following sequence when prompted Eight_Two_Nine_Six_One_Five ."
  assert.equal(otpFromSms(text), "829615")
})

test("converts the second IVAC sample", () => {
  const text =
    "(IVACBD) For security, type the following sequence when prompted Nine_Zero_Zero_Two_Three_Zero ."
  assert.equal(otpFromSms(text), "900230")
})

test("returns nothing when a keyword message has no code", () => {
  assert.equal(otpFromSms("Your OTP could not be read."), "")
})

test("ignores a message that is not an OTP", () => {
  assert.equal(otpFromSms("Hi"), "")
})
