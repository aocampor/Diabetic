import os, sys
from ROOT import *
import ROOT as te

if __name__ == "__main__":
    #print 'TrainMVA.py outfile.root background.root signal.root #eventsback #eventsignal $cut $#cycles $#hiddenlayers'
    
    te.TMVA.Tools.Instance()
    #folder = '/media/aocampor/MyDisk/DiabeticRetinophaty/Output/'
    #folder = '/home/aocampor/DiabeticRetinophaty/Output/'
    #fout = TFile(folder + "test.root","RECREATE")
    nameout = sys.argv[1]
    fout = TFile(nameout,"RECREATE")

    factory = te.TMVA.Factory("TMVAClassification", fout,
                                ":".join([
                                    "!V",
                                    "!Silent",
                                    "Color",
                                    "DrawProgressBar",
                                    "Transformations=I;D;P;G,D",
                                    "AnalysisType=Classification"]
                                     ))


    square = 248
    for i in range(0, square, 8):
        #varr = 'var'+str(i)+'R := ('+'pix'+str(i) + ' & 0xff0000) >> 16'
        #varg = 'var'+str(i)+'G := ('+'pix'+str(i) + ' & 0x00ff00) >> 8'
        #varb = 'var'+str(i)+'B := ('+'pix'+str(i) + ' & 0x0000ff) '
        
        varr = 'NH'+str(i) + 'R := H' + str(i) + 'R + H' + str(i + 1) + 'R + H' + str(i+2) + 'R + H' + str(i+3) + 'R + H' + str(i+4) + 'R + H' + str(i+5) + 'R + H' + str(i+6) + 'R'  
        #varr = 'H'+str(i) + 'R'
        #varr = 'NH'+str(i) + 'R := H' + str(i) + 'R + H' + str(i + 1) + 'R + H' + str(i+2) + 'R'
        varg = 'NH'+str(i) + 'G := H' + str(i) + 'G + H' + str(i + 1) + 'G + H' + str(i+2) + 'G + H' + str(i+3) + 'G + H' + str(i+4) + 'G + H' + str(i+5) + 'G + H' + str(i+6) + 'G'
        varb = 'NH'+str(i) + 'B := H' + str(i) + 'B + H' + str(i + 1) + 'B + H' + str(i+2) + 'B + H' + str(i+3) + 'B + H' + str(i+4) + 'B + H' + str(i+5) + 'B + H' + str(i+6) + 'B'
        #print 
        factory.AddVariable(varr,"I")
        factory.AddVariable(varg,"I")
        #factory.AddVariable(varb,"I")
        #factory.AddVariable('pix'+str(i),"I")
        #factory.AddVariable('pix'+str(i),"I")

    factory.AddVariable('height',"I")
    factory.AddVariable('width',"I")

    #factory.AddVariable("Red","F")
    #factory.AddVariable("Green","F")
    #factory.AddVariable("Blue","F")
    ##factory.AddVariable("Hue","F")
    ##factory.AddVariable("Bright","F")
    ##factory.AddVariable("Saturation","F")
    ##factory.AddVariable("GreenOverRed","F")
    ##factory.AddVariable("BlueOverRed","F")
    #factory.AddVariable("PixelX","F")
    #factory.AddVariable("PixelY","F")

    #for i in range(0,100):
    #    factory.AddVariable("Pix"+str(i),"F")

    #files = ['background.root','signal.root']

    #f1 = TFile(folder + files[0])
    #f2 = TFile(folder + files[1])
    nameback = sys.argv[2]
    namesig = sys.argv[3]
    f1 = TFile(nameback)
    f2 = TFile(namesig)

    chain0 = f1.Get('Images')    
    chain1 = f2.Get('Images')    
    
    factory.AddBackgroundTree(chain0)
    factory.AddSignalTree(chain1)

    sigCut = te.TCut("drlevel >= " + str(sys.argv[6]))
    bgCut = te.TCut("drlevel < " + str(sys.argv[6]))

    nbck = str(sys.argv[5])
    nsig = str(sys.argv[4])
    
    inpfortraining = []

    if(nsig != '-1'):
        inpfortraining.append('nTrain_Signal='+nsig)
    if(nbck != '-1'):
        inpfortraining.append('nTrain_Background='+nbck)

        
    inpfortraining.append('SplitMode=Random')
    inpfortraining.append('NormMode=NumEvents')
    inpfortraining.append('!V')
        
    #factory.PrepareTrainingAndTestTree(sigCut,   # signal events
    #                                   bgCut,    # background events
    #                                   ":".join([
    #                                       "nTrain_Signal=" + str(sys.argv[4]),
    #                                       "nTrain_Background="+str(sys.argv[5]),
    #                                       "SplitMode=Random",
    #                                       "NormMode=NumEvents",
    #                                       "!V"
    #                                   ]))
    
    factory.PrepareTrainingAndTestTree(sigCut,   # signal events
                                       bgCut,    # background events
                                       ":".join( inpfortraining ) )    

    method = factory.BookMethod(te.TMVA.Types.kMLP, "MLP",
                                ":".join([
                                    "!H",
                                    "!V",
                                    #"NeuronType=tanh",
                                    "VarTransform=N",
                                    "NCycles="+str(sys.argv[7]),
                                    "HiddenLayers=N+"+str(sys.argv[8]),
                                    "TestRate=10",
                                    #"NTrees=850",
                                    #"nEventsMin=150",
                                    #"MaxDepth=30",
                                    #"BoostType=AdaBoost",
                                    #"AdaBoostBeta=0.5",
                                    #"SeparationType=GiniIndex",
                                    #"nCuts=20",
                                    #"PruneMethod=NoPruning",
                                ]))

    #method = factory.BookMethod( te.TMVA.Types.kLikelihood, "Likelihood", 
    #                             ":".join(["H","!V","PDFInterpol=Spline2",
    #                                       "NSmoothSig[0]=20","NSmoothBkg[0]=20",
    #                                       "NSmoothBkg[1]=10","NSmooth=1","NAvEvtPerBin=50"]))

    factory.TrainAllMethods()
    factory.TestAllMethods()
    factory.EvaluateAllMethods()
