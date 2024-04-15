# Development notes

## Exporting the Keycloak realm

The Keycloak realm is exported to the `keycloak-realm.json` file. It is used to configure the Keycloak server.

To export the realm, run the following command:

```shell
docker exec -it your-rents-api-keycloak-1 /opt/keycloak/bin/kc.sh \
  export \
  --file /opt/keycloak/data/import/your-rents-realm.json \
  --users realm_file \
  --realm your-rents \
  --db=postgres \
  --db-url=jdbc:postgresql://postgres_keycloak:5432/keycloak \
  --db-username=keycloak \
  --db-password=keycloak
```

See this  article [Export and import keycloak realm with users](https://simonscholz.github.io/tutorials/keycloak-realm-export-import#exporting-a-keycloak-realm-including-users-and-roles) for more information.
