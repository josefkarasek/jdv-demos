Simple demo that demonstrates how to deploy JDV with an internal PostgreSQL.
```
# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/jdv-app-secret.json

# Create templates
# JDV secured
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/jdv63-secure-s2i.json
# Postgresql ephemeral
oc create -f https://raw.githubusercontent.com/openshift/origin/master/examples/db-templates/postgresql-ephemeral-template.json

# Start postgresql
oc new-app --template=postgresql-ephemeral -p DATABASE_SERVICE_NAME=testdb-postgresql -p POSTGRESQL_USER=testuser -p POSTGRESQL_PASSWORD=testpwd -p POSTGRESQL_DATABASE=testdb -p POSTGRESQL_VERSION=latest

oc exec -i <postgresql_pod> -- /bin/sh -i -c 'psql -h 127.0.0.1 -U $POSTGRESQL_USER -q -d $POSTGRESQL_DATABASE' < init.sql

# Create secret with configuration
oc secrets new jdv-app-config datasources.env

# Process the template
oc process jdv63-secure-s2i \
-v TEIID_USERNAME='teiidUser' \
-v TEIID_PASSWORD='JBoss.123' \
-v SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
-v SOURCE_REPOSITORY_REF=master \
-v CONTEXT_DIR='internal_postgresql/vdb' | oc create -f -

```