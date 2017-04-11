### JBoss Data Grid as datasource for JBoss Data Virtualization with authentication

```
# Create new project
oc new-project jdv-jdg-datasource

# Create templates
# Datavirt secured
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/datavirt63-secure-s2i.json
# Datagrid basic with authentication enabled
oc create -f ./datagrid65-basic-auth.json

# Create service account with secrets
oc create -f jdv-sa.yaml

# grant view rights on the project
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):default -n $(oc project -q)

oc new-app --template=datagrid65-basic-auth \
  -p CACHE_NAMES=ADDRESSBOOK \
  -p INFINISPAN_CONNECTORS=hotrod \
  -p HOTROD_AUTHENTICATION=true \
  -p USERNAME=jdg \
  -p PASSWORD=JBoss.123 \
  -p CACHE_ROLES=admin \
  -p CONTAINER_SECURITY_ROLE_MAPPER=identity-role-mapper \
  -p CONTAINER_SECURITY_ROLES='admin=ALL'

oc new-app --template=datavirt63-secure-s2i \
  -p SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
  -p CONTEXT_DIR=jdg_datasource_auth/src \
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
