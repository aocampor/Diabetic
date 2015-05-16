import os, sys
from ROOT import *
import array as array
import ROOT

if __name__ == "__main__":
    outrootfile = TFile('OutputClassification.root')
    keys = outrootfile.GetListOfKeys()
    txtout = open('firstatempt.csv','w')
    txtout.write('image,level\n')
    for item in keys:
        #print item
        name = item.GetName()
        #print name
        #c1 = TCanvas()
        histo = outrootfile.Get(name)
        mean = histo.GetMean()
        if(mean > 0.5):
            txtout.write(name + ',' + str(1) + '\n')
        else: 
            txtout.write(name + ',' + str(0)+'\n')
        #histo.Draw()
        #c1.SaveAs('test.png')
    txtout.close()

