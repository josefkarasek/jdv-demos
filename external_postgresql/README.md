Simple demo that demonstrates how to deploy JDV with an external PostgreSQL.
```
# Create new project
oc new-project jdv-external

# Start PostgreSQL server outside of OpenShift.
docker run -d -p 32769:5432 \
	-e "POSTGRESQL_USERNAME=testuser" \
	-e "POSTGRESQL_SHARED_BUFFERS=16MB" \
	-e "POSTGRESQL_PASSWORD=testpwd" \
	-e "POSTGRESQL_MAX_PREPARED_TRANSACTIONS=90" \
	-e "POSTGRESQL_MAX_CONNECTIONS=100" \
	-e "POSTGRESQL_DATABASE=testdb" \
	-e "POSTGRESQL_USER=testuser" \
	registry.access.redhat.com/openshift3/postgresql-92-rhel7:latest  
# Init PostgreSQL
sudo docker exec -i <pod_id> /bin/sh -i -c 'psql -h 127.0.0.1 -U $POSTGRESQL_USER -q -d $POSTGRESQL_DATABASE' < init.sql

# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/datavirt-app-secret.json

# Create templates
# JDV secured
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/datavirt63-secure-s2i.json

# Create secret with configuration
oc secrets new datavirt-app-config datasources.env

# Process the template
oc process datavirt63-secure-s2i \
	-v TEIID_USERNAME='teiidUser' \
	-v TEIID_PASSWORD='JBoss.123' \
	-v SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
	-v SOURCE_REPOSITORY_REF=master \
	-v CONTEXT_DIR='external_postgresql/vdb' | oc create -f -

# Query JDV
cd client
mvn package exec:java -Dexec.args='<jdbc-jdv-app-route>' -Dorg.teiid.ssl.trustStore=truststore.ts
```