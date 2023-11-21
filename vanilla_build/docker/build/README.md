
### Docker

docker build -t bpmconseil/v1_vanilla-webapps -f docker/build/webapps/Dockerfile .
docker build -t bpmconseil/v1_vanilla-runtime -f docker/build/runtime/Dockerfile .

// This image is used to populate volumes in openshift as it does not work like docker
docker build -t bpmconseil/v1_vanilla-init-openshift -f docker/build/init-openshift/Dockerfile .

docker push bpmconseil/v1_vanilla-webapps
docker push bpmconseil/v1_vanilla-runtime
docker push bpmconseil/v1_vanilla-init-openshift

## Openshift

//TODO: Create an image to use for initContainers for OS with vanilla_files and db init files

//To Check logs from initcontainer

oc login
oc get pods
oc logs xxxx
oc logs xxxx -c init-mydb

//To update an imagestream (par exemple vanilla-runtime)

oc login
sudo docker pull bpmconseil/v1_vanilla-runtime:latest
oc describe is/vanilla-runtime
oc tag docker.io/bpmconseil/v1_vanilla-runtime:latest vanilla-runtime:latest


## Troubleshoot

If you get /docker-entrypoint.sh "No such file or directory" check the line endings (must be linux)