# xdiff.ncc
A document comparison tool, support PDF, Postscript and AFP.

## Showcase
https://lumpchen.github.io/xdiff.ncc/

Testcase | Control | Test | Report
------------ | ------------- | ------------- | ------------- 
misc | [control.pdf](./src/test/resources/testcases/xdiff/misc/control.pdf) | [test.pdf](./src/test/resources/testcases/xdiff/misc/test.pdf) | [report.html](./src/test/resources/testcases/xdiff/misc/report/report.html)
Content in the first column | Content in the second column | Content in the second column | Content in the second column

Build
-----

You need Java 6 (or higher) and Maven 2 <http://maven.apache.org/> to build PDFBox. The recommended build command is:

    mvn clean install

The default build will compile the Java sources and package the binary classes into jar packages. See the Maven documentation for all the other available build options.
