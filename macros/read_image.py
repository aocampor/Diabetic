from PIL import Image
import os,sys
from array import array
from ROOT import TFile, TTree
import math
import os.path


def Log(sr):
    if(sr == 0):
        return 0
    elif(sr < 0):
        return 0
    else:
        return math.log(sr)

def GetDic(labels):
    dic = {}
    labf = open(labels, 'r')
    for item in labf:
        #print item
        if(item != 'image,level'):
            sta = item.rsplit(',')
            dic[sta[0]] = sta[1].rsplit('\n')[0]
    labf.close()
    return dic

if __name__ == "__main__":

    #Folder = '/home/aocampor/DiabeticRetinophaty/sample/'
    #Folder = '/media/aocampor/MyDisk/DiabeticRetinophaty/'
    #fols = ['0Fold','1Fold','2Fold','3Fold','4Fold']
    Folder = str(sys.argv[1])
    files = []
    #for item in fols:
        #fi = os.listdir(Folder+item+'/') 
    fi = os.listdir(Folder+'/') 
    for ix in fi:
        files.append( ix )

    #labelfile = '/home/aocampor/DiabeticRetinophaty/trainLabels.csv'
    labelfile = str(sys.argv[2])    

    dic = GetDic(labelfile)

    for item in files:
        item1 = 'Lowres_' + item
        token = item.rsplit('.jpeg')
        print token
        inputs = Folder + '/' + item
        #output = Folder + fols[ int(dic[token[0]]) ] + 'LowRes/' + item1
        output = Folder + '/' + item1
        if(not os.path.isfile(output) ):
            command = 'convert  -resize 10% ' + inputs + ' ' + output
            os.system(command)
        rootname = Folder + '/Image' + token[0] + '.root'    
        if(os.path.isfile(rootname)):
            continue
        rootfil = TFile(rootname, 'recreate')
        tree = TTree('Images', 'Images')
        colorr = array('d', [0])
        colorg = array('d', [0])
        colorb = array('d', [0])
        colorgor = array('d', [0])
        colorbor = array('d', [0])
        level = array('i', [0])
        
        level[0] = int(dic[token[0]])
        
        tree.Branch('Red', colorr, 'Red/D')
        tree.Branch('Green', colorg, 'Green/D')
        tree.Branch('Blue', colorb, 'Blue/D')
        tree.Branch('GreenOverRed', colorgor, 'GreenOverRed/D')
        tree.Branch('BlueOverRed', colorbor, 'BlueOverRed/D')
        tree.Branch('Level', level, 'Level/I')

        #print(item)
        im = Image.open(output)  # Can be many different formats.
        pix = im.load()
        #pixg = im.convert('LA').load()

        for i in range(im.size[0]):
            for j in range(im.size[1]):
                if(pix[i, j][0] <= 3 and pix[i, j][1] <= 3 and
                   pix[i, j][2] <= 3):
                    continue
                colorr[0] = pix[i, j][0]
                colorg[0] = pix[i, j][1]
                colorb[0] = pix[i, j][2]
                if(colorr[0] != 0):
                    colorbor[0] = colorb[0]/colorr[0]
                    colorgor[0] = colorg[0]/colorr[0]
                else:
                    colorbor[0] = 0
                    colorgor[0] = 0
                tree.Fill()

        rootfil.Write()
        rootfil.Close()

