# Bamboo secrets updater

Since editing Bamboo secrets has been a PITA for a long time, I have created a script that can do this in minutes.

In order to successfully run it, you would need 3 things:
- the jar or cloned repository
- file with secrets to paste (example below)
- run script file (optional as it can be executed from cmd)

Important notes:
- script will substitute all secrets
- script will stop in the end for you to click the button "Update secrets". It will not click it for you.

Run using cloned repository:  
`gradlew run --args="--email name.surname@example.com --password your_password --path sercret.txt --environment Development --url https://console.forge.com/artifact/example/secrets"`

Run using generated jar file:  
```
#!/bin/bash
 java -jar ./Secreter-beta.jar --email 'your.name@example.com' --password 'your_password' --path sercret.txt --environment Development --url 'https://console.forge.com/artifact/somelink/secrets'
```

Secrets [secrets.txt]
```
bamboo_secret_Development_some_client_id=client_id_value
bamboo_secret_Development_some_client_secret=example_secret
```
Notes:
- blank lines not allowed
- if the key will contain 'bamboo_secret_Development_' it will be removed by script