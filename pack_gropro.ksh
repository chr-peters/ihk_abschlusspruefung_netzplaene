#! /bin/ksh

# name of the created zip file
ZIP_NAME='GroPro_ChristianPeters'

# path to the documentation
DOC_PATH='doc/latex_output/Master.pdf'

# path to the executable
JAR_PATH='dist/netzplanerstellung-1.0.0.jar'

# create temporary folder
TMP_NAME=$ZIP_NAME
mkdir $TMP_NAME

# create the required structure inside the temporary folder
cp build.xml $TMP_NAME
cp run_testcases.ksh $TMP_NAME
cp $DOC_PATH $TMP_NAME
cp -r src $TMP_NAME
cp -r testcases $TMP_NAME
cp $JAR_PATH $TMP_NAME

# create the zip file
zip -r $ZIP_NAME'.zip' $TMP_NAME

# remove the temporary folder
rm -rf $TMP_NAME
