Card Transaction Analyzer
This is a simple Java desktop app that helps you read, view, and analyze credit card transaction data from XML files. You can group data, see totals, and export it to PDF or Excel.

✅ What This App Can Do
Read and show XML transaction files

Group data by Date, Card Type, or File

Show total Gross, Net, Fee, and Quantity

Display charts for quick visual summary

Export reports to PDF or Excel

Sort reports from newest to oldest (or vice versa)

Easily switch between multiple XML files

==> Files in This Project

| File Name                    | What it does                  |
| ---------------------------- | ----------------------------- |
| `Main.java`                  | Starts the app                |
| `Transaction_Viewer_UI.java` | Main user interface (GUI)     |
| `XML_Parser.java`            | Reads data from XML files     |
| `Card_Transaction.java`      | Holds one transaction's data  |
| `Report_Analyzer.java`       | Makes summaries for reports   |
| `PDF_Exporter.java`          | Saves table to a PDF file     |
| `Excel_Exporter.java`        | Saves report to an Excel file |
| `Program_Manager.java`       | Manages files and data flow   |


📦 Requirements
Java 8 or newer

Apache PDFBox (for PDF export)

Apache POI (for Excel export)

🖥️ How to Use
Double-click the CardTransactionAnalyzer.jar file to open the app
(Make sure you have Java 8 or higher installed)

You have two ways to add XML files:

Option 1: Use the app

Click the “Add XML File” button inside the app

Select your .xml file — it will be copied automatically into the xml_files folder

Option 2: Add manually

Create a folder named xml_files in the same location as the .jar file

Move or copy your .xml files into that folder before running the app

After adding files, choose how you want to view the report (by Date, Card Type, etc.)

You can also export the report as a PDF or Excel using the export buttons.

cle
==> Example Folder Setup
MyFolder/
├── CardTransactionAnalyzer.jar
└── xml_files/
    ├── file1.xml
    └── file2.xml
