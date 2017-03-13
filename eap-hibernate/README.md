### Hibernate on top of teiid
This directory contains instructions on how to deploy [hibernate-on-top-of-teiid](https://github.com/teiid/teiid-quickstarts/tree/master/hibernate-on-top-of-teiid) into OpenShift 3.
It is expected that [datafederation](https://github.com/josefkarasek/jdv-demos/tree/master/datafederation) quickstart has been deployed in the `jdv-datafederation` project.
```
# Go to the jdv-datafederation project
oc project jdv-datafederation

# Create Service Account
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/secrets/eap-app-secret.json

# Add and link config secret
oc secrets new eap-app-config datasources.env
oc secret link eap-service-account eap-app-config

# Create Template
oc create -f https://raw.githubusercontent.com/jboss-openshift/application-templates/master/datavirt/eap64-third-party-db-s2i.json

# Process template
oc new-app --template=eap64-third-party-db-s2i
```
This quickstart comes with a web UI, where you can query teiid and create new items.
