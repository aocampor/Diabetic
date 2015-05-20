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
    gbor = array.array('f',[0])
    reader.AddVariable("Green*Blue/Red",gbor)
    bpgor = array.array('f',[0])
    reader.AddVariable("(Blue+Green)/Red",bpgor)
    bmgor = array.array('f',[0])
    reader.AddVariable("(Blue-Green)/Red",bmgor)
    #pixx = array.array('f',[0])
    #reader.AddVariable("PixelX",pixx)
    #pixy = array.array('f',[0])
    #reader.AddVariable("PixelY",pixy)

    reader.BookMVA("MLP",str(sys.argv[1]))

    reader1.AddVariable("Red", red)
    reader1.AddVariable("Green",green)
    reader1.AddVariable("Blue",blue)
    reader1.AddVariable("Green*Blue/Red",gbor)
    reader1.AddVariable("(Blue+Green)/Red",bpgor)
    reader1.AddVariable("(Blue-Green)/Red",bmgor)

    #reader1.AddVariable("PixelX",pixx)
    #reader1.AddVariable("PixelY",pixy)
    #
    #reader1.BookMVA("MLP",str(sys.argv[2]))
    #
    #
    reader2.AddVariable("Red", red)
    reader2.AddVariable("Green",green)
    reader2.AddVariable("Blue",blue)
    #reader2.AddVariable("GreenOverRed",greenor)
    #reader2.AddVariable("BlueOverRed",blueor)
    reader2.AddVariable("Green*Blue/Red",gbor)
    reader2.AddVariable("(Blue+Green)/Red",bpgor)
    reader2.AddVariable("(Blue-Green)/Red",bmgor)
    #reader2.BookMVA("MLP",str(sys.argv[3]))
    #
    reader3.AddVariable("Red", red)
    reader3.AddVariable("Green",green)
    reader3.AddVariable("Blue",blue)
    reader3.AddVariable("Green*Blue/Red",gbor)
    reader3.AddVariable("(Blue+Green)/Red",bpgor)
    reader3.AddVariable("(Blue-Green)/Red",bmgor)
    #reader3.AddVariable("GreenOverRed",greenor)
    #reader3.AddVariable("BlueOverRed",blueor)
    #reader3.BookMVA("MLP",str(sys.argv[4]))

    reader1.BookMVA("MLP",str(sys.argv[2]))
    reader2.BookMVA("MLP",str(sys.argv[3]))
    reader3.BookMVA("MLP",str(sys.argv[4]))

    print 'so far so good'
    outrootfile = TFile.Open(str(sys.argv[5]),'recreate')

    Folder = str(sys.argv[6])
    #fols = ['0FoldLowRes/']#,'1FoldLowRes/','2FoldLowRes/','3FoldLowRes/','4FoldLowRes/']
    files = []
    #for item in fols:
    fi = os.listdir(Folder + '/') 
    for ix in fi:
        files.append( Folder + '/' + ix )

    #c1 = TCanvas()
    means = TH1F("means","means",100,-1,1)

    for item in files:
        tokens = item.rsplit('.')
        if(tokens[1] != 'root'):
            continue
        name = tokens[0].rsplit('Image')
        histo = TH1F(name[1]+'_01',name[1]+'_01',200,-1,2)    
        histo1 = TH1F(name[1]+'_02',name[1]+'_02',200,-1,2)    
        histo2 = TH1F(name[1]+'_03',name[1]+'_03',200,-1,2)    
        histo3 = TH1F(name[1]+'_04',name[1]+'_04',200,-1,2)    
        file1 = TFile(item)
        tree = file1.Get('Images')
        #i=0
        for entry in tree: 
            red[0] = entry.Red
            green[0] = entry.Green
            blue[0] = entry.Blue
            if(entry.Red != 0 ):
                gbor[0] = (entry.Green * entry.Blue)/ entry.Red
            else:
                gbor[0] = -1
            if(entry.Red != 0 ):
                bpgor[0] = (entry.Green + entry.Blue)/ entry.Red
            else:
                bpgor[0] = -1
            if(entry.Red != 0 ):
                bmgor[0] = ( entry.Blue - entry.Green )/ entry.Red
            else:
                bmgor[0] = -1

            #pixx[0] = entry.PixelX
            #pixy[0] = entry.PixelY

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
        #me = histo.GetMean()
        #print name[1] , me
        #means.Fill(me)

        #means.Draw()
        #histo1.Write()
        #histo2.Write()
        #histo3.Write()
    outrootfile.Close()
        #c1 = TCanvas()
        #histo.Draw()
        #c1.SaveAs('tesr.png')
