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
@file:Suppress("TopLevelPropertyNaming")
package tech.alexib.yaba.stubs.json

const val accountsChaseJson = """
[
  {
    "id": "f4dd6752-dc3c-4695-8bb6-afdd0f121cd2",
    "name": "yaba Savings",
    "currentBalance": 500,
    "availableBalance": 500,
    "creditLimit": null,
    "mask": "0654",
    "itemId": "2e632f19-12fc-4469-9ce6-d20a5da7cbd0",
    "type": "DEPOSITORY",
    "subtype": "SAVINGS",
    "hidden": false,
    "institutionName": "Chase"
  },
  {
    "id": "cd935aa7-44c6-464c-aca3-a33ad295673d",
    "name": "yaba 401k",
    "currentBalance": 500,
    "availableBalance": null,
    "creditLimit": null,
    "mask": "0871",
    "itemId": "2e632f19-12fc-4469-9ce6-d20a5da7cbd0",
    "type": "INVESTMENT",
    "subtype": "FOUR_HUNDRED_ONE_K",
    "hidden": false,
    "institutionName": "Chase"
  },
  {
    "id": "6a89d648-d814-4898-8c82-6d857d8982b6",
    "name": "yaba Student Loan",
    "currentBalance": 25000,
    "availableBalance": 25000,
    "creditLimit": null,
    "mask": "6776",
    "itemId": "2e632f19-12fc-4469-9ce6-d20a5da7cbd0",
    "type": "LOAN",
    "subtype": "STUDENT",
    "hidden": false,
    "institutionName": "Chase"
  },
  {
    "id": "4fcdefc5-8564-42e9-ab3c-875f592ad10e",
    "name": "yaba Checking",
    "currentBalance": 500,
    "availableBalance": 500,
    "creditLimit": null,
    "mask": "7471",
    "itemId": "2e632f19-12fc-4469-9ce6-d20a5da7cbd0",
    "type": "DEPOSITORY",
    "subtype": "CHECKING",
    "hidden": false,
    "institutionName": "Chase"
  },
  {
    "id": "1ead2c2c-a7a6-4321-8337-eae2e0c4fb1c",
    "name": "yaba Credit Card",
    "currentBalance": 500,
    "availableBalance": 34361.15,
    "creditLimit": 35000,
    "mask": "7579",
    "itemId": "2e632f19-12fc-4469-9ce6-d20a5da7cbd0",
    "type": "CREDIT",
    "subtype": "CREDIT_CARD",
    "hidden": false,
    "institutionName": "Chase"
  },
  {
    "id": "d0dfecaf-0b8c-4027-b291-c29e534118c6",
    "name": "yaba Auto Loan",
    "currentBalance": 500,
    "availableBalance": null,
    "creditLimit": null,
    "mask": "8770",
    "itemId": "2e632f19-12fc-4469-9ce6-d20a5da7cbd0",
    "type": "LOAN",
    "subtype": "AUTO",
    "hidden": false,
    "institutionName": "Chase"
  }
]
"""

const val accountsWellsJson = """
[
  {
    "id": "840f907b-5460-426f-bc7e-767350fa6381",
    "name": "yaba Select Checking",
    "currentBalance": 500,
    "availableBalance": 500,
    "creditLimit": null,
    "mask": "3146",
    "itemId": "6b37f4ea-b003-40c3-bdee-bc15cb2f3dd6",
    "type": "DEPOSITORY",
    "subtype": "CHECKING",
    "hidden": false,
    "institutionName": "Wells Fargo"
  },
  {
    "id": "dee63172-d1e9-4d01-878f-0e4ab1db6296",
    "name": "yaba Platinum Credit Card",
    "currentBalance": 500,
    "availableBalance": 2986.86,
    "creditLimit": 3500,
    "mask": "5853",
    "itemId": "6b37f4ea-b003-40c3-bdee-bc15cb2f3dd6",
    "type": "CREDIT",
    "subtype": "CREDIT_CARD",
    "hidden": true,
    "institutionName": "Wells Fargo"
  }
]
"""
