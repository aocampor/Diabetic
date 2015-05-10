import os, sys
from ROOT import *
import array as array
import ROOT

if __name__ == "__main__":

    reader = TMVA.Reader()
    reader1 = TMVA.Reader()
    reader2 = TMVA.Reader()
    reader3 = TMVA.Reader()

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
    reader.BookMVA("MLP",str(sys.argv[1]))

    reader1.AddVariable("Red", red)
    reader1.AddVariable("Green",green)
    reader1.AddVariable("Blue",blue)
    reader1.AddVariable("GreenOverRed",greenor)
    reader1.AddVariable("BlueOverRed",blueor)
    reader1.BookMVA("MLP",str(sys.argv[2]))


    reader2.AddVariable("Red", red)
    reader2.AddVariable("Green",green)
    reader2.AddVariable("Blue",blue)
    reader2.AddVariable("GreenOverRed",greenor)
    reader2.AddVariable("BlueOverRed",blueor)
    reader2.BookMVA("MLP",str(sys.argv[3]))

    reader3.AddVariable("Red", red)
    reader3.AddVariable("Green",green)
    reader3.AddVariable("Blue",blue)
    reader3.AddVariable("GreenOverRed",greenor)
    reader3.AddVariable("BlueOverRed",blueor)
    reader3.BookMVA("MLP",str(sys.argv[4]))

    outrootfile = TFile.Open(str(sys.argv[5]),'recreate')

    Folder = str(sys.argv[6])
    #fols = ['0FoldLowRes/']#,'1FoldLowRes/','2FoldLowRes/','3FoldLowRes/','4FoldLowRes/']
    files = []
    #for item in fols:
    fi = os.listdir(Folder + '/') 
    for ix in fi:
        files.append( Folder + '/' + ix )

    for item in files:
        tokens = item.rsplit('.')
        if(tokens[1] != 'root'):
            continue
        name = tokens[0].rsplit('Image')
        histo = TH1F(name[1]+'_01',name[1]+'_01',200,-1,2)    
        histo1 = TH1F(name[1]+'_12',name[1]+'_12',200,-1,2)    
        histo2 = TH1F(name[1]+'_23',name[1]+'_23',200,-1,2)    
        histo3 = TH1F(name[1]+'_34',name[1]+'_34',200,-1,2)    
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
            bdtOutput1 = reader1.EvaluateMVA("MLP")
            bdtOutput2 = reader2.EvaluateMVA("MLP")
            bdtOutput3 = reader3.EvaluateMVA("MLP")
            histo.Fill(bdtOutput)
            histo1.Fill(bdtOutput1)
            histo2.Fill(bdtOutput2)
            histo3.Fill(bdtOutput3)
        file1.Close()
        outrootfile.cd()
        histo.Write()
        histo1.Write()
        histo2.Write()
        histo3.Write()
    outrootfile.close()
        #c1 = TCanvas()
        #histo.Draw()
        #c1.SaveAs('tesr.png')
