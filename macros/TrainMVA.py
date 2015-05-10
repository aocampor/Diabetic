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

    factory.AddVariable("Red","F")
    factory.AddVariable("Green","F")
    factory.AddVariable("Blue","F")
    factory.AddVariable("GreenOverRed","F")
    factory.AddVariable("BlueOverRed","F")


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

    sigCut = te.TCut("Level >= " + str(sys.argv[6]))
    bgCut = te.TCut("Level < " + str(sys.argv[6]))

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
