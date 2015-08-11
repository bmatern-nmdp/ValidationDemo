# This demo is a walkthrough on how to set up a Docker image

# This script is derived from the (excellent) tutorial on docker's website, here:
# https://docs.docker.com/mac/started/
# https://docs.docker.com/windows/started/

# You must have Docker Toolbox installed in order to run this example on Mac and Windows.
# Linux may be able to run this natively, but I haven't tested that.

# 1) Make an account on (https://hub.docker.com/)
# 2) Download + Install Docker Toolbox (https://www.docker.com/toolbox)

read -p "***Press [Enter] to list your local images"
docker images

read -p "***Press [Enter] to run the hello-world image"
docker run hello-world

read -p "***Press [Enter] to list your local images"
docker images

read -p "***Press [Enter] to run the whalesay image, piping in a lame Finding Nemo quote"
docker run docker/whalesay cowsay SHARKBAIT, HOO HAH HAH!

read -p "***Press [Enter] to list your local images"
docker images

read -p "***Press [Enter] to build our own new image called docker-whale, using docker/whalesay as a base and installing the fortunes program"
cat dockerfile
docker build -t docker-whale .

read -p "***Press [Enter] to list your local images"
docker images

read -p "***Press [Enter] to run the shiny new image we just made"
docker run docker-whale
read -p "***Press [Enter] to do that again, because that was really fun"
docker run docker-whale

read -p "***Press [Enter] to delete all local images"
docker rmi -f $(docker images -q)