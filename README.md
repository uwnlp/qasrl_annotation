# qasrl_annotation
Generating Annotation Spreadsheet for QA-SRL Scheme

## Build

ant build

## Generate Excel spreadsheet for annotation

java -cp bin:libs/* edu.uw.qasrl_annotation.main.AnnotationSheetGenerator

or 

java -cp bin:libs/* edu.uw.qasrl_annotation.main.AnnotationSheetGenerator -input input.txt -output output.xlsx

See sample/input.txt for input file format.

## Dependencies:

args4j-2.0.10.jar

poi-3.11-20141221.jar

poi-ooxml-3.11-20141221.jar

poi-ooxml-schemas-3.11-20141221.jar

trove-3.0.3.jar

xmlbeans-2.6.0.jar
