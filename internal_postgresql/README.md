# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/jdv-app-secret.json

# Create templates
# JDV secured
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/jdv63-secure-s2i.json
# Postgresql ephemeral
oc create -f https://raw.githubusercontent.com/openshift/origin/master/examples/db-templates/postgresql-ephemeral-template.json

# Start postgresql
oc new-app --template=postgresql-ephemeral -p DATABASE_SERVICE_NAME=testdb-postgresql -p POSTGRESQL_USER=testuser -p POSTGRESQL_PASSWORD=testpwd -p POSTGRESQL_DATABASE=testdb -p POSTGRESQL_VERSION=latest

oc exec -i testdb-postgresql-1-plp9n -- /bin/sh -i -c 'psql -h 127.0.0.1 -U $POSTGRESQL_USER -q -d $POSTGRESQL_DATABASE' < init.sql