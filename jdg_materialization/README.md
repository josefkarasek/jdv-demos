# Create Image Stream for PostgreSQL database in 'openshift' namespace
oc create -n openshift -f https://raw.githubusercontent.com/josefkarasek/jdv-demos/master/jdg-datasource/postgres-is.yaml
# Postgresql ephemeral
oc create -f https://raw.githubusercontent.com/openshift/origin/master/examples/db-templates/postgresql-ephemeral-template.json

# Start postgresql
oc new-app --template=postgresql-ephemeral \
-p DATABASE_SERVICE_NAME=testdb-postgresql \
-p POSTGRESQL_USER=testuser \
-p POSTGRESQL_PASSWORD=testpwd \
-p POSTGRESQL_DATABASE=testdb \
-p POSTGRESQL_VERSION=latest
