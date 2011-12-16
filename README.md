# Running the stack locally

## Pre-requisites

(Commands are given assuming an Ubuntu 11.10 machine)

### Rake & Ruby stuff

*Rake* is needed to build the front-end: `sudo apt-get install rake`

You'll need the *haml* Gem to compile `haml` templates: `sudo gem install haml`

*Jammit* is needed to package assets: `sudo gem install jammit`

*FSSM* is needed to monitor changes in the Web front-end files and recompile everything: `sudo gem install fssm`.

### NodeJS

You'll also need the *NodeJS Package Manager* (`npm`) to be able to compile _Less_ CSS files and _Coffee Script_:

```
sudo apt-get install npm
npm install coffee-script
npm install less
```

By default the binaries are installed to `$HOME/bin`. Ensure that this folder is on your `$PATH` !

### MongoDB

The data is stored in *Mongo DB*, so you'll need to install it too: `sudo apt-get install mongodb`.

MongoDB listens to `127.0.0.1` only by default. The Java components are connecting to `localhost`. Depending of your system configuration `localhost` can mean different things. If you encounter connection issues while building the Java components here are a couple of things to try:

* On some Ubuntu systems an alias is set to your hostname on `127.0.1.1` in `/etc/hosts`. If you encounter connections problems, it's the first place to look at. You should be able to safely delete the `127.0.1.1` line in `/etc/hosts`.
* Edit your MongoDB configuration (`/etc/mongodb.conf`) and comment the `bind_ip` line, then restart MongoDB. It should now listen on all interfaces and connection should succeed regardless of the IP address used.

### Java

To build the Java components, you'll need *Maven*: `sudo apt-get install maven2`

## Building the web front-end

Go to the `web-front/` folder and use: `rake`.

## Building the Java components

Go to the root folder, and: `mvn clean install`.

## Running

To start the whole stack: `mvn -f web/pom.xml exec:java -DfrenchTtsUrl=http://localhost/`

(TODO: Document the `frenchTtsUrl` property.)

This will start a webserver on port `8999`. Try [http://localhost:8999/index.html]()

You can also start `rake` on a separate terminal to monitor your changes in the front-end code and re-publish them:

```
cd web-front/
mkdir public
rake watch
```

