%%
%% This is file `gropro.cls',
%% 
\NeedsTeXFormat{LaTeX2e}[1999/12/01]
\ProvidesClass{gropro}
    [2010/04/30 v1.1b gropro class]
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{gropro}[2010/04/30 gropro class]
\RequirePackage{ifthen}
\newboolean{gropro@setautor}\setboolean{gropro@setautor}{false}%
\newboolean{gropro@setpruefnr}\setboolean{gropro@setpruefnr}{false}%
\newboolean{gropro@setort}\setboolean{gropro@setort}{false}%
\newboolean{gropro@setsprache}\setboolean{gropro@setsprache}{false}%
\newboolean{gropro@setcompiler}\setboolean{gropro@setcompiler}{false}%
\newboolean{gropro@setrechner}\setboolean{gropro@setrechner}{false}%
\newboolean{gropro@setos}\setboolean{gropro@setos}{false}%
\newboolean{gropro@setthema}\setboolean{gropro@setthema}{false}%
\newboolean{gropro@weiblich}\setboolean{gropro@weiblich}{false}%
\newboolean{gropro@settitle}\setboolean{gropro@settitle}{false}%
\newboolean{gropro@umlcd}\setboolean{gropro@umlcd}{false}%
\newboolean{gropro@umlsd}\setboolean{gropro@umlsd}{false}%
\newboolean{gropro@nassi}\setboolean{gropro@nassi}{false}%
\newcommand{\gropro@titel}{}
\newcommand{\gropro@pruefling}{}
\newcommand{\gropro@thema}{}
\newcommand{\gropro@autor}{}
\newcommand{\gropro@pruefnr}{}
\newcommand{\gropro@ort}{}
\newcommand{\gropro@jahr}{\the\year}
\newcommand{\gropro@sprache}{}
\newcommand{\gropro@compiler}{}
\newcommand{\gropro@rechner}{}
\newcommand{\gropro@os}{}
\newcommand{\Autor}[1]{%
  \renewcommand{\gropro@autor}{#1}%
  \setboolean{gropro@setautor}{true}}
\newcommand{\PruefungsNR}[1]{%
  \renewcommand{\gropro@pruefnr}{#1}%
  \setboolean{gropro@setpruefnr}{true}}
\newcommand{\Ausbildungsort}[1]{%
  \renewcommand{\gropro@ort}{#1}%
  \setboolean{gropro@setort}{true}}
\newcommand{\Programmiersprache}[1]{%
  \renewcommand{\gropro@sprache}{#1}%
  \setboolean{gropro@setsprache}{true}}
\newcommand{\Compiler}[1]{%
  \renewcommand{\gropro@compiler}{#1}%
  \setboolean{gropro@setcompiler}{true}}
\newcommand{\Rechner}[1]{%
  \renewcommand{\gropro@rechner}{#1}%
  \setboolean{gropro@setrechner}{true}}
\newcommand{\Betriebssystem}[1]{%
  \renewcommand{\gropro@os}{#1}%
  \setboolean{gropro@setos}{true}}
\newcommand{\Thema}[1]{%
  \renewcommand{\gropro@thema}{#1}%
  \setboolean{gropro@setthema}{true}}
\newcommand{\MW}[1]{%
  \ifthenelse{\equal{#1}{w}}
  {%then
    \setboolean{gropro@weiblich}{true}}%
  {%else
    \ifthenelse{\equal{#1}{m}}%
    {%then
      \setboolean{gropro@weiblich}{false}}%
    {%else
      \ClassError{gropro}{%
        Only 'w' or 'm' are allowed for MW\MessageBreak}}}}
\newcommand{\PruefungsJahr}[1]{\renewcommand{\gropro@jahr}{#1}}
\DeclareOption{10pt}{\PassOptionsToClass{10pt}{report}}
\DeclareOption{11pt}{\PassOptionsToClass{11pt}{report}}
\ProcessOptions
\LoadClass[a4paper,12pt,twoside]{report}
\RequirePackage{ifpdf}
\RequirePackage{ngerman}
\RequirePackage{fancyhdr}
\RequirePackage{tabularx}
\RequirePackage{alltt}
\RequirePackage{times}
\RequirePackage{mathptmx}
\ifpdf
\RequirePackage[pdftex]{graphicx,color}
  \RequirePackage[pdftex, pdfborder={0 0 0}]{hyperref}
\else
\RequirePackage{color}
\RequirePackage[dvips]{graphicx}
\RequirePackage[dvips, pdfborder={0 0 0}]{hyperref}
\fi
%
\DeclareOption{umlcd}{\setboolean{gropro@umlcd}{true}}%
\DeclareOption{umlsd}{\setboolean{gropro@umlsd}{true}}%
\DeclareOption{nassi}{\setboolean{gropro@nassi}{true}}%
\ProcessOptions%
%
\ifthenelse{\boolean{gropro@umlcd}}{
  \RequirePackage{tikz}
  \RequirePackage{styles/pgf-umlcd}
}{}%
\ifthenelse{\boolean{gropro@umlsd}}{
  \RequirePackage{tikz}
  \RequirePackage{styles/pgf-umlsd}
}{}%
\ifthenelse{\boolean{gropro@nassi}}{%
  \RequirePackage{struktex}%
}{}%


\setlength{\parindent}{0pt}
\setlength{\parskip}{10pt}
\setlength{\textwidth}{15cm}
\setlength{\evensidemargin}{0.5cm}
%
\renewcommand{\headrulewidth}{0.4pt}
\renewcommand{\footrulewidth}{0.4pt}
\renewcommand{\chaptermark}[1]{\markboth{#1}{}}
%
\renewcommand\chapter{\if@openright\cleardoublepage\else\clearpage\fi
  \thispagestyle{empty}%
  \global\@topnum\z@
  \@afterindentfalse
  \secdef\@chapter\@schapter}
%
\newcommand\chapterS[1]{
  \addtocounter{chapter}{1}
  \phantomsection
  \makeatletter
  \addtocontents{toc}{\protect\contentsline{chapter}{%
      \protect\numberline{\thechapter}#1}{}{\@currentHref}}
  \makeatother
  \chapter*{Anhang \thechapter\\[1.2cm] #1}
}
% 
\newcommand\setpagestyle{%
  \pagestyle{fancy}
  \fancyhf{}
  \fancyhead[LO]{\bfseries \small{\leftmark}}
  \fancyhead[RE]{\bfseries \small{Dokumentation der Praktischen Arbeit \gropro@jahr}}
  \fancyhead[LE,RO]{\bfseries \small{\thepage}}
  \fancyfoot[RE]{\ifthenelse{\boolean{gropro@setthema}}{{\bfseries \small{\gropro@thema}}}{}}
  \ifthenelse{\boolean{gropro@setautor}}{
    \fancyfoot[LO]{\bfseries \small{\gropro@autor}}
  }{\ClassError{gropro}{Die Variablen AUTOR wurde nicht gesetzt !!!\MessageBreak}{}}
}
%
\AtBeginDocument{\setpagestyle}
%
\newcommand\setgeschlecht{%
  \ifthenelse{\boolean{gropro@settitle}}{}{%
    \setboolean{gropro@settitle}{true}%
    \ifthenelse{\boolean{gropro@weiblich}}%
    {%then
      \renewcommand{\gropro@titel}{
        Dokumentation der Praktischen Arbeit\\%
        zur Pr"ufung zur\\%
        Mathematisch-technischen Softwareentwicklerin}%
      \renewcommand{\gropro@pruefling}{der Pr"ufungsteilnehmerin}}%
    {%else
      \renewcommand{\gropro@titel}{
        Dokumentation der Praktischen Arbeit\\%
        zur Pr"ufung zum\\%
        Mathematisch-technischen Softwareentwickler}%
      \renewcommand{\gropro@pruefling}{des Pr"ufungsteilnehmers}}}}
%
\renewcommand\maketitle{%
  \pagestyle{empty}
  \@maketitle\cleardoublepage
  \setpagestyle
}
%
\def\@maketitle{%
  \setgeschlecht
  \newpage
  \vskip 2em%
  \begin{center}%
    \rule{0.95\textwidth}{1pt}%
    \vskip .3cm%
    \begin{minipage}{0.9\textwidth}
      \renewcommand{\baselinestretch}{1.3}
      \begin{center}
        \Large \textbf{\gropro@titel}
      \end{center}
    \end{minipage}
    \vskip .3cm%
    \rule{0.95\textwidth}{1pt}\vfill\vfill
    \ifthenelse{\boolean{gropro@setthema}}{{\Large%
      \textbf{Thema:\begin{tabular}[t]{l}\gropro@thema\end{tabular}}}}{}
    \vskip .5cm
    \today
    \ifthenelse{\boolean{gropro@setautor}}{
      \vfill\vfill\vfill{\large \textbf{\gropro@autor}}
    }{\ClassError{gropro}{Die Variablen AUTOR wurde nicht gesetzt !!!\MessageBreak}{}}
    \vfill\vfill\vfill
    \begin{tabular}{p{6cm}p{6cm}}
      \ifthenelse{\boolean{gropro@setpruefnr}}{
        \hfill Pr"ufungs-Nummer: & \gropro@pruefnr\\[.3cm]
      }{\ClassError{gropro}{Die Variablen PRUEFUNGSNR wurde nicht gesetzt !!!\MessageBreak}{}}
      \ifthenelse{\boolean{gropro@setsprache}}{
        \hfill Programmiersprache: & \gropro@sprache\\[.3cm]
      }{\ClassError{gropro}{Die Variablen PROGRAMMIERSPRACHE wurde nicht gesetzt !!!\MessageBreak}{}}
      \ifthenelse{\boolean{gropro@setort}}{
        \hfill Ausbildungsort: & \hskip -5pt \begin{tabular}[t]{l}\gropro@ort\end{tabular}
      }{\ClassError{gropro}{Die Variablen AUSBILDUNGSORT wurde nicht gesetzt !!!\MessageBreak}{}}
    \end{tabular}
  \end{center}}

\newcommand{\Ginclude}[1]{\include{#1}\cleardoublepage}
%
\newcommand\makeenvironment{%
  \begin{center}%
    \begin{tabular}{p{3.5cm}cp{9cm}}%
      \ifthenelse{\boolean{gropro@setsprache}}{
        Programmiersprache & : & \gropro@sprache\\%
      }{\ClassError{gropro}{Die Variablen PROGRAMMIERSPRACHE wurde nicht gesetzt !!!\MessageBreak}{}}
      \ifthenelse{\boolean{gropro@setcompiler}}{
        Compiler & : & \gropro@compiler\\%
      }{\ClassError{gropro}{Die Variablen COMPILER wurde nicht gesetzt !!!\MessageBreak}{}}
      \ifthenelse{\boolean{gropro@setrechner}}{
        Rechner & : & \gropro@rechner\\%
      }{\ClassError{gropro}{Die Variablen RECHNER wurde nicht gesetzt !!!\MessageBreak}{}}
      \ifthenelse{\boolean{gropro@setos}}{
        Betriebssystem & : & \gropro@os\\%
      }{\ClassError{gropro}{Die Variablen BETRIEBSSYSTEM wurde nicht gesetzt !!!\MessageBreak}{}}
    \end{tabular}%
  \end{center}}
%
\newcommand\makedeposition{
  \setgeschlecht
  \chapter{Erkl"arung \gropro@pruefling}\label{Erklaerung}
  Ich versichere durch meine Unterschrift, dass ich das
  Pr"ufungsprodukt selbstst"andig und ohne fremde Hilfe angefertigt und
  alle Stellen, die ich w"ortlich oder ann"ahernd w"ortlich aus
  Ver"offentlichungen entnommen habe, als solche kenntlich gemacht
  habe. Die Arbeit hat in dieser Form keiner anderen
  Pr"ufungsinstitution vorgelegen. 

  Das auf den beiden identischen CDs abgelegte Pr"ufungsprodukt
  entspricht der gedruckten Version.\\[1cm]

  \begin{tabbing}
    \rule{5cm}{0.4pt}\=\hspace*{3cm}\=\rule{7cm}{0.4pt}\\
    \small{Ort und Datum}\>\>\small{Unterschrift \gropro@pruefling}
  \end{tabbing}
  \cleardoublepage
}
