asciidoctor index.adoc -o index.html -a stylesheet=golo.css
pushd ..
./gradlew javadoc && mkdir -p docs/apidocs && cp -r build/docs/javadoc/* docs/apidocs
popd