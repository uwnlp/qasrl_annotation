# qasrl_annotation
Generating Annotation Spreadsheet for QA-SRL Scheme

## Build

ant build

## Generate Excel spreadsheet for annotation

java -cp bin:libs/* edu.uw.qasrl_annotation.main.AnnotationSheetGenerator

or 

java -cp bin:libs/* edu.uw.qasrl_annotation.main.AnnotationSheetGenerator -input input.txt -output output.xlsx

See sample/input.txt for input file format.
