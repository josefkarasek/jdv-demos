### JBoss Data Grid as a materialization target for JBoss Data Virtualization
```
# Create new project
oc new-project jdv-jdg-materialization

# Create Image Stream for PostgreSQL database in 'openshift' namespace
oc create -n openshift -f https://raw.githubusercontent.com/josefkarasek/jdv-demos/master/jdg-datasource/postgres-is.yaml

# Create templates
# Datavirt secured
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/datavirt63-secure-s2i.json
# Datagrid basic
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datagrid/datagrid65-basic.json
# Postgresql ephemeral
oc create -f https://raw.githubusercontent.com/openshift/origin/master/examples/db-templates/postgresql-ephemeral-template.json

# Start postgresql
oc new-app --template=postgresql-ephemeral \
  -p DATABASE_SERVICE_NAME=postgresql \
  -p POSTGRESQL_USER=testuser \
  -p POSTGRESQL_PASSWORD=testpwd \
  -p POSTGRESQL_DATABASE=testdb \
  -p POSTGRESQL_VERSION=latest

# Init PostgreSQL
oc exec -i <postgresql_pod> -- /bin/sh -i -c 'psql -h 127.0.0.1 -U $POSTGRESQL_USER -q -d $POSTGRESQL_DATABASE' < init.sql

# Create service account with secrets
oc create -f jdv-sa.yaml

# grant view rights on the project
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):datavirt-service-account -n $(oc project -q)

oc new-app --template=datagrid65-basic \
  -p DATAVIRT_CACHE_NAMES=addressbook \
  -p CACHE_NAMES="" \
  -p INFINISPAN_CONNECTORS=hotrod

oc new-app --template=datavirt63-secure-s2i \
  -p SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
  -p CONTEXT_DIR=jdg_materialization/src \
  -p TEIID_USERNAME=teiidUser \
  -p TEIID_PASSWORD=JBoss.123 \
  -p HTTPS_NAME=jboss \
  -p HTTPS_PASSWORD=mykeystorepass \
  -p JGROUPS_ENCRYPT_NAME=secret-key \
  -p JGROUPS_ENCRYPT_PASSWORD=password \
  -p SSO_URL="''" \
  -p SSO_REALM="''" \
  -p SSO_SECRET="''"

# Query JDV
cd client
mvn package exec:java -Dorg.teiid.ssl.trustStore=truststore.ts -Dorg.teiid.ssl.protocol=TLSv1.2 -Dexec.args='<jdbc-jdv-app-route>'

```
