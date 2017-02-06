### JBoss Data Grid as datasource for JBoss Data Virtualization
```
# Create new project
oc new-project jdv-jdg-datasource

# Create templates
# Datavirt secured
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/datavirt63-secure-s2i.json
# Datagrid basic
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datagrid/datagrid65-basic.json

# Create service account with secrets
oc create -f jdv-sa.yaml

oc new-app --template=datagrid65-basic \
  -p CACHE_NAMES=addressbook \
  -p INFINISPAN_CONNECTORS=hotrod

oc new-app --template=datavirt63-secure-s2i \
  -p SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
  -p CONTEXT_DIR=jdg_datasource/src \
  -p TEIID_USERNAME=teiidUser \
  -p TEIID_PASSWORD=JBoss.123 \
  -p HTTPS_NAME=jboss \
  -p HTTPS_PASSWORD=mykeystorepass \
  -p JGROUPS_ENCRYPT_NAME=secret-key \
  -p JGROUPS_ENCRYPT_PASSWORD=password

# Query JDV
cd client
mvn package exec:java -Dorg.teiid.ssl.trustStore=truststore.ts -Dexec.args='<jdbc-jdv-app-route>'
```
