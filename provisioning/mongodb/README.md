## Running MongoDB

1. For building mongodb docker image, run the following command:

```bash
./build.sh
```

2. For running mongodb docker image, run the following command:

```bash
./run-daemon.sh
```

## Securing MongoDB

- First start DB with the configuration:

```yaml
security:
authorization: disabled
```

1. 1. Check docker container by the following command:

```bash
docker ps
```

2. Connect to the mongodb docker container by the following command:

```bash
docker exec -it [container id] bash
```

3. Connect to the mongo shell by the following command:

```bash
mongo
```

4. Add new Users to the MongoDB:

```shell

use crypto_bot
db.createUser(
    {
        user: "crypto-bot-db-user",
        pwd: "change_me",
        roles: [ { role: "readWrite", db: "crypto_bot"}]
    }
)
```

2. Rebuild MongoDB container with updated config.

3. For connection to the mongo shell use:

```
mongo
use crypto_bot
db.auth("crypto-bot-db-user", "change_me")
```
