#! /bin/ksh

# die Standard-Konfiguration
PROGRAMM="./netzplanerstellung-1.0.0.jar"
INPUT_ORDNER="./testcases"
OUTPUT_ORDNER="./testcases"

# ueberpruefe, ob der Benutzer ein anderes Programm angegeben hat
if ! [ -z "$1" ]
then
    PROGRAMM=$1
fi

# teste, ob die Programmdatei auch existiert
if ! [ -f "$PROGRAMM" ]
then
    echo "Das Programm $PROGRAMM konnte nicht gefunden werden."
    echo "Verwendung: ./run_testcases.ksh <programm> <input-ordner> <output-ordner>"
    exit 1
fi

# ueberpruefe, ob der Benutzer einen anderen Input-Ordner angegeben hat
if ! [ -z "$2" ]
then
    INPUT_ORDNER=$2
fi

# teste, ob der Ordner auch existiert
if ! [ -d "$INPUT_ORDNER" ]
then
    echo "Der Input-Ordner $INPUT_ORDNER konnte nicht gefunden werden."
    echo "Verwendung: ./run_testcases.ksh <programm> <input-ordner> <output-ordner>"
    exit 1
fi

# ueberpruefe, ob der Benutzer einen anderen Output-Ordner angegeben hat
if ! [ -z "$3" ]
then
    OUTPUT_ORDNER=$3
fi

# teste, ob der Ordner auch existiert
if ! [ -d "$OUTPUT_ORDNER" ]
then
    echo "Der Output-Ordner $OUTPUT_ORDNER konnte nicht gefunden werden."
    echo "Verwendung: ./run_testcases.ksh <programm> <input-ordner> <output-ordner>"
    exit 1
fi

# Farbe in der Ausgabe
GRUEN='\033[0;32m'
HELLROT='\033[1;31m'
HELLGRUEN='\033[1;32m'
NC='\033[0m' # ende farbige Ausgabe

# gebe dem Benutzer Informationen vor dem Durchlauf
echo -e "${HELLGRUEN}run_testcases.ksh${NC}"
echo -e "${HELLGRUEN}=================${NC}"
echo
echo "Beginne Abarbeitung der Testfaelle mit den folgenden Parametern:"
PARAM_OUTPUT="Programm = $PROGRAMM\nInput-Ordner = $INPUT_ORDNER\nOutput-Ordner = $OUTPUT_ORDNER"
echo -e "$PARAM_OUTPUT" | column -t

# durchlaufe die Eingabedaten
for AKT_EINGABE in "$INPUT_ORDNER"/*.in
do
    echo
    echo -e "${GRUEN}Verarbeite Testdatei $AKT_EINGABE:${NC}"

    # passe die Endung fuer die Ausgabedatei an
    AKT_AUSGABE=`basename "$AKT_EINGABE" .in`.out

    # fuehre das Programm aus
    if ! java -jar "$PROGRAMM" "$AKT_EINGABE" "$OUTPUT_ORDNER"/"$AKT_AUSGABE"
    then
        # falls das Programm nicht erfolgreich war
        echo -e "${HELLROT}Fehler bei Abarbeitung von ${AKT_EINGABE}${NC}"
    else
        echo "Abarbeitung erfolgreich."
    fi
done
