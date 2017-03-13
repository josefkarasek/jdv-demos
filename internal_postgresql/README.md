Simple demo that demonstrates how to deploy JDV with an internal PostgreSQL.
```
# Create new project
oc new-project jdv-internal

# Create Image Stream for PostgreSQL database in 'openshift' namespace.
cat <<EOF | oc create -n openshift -f -
{
  "kind": "ImageStream",
  "apiVersion": "v1",
  "metadata": {
    "name": "postgresql"
  },
  "spec": {
    "dockerImageRepository": "registry.access.redhat.com/openshift3/postgresql-92-rhel7"
  }
}
EOF

# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/datavirt-app-secret.yaml
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):datavirt-service-account -n $(oc project -q)

# Create templates
# JDV secured
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/datavirt63-secure-s2i.json
# Postgresql ephemeral
oc create -f https://raw.githubusercontent.com/openshift/origin/master/examples/db-templates/postgresql-ephemeral-template.json

# Start postgresql
oc new-app --template=postgresql-ephemeral \
  -p DATABASE_SERVICE_NAME=testdb-postgresql \
  -p POSTGRESQL_USER=testuser \
  -p POSTGRESQL_PASSWORD=testpwd \
  -p POSTGRESQL_DATABASE=testdb \
  -p POSTGRESQL_VERSION=latest
# Init postgres
oc exec -i <postgresql_pod> -- /bin/sh -i -c 'psql -h 127.0.0.1 -U $POSTGRESQL_USER -q -d $POSTGRESQL_DATABASE' < init.sql

# Create secret with configuration
oc secrets new datavirt-app-config datasources.env

# Process the template
oc new-app --template=datavirt63-secure-s2i \
  -p TEIID_USERNAME='teiidUser' \
  -p TEIID_PASSWORD='JBoss.123' \
  -p SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
  -p SOURCE_REPOSITORY_REF=master \
  -p CONTEXT_DIR='internal_postgresql/vdb'

# Query JDV
cd client
mvn package exec:java -Dorg.teiid.ssl.trustStore=truststore.ts -Dorg.teiid.ssl.protocol=TLSv1.2 -Dexec.args='<jdbc-jdv-app-route>'
```
