# Crypto Currency Price Change Notification Bot
The bot allows to notify about the cryptocurrency price change.

## Configuration
Configuration file is located [here](src/main/resources/application.yml).

### Database configuration
First of all, the bot requires mongodb to work. The startup guide is present in [here](provisioning/mongodb).
Names of properties speak for themselves:
- spring.data.mongodb.host: change to ip address of you database server
- spring.data.mongodb.port: change to port of your database server
- spring.data.mongodb.database: change to name of your database
- spring.data.mongodb.username: change username of user to that which has access to database
- spring.data.mongodb.password: change password to password of user having access to database

### Application configuration
The application has various properties to configure how it will execute:
- demo.delay-seconds: this property sets the delay between application checks of crypto changes and user notification. Set this value min to 1 second.
- demo.percent-to-notify: this property defines the percentage the cryptocurrency must change by to be notified about.
- demo.bot.token: this property is the token of bot you receive when register your bot using `BotFather` in Telegram.
- demo.bot.username: this property is unique username of bot you receive when register your bot using `BotFather` in Telegram.
- demo.bot.max-users: this property defines the maximum amount of users allowed to simultaneously use bot
