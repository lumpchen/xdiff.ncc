# xdiff.ncc
A PDL file comparison tool, support comparison of PDF, Postscript and AFP documents. xdiff.ncc not only compares rendering bitmap of each page, but also analyzes and compares all contents in each page. For [detail](https://lumpchen.github.io/xdiff.ncc/), see showcase below.
![xdiff sample 0](./docs/web/thumb-0.png){:height="24px" width="48px"}


## Showcase
https://lumpchen.github.io/xdiff.ncc/

Testcase | Control | Test | Report
------------ | ------------- | ------------- | ------------- 
misc | [control.pdf](./src/test/resources/testcases/xdiff/misc/control.pdf) | [test.pdf](./src/test/resources/testcases/xdiff/misc/test.pdf) | [report.html](./src/test/resources/testcases/xdiff/misc/report/report.html)
form control | [control.pdf](./src/test/resources/testcases/xdiff/annot/form_control/control.pdf) | [test.pdf](./src/test/resources/testcases/xdiff/annot/form_control/test.pdf) | [report.html](./src/test/resources/testcases/xdiff/annot/form_control/report/report.html)

Build
-----

You need Java JDK 7 (or higher) and Maven 3 <http://maven.apache.org/> to build xdiff.ncc. The recommended build command is:

    mvn clean install

The default build will compile the Java sources and package the binary classes into jar packages. See the Maven documentation for all the other available build options.

Binary download: 
-----
[xdiff.jar](./dst/), download both xdiff.jar and config.properties. xdiff.jar is an excutable java program, see detail in usage section.

Usage
-----
    Usage: java -jar xdiff.jar [options] <baseline-pdf> <test-pdf> <result-folder>

    Options:
      -config                            : Comparison configuration file path.

For Postscript file comparion, xdiff.ncc need [GhostScript](https://www.ghostscript.com/download/gsdnld.html) installed in advance, xdiff.ncc will use it as a Postscript to PDF convertor. Set GS_Path in config.properties:
    
    GS_Path = C:/Program Files/gs/gs9.18/bin/gswin64c.exe

Resource
-----
xdiff.ncc uses PDFBox for PDF interpreting and rendering. PDFBox is a project of the Apache Software Foundation <http://www.apache.org/>. xdiff.ncc is based on PDFBox 2.0.7.

Support
-----
lumpchen@gmail.com 

License
-----
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
