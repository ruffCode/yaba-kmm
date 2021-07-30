/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.alexib.yaba.data.domain.stubs

const val accountsJson = """
    [
    {
      "id": "228021f2-7fbc-4929-9c36-01e262c1e858",
      "name": "Plaid Checking",
      "currentBalance": 110,
      "availableBalance": 100,
      "creditLimit": null,
      "mask": "0000",
      "itemId": "4ea99dc5-db41-433e-afa2-db7e0b54bf95",
      "type": "DEPOSITORY",
      "subtype": "CHECKING",
      "hidden": false
    },
    {
      "id": "fcf23dcf-ff10-40a4-93c0-5af4add2e07b",
      "name": "Plaid Saving",
      "currentBalance": 210,
      "availableBalance": 200,
      "creditLimit": null,
      "mask": "1111",
      "itemId": "4ea99dc5-db41-433e-afa2-db7e0b54bf95",
      "type": "DEPOSITORY",
      "subtype": "SAVINGS",
      "hidden": false
    },
    {
      "id": "6353544f-9e4f-4040-861e-b59eea721d27",
      "name": "Plaid CD",
      "currentBalance": 1000,
      "availableBalance": null,
      "creditLimit": null,
      "mask": "2222",
      "itemId": "4ea99dc5-db41-433e-afa2-db7e0b54bf95",
      "type": "DEPOSITORY",
      "subtype": "CD",
      "hidden": false
    },
    {
      "id": "50886f79-b633-4c37-925d-89d69ba6046c",
      "name": "Plaid Credit Card",
      "currentBalance": 410,
      "availableBalance": null,
      "creditLimit": 2000,
      "mask": "3333",
      "itemId": "4ea99dc5-db41-433e-afa2-db7e0b54bf95",
      "type": "CREDIT",
      "subtype": "CREDIT_CARD",
      "hidden": false
    },
    {
      "id": "e8d05ad8-de28-4f6a-9963-8adae779297e",
      "name": "Plaid Money Market",
      "currentBalance": 43200,
      "availableBalance": 43200,
      "creditLimit": null,
      "mask": "4444",
      "itemId": "4ea99dc5-db41-433e-afa2-db7e0b54bf95",
      "type": "DEPOSITORY",
      "subtype": "MONEY_MARKET",
      "hidden": false
    },
    {
      "id": "a6b00f29-42cf-4245-b2a9-cef76e0b4c6a",
      "name": "Plaid IRA",
      "currentBalance": 320.76,
      "availableBalance": null,
      "creditLimit": null,
      "mask": "5555",
      "itemId": "4ea99dc5-db41-433e-afa2-db7e0b54bf95",
      "type": "INVESTMENT",
      "subtype": "IRA",
      "hidden": false
    },
    {
      "id": "2c9c536a-70e7-4675-b9c4-1657859d06cc",
      "name": "Plaid Mortgage",
      "currentBalance": 56302.06,
      "availableBalance": null,
      "creditLimit": null,
      "mask": "8888",
      "itemId": "4ea99dc5-db41-433e-afa2-db7e0b54bf95",
      "type": "LOAN",
      "subtype": "MORTGAGE",
      "hidden": false
    },
    {
      "id": "c8e92ce1-4291-4f5e-ad51-64a90262c497",
      "name": "Plaid 401k",
      "currentBalance": 23631.9805,
      "availableBalance": null,
      "creditLimit": null,
      "mask": "6666",
      "itemId": "4ea99dc5-db41-433e-afa2-db7e0b54bf95",
      "type": "INVESTMENT",
      "subtype": "FOUR_HUNDRED_ONE_K",
      "hidden": false
    },
    {
      "id": "7f7d3e73-7dd2-41f9-805d-fed8726149b6",
      "name": "Plaid Student Loan",
      "currentBalance": 65262,
      "availableBalance": null,
      "creditLimit": null,
      "mask": "7777",
      "itemId": "4ea99dc5-db41-433e-afa2-db7e0b54bf95",
      "type": "LOAN",
      "subtype": "STUDENT",
      "hidden": false
    }
  ]
"""
