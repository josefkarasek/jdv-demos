Demonstration of Rolling update with JDV in OpenShift.
Note that clients can loose connection during update. 
```
# Create Image Stream
cat <<EOF | oc create -n openshift -f -
{
  "kind": "ImageStream",
  "apiVersion": "v1",
  "metadata": {
    "name": "jboss-datavirt63-openshift"
  },
  "spec": {
    "dockerImageRepository": "<docker_repository_url>"
  }
}
EOF

# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/jdv-app-secret.json
oc policy add-role-to-user view system:serviceaccount:$(oc project -q):jdv-service-account -n $(oc project -q)

# Create Template
oc create -f datavirt63-secure-rolling-s2i.json

# Create empty secret. This scenario uses no data source, so secret can be empty.
oc secrets new jdv-app-config empty.env

oc process datavirt63-secure-rolling-s2i \
-v TEIID_USERNAME='teiidUser' \
-v TEIID_PASSWORD='JBoss.123' \
-v SOURCE_REPOSITORY_URL=https://github.com/josefkarasek/jdv-demos.git \
-v SOURCE_REPOSITORY_REF=master \
-v CONTEXT_DIR='rolling_update/v1' | oc create -f -

# Wait for start of 5 replicas
# Test the application

# To start the rolling update edit Build Config to use VDB v2.
# Note that Config Change trigger on Build Config starts a new build only on creation of the BC, but not on change.
oc edit bc jdv-app
oc start-build jdv-app --follow

# Once the build has finished you'll be able to see progress of the rolling update either in the web UI or in terminal.
# Note that connections may fail during rolling update
```