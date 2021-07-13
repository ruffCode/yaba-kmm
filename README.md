![yaba logo](./assets/yaba.png)

# yaba (yet another budgeting app)

## Kotlin Multiplatform Mobile

#### Learning in public

#### An app which uses Plaid's APIs and allows users to track the balances and transactions for any financial institution they choose to link.

#### This is a work in progress and at this time, only the Android app is being developed as I am just starting to learn Swift.

The [backend]((https://github.com/ruffCode/yaba-server)) is using Plaid's sandbox api, so you can
only get fake data.

## Usage

There are two build types you can install, debug and staging, Staging requires you to run the server locally (link below), debug
does not.

Install your preferred flavor

````shell script
./gradlew installSandboxDebug
./gradlew installSandboxStaging 
````

Register with any email address you'd like, the format needs to be valid but nothing will be emailed
to you.

When linking an account, when prompted for a username and password for the financial institution,
the credentials are:

```
username: user_good
password: pass_good
pin: credential_good (when required)
MFA code: 1234 (when required)
```

Please see [Plaid sandbox test credentials](https://plaid.com/docs/sandbox/test-credentials/) for
MFA credentials if the institution you are attempting to link requires them.

## Prior Art

### [tivi by Chris Banes](https://github.com/chisbanes/tivi)

This project is a great example of how to build a large and stable project. I was greatly inspired
by Chris' state management technique and use some of his code, including Flow extensions, observers
and interactors. Credit is given where used.

### [KaMPKit by Touchlab](https://github.com/touchlab/KaMPKit)

Great starter template for KMM. I used the Koin setup logic as well as some Koin extensions.

## 📝 License

```
Copyright © 2021 - Alexi Bre

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
