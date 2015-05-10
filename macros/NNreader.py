import os, sys
from ROOT import *
import array as array
import ROOT

if __name__ == "__main__":

    reader = TMVA.Reader()

    red  = array.array('f',[0])
    reader.AddVariable("Red", red)
    green =  array.array('f',[0])
    reader.AddVariable("Green",green)
    blue = array.array('f',[0])
    reader.AddVariable("Blue",blue)
    greenor =  array.array('f',[0])
    reader.AddVariable("GreenOverRed",greenor)
    blueor = array.array('f',[0])
    reader.AddVariable("BlueOverRed",blueor)
    reader.BookMVA("MLP","weights/TMVAClassification_MLP.weights_01_1_500_1.xml")

    outrootfile = TFile.Open('/home/aocampor/DiabeticRetinophaty/Output/OutputClassification.root','recreate')

    Folder = '/media/aocampor/MyDisk/DiabeticRetinophaty/'
    fols = ['0FoldLowRes/']#,'1FoldLowRes/','2FoldLowRes/','3FoldLowRes/','4FoldLowRes/']
    files = []
    for item in fols:
        fi = os.listdir(Folder+item+'/') 
        for ix in fi:
            files.append( Folder+item+ix )

    for item in files:
        tokens = item.rsplit('.')
        if(tokens[1] != 'root'):
            continue
        name = tokens[0].rsplit('Image')
        histo = TH1F(name[1],name[1],200,-1,2)    
        file1 = TFile(item)
        tree = file1.Get('Images')
        #i=0
        for entry in tree: 
            red[0] = entry.Red
            green[0] = entry.Green
            blue[0] = entry.Blue
            greenor[0] = entry.GreenOverRed
            blueor[0] = entry.BlueOverRed
            
            bdtOutput = reader.EvaluateMVA("MLP")
            histo.Fill(bdtOutput)
        file1.Close()
        outrootfile.cd()
        histo.Write()
    outrootfile.close()
        #c1 = TCanvas()
        #histo.Draw()
        #c1.SaveAs('tesr.png')
