# Test Results & Validation Harness

This directory contains the automated test suites and validation reports for the **Campus Smart-Parking System**.

## Contents
1. `Task11_Verification_Test.java`: Automated test suite covering Scenarios 01 through 13 (Standard Smart Parking System features).
2. `Task12_Verification_Test.java`: Automated test suite covering Scenarios 14 through 19 (Extended Design Challenge features: E-Bikes, Delivery Vans, Passes, 5 Floors).
3. `Test_Execution_Report.md`: Markdown test table documenting expected vs actual results for all 19 test scenarios.

## Execution Instructions
From the `Test_Results/` directory:
```bash
# Compile
javac -cp "..\Task11_Complete_Implementation" Task11_Verification_Test.java
javac -cp "..\Task12_Design_Challenge" Task12_Verification_Test.java

# Execute
java -cp ".;..\Task11_Complete_Implementation" Task11_Verification_Test
java -cp ".;..\Task12_Design_Challenge" Task12_Verification_Test
```
All 19 test scenarios execute with **100% PASS** rate.
