#! /bin/sh

MAIN_FILE="presentation"

# pdflatex compile flags
FLAGS=""

# set and create output directory
OUTPUT_DIR=latex_output
mkdir -p $OUTPUT_DIR

# compile twice when successful once
if pdflatex $FLAGS -output-directory="$OUTPUT_DIR" "$MAIN_FILE.tex" && pdflatex $FLAGS -output-directory="$OUTPUT_DIR" "$MAIN_FILE.tex"
then
    # move .pdf to main directory
    mv "$OUTPUT_DIR/$MAIN_FILE.pdf" .
fi

