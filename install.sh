

echo 'Builds and installs into $HOME/bin the "explainer" program (and jar)'

# make the jar
clj -T:build jar

# put it in the big dir
mv target/explainer-standalone.jar ~/bin/

# create shell script for running the jar.
echo 'java -jar $HOME/bin/explainer-standalone.jar $*' > ~/bin/explainer

chmod a+x ~/bin/explainer

