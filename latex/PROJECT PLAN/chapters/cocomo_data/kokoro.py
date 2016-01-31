#!/bin/python3
"""
    Requirements:
        pypandoc
        beautifulsoup

    Usage:
        1) run http://csse.usc.edu/tools/COCOMOII.php
        2) save the page as "cocomo.htm", in the same folder as this script
        3) run the script
        4) developement.tex and effort.tex may now be injected into your latex document
"""

from bs4 import BeautifulSoup
import pypandoc

raw = ""
with open("cocomo.htm", "r") as loadfrom:
    raw = loadfrom.read()

soup = BeautifulSoup(raw, "html.parser")

td1 = soup.find_all("td", style="vertical-align: top; text-align: left;")

developement = td1[0].decode_contents()
pypandoc.convert(developement, "tex", outputfile="developement.tex", format="html")

table2 = soup.find_all("table", width="200 pixels", cellspacing="0", cellpadding="1", border="1", cols="5")
effort = "<b>Software Effort Distribution for RUP/MBASE (Person-Months)</b>\n"
effort += str(table2[0])
pypandoc.convert(effort, "tex", outputfile="effort.tex", format="html")

print("All done!")