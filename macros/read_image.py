from PIL import Image, PixarImagePlugin
import os,sys
from array import array
from ROOT import TFile, TTree
import math
import os.path
import locale
from dialog import Dialog

def Log(sr):
    if(sr == 0):
        return 0
    elif(sr < 0):
        return 0
    else:
        return math.log(sr)

def getRGBMean(R,G,B):
    return (10*R + 10*G + B)/3

def ImageCrop(im):
    cropSpan = 50
    searchSpan = 2
    # Find the center ofthe rectangle to crop
    center = ( 0,0 )
    maxmean = 0
    maxi = 0;
    maxj = 0;
    pix = im.load()
    for ii in range(searchSpan, im.size[0] - searchSpan):
        for jj in range(searchSpan, im.size[1] - searchSpan):
 
            # span over a few neightbors now
            regionR = 0
            regionG = 0
            regionB = 0
            for i in range(ii - searchSpan, ii + searchSpan):
                for j in range(jj - searchSpan, jj + searchSpan):
                    regionR += pix[i, j][0]
                    regionG += pix[i, j][1]
                    regionB += pix[i, j][2]
                    
            mean = getRGBMean(regionR,regionG,regionB)
            if( mean > maxmean ):
                maxmean = mean
                maxi = ii
                maxj = jj
    
    cropRect = ( maxi - cropSpan, maxj - cropSpan, maxi + cropSpan, maxj + cropSpan )
    #cropRect = ( 0,0,100,100 )
    
    # make a crop
    return im.crop( cropRect )
        

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
        level = array('i', [0])
        level[0] = int(dic[token[0]])

        print("%s | level: %d"% (token, level[0]))
        
        inputs = Folder + '/' + item
        #output = Folder + fols[ int(dic[token[0]]) ] + 'LowRes/' + item1
        output = Folder + '/mod/' + str(level[0]) + '/' + item1
        if(not os.path.isfile(output) ):
            command = 'convert  -resize 10% ' + inputs + ' ' + output
            os.system(command)
        rootname = Folder + '/mod/' + str(level[0]) + '/Image' + token[0] + '.root'    
        if(os.path.isfile(rootname)):
            continue
        rootfil = TFile(rootname, 'recreate')
        tree = TTree('Images', 'Images')
        colorr = array('d', [0])
        colorg = array('d', [0])
        colorb = array('d', [0])
        colorgor = array('d', [0])
        colorbor = array('d', [0])
        
        tree.Branch('Red', colorr, 'Red/D')
        tree.Branch('Green', colorg, 'Green/D')
        tree.Branch('Blue', colorb, 'Blue/D')
        tree.Branch('GreenOverRed', colorgor, 'GreenOverRed/D')
        tree.Branch('BlueOverRed', colorbor, 'BlueOverRed/D')
        tree.Branch('Level', level, 'Level/I')

        # just a dialogue to interact with this
        d = Dialog(dialog="dialog")
        #d.set_background_title("Diabetes")

        #print(item)
        imOriginal10p = Image.open(output)  # Can be many different formats.
        #im.rotate(0).show()
        
        # Selection
        im = ImageCrop(imOriginal10p)
        #pid = im.rotate(0).show()
        #if d.yesno("Continue?") == 0:
        #    print pid
        #else:
        #    sys.exit(0)
                
        # work on the crop
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

