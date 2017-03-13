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
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/datavirt-app-secret.yaml

# Create templates
# JDV secured
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/datavirt63-secure-s2i.json
# By default the quickstart configuration uses 'derby' data source,
# to use postgresql add this variable to deployment config in
# template
oc edit template datavirt63-secure-s2i
# copy-paste:
          - name: QS_DB_TYPE
            value: postgresql

# Add your specific configuration to datasources.env file:
# TESTDB_POSTGRESQL_SERVICE_HOST=<your_server>
# Create secret with configuration
oc secrets new datavirt-app-config datasources.env

# Process the template
oc new-app --template=datavirt63-secure-s2i \
	-p TEIID_USERNAME='teiidUser' \
	-p TEIID_PASSWORD='JBoss.123' \
	-p SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
	-p SOURCE_REPOSITORY_REF=master \
	-p CONTEXT_DIR='external_postgresql/vdb'

# Query JDV
cd client
mvn package exec:java -Dorg.teiid.ssl.trustStore=truststore.ts -Dorg.teiid.ssl.protocol=TLSv1.2 -Dexec.args='<jdbc-jdv-app-route>'
```
