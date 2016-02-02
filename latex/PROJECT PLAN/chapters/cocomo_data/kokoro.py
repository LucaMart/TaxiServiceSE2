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
import re
from collections import OrderedDict
import urllib.request

raw = "<html></html>"
with open("cocomo.htm", "r") as loadfrom:
    raw = loadfrom.read()

soup = BeautifulSoup(raw, "html.parser")

inputnum = soup.find_all("table", cellspacing="2", cellpadding="2", style="width: 60%; text-align: left;")
for inputtag in inputnum[0].find_all("input"):
    try:
        value = str(inputtag["value"])
        inputtag.replaceWith(value)
    except KeyError:
        inputtag.replaceWith("")
pypandoc.convert(str(inputnum[0]), "tex", outputfile="input_numeric.tex", format="html")


inputdriverFind = soup.find_all("table", border="0")
super_candidates = []
for candidate in inputdriverFind:
    if str(candidate).find("Precedentedness") != -1:
        super_candidates.append(candidate)
def keyfun(elem):
    return len(str(elem))
inputdriver = sorted(super_candidates, key=keyfun, reverse=True)
for seltag in inputdriver[0].find_all("select"):
    for selected in seltag.find_all("option", selected=re.compile(".*")):
        value = str(selected.text).strip()
        seltag.replaceWith(value)
        break
# this HTML is messy, so webuild the latex my hand
drivers = OrderedDict()
for trtag in inputdriver[0].find_all("tr"):
    cells = trtag.find_all("td")
    if len(cells) == 2:
        drivers[cells[0].text.strip()] = cells[1].text.strip()
with open("input_drivers.tex", "w") as texfile:
    texfile.write("\\begin{description}\n")
    for key,val in drivers.items():
        texfile.write("\\item [%s:] %s\n" % (key,val))
    texfile.write("\\end{description}\n")


td1 = soup.find_all("td", style="vertical-align: top; text-align: left;")
developement = td1[0].decode_contents()
pypandoc.convert(developement, "tex", outputfile="developement.tex", format="html")


table2 = soup.find_all("table", width="200 pixels", cellspacing="0", cellpadding="1", border="1", cols="5")
effort = "<b>Software Effort Distribution for RUP/MBASE (Person-Months)</b>\n"
effort += str(table2[0])
pypandoc.convert(effort, "tex", outputfile="effort.tex", format="html")

print("All done!")