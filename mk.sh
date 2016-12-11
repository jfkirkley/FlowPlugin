rm -rf tmp
mkdir tmp
  cd tmp
  unzip  ../FlowPlugin.zip 
  mkdir fjar
  cd fjar
  jar xvf ../FlowPlugin/lib/FlowPlugin.jar 
  rm -rf biz com org/
  cp -a ../../out/production/FlowPlugin/org ../../out/production/FlowPlugin/com/ ../../out/production/FlowPlugin/biz/  ../../out/production/FlowPlugin/aspectj/ .
  jar cvf FlowPlugin.jar com/ org/ biz/ META-INF/ aspectj/
  mv FlowPlugin.jar ../FlowPlugin/lib/
  cd ..
  rm -f FlowPlugin/lib/FlowPlugin1.jar 
  zip -r FlowPlugin.zip FlowPlugin/

