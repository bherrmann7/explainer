
echo Builds and installs into your $HOME/bin the "explainer" program

clj -T:build jar
mv target/explainer-standalone.jar ~/bin/
echo 'java -jar $HOME/bin/explainer-standalone.jar $*' > ~/bin/explainer
chmod a+x ~/bin/explainer
