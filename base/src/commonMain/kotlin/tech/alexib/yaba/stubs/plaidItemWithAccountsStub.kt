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
package tech.alexib.yaba.stubs

internal val wellFargoStub = """
{
  "plaidItem": {
    "id": "6b37f4ea-b003-40c3-bdee-bc15cb2f3dd6",
    "plaidInstitutionId": "ins_4",
    "base64Logo": "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",
    "name": "Wells Fargo"
  },
  "accounts": [
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
      "institutionName": "Wells Fargo"
    }
  ]
}
""".trimIndent()

internal val chaseStub = """
{
  "plaidItem": {
    "id": "2e632f19-12fc-4469-9ce6-d20a5da7cbd0",
    "plaidInstitutionId": "ins_4",
    "base64Logo": "iVBORw0KGgoAAAANSUhEUgAAAJgAAACYCAMAAAAvHNATAAAANlBMVEVHcEwOW6f///8IVqUPW6gQXKgRXKkPW6gTYKwPW6gbYqsxcbJ3ocza5vDs8/b0+Pmlwt1Vir96WNmQAAAACnRSTlMA////5HhLoCDGuAtrFAAABQNJREFUeNrt3H9vpCAQBuACIuC6wn7/L3vuXttb8UX5MdjJpVya3D9tngDiqDPz8dE67KiNcc4NgxDDsP7HGD3aj58cVhu3avAYnNE/wbOjSZredObauctCveGuWsAC1aet/6Ja7UTVcF1t1oiGYSxLVjdaO6sLjYZFT9OTIBuTpju2nCAdbmS2irTrOQ6iwxiaJ02LTqNtp9n07lJT7kjtNNtjGdUt3JfMMSvq5Uwvo3osMn+kZLXLaTDp9jyL7rJkBJGgGTKXeCw3teqKXH6VCTKZw/Pl/RM2y8KRnDNHcjk+t1YdTIZJUVycdsCX4rrlK2Fp2WBb11Hcnlu+FibvU/tq4nV8uaphXt5vrTJz4KqfMemTMtNyTny6GmBStsnwef99pLbA5JKSZdwDRnzH/j7qm2ByeSRkY9VBoaYgKWA+LTs9NNAFqUSQkmbG1t9PyFzNBguSDiZ9ItjQxRts42qHJWVj6QYLkhaWCtAOtpk5d5HAEjJTtJCxgQSWCNDG/CtyT6CB4QDNZV+RQEAEwzJ4ZdoJBazdYDBAm2zWzkcuOhgM0MD+t3kuQphHMns+YYlnRzoYDNDM6YS9Avy+MBig2bMJS7hoYUBmjifsK5DuDQNhkD2asLSLGuZ3MnMwYQcuahiYM5s+9NdA2l8G24WOOnWXVIfvcuhhcYDmEiv5HuBfA4vDIAtX8uXyJ7DHnXg88FoOyUAaw8REPjahLAwQg8yArSFL0b+/v5L8UYmA0aQD6RSs7zC7lVx3te8CU2dDgLW04A0FMWwKx2OO/qiNt1gnmLqdrUP0MWCMt1g32FJ2/Jv4sPg5mAwq3mRWsIDdp3iTaY4wHYVibGAmiizYwFx0o2QDe+5+wREmoliMD8xuQws+sHEb7vOB6e2DGx+Y4QtzPGHuF1YMG3jCBr4wwRMm+MJ+91gp7Pcc+29gv9FFKYxtoMg2tGb7MML28Y3vAy/bVwRsX6qwfQ3F9sUdk1edG9jA5+XwFmbi1+mKCWyMP0BkfO+7BGb3H98CB9gAvtRP890fjqUTbJn2H7m2eWPT7XjUfOLK+DLyPmMj/JAqSr6TkcHeM0OG/jU+2bDNBtHwY70iHxmwbe6FRekN6jETj+fH/RPY1uVwQsitrIAmK3XhBBZlhGicQnOSdtEBFmeq2FTSEbHsDBYnEpuDNK1wHczvknXtUWJbuG7GZnWcC7hN71HhKtiszrInzVY2XwML6jTf1J7kWHeBhdPcSZANOPvusH1as8lIacb5uZSwkJfSvLuVE8mSMJBsrfPS5svqTkthwOVyCw1IZBDmYQr4mFuaoRTBLR3PWGZqerKYhUAGYagK7qhmahT0MgSD1XljWcFUswzAoEuXlpg9AzRPCoNPgK6iKK8tQNvBYF3eeSXvKIhlMQzXC45VhZ+qJUCLYLhaUNeWyjYEaFsYdjUU8dbLtrC5wYVrzapDx3cYrmBsLRSf606Nd1irK1FoObfCoGsoa0YAa3mrArR/sADnq7SDCZYtDbDQuo5H12aF7AsG6xapWoSoCtknDNagVrnw+7zyYOMF87DdRXXvHtgep1D2Kn65IVdLtyN0cRbO2WvLT8Dl2vpD6TbZkuw/09zsCyynmkrec8IxEHT6svDqzH8zDK9GmrZt1Y3RUj1U6Do+Mm0lx7j5Ht92hYwbPDJuicm4iSjjtqucG9Vybu37vagMmyG/z12X9tF/APITr9CCbDsMAAAAAElFTkSuQmCC",
    "name": "Chase"
  },
  "accounts": [
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
}
""".trimIndent()
